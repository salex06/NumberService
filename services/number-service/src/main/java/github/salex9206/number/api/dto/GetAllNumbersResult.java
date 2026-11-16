package github.salex9206.number.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GetAllNumbersResult(
        int numberCount,
        List<NumberDetails> numbers
)
{ }
