package aiss.dailymotion_miner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DailymotionMinerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailymotionMinerApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
}
}
