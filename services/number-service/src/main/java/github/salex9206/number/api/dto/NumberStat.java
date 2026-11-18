package github.salex9206.number.api.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record NumberStat(
        double average,
        long count,
        Map<Short, Long> distribution,
        short myRating
) {
}
