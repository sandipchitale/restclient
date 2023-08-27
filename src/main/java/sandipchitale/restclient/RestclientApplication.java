package sandipchitale.restclient;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class RestclientApplication {
	record Todo(long id, long userid, String title, boolean completed) {}

	@Bean
	RestClient restClient(RestClient.Builder builder) {
		return builder.baseUrl("https://jsonplaceholder.typicode.com").build();
	}

	@Bean
	public CommandLineRunner clrRestClient (RestClient restClient) {
	    return (args) -> {
			ResponseEntity<Todo> todoResponseEntity = restClient
					.get()
					.uri("/todos/1")
					.retrieve()
					.toEntity(Todo.class);
			System.out.println("Using RestClient: " + todoResponseEntity.getBody());
		};
	}

	interface TodoClient {
		@GetExchange("/todos/1")
		Todo getTodo();
	}

	@Bean
	TodoClient todoClient(RestClient restClient) {
		HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
		return proxyFactory.createClient(TodoClient.class);
	}

	@Bean
	public CommandLineRunner clrTodoClient (TodoClient todoClient) {
		return (args) -> {
			System.out.println("Using TodoClient: " + todoClient.getTodo());
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(RestclientApplication.class, args);
	}

}
