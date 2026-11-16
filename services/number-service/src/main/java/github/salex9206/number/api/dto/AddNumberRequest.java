package github.salex9206.number.api.dto;

import jakarta.validation.constraints.Size;

public record AddNumberRequest(
        @Size(max = 255)
        String rawInput
) {
}
