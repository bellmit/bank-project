package com.epam.clientinterface.service.impl;

import com.epam.clientinterface.domain.exception.UserAlreadyExistException;
import com.epam.clientinterface.entity.User;
import com.epam.clientinterface.repository.UserRepository;
import com.epam.clientinterface.service.AuthService;
import com.epam.clientinterface.service.UserService;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User signUp(String name, String surname, String phoneNumber,
                       String username, String email, String rawPassword)
        throws UserAlreadyExistException {
        if (isEmailExist(email)) {
            throw new UserAlreadyExistException(email);
        }
        User newUser = userService.create(name, surname, phoneNumber, username, email, rawPassword);

        return newUser;
    }

    private boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }
}
