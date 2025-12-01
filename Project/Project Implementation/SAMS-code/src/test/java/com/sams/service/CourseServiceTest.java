package com.sams.service;

import com.sams.entity.Course;
import com.sams.entity.User;
import com.sams.exception.CourseNotFoundException;
import com.sams.exception.DuplicateCourseCodeException;
import com.sams.repository.CourseRepository;
import com.sams.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private User testInstructor;

    @BeforeEach
    void setUp() {
        // create test instructor
        testInstructor = new User();
        testInstructor.setId(1L);
        testInstructor.setUsername("prof_smith");
        testInstructor.setEmail("smith@faculty.com");
        testInstructor.setRole("FACULTY");

        // create test course
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setCourseCode("CS101");
        testCourse.setCourseName("Introduction to Computer Science");
        testCourse.setDescription("Basic programming concepts");
        testCourse.setCredits(3);
        testCourse.setCapacity(30);
        testCourse.setInstructor(testInstructor);
    }

    @Test
    void testCreateCourse_Success() {
        // arrange
        when(courseRepository.existsByCourseCode(testCourse.getCourseCode())).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // act
        Course savedCourse = courseService.createCourse(testCourse);

        // assert
        assertNotNull(savedCourse);
        assertEquals("CS101", savedCourse.getCourseCode());
        assertEquals("Introduction to Computer Science", savedCourse.getCourseName());
        assertEquals(3, savedCourse.getCredits());
        verify(courseRepository, times(1)).save(testCourse);
    }

    @Test
    void testCreateCourse_DuplicateCourseCode_ThrowsException() {
        // arrange
        when(courseRepository.existsByCourseCode(testCourse.getCourseCode())).thenReturn(true);

        // act & assert
        assertThrows(DuplicateCourseCodeException.class, () -> {
            courseService.createCourse(testCourse);
        });

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testGetCourseById_Found() {
        // arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // act
        Course foundCourse = courseService.getCourseById(1L);

        // assert
        assertNotNull(foundCourse);
        assertEquals(1L, foundCourse.getId());
        assertEquals("CS101", foundCourse.getCourseCode());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCourseById_NotFound_ThrowsException() {
        // arrange
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(CourseNotFoundException.class, () -> {
            courseService.getCourseById(999L);
        });
    }

    @Test
    void testGetAllCourses() {
        // arrange
        Course course2 = new Course();
        course2.setId(2L);
        course2.setCourseCode("CS102");
        course2.setCourseName("Data Structures");

        List<Course> courseList = Arrays.asList(testCourse, course2);
        when(courseRepository.findAll()).thenReturn(courseList);

        // act
        List<Course> result = courseService.getAllCourses();

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testUpdateCourse_Success() {
        // arrange
        Course updatedDetails = new Course();
        updatedDetails.setCourseCode("CS101");
        updatedDetails.setCourseName("Intro to CS - Updated");
        updatedDetails.setDescription("Updated description");
        updatedDetails.setCredits(4);
        updatedDetails.setCapacity(40);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // act
        Course result = courseService.updateCourse(1L, updatedDetails);

        // assert
        assertNotNull(result);
        verify(courseRepository, times(1)).save(testCourse);
    }

    @Test
    void testDeleteCourse_Success() {
        // arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        doNothing().when(courseRepository).delete(testCourse);

        // act
        courseService.deleteCourse(1L);

        // assert
        verify(courseRepository, times(1)).delete(testCourse);
    }

    @Test
    void testSearchCoursesByName() {
        // arrange
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findByCourseNameContainingIgnoreCase("Computer")).thenReturn(courses);

        // act
        List<Course> result = courseService.searchCoursesByName("Computer");

        // assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getCourseName().contains("Computer"));
        verify(courseRepository, times(1)).findByCourseNameContainingIgnoreCase("Computer");
    }

    @Test
    void testAssignInstructor_Success() {
        // arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testInstructor));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // act
        Course result = courseService.assignInstructor(1L, 1L);

        // assert
        assertNotNull(result);
        assertNotNull(result.getInstructor());
        assertEquals("FACULTY", result.getInstructor().getRole());
        verify(courseRepository, times(1)).save(testCourse);
    }

    @Test
    void testAssignInstructor_NonFaculty_ThrowsException() {
        // arrange
        User student = new User();
        student.setId(2L);
        student.setUsername("john_doe");
        student.setRole("STUDENT");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(userRepository.findById(2L)).thenReturn(Optional.of(student));

        // act & assert
        assertThrows(IllegalArgumentException.class, () -> {
            courseService.assignInstructor(1L, 2L);
        });

        verify(courseRepository, never()).save(any(Course.class));
    }
}
