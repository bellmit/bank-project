package com.epam.bank.atm.controller.di;

import com.auth0.jwt.algorithms.Algorithm;
import com.epam.bank.atm.controller.session.TokenService;
import com.epam.bank.atm.controller.session.TokenSessionService;
import com.epam.bank.atm.domain.model.AuthDescriptor;
import com.epam.bank.atm.entity.Account;
import com.epam.bank.atm.entity.Card;
import com.epam.bank.atm.entity.User;
import com.epam.bank.atm.infrastructure.session.JWTTokenPolicy;
import com.epam.bank.atm.infrastructure.session.JWTTokenSessionService;
import com.epam.bank.atm.repository.AccountRepository;
import com.epam.bank.atm.repository.CardRepository;
import com.epam.bank.atm.repository.UserRepository;
import com.epam.bank.atm.service.AuthService;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class DIContainer {
    private static volatile DIContainer instance = instance();
    private final ConcurrentHashMap<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Supplier<?>> prototypes = new ConcurrentHashMap<>();

    public static DIContainer instance() {
        if (instance == null) {
            synchronized (DIContainer.class) {
                if (instance == null) {
                    instance = new DIContainer();
                    instance.init();
                }
            }
        }

        return instance;
    }

    private void init() {
        this.singletons.putIfAbsent(AuthService.class, this.createAuthService());
        this.prototypes.putIfAbsent(AuthService.class, this::createAuthService);
        this.singletons.putIfAbsent(TokenSessionService.class, this.createTokenSessionService());
        this.prototypes.putIfAbsent(TokenSessionService.class, this::createTokenSessionService);
        this.singletons.putIfAbsent(UserRepository.class, this.createUserRepository());
        this.prototypes.putIfAbsent(UserRepository.class, this::createUserRepository);
        this.singletons.putIfAbsent(AccountRepository.class, this.createAccountRepository());
        this.prototypes.putIfAbsent(AccountRepository.class, this::createAccountRepository);
        this.singletons.putIfAbsent(CardRepository.class, this.createCardRepository());
        this.prototypes.putIfAbsent(CardRepository.class, this::createCardRepository);
        this.singletons.putIfAbsent(TokenService.class, this.createTokenSessionService());
        this.prototypes.putIfAbsent(TokenService.class, this::createTokenSessionService);
    }

    public <U extends T, T> U getSingleton(Class<T> aClass) {
        return (U) this.singletons.computeIfAbsent(aClass, k -> {
            throw new RuntimeException("Service is not configured");
        });
    }

    private <U extends T, T> U getSingleton(Class<T> aClass, Supplier<U> supplier) {
        return (U) this.singletons.computeIfAbsent(aClass, k -> supplier.get());
    }

    public <U extends T, T> U getPrototype(Class<T> aClass) {
        return (U) this.prototypes.computeIfAbsent(aClass, k -> {
            throw new RuntimeException("Service is not configured");
        }).get();
    }

    private <U extends T, T> U getPrototype(Class<T> aClass, Supplier<U> supplier) {
        return (U) this.prototypes.computeIfAbsent(aClass, k -> supplier).get();
    }

    private AuthService createAuthService() {
        return new AuthService() {
            @Override
            public AuthDescriptor login(String cardNumber, String pin) {
                return new AuthDescriptor(
                                        new User(1L, "name", "surname",
                                        "phone number", "username",
                                                "email@mail.com", "password"),
                                        new Account(1L, 1L),
                                        new Card(1L, 123456, 1L, 1234));
            }
        };
    }

    private UserRepository createUserRepository() {
        return new UserRepository() {
            User user = new User(1L, "name", "surname",
                "phone number", "username",
                "email@mail.com", "password");
            @Override
            public User getById(long id) {
                return user;
            }

            @Override
            public void save(User user) {

            }

            @Override
            public List<User> getAll() {
                return Arrays.asList(user);
            }
        };
    }

    private AccountRepository createAccountRepository() {
        return new AccountRepository() {
            @Override
            public Account getById(long id) {
                return new Account(1L, 1L);
            }
        };
    }

    private CardRepository createCardRepository() {
        return new CardRepository() {
            @Override
            public Card getById(long id) {
                return new Card(1L, 123456, 1L, 1234);
            }

            @Override
            public Card getByNumber(String number) {
                return new Card(1L, 123456, 1L, 1234);
            }
        };
    }

    private TokenSessionService createTokenSessionService() {
        return new JWTTokenSessionService(
            new JWTTokenPolicy() {
                @Override
                public int getExpirationPeriod() {
                    return 86400;
                }

                @Override
                public Algorithm getAlgorithm() {
                    return Algorithm.HMAC512("secret");
                }
            },
            this.getSingleton(UserRepository.class, this::createUserRepository),
            this.getSingleton(AccountRepository.class, this::createAccountRepository),
            this.getSingleton(CardRepository.class, this::createCardRepository)
        );
    }
}
