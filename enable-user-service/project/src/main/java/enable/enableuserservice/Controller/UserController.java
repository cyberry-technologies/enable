package enable.enableuserservice.Controller;

import enable.enableuserservice.Configuration.Security.JwtTokenProvider;
import enable.enableuserservice.Dto.ProfileRequest;
import enable.enableuserservice.Dto.UserProfileDto;
import enable.enableuserservice.Service_Interface.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final JwtTokenProvider tokenProvider;

    private final IUserService userService;

    public UserController(IUserService userService, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/getProfileById")
    public ResponseEntity<?> getProfileById(@RequestBody ProfileRequest profileRequest) {
        if (profileRequest.getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        UserProfileDto userProfile = userService.getUserProfileById(profileRequest.getId());

        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/getProfileByJwt")
    public ResponseEntity<?> getProfileByJwt(@RequestBody ProfileRequest profileRequest) {
        if (profileRequest.getJwt() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (StringUtils.hasText(profileRequest.getJwt()) && tokenProvider.validateToken(profileRequest.getJwt())) {
            String email = tokenProvider.getEmailFromJWT(profileRequest.getJwt());

            UserProfileDto userProfile = userService.getUserProfileByEmail(email);

            if(userProfile == null) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(userProfile);
        }
        return ResponseEntity.notFound().build();
    }
}

