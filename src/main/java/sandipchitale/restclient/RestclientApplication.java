package sandipchitale.restclient;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

@SpringBootApplication
public class RestclientApplication {
	record Todo(long id, long userid, String title, boolean completed) {}

	@Bean
	RestClient restClient(RestClient.Builder builder) {
		return builder.baseUrl("https://jsonplaceholder.typicode.com/todos").build();
	}

	@Bean
	public CommandLineRunner clrRestClient (RestClient restClient) {
	    return (String... args) -> {
			ResponseEntity<Todo> todoResponseEntity = restClient
					.get()
					.uri("/{id}", 1)
					.retrieve()
					.toEntity(Todo.class);
			System.out.println("Todo using RestClient: " + todoResponseEntity.getBody());

			ResponseEntity<List<Todo>> todosResponseEntity = restClient
					.get()
					.retrieve()
					// IMPORTANT: We have to create subclass to capture the generic type
					// that can be used at runtime.
					.toEntity(new ParameterizedTypeReference<>(){});
			System.out.println("Todos using RestClient: " + todosResponseEntity.getBody());
		};
	}

	interface TodoClient {
		@GetExchange
		List<Todo> getTodos();

		@GetExchange("/{id}")
		Todo getTodo(@PathVariable("id") long id);
	}

	@Bean
	TodoClient todoClient(RestClient restClient) {
		HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
		return proxyFactory.createClient(TodoClient.class);
	}

	@Bean
	public CommandLineRunner clrTodoClient (TodoClient todoClient) {
		return (String... args) -> {
			System.out.println("Todo using TodoClient: " + todoClient.getTodo(1));
			System.out.println("Todos using TodoClient: " + todoClient.getTodos());
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(RestclientApplication.class, args);
	}

}
