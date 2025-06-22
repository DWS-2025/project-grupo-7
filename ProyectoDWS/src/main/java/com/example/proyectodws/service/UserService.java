package com.example.proyectodws.service;

import com.example.proyectodws.dto.CourseDTO;
import com.example.proyectodws.dto.CourseMapper;
import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.dto.UserMapper;
import com.example.proyectodws.entities.Course;
import com.example.proyectodws.entities.User;
import com.example.proyectodws.repository.CourseRepository;
import com.example.proyectodws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CourseService courseService;

    public UserDTO createUser(UserDTO userDTO){
        User user = userMapper.toDomain(userDTO);
        return userMapper.toDTO(userRepository.save(user));
    }

    public UserDTO updateUser(Long id, UserDTO userRequestDTO){
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String currentPassword = user.getEncodedPassword();

        if (userRequestDTO.username() != null) {
            user.setUsername(userRequestDTO.username());
        }
        if (userRequestDTO.first_name() != null) {
            user.setFirst_name(userRequestDTO.first_name());
        }
        if (userRequestDTO.last_name() != null) {
            user.setLast_name(userRequestDTO.last_name());
        }
        if (userRequestDTO.imageName() != null) {
            user.setImageName(userRequestDTO.imageName());
        }
        if (userRequestDTO.roles() != null) {
            user.setRoles(userRequestDTO.roles());
        }
        if (userRequestDTO.courses() != null) {
            user.setCourses(userRequestDTO.courses().stream().map(courseMapper::toDomain).collect(Collectors.toSet()));
        }

        if (userRequestDTO.encodedPassword() != null) {
            user.setEncodedPassword(userRequestDTO.encodedPassword());
        } else {
            user.setEncodedPassword(currentPassword);
        }

        return userMapper.toDTO(userRepository.save(user));
    }

    public UserDTO getUserById(Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        return userMapper.toDTO(optionalUser.orElse(null));
    }

    public UserDTO getUserByUsername(String username){
        User user = userRepository.findByUsername(username).orElse(null);
        return userMapper.toDTO(user);
    }

    public List<UserDTO> getAllUsers(){
        return userMapper.toDTOs(userRepository.findAll());
    }

    public void deleteUser (Long id){
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            commentService.deleteUserComments(id);
            courseService.deleteUserCourses(id);
            user.getRoles().clear();
            userRepository.save(user);
        }
        userRepository.deleteById(id);
    }

    public void enrollUserInCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (user != null && course != null) {
            user.addCourse(course);
            userRepository.save(user);
        }
    }

    public Set<CourseDTO> getEnrolledCourses(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return courseMapper.toDTOs(user.getCourses());
    }

    public void unenrollUserFromCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (user != null && course != null) {
            user.removeCourse(course);
            userRepository.save(user);
        }
    }

    public int getNumCourses(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getCourses().size();
    }

    public User getLoggedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).get();
    }

    public UserDTO getLoggedUserDTO() {
        return userMapper.toDTO(getLoggedUser());
    }
}
