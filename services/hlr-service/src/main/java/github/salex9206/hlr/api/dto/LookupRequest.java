package github.salex9206.hlr.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LookupRequest(
        @NotBlank String e164,
        boolean forceRefresh) {
}
