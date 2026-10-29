package github.salex9206.user.shared;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserProfileShared {
    @NotNull private UUID userId;
    @NotBlank private String name;
    @NotBlank private String surname;
}
