package pl.dkaluza.userservice.adapters.in.web.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.dkaluza.userservice.domain.User;

import java.util.Collection;
import java.util.Collections;

class DefaultUserDetails implements UserDetails {
    private final Long id;
    private final String emailAddress;
    private final String encodedPassword;
    private final String name;

    private DefaultUserDetails(Long id, String emailAddress, String encodedPassword, String name) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.encodedPassword = encodedPassword;
        this.name = name;
    }

    public static DefaultUserDetails of(User user) {
        return new DefaultUserDetails(
            user.getId().getId(),
            user.getEmail().getValue(),
            new String(user.getPassword().getValue()),
            user.getName().getValue()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return encodedPassword;
    }

    @Override
    public String getUsername() {
        return emailAddress;
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
