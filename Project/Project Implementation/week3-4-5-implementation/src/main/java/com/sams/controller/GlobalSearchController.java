package com.sams.controller;

import com.sams.entity.Course;
import com.sams.entity.User;
import com.sams.service.CourseService;
import com.sams.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GlobalSearchController {

    private final UserService userService;
    private final CourseService courseService;

    public GlobalSearchController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    /**
     * Global search across all entities
     * GET /api/search?query=...
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> globalSearch(@RequestParam String query) {
        Map<String, Object> results = new HashMap<>();

        // Search users
        List<User> allUsers = userService.getAllUsers();
        List<Map<String, Object>> users = allUsers.stream()
                .filter(user ->
                        user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                        user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                        (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(query.toLowerCase())) ||
                        (user.getLastName() != null && user.getLastName().toLowerCase().contains(query.toLowerCase()))
                )
                .limit(10) // Limit to 10 results
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("email", user.getEmail());
                    userMap.put("role", user.getRole());
                    userMap.put("type", "user");
                    return userMap;
                })
                .collect(Collectors.toList());

        // Search courses
        List<Course> coursesByName = courseService.searchCoursesByName(query);
        List<Course> coursesByCode = courseService.searchCoursesByCode(query);

        List<Map<String, Object>> courses = new java.util.ArrayList<>();
        courses.addAll(coursesByName.stream()
                .limit(10)
                .map(course -> {
                    Map<String, Object> courseMap = new HashMap<>();
                    courseMap.put("id", course.getId());
                    courseMap.put("code", course.getCourseCode());
                    courseMap.put("name", course.getCourseName());
                    courseMap.put("credits", course.getCredits());
                    courseMap.put("type", "course");
                    return courseMap;
                })
                .collect(Collectors.toList()));

        courses.addAll(coursesByCode.stream()
                .filter(course -> coursesByName.stream()
                        .noneMatch(c -> c.getId().equals(course.getId())))
                .limit(10 - courses.size())
                .map(course -> {
                    Map<String, Object> courseMap = new HashMap<>();
                    courseMap.put("id", course.getId());
                    courseMap.put("code", course.getCourseCode());
                    courseMap.put("name", course.getCourseName());
                    courseMap.put("credits", course.getCredits());
                    courseMap.put("type", "course");
                    return courseMap;
                })
                .collect(Collectors.toList()));

        results.put("users", users);
        results.put("courses", courses);
        results.put("query", query);
        results.put("totalResults", users.size() + courses.size());

        return ResponseEntity.ok(results);
    }
}
