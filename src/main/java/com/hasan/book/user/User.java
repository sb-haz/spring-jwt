package com.hasan.book.user;


import com.hasan.book.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter // Generates getters for all fields
@Setter // Generates setters for all fields
@Builder // Generates a builder for the class, useful for creating objects eg User.builder().firstName("John").build()
@AllArgsConstructor // Generates a constructor with all the fields of the class as arguments
@NoArgsConstructor // Generates a no-argument constructor
@Entity // Used to mark the class as a persistent Java class for JPA
@Table(name = "_user") // Need to change the table name to _user because user is a reserved keyword in SQL
@EntityListeners(AuditingEntityListener.class) // Used to automatically populate the createdDate and lastModifiedDate fields
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    @Column(unique = true) // Ensures that the email field is unique
    private String email;
    private String password;
    private boolean accountLocked;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER) // When we load a user, we'll eagerly load the roles
     private List<Role> roles;

    @CreatedDate
    @Column(nullable = false, updatable = false) // Ensures that the createdDate field is not null and cannot be updated
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(insertable = false) // Ensures that the lastModifiedDate field cannot be inserted
    private LocalDateTime lastModifiedDate;

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // Returns the roles of the user
        return this.roles
                .stream() // Converts the list of roles to a list of SimpleGrantedAuthority objects
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { // Returns true if the account is not expired, so not expired
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() { // Returns true if the credentials are not expired, so not expired
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
