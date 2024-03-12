//package enable.enableapigateway.Configuration.Security;
//
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//    @Bean
//    public RouteLocatorBuilder routeLocatorBuilder(ConfigurableApplicationContext context) {
//        return new RouteLocatorBuilder(context);
//    }
//
//    @Bean
//    protected SecurityFilterChain filterChain(HttpSecurity http, TokenAuthenticationFilter tokenAuthenticationFilter) throws Exception {
//        return http
//            .cors()
//                .disable()
//            .addFilterBefore(tokenAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//            .authorizeExchange()
//                .pathMatchers("/api/auth/**")
//                    .permitAll()
//                .anyExchange()
//                    .authenticated()
//                .and()
//            .csrf()
//                .disable()
//            .build();
//    }
//}
//
//
