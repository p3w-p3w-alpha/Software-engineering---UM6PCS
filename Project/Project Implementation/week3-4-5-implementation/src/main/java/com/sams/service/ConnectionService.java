package com.sams.service;

import com.sams.entity.Connection;
import com.sams.entity.User;
import com.sams.exception.ResourceNotFoundException;
import com.sams.repository.ConnectionRepository;
import com.sams.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// service for managing user connections (friendships/colleague relationships)
@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ConnectionService(ConnectionRepository connectionRepository,
                            UserRepository userRepository,
                            NotificationService notificationService) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // ========== CONNECTION REQUEST OPERATIONS ==========

    /**
     * Send a connection request
     */
    @Transactional
    public Connection sendConnectionRequest(Long requesterId, Long receiverId) {
        // validate users exist
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found with id: " + requesterId));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with id: " + receiverId));

        // cannot send request to yourself
        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send connection request to yourself");
        }

        // chekc if connection already exists
        if (connectionRepository.existsBetweenUsers(requesterId, receiverId)) {
            throw new IllegalStateException("Connection already exists between these users");
        }

        // chekc if either user has blocked the other
        if (connectionRepository.isBlocked(requesterId, receiverId) ||
            connectionRepository.isBlocked(receiverId, requesterId)) {
            throw new IllegalStateException("Cannot send connection request to blocked user");
        }

        // create connection request
        Connection connection = new Connection(requester, receiver);
        Connection saved = connectionRepository.save(connection);

        // notify receiver of connection request
        notificationService.notifyConnectionRequest(receiver, requester);

        return saved;
    }

    /**
     * Accept a connection request
     */
    @Transactional
    public Connection acceptConnectionRequest(Long connectionId, Long receiverId) {
        Connection connection = getConnectionById(connectionId);

        // chekc if user is the receiver
        if (!connection.getReceiver().getId().equals(receiverId)) {
            throw new IllegalStateException("Only the receiver can accept the connection request");
        }

        connection.accept();
        Connection accepted = connectionRepository.save(connection);

        // notify requester that connection was accepted
        notificationService.notifyConnectionAccepted(connection.getRequester(), connection.getReceiver());

        return accepted;
    }

    /**
     * Reject a connection request
     */
    @Transactional
    public Connection rejectConnectionRequest(Long connectionId, Long receiverId) {
        Connection connection = getConnectionById(connectionId);

        // chekc if user is the receiver
        if (!connection.getReceiver().getId().equals(receiverId)) {
            throw new IllegalStateException("Only the receiver can reject the connection request");
        }

        connection.reject();
        return connectionRepository.save(connection);

        // Note: typically we don't notify for rejections
    }

    /**
     * Cancel a connection request (by requester)
     */
    @Transactional
    public void cancelConnectionRequest(Long connectionId, Long requesterId) {
        Connection connection = getConnectionById(connectionId);

        // chekc if user is the requester
        if (!connection.getRequester().getId().equals(requesterId)) {
            throw new IllegalStateException("Only the requester can cancel the connection request");
        }

        // only pending requests can be cancelled
        if (!"PENDING".equals(connection.getStatus())) {
            throw new IllegalStateException("Can only cancel pending connection requests");
        }

        connectionRepository.delete(connection);
    }

    /**
     * Remove a connection (unfriend/disconnect)
     */
    @Transactional
    public void removeConnection(Long connectionId, Long userId) {
        Connection connection = getConnectionById(connectionId);

        // chekc if user is involved in this connection
        if (!connection.involvesUser(userId)) {
            throw new IllegalStateException("You are not part of this connection");
        }

        // only accepted connections can be removed
        if (!"ACCEPTED".equals(connection.getStatus())) {
            throw new IllegalStateException("Can only remove accepted connections");
        }

        connectionRepository.delete(connection);
    }

    /**
     * Block a user
     */
    @Transactional
    public Connection blockUser(Long blockerId, Long blockedUserId) {
        // validate users exist
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new ResourceNotFoundException("Blocker not found with id: " + blockerId));

        User blocked = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Blocked user not found with id: " + blockedUserId));

        // cannot block yourself
        if (blockerId.equals(blockedUserId)) {
            throw new IllegalArgumentException("Cannot block yourself");
        }

        // find existing connection or create new one
        Optional<Connection> existingConnection = connectionRepository
                .findConnectionBetweenUsers(blockerId, blockedUserId);

        Connection connection;
        if (existingConnection.isPresent()) {
            connection = existingConnection.get();
        } else {
            // create new connection for blocking
            connection = new Connection(blocker, blocked);
        }

        connection.block(blocker);
        return connectionRepository.save(connection);
    }

    /**
     * Unblock a user
     */
    @Transactional
    public void unblockUser(Long blockerId, Long blockedUserId) {
        Connection connection = connectionRepository
                .findConnectionBetweenUsers(blockerId, blockedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found"));

        // chekc if blocker is the one who blocked
        if (!connection.getBlockedBy().getId().equals(blockerId)) {
            throw new IllegalStateException("You did not block this user");
        }

        // remove the blocking connection
        connectionRepository.delete(connection);
    }

    // ========== QUERY OPERATIONS ==========

    /**
     * Get connection by ID
     */
    public Connection getConnectionById(Long id) {
        return connectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found with id: " + id));
    }

    /**
     * Get all accepted connections (friends) for a user
     */
    public List<Connection> getConnections(Long userId) {
        return connectionRepository.findAcceptedConnections(userId);
    }

    /**
     * Get all pending connection requests sent by a user
     */
    public List<Connection> getPendingRequestsSent(Long userId) {
        return connectionRepository.findPendingRequestsSent(userId);
    }

    /**
     * Get all pending connection requests received by a user
     */
    public List<Connection> getPendingRequestsReceived(Long userId) {
        return connectionRepository.findPendingRequestsReceived(userId);
    }

    /**
     * Get all blocked users for a user
     */
    public List<Connection> getBlockedUsers(Long userId) {
        return connectionRepository.findBlockedUsers(userId);
    }

    /**
     * Get all rejected connections for a user
     */
    public List<Connection> getRejectedConnections(Long userId) {
        return connectionRepository.findRejectedConnections(userId);
    }

    /**
     * Get connection between two users
     */
    public Optional<Connection> getConnectionBetweenUsers(Long user1Id, Long user2Id) {
        return connectionRepository.findConnectionBetweenUsers(user1Id, user2Id);
    }

    /**
     * Chekc if two users are connected
     */
    public boolean areUsersConnected(Long user1Id, Long user2Id) {
        Optional<Connection> connection = connectionRepository.findConnectionBetweenUsers(user1Id, user2Id);
        return connection.isPresent() && connection.get().isActive();
    }

    /**
     * Chekc if user has blocked another user
     */
    public boolean hasUserBlocked(Long blockerId, Long blockedUserId) {
        return connectionRepository.isBlocked(blockerId, blockedUserId);
    }

    /**
     * Get count of connections for a user
     */
    public long getConnectionCount(Long userId) {
        return connectionRepository.countAcceptedConnections(userId);
    }

    /**
     * Get count of pending requests received by a user
     */
    public long getPendingRequestCount(Long userId) {
        return connectionRepository.countPendingRequestsReceived(userId);
    }

    /**
     * Get all users connected to a user (extract User objects)
     */
    public List<User> getConnectedUsers(Long userId) {
        List<Connection> connections = getConnections(userId);
        return connections.stream()
                .map(conn -> conn.getOtherUser(userId))
                .toList();
    }

    /**
     * Search connected users by name
     */
    public List<User> searchConnectedUsers(Long userId, String searchTerm) {
        List<User> connectedUsers = getConnectedUsers(userId);
        String lowerSearch = searchTerm.toLowerCase();

        return connectedUsers.stream()
                .filter(user -> {
                    String fullName = (user.getFirstName() + " " + user.getLastName()).toLowerCase();
                    return fullName.contains(lowerSearch) || user.getEmail().toLowerCase().contains(lowerSearch);
                })
                .toList();
    }

    /**
     * Get mutual connections between two users
     * Returns list of users who are connected to both user1 and user2
     */
    public List<User> getMutualConnections(Long user1Id, Long user2Id) {
        // Get connections for both users
        List<User> user1Connections = getConnectedUsers(user1Id);
        List<User> user2Connections = getConnectedUsers(user2Id);

        // Create a set of user2's connection IDs for efficient lookup
        Set<Long> user2ConnectionIds = user2Connections.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        // Find mutual connections (present in both lists)
        return user1Connections.stream()
                .filter(user -> user2ConnectionIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Get detailed connection status between two users
     * Returns status and connection ID if exists
     */
    public Map<String, Object> getConnectionStatus(Long user1Id, Long user2Id) {
        Map<String, Object> result = new HashMap<>();

        // Get connection between users
        Optional<Connection> connectionOpt = connectionRepository.findConnectionBetweenUsers(user1Id, user2Id);

        if (connectionOpt.isEmpty()) {
            result.put("status", "NOT_CONNECTED");
            result.put("connectionId", null);
            return result;
        }

        Connection connection = connectionOpt.get();
        result.put("connectionId", connection.getId());

        switch (connection.getStatus()) {
            case "ACCEPTED":
                result.put("status", "CONNECTED");
                break;
            case "PENDING":
                // Determine if user1 sent or received the request
                if (connection.getRequester().getId().equals(user1Id)) {
                    result.put("status", "PENDING_SENT");
                } else {
                    result.put("status", "PENDING_RECEIVED");
                }
                break;
            case "BLOCKED":
                // Check who blocked whom
                if (connection.getBlockedBy() != null && connection.getBlockedBy().getId().equals(user1Id)) {
                    result.put("status", "BLOCKED_BY_YOU");
                } else {
                    result.put("status", "BLOCKED");
                }
                break;
            case "REJECTED":
                result.put("status", "REJECTED");
                break;
            default:
                result.put("status", "NOT_CONNECTED");
        }

        return result;
    }
}
