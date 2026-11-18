package github.salex9206.number.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record NumberSummary(
    String id,
    String e164,
    String numberType,
    String regionCode,
    String countryCode,
    String currentCarrier,
    String verdict,
    int score,
    double averageRating,
    int ratingCount,
    int commentCount,
    List<String> tags,
    String lastHlrCheckAt,
    String createdAt,
    String updatedAt
)
{ }
