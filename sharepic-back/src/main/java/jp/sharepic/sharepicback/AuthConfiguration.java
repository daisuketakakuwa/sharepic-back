package jp.sharepic.sharepicback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jp.sharepic.sharepicback.domains.jwt.JwtTokenFilter;

@EnableWebSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${domain}")
    String domain;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    // @Qualifier("inMemoryUserDetailsManager")
    @Qualifier("passwordUsernameUserDetails")
    UserDetailsService userDetailsService;

    @Autowired
    JwtTokenFilter jwtTokenFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable().csrf().disable().cors().configurationSource(corsConfigurationSource()).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests();

        // ログイン認証のリクエスト
        http.authorizeRequests().antMatchers("/auth/login").permitAll();
        // 新規登録のリクエスト
        http.authorizeRequests().antMatchers("/user/register").permitAll();
        // その他のリクエストは認証が必要
        http.authorizeRequests().anyRequest().authenticated();

        // JWTのフィルターをUsernamePasswordFilterの前に追加する
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    // authenticationManagerBean()によって、AuthentificationManagerがBeanとして生成可能になる
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }

    @Bean
    @Override
    // このメソッドauthenticationManagerBean()の役割は
    // configure(AuthenticationManagerBuilder auth)で設定されたAuthenticationMagegerを
    // 外部から参照できるようにBeanにすること
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addAllowedMethod(CorsConfiguration.ALL);
        config.addAllowedOrigin("http://" + domain);
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
