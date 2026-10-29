package github.salex9206.user.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileInfo {
        @NotBlank private String name;
        @NotBlank private String surname;
    }

    @NotBlank private String username;
    @NotBlank private String password;
    @NotNull private ProfileInfo profileInfo;
}
