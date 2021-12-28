package com.epam.clientinterface.util;

import com.epam.clientinterface.domain.UserDetailAuth;
import com.epam.clientinterface.domain.dto.UserDto;
import com.epam.clientinterface.entity.Role;
import com.epam.clientinterface.entity.User;
import java.util.HashSet;
import java.util.List;

public class UserTestData {

    public static UserDetailAuth user = new UserDetailAuth(new User(1L, "User", "Userovich",
        "+8979878512121", "aa@email.com",
        "$2a$10$g2SYZuOzPlc5l9a9FPF7ReW09tmapH2VI86W/uv2V/eICElXqxm6u", true, 0,
        new HashSet<>(List.of(new Role(1L, "USER")))));

    public static UserDetailAuth userToBlock =
        new UserDetailAuth(new User(1L, "User", "Userovich", "+8979878512121",
            "aa@email.com",
            "$2a$10$g2SYZuOzPlc5l9a9FPF7ReW09tmapH2VI86W/uv2V/eICElXqxm6u", true, 0,
            new HashSet<>(List.of(new Role(1L, "USER")))));

    public static UserDetailAuth admin = new UserDetailAuth(new User(2L, "Admin", "Adminov",
        "+89798785145", "bb@email.com",
        "$2a$11$2DRQxMFrmSVUJm3DJMvv0uw6icPLOJnqFQ4t.r64pW.oHCqlAKJJe", true, 0,
        new HashSet<>(List.of(new Role(2L, "ADMIN")))));

    public static UserDto getUserView(UserDetailAuth user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName(), user.getSurname(), user.getAuthorities());
    }

}
