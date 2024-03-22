package dev.pulceo.pna;

import dev.pulceo.pna.util.ProcessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PulceoNodeAgentApplication {

	private final static Logger logger = LoggerFactory.getLogger(PulceoNodeAgentApplication.class);

	public static void main(String[] args) {
		// TODO: remove for dev
		Process p = null;
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", "./bootstrap-k3s-access.sh");
			p = processBuilder.start();
			p.waitFor();
		} catch (InterruptedException | IOException e) {
			System.out.println("Error while running bootstrap-k3s-access.sh");
        } finally {
            try {
                ProcessUtils.closeProcess(p);
            } catch (IOException e) {
				logger.error("Could not close process", e);
            }
        }
        SpringApplication.run(PulceoNodeAgentApplication.class, args);
	}

}
