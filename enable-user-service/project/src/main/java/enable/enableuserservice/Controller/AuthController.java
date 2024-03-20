package enable.enableuserservice.Controller;

import enable.enableuserservice.Dto.JwtAuthenticationResponse;
import enable.enableuserservice.Configuration.Security.JwtTokenProvider;
import enable.enableuserservice.Dto.SignInRequest;
import enable.enableuserservice.Dto.SignUpRequest;
import enable.enableuserservice.Model.User;
import enable.enableuserservice.Service_Interface.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;

@RestController
@RequestMapping("/api/auth")
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

        User result = userService.registerUser(signUpRequest);

        System.out.println(result.getId());

        return ResponseEntity.created(URI.create("/user/get/id/?id=" + result.getId()))
                .body(result);
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {

        User user = userService.loginUser(signInRequest);

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
