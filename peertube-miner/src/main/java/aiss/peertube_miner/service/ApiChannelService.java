@Service
public class ApiChannelService {

    @Autowired
    RestTemplate restTemplate;

    public Channel getChannelFromPeerTube(String instance, String channelId, int maxVideos, int maxComments) {
        String baseUrl = "https://" + instance + "/api/v1";

        // --- PASO 0: Obtener el Canal (Lo que ya teníamos) ---
        String urlCanal = baseUrl + "/video-channels/" + channelId;
        ApiChannel resCanal = restTemplate.getForObject(urlCanal, ApiChannel.class);
        
        if (resCanal == null || resCanal.getData().isEmpty()) return null;
        
        Datum ptChannel = resCanal.getData().get(0);
        Channel videominerChannel = new Channel();
        videominerChannel.setId(ptChannel.getId().toString());
        videominerChannel.setName(ptChannel.getDisplayName());
        videominerChannel.setDescription(ptChannel.getDescription());
        videominerChannel.setCreatedTime(ptChannel.getCreatedAt());

        // --- PASO A: Obtener los Vídeos ---
        String urlVideos = urlCanal + "/videos?count=" + maxVideos;
        // VideoResponse es el "Sobre" de vídeos que generaste
        VideoResponse resVideos = restTemplate.getForObject(urlVideos, VideoResponse.class);

        if (resVideos != null && resVideos.getData() != null) {
            List<Video> listaVideosLimpia = new ArrayList<>();

            for (VideoDatum ptVideo : resVideos.getData()) {
                // Convertimos el vídeo de PeerTube al de VideoMiner
                Video v = new Video();
                v.setId(ptVideo.getUuid()); // PeerTube suele usar UUID para vídeos
                v.setName(v.getName());
                v.setDescription(ptVideo.getDescription());
                v.setReleaseTime(ptVideo.getPublishedAt());
                
                // --- PASO B: Obtener los Comentarios para ESTE vídeo ---
                String urlComments = baseUrl + "/videos/" + ptVideo.getId() + "/comment-threads?count=" + maxComments;
                // CommentResponse es el "Sobre" de comentarios
                CommentResponse resComments = restTemplate.getForObject(urlComments, CommentResponse.class);

                if (resComments != null && resComments.getData() != null) {
                    List<Comment> listaCommentsLimpia = new ArrayList<>();
                    
                    for (CommentDatum ptComment : resComments.getData()) {
                        Comment c = new Comment();
                        c.setId(ptComment.getId().toString());
                        c.setText(ptComment.getText());
                        c.setCreatedTime(ptComment.getCreatedAt());
                        listaCommentsLimpia.add(c);
                    }
                    v.setComments(listaCommentsLimpia);
                }

                listaVideosLimpia.add(v);
            }
            // Metemos la lista de vídeos terminada en el canal
            videominerChannel.setVideos(listaVideosLimpia);
        }

        return videominerChannel;
    }
}