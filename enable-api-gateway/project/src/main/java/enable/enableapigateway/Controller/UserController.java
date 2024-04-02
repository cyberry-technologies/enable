package enable.enableapigateway.Controller;

import enable.enableapigateway.Configuration.Security.JwtTokenProvider;
import enable.enableapigateway.Dto.ProfileRequest;
import enable.enableapigateway.Dto.UserProfileDto;
import enable.enableapigateway.Service_Interface.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/profile/get/id")
    public ResponseEntity<?> getProfileById(@RequestBody ProfileRequest profileRequest) {
        if (profileRequest.getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        UserProfileDto userProfile = userService.getUserProfileById(profileRequest.getId());

        return ResponseEntity.ok(userProfile);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

