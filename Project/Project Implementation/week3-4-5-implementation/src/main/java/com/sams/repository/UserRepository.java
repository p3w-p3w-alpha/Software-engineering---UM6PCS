package com.sams.repository;

import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // get all users by role
    List<User> findByRole(String role);

    // find user by email and role
    Optional<User> findByEmailAndRole(String email, String role);

    // check if username exists
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // soft delete support - find only active records
    List<User> findByActiveTrue();

    Optional<User> findByIdAndActiveTrue(Long id);

    Optional<User> findByEmailAndActiveTrue(String email);

    List<User> findByRoleAndActiveTrue(String role);

    boolean existsByEmailAndActiveTrue(String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.active = true AND u.id != :excludeId")
    boolean existsByEmailAndActiveTrueExcludingId(@Param("email") String email, @Param("excludeId") Long excludeId);
}
