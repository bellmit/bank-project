package com.epam.bank.operatorinterface.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public class RolesGrantedAuthorities implements GrantedAuthority {

    private final String authority;

}