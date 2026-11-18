package github.salex9206.number.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ParsedNumber {
    private String e164;

    private String countryCode;

    private String nsrn;

    private String region;

    private PhoneNumber.NumberType type;
}
