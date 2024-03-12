package enable.enableuserservice;

import enable.enableuserservice.Dto.SignUpRequest;
import enable.enableuserservice.Model.User;
import enable.enableuserservice.Repository.UserRepository;
import enable.enableuserservice.Service.UserService;

import java.util.Optional;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SpringBootTest
public class UserServiceUnitTests {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_ValidSignUpRequest_ReturnsUser() {
        // Arrange
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("john");
        signUpRequest.setEmail("john@example.com");
        signUpRequest.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // Act
        User result = userService.registerUser(signUpRequest);

        // Assert
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPassword(), result.getPassword());
    }

    @Test
    public void testRegisterUser_NullSignUpRequest_ThrowsIllegalArgumentException() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(null));
    }

    @Test
    public void testIsUsernameTaken_ExistingUsername_ReturnsTrue() {
        // Arrange
        String existingUsername = "john";
        Mockito.when(userRepository.existsByUsername(existingUsername)).thenReturn(true);

        // Act
        boolean result = userService.isUsernameTaken(existingUsername);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsUsernameTaken_NonExistingUsername_ReturnsFalse() {
        // Arrange
        String nonExistingUsername = "doe";
        Mockito.when(userRepository.existsByUsername(nonExistingUsername)).thenReturn(false);

        // Act
        boolean result = userService.isUsernameTaken(nonExistingUsername);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testIsEmailTaken_ExistingEmail_ReturnsTrue() {
        // Arrange
        String existingEmail = "john@example.com";
        Mockito.when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

        // Act
        boolean result = userService.isEmailTaken(existingEmail);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsEmailTaken_NonExistingEmail_ReturnsFalse() {
        // Arrange
        String nonExistingEmail = "jane@example.com";
        Mockito.when(userRepository.existsByEmail(nonExistingEmail)).thenReturn(false);

        // Act
        boolean result = userService.isEmailTaken(nonExistingEmail);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testGetUserById_ExistingId_ReturnsUser() {
        // Arrange
        Long existingId = 1L;
        User user = new User();
        user.setId(existingId);
        Mockito.when(userRepository.findById(existingId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(existingId);

        // Assert
        assertEquals(existingId, result.getId());
    }

    @Test
    public void testGetUserById_NonExistingId_ThrowsResourceNotFoundException() {
        // Arrange
        Long nonExistingId = 2L;
        Mockito.when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(nonExistingId));

    }
}


