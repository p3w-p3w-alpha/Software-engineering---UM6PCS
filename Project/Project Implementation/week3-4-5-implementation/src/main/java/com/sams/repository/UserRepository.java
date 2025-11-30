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

    // Dashboard statistics
    long countByRole(String role);

    // Count all active users (for demographics without loading all)
    long countByActiveTrue();

    long countByRoleAndActive(String role, boolean active);

    // Gender demographics
    long countByGenderAndActiveTrue(String gender);

    // Count users created in date range (for growth calculations)
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.createdAt >= :startDate AND u.createdAt < :endDate")
    long countByRoleAndCreatedAtBetween(@Param("role") String role, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    // Get recent users with pagination (for recent activities fallback - avoids N+1)
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(org.springframework.data.domain.Pageable pageable);
}
