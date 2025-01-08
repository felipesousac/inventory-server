package com.inventory.server.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndEnabledTrue(String username);

    Optional<User> findByIdAndEnabledTrue(Long id);

    Boolean existsByUsernameAndEnabledTrue(String username);
}
