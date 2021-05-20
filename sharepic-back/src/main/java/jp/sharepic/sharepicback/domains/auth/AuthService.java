package jp.sharepic.sharepicback.domains.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jp.sharepic.sharepicback.domains.jwt.TokenProvider;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager manager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    TokenProvider tokenProvider;

    public String authenticate(String username, String password) {
        // authenticate()で実行される処理は以下の２つ
        // 01. UserDetailServiceにより、検索条件「username」で該当ユーザ情報(UserDetails)取得。
        // 02. 01で取得したUserDetailsのパスワードと引数のパスワードの認証処理。
        // ※ここで指定されているPasswordEncoderが利用される。なので、↓引数のpasswordは平文でOK。

        // 認証処理にミスると例外発生して、そのまま無視すると「403」で報告される。
        // 適切にハンドリングする必要がある。
        try {
            Authentication authentication = manager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            // Authentication → JWTトークン(文字列)
            return tokenProvider.createToken(authentication);
        } catch (Exception e) {
            return "";
        }

    }

}
