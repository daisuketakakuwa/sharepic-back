package jp.sharepic.sharepicback.domains.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.sharepic.sharepicback.domains.auth.request.RegisterRequest;
import jp.sharepic.sharepicback.domains.auth.response.RegisterResponse;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final String DUPLICATE_USERNAME = "duplicate_username";

    @Autowired
    UserService userService;

    @PutMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest req) {
        String token = userService.register(req.getName(), req.getPassword());
        if (token.isEmpty()) {
            return RegisterResponse.builder().success(false).build();
        } else if (token.equals(DUPLICATE_USERNAME)) {
            return RegisterResponse.builder().success(false).username("DUPLICATE_USERNAME").build();
        } else {
            return RegisterResponse.builder().success(true).username(req.getName()).token(token).build();
        }
    }

}
