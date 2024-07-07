package com.hasan.book.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hasan.book.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)

// The Role entity is used to represent the roles that a user can have in the application
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles") // The mappedBy attribute is used to specify the inverse side of the relationship between the User and Role entities
    @JsonIgnore // Allows users to be fetched, but prevents the users field from being serialized to JSON when a role is returned
    private List<User> users; // Users are loaded too when we load a role

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @CreatedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;


}
