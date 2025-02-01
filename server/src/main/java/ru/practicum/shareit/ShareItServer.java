package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;

@SpringBootApplication
public class ShareItServer {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ShareItServer.class, args);
	}
}
