package pvi.samplespring;

public class EchoHandler {

	public String echo(String data) {
		return buildResult(data);
	}

	private String buildResult(String param) {
		return String.format("ECHONEW: %s", param);
	}
}
