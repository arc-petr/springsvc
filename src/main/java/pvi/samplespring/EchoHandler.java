package pvi.samplespring;

import org.springframework.stereotype.Component;

public class EchoHandler {

	public String echo(String data) {
		return buildResult(data);
	}

	private String buildResult(String param) {
		return String.format("ECHO: %s", param);
	}
}
