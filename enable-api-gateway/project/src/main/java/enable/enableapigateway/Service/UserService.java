package enable.enableapigateway.Service;

import enable.enableapigateway.Dto.SignInRequest;
import enable.enableapigateway.Dto.SignUpRequest;
import enable.enableapigateway.Dto.UserProfileDto;
import enable.enableapigateway.Model.User;
import enable.enableapigateway.Repository.UserRepository;
import enable.enableapigateway.Service_Interface.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User signUp(SignUpRequest signUpRequest) {
        if (signUpRequest == null) {
            throw new IllegalArgumentException();
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User signin(SignInRequest signInRequest) {
        if (signInRequest == null) {
            throw new IllegalArgumentException();
        }

        User user = userRepository.findByEmail(signInRequest.getEmail()).orElse(null);

        if (user != null && passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            return user;
        }

        return null;
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserProfileDto getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User could not be found"));

        return new UserProfileDto(user.getId(), user.getUsername(), user.getEmail());
    }

    @Override
    public UserProfileDto getUserProfileById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User could not be found"));

        return new UserProfileDto(user.getId(), user.getUsername(), user.getEmail());
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User could not be found"));
    }
}

