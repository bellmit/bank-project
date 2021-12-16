package com.epam.bank.operatorinterface.domain;

import com.epam.bank.operatorinterface.entity.User;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
public class UserDetailsAuthImpl extends User implements UserDetails {

    public UserDetailsAuthImpl(User user) {
        super(
            user.getId(),
            user.getName(),
            user.getSurname(),
            user.getPhoneNumber(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getAccounts(),
            user.isEnabled(),
            user.getFailedLoginAttempts(),
            user.getRoles());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream()
            .map(role -> new RolesGrantedAuthorities(role.getId(), role.getRole()))
            .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return super.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return super.isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return super.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return super.isEnabled();
    }
}
