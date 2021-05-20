package jp.sharepic.sharepicback.domains.jwt;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jp.sharepic.sharepicback.domains.user.UserInfo;

@Component
public class JwtTokenProvider implements TokenProvider {

    private Key secretkey;

    @PostConstruct
    protected void init() {
        secretkey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // JWT(文字列) → Authenticationオブジェクトに変換
    // (UserDetails principal, "", Collection<? extends GrantedAuthority> roles)
    @Override
    public Authentication buildAuthentication(String token) {
        // PrincipalとRolesが欲しい。
        Jws<Claims> claims = parseClaimsJws(token);
        UserDetails userDetails = new UserInfo("id", claims.getBody().get("name").toString(), "password",
                claims.getBody().get("role").toString().split(","));

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : claims.getBody().get("role").toString().split(",")) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    // Authenticationオブジェクト → JWT(文字列)に変換
    @Override
    public String createToken(Authentication auth) {
        List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::toString)
                .collect(Collectors.toList());
        Map<String, String> userDetailMap = createUserDetailsMap(auth);

        Claims claims = Jwts.claims();
        claims.put("role", roles);
        userDetailMap.entrySet().stream().forEach(entry -> claims.put(entry.getKey(), entry.getValue()));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validity = now.plusMinutes(60);

        return Jwts.builder().setClaims(claims).setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(validity.atZone(ZoneId.systemDefault()).toInstant())).signWith(secretkey)
                .compact();

    }

    private Map<String, String> createUserDetailsMap(Authentication auth) {
        UserDetails userDetail = (UserInfo) auth.getPrincipal();
        Map<String, String> map = new HashMap<>();
        map.put("name", userDetail.getUsername());
        return map;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // JWT(文字列)よりClaims(BODY部分)を取得
    private Jws<Claims> parseClaimsJws(String token) {
        return Jwts.parserBuilder().setSigningKey(secretkey).build().parseClaimsJws(token);
    }

}
