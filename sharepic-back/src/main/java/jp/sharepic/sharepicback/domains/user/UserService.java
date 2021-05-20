package jp.sharepic.sharepicback.domains.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jp.sharepic.sharepicback.domains.auth.AuthService;

@Service
public class UserService implements UserDetailsService {

    private static final String DUPLICATE_USERNAME = "duplicate_username";

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    // このメソッドでは「検索条件：USERNAME」に該当するやつがあるかどうかだけチェックする。
    // パスワードチェックは、こいつを呼び出している「AuthentificationManager.authenticate()」
    // で行われる。
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // UserEntity取得
        // orElseThrow()で「値あり→value返す 値なし→例外返す」の分岐をしてくれている
        // →orElseThrow()はOptionalだから使える。
        UserEntity entity = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("login Failed"));

        // UserEntity → UserInfo(extends User)作成
        return new UserInfo(entity.getId(), entity.getName(), entity.getPassword(), entity.getRoles().split(","));
    }

    public String register(String username, String password) {
        // 既に登録されているusernameかどうか確認
        if (userRepository.findByName(username).isPresent()) {
            return DUPLICATE_USERNAME;
        }
        // IDをUUID()で生成
        String id = UUID.randomUUID().toString();
        // PASSWORDをBcryptで暗号化
        String encodedPassword = encoder.encode(password);
        // 登録
        userRepository.save(new UserEntity(id, username, encodedPassword, "ROLE_USER"));
        // トークン生成
        return authService.authenticate(username, password);
    }

}
