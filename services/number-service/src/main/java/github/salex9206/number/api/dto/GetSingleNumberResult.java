package github.salex9206.number.api.dto;

import lombok.Builder;

@Builder
public record GetSingleNumberResult(NumberDetails details) {
}
