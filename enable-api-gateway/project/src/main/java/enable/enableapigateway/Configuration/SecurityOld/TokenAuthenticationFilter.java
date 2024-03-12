//package enable.enableapigateway.Configuration.Security;
//
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextImpl;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.server.context.ServerSecurityContextRepository;
//import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.GenericFilterBean;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import java.io.IOException;
//
//
//@Component
//public class TokenAuthenticationFilter extends GenericFilterBean {
//    private final ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();
//
//	private final Environment environment;
//
//    public TokenAuthenticationFilter(Environment environment) {
//        this.environment = environment;
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
//        // Extract the token from the request
//        String token = extractToken(servletRequest);
//
//        if (token != null) {
//            Mono<UserProfileDto> userProfileMono = validateToken(token);
//            return userProfileMono.flatMap(userProfile -> {
//                UserDetails userDetails = UserPrincipal.create(userProfile);
//
//                // Create authentication token with user profile
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                // Set the authentication in the security context
//                SecurityContext securityContext = new SecurityContextImpl(authentication);
//                try {
//                    return securityContextRepository.save(servletRequest, servletResponse, securityContext)
//                            .then(chain.doFilter(servletRequest, servletResponse));
//                } catch (IOException | ServletException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//
//        chain.doFilter(servletRequest, servletResponse);
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        // Extract the token from the request
//        String token = extractToken(exchange.getRequest());
//
//        if (token != null) {
//            Mono<UserProfileDto> userProfileMono = validateToken(token);
//            return userProfileMono.flatMap(userProfile -> {
//                UserDetails userDetails = UserPrincipal.create(userProfile);
//
//                // Create authentication token with user profile
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                // Set the authentication in the security context
//                SecurityContext securityContext = new SecurityContextImpl(authentication);
//                return securityContextRepository.save(exchange, securityContext)
//                        .then(chain.filter(exchange));
//            }).switchIfEmpty(chain.filter(exchange));
//        }
//
//        return chain.filter(exchange);
//    }
//
//    private String extractToken(ServerHttpRequest request) {
//        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            return authorizationHeader.substring(7);
//        }
//        return null;
//    }
//
//
//    private Mono<UserProfileDto> validateToken(String token) {
//		String userService_baseUrl = environment.getProperty("CYBERRY_AUTH_USER_SERVICE_BASEURL");
//
//        WebClient webClient = WebClient.create();
//
//        // Make a request to the User Service endpoint with the token
//        String url = userService_baseUrl + "/api/user/getProfileByJwt";
//        return webClient.post()
//                .uri(url)
//                .bodyValue(new ProfileRequest(null, token))
//                .retrieve()
//                .toEntity(UserProfileDto.class)
//                .mapNotNull(responseEntity -> {
//                    // Process the response and determine the validity of the token
//                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
//                        // Token is considered valid
//                        return responseEntity.getBody();
//                    }
//
//                    // Token is considered invalid
//                    return null;
//                });
//    }
//}
//
//
