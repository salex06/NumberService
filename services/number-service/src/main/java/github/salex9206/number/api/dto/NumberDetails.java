package github.salex9206.number.api.dto;

import lombok.Builder;

@Builder
public record NumberDetails(
        NumberSummary commonInfo,
        String nationalDestinationCode,
        String subscriberNumber,
        String rawInput,
        String originalCarrier,
        boolean isPorted,
        String status,
        HlrInfo hlrInfo,
        ScoreInfo scoreInfo,
        int myRating
)
{ }
