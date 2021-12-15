package com.epam.bank.operatorinterface.domain;

import com.epam.bank.operatorinterface.entity.Role;
import org.springframework.security.core.GrantedAuthority;

public class RolesGrantedAuthorities extends Role implements GrantedAuthority {

    public RolesGrantedAuthorities(Long id, String authorities) {
        super(id, authorities);
    }

    @Override
    public String getAuthority() {
        return super.getRole();
    }
}