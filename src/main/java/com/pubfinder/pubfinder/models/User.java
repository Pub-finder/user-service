package com.pubfinder.pubfinder.models;


import com.pubfinder.pubfinder.models.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * The type User.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    @Column(unique = true, nullable = false)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String username;
    private String firstname;
    private String lastname;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Role role;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    @Builder.Default
    Set<User> following = new HashSet<>();

    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @Builder.Default
    Set<User> followers = new HashSet<>();

    @PreRemove
    private void cleanupFollowerRelationshipsBeforeDeletion() {
        for (User user : this.following) {
            user.getFollowers().remove(this);
        }
    }

    public void addFollowing(User user) {
        if (!this.following.contains(user)) {
            this.following.add(user);
            user.getFollowers().add(this);
        }
    }

    public void removeFollowing(User user) {
        if (this.following.contains(user)) {
            this.following.remove(user);
            user.getFollowers().remove(this);
        }
    }
}