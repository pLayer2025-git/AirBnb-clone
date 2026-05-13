package com.project.Airbnb.entity;

import com.project.Airbnb.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false) //postgres automatically create index for unique constraint
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER) //it will create new table between role and user
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
}
//postgres sql do not allow to use table name as name- "user"