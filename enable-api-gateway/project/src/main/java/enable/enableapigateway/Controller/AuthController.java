package enable.enableapigateway.Controller;

import enable.enableapigateway.Configuration.Security.JwtTokenProvider;
import enable.enableapigateway.Dto.JwtAuthenticationResponse;
import enable.enableapigateway.Dto.SignInRequest;
import enable.enableapigateway.Dto.SignUpRequest;
import enable.enableapigateway.Model.User;
import enable.enableapigateway.Service_Interface.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IUserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(IUserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userService.isUsernameTaken(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already in use!");
        }

        if(userService.isEmailTaken(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Username is already in use!");
        }

        User result = userService.signUp(signUpRequest);

        System.out.println(result.getId());

        return ResponseEntity.created(URI.create("/user/get/id/?id=" + result.getId()))
                .body(result);
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {

        User user = userService.signin(signInRequest);

        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        String jwt = jwtTokenProvider.generateToken(user.getEmail());

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @GetMapping("/checkUsernameAvailability")
    public ResponseEntity<?> checkUsernameAvailability(@RequestParam(value = "username") String username) {
        return ResponseEntity.ok(!userService.isUsernameTaken(username));
    }

    @GetMapping("/checkEmailAvailability")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam(value = "email") String email) {
        return ResponseEntity.ok(!userService.isEmailTaken(email));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
