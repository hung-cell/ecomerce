package org.example.ecomerce.module.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.common.exception.BadRequestException;
import org.example.ecomerce.common.exception.ResourceNotFoundException;
import org.example.ecomerce.common.response.PageResponse;
import org.example.ecomerce.module.user.dto.UserRequest;
import org.example.ecomerce.module.user.dto.UserResponse;
import org.example.ecomerce.module.user.entity.User;
import org.example.ecomerce.module.user.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Getting all users with page: {}", pageable.getPageNumber());
        
        Page<User> userPage = userRepository.findAll(pageable);
        
        return PageResponse.<UserResponse>builder()
                .content(userPage.getContent().stream()
                        .map(user -> modelMapper.map(user, UserResponse.class))
                        .toList())
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .first(userPage.isFirst())
                .empty(userPage.isEmpty())
                .build();
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        return modelMapper.map(user, UserResponse.class);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.info("Getting user by username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return modelMapper.map(user, UserResponse.class);
    }
    
    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("Creating new user with username: {}", request.getUsername());
        
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(User.Role.USER)
                .status(User.UserStatus.ACTIVE)
                .emailVerified(false)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        
        return modelMapper.map(savedUser, UserResponse.class);
    }
    
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        log.info("Updating user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Check username if changed
        if (!user.getUsername().equals(request.getUsername()) 
                && userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        
        // Check email if changed
        if (!user.getEmail().equals(request.getEmail()) 
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        // Update user fields
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());
        
        return modelMapper.map(updatedUser, UserResponse.class);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        userRepository.delete(user);
        log.info("User deleted successfully with id: {}", id);
    }
}

