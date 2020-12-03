package tech.czatmat.app.CzatMatApp.users;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tech.czatmat.app.CzatMatApp.users.authorities.Authority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImplementation implements UserDetails {
    private final Collection<? extends GrantedAuthority> authorities;
    private final int id;
    private final String username;
    private final String password;
    private final String surname;
    private final String email;
    private final boolean enabled;
    public UserDetailsImplementation(int id, String username, String password, String surname, String email,
                                     boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.surname = surname;
        this.email = email;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static UserDetailsImplementation build(User user, List<Authority> authority) {
        List<GrantedAuthority> authorities = authority.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());

        return new UserDetailsImplementation(user.getID(), user.getUsername(), user.getPassword(), user.getSurname(), user.getEmail(), user.isEnabled(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
