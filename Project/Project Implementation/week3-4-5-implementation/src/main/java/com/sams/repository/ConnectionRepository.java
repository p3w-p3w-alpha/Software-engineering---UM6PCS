package com.sams.repository;

import com.sams.entity.Connection;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // find connection between two users (in either direction)
    @Query("SELECT c FROM Connection c WHERE " +
           "(c.requester.id = :user1Id AND c.receiver.id = :user2Id) OR " +
           "(c.requester.id = :user2Id AND c.receiver.id = :user1Id)")
    Optional<Connection> findConnectionBetweenUsers(@Param("user1Id") Long user1Id,
                                                    @Param("user2Id") Long user2Id);

    // chekc if connection exists between two users
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Connection c WHERE " +
           "((c.requester.id = :user1Id AND c.receiver.id = :user2Id) OR " +
           "(c.requester.id = :user2Id AND c.receiver.id = :user1Id))")
    boolean existsBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    // chekc if one user has blocked another
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Connection c WHERE " +
           "c.status = 'BLOCKED' AND " +
           "((c.requester.id = :user1Id AND c.receiver.id = :user2Id AND c.blockedBy.id = :user1Id) OR " +
           "(c.requester.id = :user2Id AND c.receiver.id = :user1Id AND c.blockedBy.id = :user1Id))")
    boolean isBlocked(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    // find all accepted connections for a user
    @Query("SELECT c FROM Connection c WHERE " +
           "c.status = 'ACCEPTED' AND " +
           "(c.requester.id = :userId OR c.receiver.id = :userId) " +
           "ORDER BY c.acceptedAt DESC")
    List<Connection> findAcceptedConnections(@Param("userId") Long userId);

    // find all pending connection requests sent by a user
    @Query("SELECT c FROM Connection c WHERE " +
           "c.status = 'PENDING' AND c.requester.id = :userId " +
           "ORDER BY c.createdAt DESC")
    List<Connection> findPendingRequestsSent(@Param("userId") Long userId);

    // find all pending connection requests received by a user
    @Query("SELECT c FROM Connection c WHERE " +
           "c.status = 'PENDING' AND c.receiver.id = :userId " +
           "ORDER BY c.createdAt DESC")
    List<Connection> findPendingRequestsReceived(@Param("userId") Long userId);

    // find all blocked users for a user
    @Query("SELECT c FROM Connection c WHERE " +
           "c.status = 'BLOCKED' AND " +
           "((c.requester.id = :userId AND c.blockedBy.id = :userId) OR " +
           "(c.receiver.id = :userId AND c.blockedBy.id = :userId)) " +
           "ORDER BY c.blockedAt DESC")
    List<Connection> findBlockedUsers(@Param("userId") Long userId);

    // find all connections by status for a user
    @Query("SELECT c FROM Connection c WHERE " +
           "c.status = :status AND " +
           "(c.requester.id = :userId OR c.receiver.id = :userId) " +
           "ORDER BY c.createdAt DESC")
    List<Connection> findConnectionsByStatus(@Param("userId") Long userId, @Param("status") String status);

    // count accepted connections for a user
    @Query("SELECT COUNT(c) FROM Connection c WHERE " +
           "c.status = 'ACCEPTED' AND " +
           "(c.requester.id = :userId OR c.receiver.id = :userId)")
    long countAcceptedConnections(@Param("userId") Long userId);

    // count pending requests received by a user
    @Query("SELECT COUNT(c) FROM Connection c WHERE " +
           "c.status = 'PENDING' AND c.receiver.id = :userId")
    long countPendingRequestsReceived(@Param("userId") Long userId);

    // find rejected connections for a user
    @Query("SELECT c FROM Connection c WHERE " +
           "c.status = 'REJECTED' AND " +
           "(c.requester.id = :userId OR c.receiver.id = :userId) " +
           "ORDER BY c.rejectedAt DESC")
    List<Connection> findRejectedConnections(@Param("userId") Long userId);
}
