package tv.skimo.meeting;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import tv.skimo.meeting.lib.StorageProperties;
import tv.skimo.meeting.services.StorageService;

@SpringBootApplication
@ComponentScan({"tv.skimo.meeting"})
@EnableConfigurationProperties(StorageProperties.class)
public class SkimoMeetingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkimoMeetingApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}
