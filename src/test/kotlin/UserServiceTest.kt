import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import com.itmo.microservices.demo.users.impl.service.DefaultUserService
import org.junit.jupiter.api.*
import org.springframework.security.core.userdetails.UserDetails

class UserServiceTest {
    lateinit var userService: DefaultUserService
    lateinit var registrationRequest: RegistrationRequest
    lateinit var userDetails: UserDetails
    lateinit var appUserModel: AppUserModel

    @BeforeEach
    fun setUp() {
        registrationRequest = RegistrationRequest(username = "username",
            name = "name",
            surname = "surname",
            email = "email@example.com",
            password = "encodedpassword"
        );
        appUserModel = AppUserModel(username = "username",
            name = "name",
            surname = "surname",
            email = "email@example.com",
            password = "encodedpassword"
        );
    }

    @Test
    fun registerUser() {
        userService.registerUser(registrationRequest);
        assert(userService.findUser("username") == appUserModel);
    }

    @Test
    fun getAccountData() {
        userService.registerUser(registrationRequest);
        assert(userService.getAccountData(userDetails) == appUserModel)
    }
}