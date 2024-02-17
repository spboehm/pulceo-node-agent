package dev.pulceo.pna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PulceoNodeAgentApplication {

	public static void main(String[] args) {
		// TODO: remove for dev
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", "./bootstrap-k3s-access.sh");
			Process p = processBuilder.start();
			p.waitFor();
		} catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        SpringApplication.run(PulceoNodeAgentApplication.class, args);
	}

}
