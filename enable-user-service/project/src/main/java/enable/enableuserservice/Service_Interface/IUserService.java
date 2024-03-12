package enable.enableuserservice.Service_Interface;

import enable.enableuserservice.Dto.SignInRequest;
import enable.enableuserservice.Dto.SignUpRequest;
import enable.enableuserservice.Dto.UserProfileDto;
import enable.enableuserservice.Model.User;

public interface IUserService {
    User registerUser(SignUpRequest signUpRequest);

    User loginUser(SignInRequest signInRequest);

    UserProfileDto getUserProfileByEmail(String email);
    UserProfileDto getUserProfileById(Long id);

    boolean isUsernameTaken(String username);

    boolean isEmailTaken(String email);

    User getUserById(Long id);
}
