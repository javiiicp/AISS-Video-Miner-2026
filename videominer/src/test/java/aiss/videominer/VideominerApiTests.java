package aiss.videominer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import aiss.videominer.model.Video;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.VideoRepository;

@SpringBootTest
@AutoConfigureMockMvc
class VideominerApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CaptionRepository captionRepository;

    @Autowired
    private CommentRepository commentRepository;

    void cleanDatabase() {
        commentRepository.deleteAll();
        captionRepository.deleteAll();
        videoRepository.deleteAll();
    }

    @Test
    void shouldReturn404WhenVideoDoesNotExist() throws Exception {
      cleanDatabase();
        mockMvc.perform(get("/videominer/videos/missing-video"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateAndReadCaptionByVideo() throws Exception {
      cleanDatabase();
        Video video = new Video();
        video.setId("video-1");
        video.setName("Sample video");
        video.setDescription("Sample description");
        video.setReleaseTime("2026-04-30T10:00:00Z");
        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        videoRepository.save(video);

        mockMvc.perform(post("/videominer/captions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "caption-1",
                                  "link": "https://example.com/caption.vtt",
                                  "language": "es",
                                  "video": { "id": "video-1" }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("caption-1"))
                .andExpect(jsonPath("$.video.id").value("video-1"));

        mockMvc.perform(get("/videominer/captions/video/video-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("caption-1"));
    }

    @Test
    void shouldCreateCommentAndRejectMissingVideoOnLookup() throws Exception {
      cleanDatabase();
        Video video = new Video();
        video.setId("video-2");
        video.setName("Another video");
        video.setDescription("Another description");
        video.setReleaseTime("2026-04-30T10:00:00Z");
        video.setComments(new ArrayList<>());
        video.setCaptions(new ArrayList<>());
        videoRepository.save(video);

        mockMvc.perform(post("/videominer/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "comment-1",
                                  "text": "Nice video",
                                  "createdOn": "2026-04-30T11:00:00Z",
                                  "video": { "id": "video-2" }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("comment-1"))
                .andExpect(jsonPath("$.video.id").value("video-2"));

        mockMvc.perform(get("/videominer/comments/video/missing-video"))
                .andExpect(status().isNotFound());
    }
}