package com.epam.bank.operatorinterface.service;

import com.epam.bank.operatorinterface.domain.UserDetailsAuthImpl;
import com.epam.bank.operatorinterface.repository.UserRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        UserDetailsAuthImpl userDetails = userRepository.getUserByEmail(userEmail)
            .map(u -> new UserDetailsAuthImpl(
                u.getPassword(),
                u.getEmail(),
                u.getRoles(),
                u.isEnabled()))
            .orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with Email %s not found", userEmail)));
        return userDetails;
    }
}
