package jp.sharepic.sharepicback.domains.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserInfo extends User {

    private String name;

    // Userが持つコンストラクタ
    // (usrename, password, Collection<? extends GrantedAuthority>)
    // GrantedAuthorityインタフェースの実装クラス＝1つの権限であり、
    // Authenticationがこれらの実装クラス(権限)をリストで持っている。
    // DBの「roles」には「USER,ADMIN」のように「カンマ区切りの文字列」で格納するが、
    // DBから取り出した時には「カンマ区切りの文字列→List<GrantedAuthorities>」に変換する。
    public UserInfo(String id, String name, String password, String[] roles) {
        super(id, password, generateAuthorities(roles));
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static List<GrantedAuthority> generateAuthorities(String[] source) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : source) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

}
