package github.salex9206.number.api.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record ScoreInfo(
        int score,
        String verdict,
        Map<String, String> factors,
        String calculatedAt
) {
}
