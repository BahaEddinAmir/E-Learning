package com.arabsoft.elearning.repository;

import com.arabsoft.elearning.entity.User;
import com.arabsoft.elearning.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByIsActiveFalse();
    List<User> findTop10ByOrderByCreatedAtDesc();
}