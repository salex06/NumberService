package github.salex9206.user.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    private UUID id;

    @Column(nullable = false, length = 64) // TODO: avoid magic number
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
}
