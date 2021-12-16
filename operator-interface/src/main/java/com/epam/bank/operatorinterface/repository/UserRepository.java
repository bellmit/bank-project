package com.epam.bank.operatorinterface.repository;

import com.epam.bank.operatorinterface.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    User save(User user);

    Optional<User> getUserByName(String name);

    @EntityGraph(attributePaths = {"roles"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> getUserByEmail(String email);
}