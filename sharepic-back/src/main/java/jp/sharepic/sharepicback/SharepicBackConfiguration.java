package jp.sharepic.sharepicback;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jp.sharepic.sharepicback.domains.user.UserService;

@Configuration
public class SharepicBackConfiguration {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean(name = "passwordUsernameUserDetails")
    public UserDetailsService userService() {
        return new UserService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
