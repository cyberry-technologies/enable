package enable.enableapigateway.Service_Interface;

import enable.enableapigateway.Dto.SignInRequest;
import enable.enableapigateway.Dto.SignUpRequest;
import enable.enableapigateway.Dto.UserProfileDto;
import enable.enableapigateway.Model.User;

public interface IUserService {
    User signUp(SignUpRequest signUpRequest);

    User signin(SignInRequest signInRequest);

    UserProfileDto getUserProfileByEmail(String email);
    UserProfileDto getUserProfileById(Long id);

    boolean isUsernameTaken(String username);

    boolean isEmailTaken(String email);

    User getUserById(Long id);
}
