package jp.sharepic.sharepicback.domains.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class JwtTokenFilter extends GenericFilterBean {

    @Autowired
    TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        // リクエストからJWT(文字列)取得
        HttpServletRequest httpServletRequest = (HttpServletRequest) req;
        String token = httpServletRequest.getHeader("Authorization");

        if (token != null && tokenProvider.validateToken(token)) {
            // JWT(文字列) → Authenticationオブジェクト
            Authentication auth = tokenProvider.buildAuthentication(token);
            // SecurityContextHolderに保管
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 後続のフィルターにバトンタッチ
        filterChain.doFilter(req, res);
    }

}
