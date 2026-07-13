package com.lionking.ddingchun.user.repository;

import com.lionking.ddingchun.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
