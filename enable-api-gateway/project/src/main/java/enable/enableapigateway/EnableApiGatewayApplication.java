package enable.enableapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;


@SpringBootApplication
public class EnableApiGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(EnableApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocatorBuilder routeLocatorBuilder(ConfigurableApplicationContext context) {
		return new RouteLocatorBuilder(context);
	}

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder, Environment environment) {
		String enable_user_service_baseurl = environment.getProperty("ENABLE_USER_SERVICE_BASEURL");
		String enable_execution_service_baseurl = environment.getProperty("ENABLE_EXECUTION_SERVICE_BASEURL");

		return builder.routes()
				.route("signup", r -> r
						.path("/api/auth/signup")
						.uri(enable_user_service_baseurl + "/api/auth/signup"))
				.route("signin", r -> r
						.path("/api/auth/signin")
						.uri(enable_user_service_baseurl + "/api/auth/signin"))
				.route("check_username_availability", r -> r
						.path("/api/auth/checkUsernameAvailability")
						.uri(enable_user_service_baseurl + "/api/auth/checkUsernameAvailability"))
				.route("check_email_availability", r -> r
						.path("/api/auth/checkEmailAvailability")
						.uri(enable_user_service_baseurl + "/api/auth/checkEmailAvailability"))

				.route("get_user_profile_by_id", r -> r
						.path("/api/user/getProfileById")
						.uri(enable_user_service_baseurl + "/api/user/getProfileById"))
				.route("get_user_profile_by_jwt", r -> r
						.path("/api/user/getProfileByJwt")
						.uri(enable_user_service_baseurl + "/api/user/getProfileByJwt"))

				.route("get_execution", r -> r
						.path("/api/execution")
						.uri(enable_execution_service_baseurl + "/api/execution"))
				.route("get_executions_of_user", r -> r
						.path("/api/execution/user")
						.uri(enable_execution_service_baseurl + "/api/execution"))
				.route("execute_from_processfile", r -> r
						.path("/api/execution/execute/new/processfile")
						.uri(enable_execution_service_baseurl + "/api/execution/execute/new/processfile"))
				.route("terminate_execution", r -> r
						.path("/api/execution/terminate")
						.uri(enable_execution_service_baseurl + "/api/execution/terminate"))
				.route("delete_execution", r -> r
						.path("/api/execution/delete")
						.uri(enable_execution_service_baseurl + "/api/execution/delete"))

				.route("get_tasks_of_execution", r -> r
						.path("/api/task/execution")
						.uri(enable_execution_service_baseurl + "/api/task/execution"))
				.route("get_tasks", r -> r
						.path("/api/task")
						.uri(enable_execution_service_baseurl + "/api/task"))
				.route("get_task", r -> r
						.path("/api/task/id")
						.uri(enable_execution_service_baseurl + "/api/task/id"))
				.route("complete_task", r -> r
						.path("/api/task/complete")
						.uri(enable_execution_service_baseurl + "/api/task/complete"))
				.route("interrupt_task", r -> r
						.path("/api/task/interrupt")
						.uri(enable_execution_service_baseurl + "/api/task/interrupt"))
				.route("claim_task", r -> r
						.path("/api/task/claim")
						.uri(enable_execution_service_baseurl + "/api/task/claim"))

				.build();
	}
}
