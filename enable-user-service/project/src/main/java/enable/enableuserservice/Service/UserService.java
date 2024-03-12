package enable.enableuserservice.Service;

import enable.enableuserservice.Dto.SignInRequest;
import enable.enableuserservice.Dto.SignUpRequest;
import enable.enableuserservice.Dto.UserProfileDto;
import enable.enableuserservice.Model.User;
import enable.enableuserservice.Repository.UserRepository;
import enable.enableuserservice.Service_Interface.IUserService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
    public User registerUser(SignUpRequest signUpRequest) {
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
    public User loginUser(SignInRequest signInRequest) {
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
                .orElseThrow(() -> new ResourceNotFoundException("User could not be found"));

        return new UserProfileDto(user.getId(), user.getUsername(), user.getEmail());
    }

    @Override
    public UserProfileDto getUserProfileById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User could not be found"));

        return new UserProfileDto(user.getId(), user.getUsername(), user.getEmail());
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User could not be found"));
    }
}

