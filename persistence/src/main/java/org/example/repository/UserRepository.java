package org.example.repository;

import org.example.model.User;
import org.example.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    List<User> findUserByRole(Role role);

    Optional<User> findFirstByRoleInAndIdNot(Collection<Role> roles, int userId);

    long countByRole(Role role);

    long countByIsBlockedTrue();
}
