package enable.enableapigateway.Configuration.Security;

import enable.enableapigateway.Dto.ProfileRequestDto;
import enable.enableapigateway.Dto.UserProfileDto;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Component
public class UserRequestService {
    private final String enable_user_service_baseurl;
    private final RestTemplate restTemplate;

    public UserRequestService(Environment environment) {
        this.enable_user_service_baseurl = environment.getProperty("ENABLE_USER_SERVICE_BASEURL");
        this.restTemplate = new RestTemplate();
    }

    public UserProfileDto authenticate(String token) {
        // Construct the URL for the getProfileByJwt endpoint
        String url = enable_user_service_baseurl + "/api/user/getProfileByJwt";

        // Create the request body if needed
        ProfileRequestDto requestBody = new ProfileRequestDto(null, token);

        // Send the HTTP request and parse the response
        return restTemplate.postForObject(url, requestBody, UserProfileDto.class);
    }
}
