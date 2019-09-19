package pvi.samplespring;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class EchoRouter {

	EchoHandler echoHandler = new EchoHandler();

	@Bean
	public RouterFunction<ServerResponse> echoMsg() {
		
		return route(RequestPredicates.GET("/echo/{msg}").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
				req -> ok().contentType(MediaType.TEXT_PLAIN)
						.body(BodyInserters.fromObject(echoHandler.echo(req.pathVariable("msg")))));
	}
}
