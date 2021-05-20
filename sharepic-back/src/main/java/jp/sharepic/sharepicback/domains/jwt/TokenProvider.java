package jp.sharepic.sharepicback.domains.jwt;

import org.springframework.security.core.Authentication;

public interface TokenProvider {

    public Authentication buildAuthentication(String token);

    public String createToken(Authentication auth);

    public boolean validateToken(String token);

}
