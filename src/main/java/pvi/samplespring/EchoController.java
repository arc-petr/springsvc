package pvi.samplespring;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class EchoController {
	@GetMapping(path = "/echor/{msg}", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String getMessage(@PathVariable(value = "msg", required = true) String msg) {
		return String.format("ECHO: %s", msg);
	}

}
