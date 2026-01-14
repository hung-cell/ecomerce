package org.example.ecomerce.module.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.ecomerce.common.entity.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "first_name", length = 50)
    private String firstName;
    
    @Column(name = "last_name", length = 50)
    private String lastName;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 500)
    private String avatar;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;
    
    public enum Role {
        USER, ADMIN, VENDOR
    }
    
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }
}

