package pvi.samplespring;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoController {
	@GetMapping(path = "/echor/{msg}", produces = { MediaType.TEXT_PLAIN_VALUE })
	@ResponseBody
	public String getMessage(@PathVariable String msg) {
		
		return String.format("ECHO: %s", msg);
	}
	@GetMapping(path = "/echorever2/{msg}", produces = { MediaType.TEXT_PLAIN_VALUE })
	@ResponseBody
	public String getMessageReverse(@PathVariable String msg) {
		return new StringBuilder().append(msg).reverse().toString();
	}

	@GetMapping(path = "/echorever/{msg}", produces = { MediaType.TEXT_PLAIN_VALUE })
	@ResponseBody
	public String getMessageReverse2(@PathVariable String msg) {
		return new StringBuilder().append(msg).reverse().toString()+"Test5";
	}
	@GetMapping(path = "/echorever3/{msg}", produces = { MediaType.TEXT_PLAIN_VALUE })
	@ResponseBody
	public String getMessageReverse3(@PathVariable String msg) {
		return new StringBuilder().append(msg).reverse().toString()+"Test5";
	}
}
