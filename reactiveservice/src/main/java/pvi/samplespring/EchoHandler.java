package pvi.samplespring;

public class EchoHandler {

	public String echo(String data) {
		return buildResult(data);
	}


	private String buildResult(String param) {
		return String.format("ECHONEW: %s", param);
	}

	public String echoRevert(String msg) {
		return new StringBuilder().append(msg).reverse().toString();
	}
	public String echoRevert2(String msg) {
		return new StringBuilder().append(msg).reverse().toString()+"/V2";
	}
}
