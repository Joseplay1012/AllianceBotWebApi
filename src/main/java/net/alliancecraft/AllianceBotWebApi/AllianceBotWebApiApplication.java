package net.alliancecraft.AllianceBotWebApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AllianceBotWebApiApplication {

	public static void main(String[] args) {
		IPBanFilter.loadBanIps();
		SpringApplication.run(AllianceBotWebApiApplication.class, args);
	}

}
