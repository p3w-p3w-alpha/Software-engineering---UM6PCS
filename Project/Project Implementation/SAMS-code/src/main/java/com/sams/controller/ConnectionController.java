package com.sams.controller;

import com.sams.dto.ConnectionResponse;
import com.sams.entity.Connection;
import com.sams.entity.User;
import com.sams.service.ConnectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * handles user connections (friend requests) - send, accept, reject, block
 * frontend uses this for social networking features
 * supports mutual connections and connection search
 */
@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    // ========== CONNECTION REQUEST OPERATIONS ==========

    /**
     * Send a connection request
     * POST /api/connections/send?requesterId=1&receiverId=2
     */
    @PostMapping("/send")
    public ResponseEntity<ConnectionResponse> sendConnectionRequest(
            @RequestParam Long requesterId,
            @RequestParam Long receiverId) {

        Connection connection = connectionService.sendConnectionRequest(requesterId, receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(connection));
    }

    /**
     * Accept a connection request
     * POST /api/connections/{id}/accept?receiverId=2
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<ConnectionResponse> acceptConnectionRequest(
            @PathVariable Long id,
            @RequestParam Long receiverId) {

        Connection connection = connectionService.acceptConnectionRequest(id, receiverId);
        return ResponseEntity.ok(convertToResponse(connection));
    }

    /**
     * Reject a connection request
     * POST /api/connections/{id}/reject?receiverId=2
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ConnectionResponse> rejectConnectionRequest(
            @PathVariable Long id,
            @RequestParam Long receiverId) {

        Connection connection = connectionService.rejectConnectionRequest(id, receiverId);
        return ResponseEntity.ok(convertToResponse(connection));
    }

    /**
     * Cancel a connection request (by requester)
     * DELETE /api/connections/{id}/cancel?requesterId=1
     */
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelConnectionRequest(
            @PathVariable Long id,
            @RequestParam Long requesterId) {

        connectionService.cancelConnectionRequest(id, requesterId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Connection request cancelled successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Remove a connection (unfriend/disconnect)
     * DELETE /api/connections/{id}?userId=1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> removeConnection(
            @PathVariable Long id,
            @RequestParam Long userId) {

        connectionService.removeConnection(id, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Connection removed successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Block a user
     * POST /api/connections/block?blockerId=1&blockedUserId=2
     */
    @PostMapping("/block")
    public ResponseEntity<ConnectionResponse> blockUser(
            @RequestParam Long blockerId,
            @RequestParam Long blockedUserId) {

        Connection connection = connectionService.blockUser(blockerId, blockedUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(connection));
    }

    /**
     * Unblock a user
     * POST /api/connections/unblock?blockerId=1&blockedUserId=2
     */
    @PostMapping("/unblock")
    public ResponseEntity<Map<String, String>> unblockUser(
            @RequestParam Long blockerId,
            @RequestParam Long blockedUserId) {

        connectionService.unblockUser(blockerId, blockedUserId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User unblocked successfully");
        return ResponseEntity.ok(response);
    }

    // ========== QUERY OPERATIONS ==========

    /**
     * Get connection by ID
     * GET /api/connections/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConnectionResponse> getConnectionById(@PathVariable Long id) {
        Connection connection = connectionService.getConnectionById(id);
        return ResponseEntity.ok(convertToResponse(connection));
    }

    /**
     * Get all connections for a user
     * GET /api/connections/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConnectionResponse>> getConnections(@PathVariable Long userId) {
        List<Connection> connections = connectionService.getConnections(userId);
        List<ConnectionResponse> responses = connections.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get pending requests sent by a user
     * GET /api/connections/user/{userId}/sent
     */
    @GetMapping("/user/{userId}/sent")
    public ResponseEntity<List<ConnectionResponse>> getPendingRequestsSent(@PathVariable Long userId) {
        List<Connection> connections = connectionService.getPendingRequestsSent(userId);
        List<ConnectionResponse> responses = connections.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get pending requests received by a user
     * GET /api/connections/user/{userId}/received
     */
    @GetMapping("/user/{userId}/received")
    public ResponseEntity<List<ConnectionResponse>> getPendingRequestsReceived(@PathVariable Long userId) {
        List<Connection> connections = connectionService.getPendingRequestsReceived(userId);
        List<ConnectionResponse> responses = connections.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get blocked users for a user
     * GET /api/connections/user/{userId}/blocked
     */
    @GetMapping("/user/{userId}/blocked")
    public ResponseEntity<List<ConnectionResponse>> getBlockedUsers(@PathVariable Long userId) {
        List<Connection> connections = connectionService.getBlockedUsers(userId);
        List<ConnectionResponse> responses = connections.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * checks connection status between two users
     * useful before allowing messaging or collaboration
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> areUsersConnected(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {

        boolean connected = connectionService.areUsersConnected(user1Id, user2Id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("connected", connected);
        return ResponseEntity.ok(response);
    }

    /**
     * Get connection count for a user
     * GET /api/connections/user/{userId}/count
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> getConnectionCount(@PathVariable Long userId) {
        long count = connectionService.getConnectionCount(userId);
        long pendingCount = connectionService.getPendingRequestCount(userId);

        Map<String, Long> counts = new HashMap<>();
        counts.put("connections", count);
        counts.put("pendingRequests", pendingCount);

        return ResponseEntity.ok(counts);
    }

    /**
     * Search connected users by name
     * GET /api/connections/user/{userId}/search?query=john
     */
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<Map<String, Object>>> searchConnectedUsers(
            @PathVariable Long userId,
            @RequestParam String query) {

        List<User> users = connectionService.searchConnectedUsers(userId, query);
        List<Map<String, Object>> responses = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("name", user.getFirstName() + " " + user.getLastName());
                    userMap.put("email", user.getEmail());
                    userMap.put("role", user.getRole());
                    return userMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Get mutual connections between two users
     * GET /api/connections/mutual?user1Id=1&user2Id=2
     */
    @GetMapping("/mutual")
    public ResponseEntity<Map<String, Object>> getMutualConnections(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {

        List<User> mutualConnections = connectionService.getMutualConnections(user1Id, user2Id);
        List<Map<String, Object>> users = mutualConnections.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("firstName", user.getFirstName());
                    userMap.put("lastName", user.getLastName());
                    userMap.put("email", user.getEmail());
                    userMap.put("role", user.getRole());
                    userMap.put("profilePicture", user.getProfilePicture());
                    return userMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("count", mutualConnections.size());
        response.put("users", users);

        return ResponseEntity.ok(response);
    }

    /**
     * Get connection status between two users
     * GET /api/connections/status?user1Id=1&user2Id=2
     * Returns: NOT_CONNECTED, PENDING_SENT, PENDING_RECEIVED, CONNECTED, BLOCKED
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getConnectionStatus(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {

        Map<String, Object> status = connectionService.getConnectionStatus(user1Id, user2Id);
        return ResponseEntity.ok(status);
    }

    // ========== HELPER METHODS FOR DTO CONVERSION ==========

    // convert Connection entity to ConnectionResponse
    private ConnectionResponse convertToResponse(Connection connection) {
        ConnectionResponse response = new ConnectionResponse();
        response.setId(connection.getId());
        response.setStatus(connection.getStatus());
        response.setCreatedAt(connection.getCreatedAt());
        response.setAcceptedAt(connection.getAcceptedAt());
        response.setRejectedAt(connection.getRejectedAt());
        response.setBlockedAt(connection.getBlockedAt());

        // set requester info
        if (connection.getRequester() != null) {
            response.setRequesterId(connection.getRequester().getId());
            response.setRequesterName(connection.getRequester().getFirstName() + " " +
                    connection.getRequester().getLastName());
            response.setRequesterEmail(connection.getRequester().getEmail());
        }

        // set receiver info
        if (connection.getReceiver() != null) {
            response.setReceiverId(connection.getReceiver().getId());
            response.setReceiverName(connection.getReceiver().getFirstName() + " " +
                    connection.getReceiver().getLastName());
            response.setReceiverEmail(connection.getReceiver().getEmail());
        }

        // set blocker info
        if (connection.getBlockedBy() != null) {
            response.setBlockedById(connection.getBlockedBy().getId());
            response.setBlockedByName(connection.getBlockedBy().getFirstName() + " " +
                    connection.getBlockedBy().getLastName());
        }

        return response;
    }
}
