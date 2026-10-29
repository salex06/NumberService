package github.salex9206.user.profile.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "user_profiles")
@NoArgsConstructor
public class UserProfile {
    @Id
    private UUID userId;

    @Column(nullable = false, length = 64) // TODO: avoid magic number
    private String name;

    @Column(nullable = false, length = 64) // TODO: avoid magic number
    private String surname;
}
