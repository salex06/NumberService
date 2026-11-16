package github.salex9206.number.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import github.salex9206.number.domain.ParsedNumber;
import github.salex9206.number.domain.PhoneNumber;
import github.salex9206.number.exception.InvalidNumberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class PhoneNumberNormalizer {
    private static final PhoneNumberUtil UTIL = PhoneNumberUtil.getInstance();
    private static final Pattern INT_PREFIX_00 = Pattern.compile("^00");

    // TODO: private final NumberCarrierResolver carrierResolver;

    public ParsedNumber normalize(String rawInput) {
        String cleaned = sanitize(rawInput);

        if (!cleaned.startsWith("+"))
            throw new InvalidNumberException(
                rawInput,
                "Phone number must be in international format: +<country code><number>"
            );

        Phonenumber.PhoneNumber parsed;
        try {
            parsed = UTIL.parse(cleaned, null);
        } catch (NumberParseException e) {
            throw new InvalidNumberException(rawInput, parseErrorMessage(e), e);
        }

        if (!UTIL.isPossibleNumber(parsed))
            throw new InvalidNumberException(
                rawInput,
                "Phone number has invalid length for country code +" + parsed.getCountryCode()
            );

        if (!UTIL.isValidNumber(parsed))
            throw new InvalidNumberException(
                rawInput,
                "Phone number is not assigned or invalid for country code +" + parsed.getCountryCode()
            );

        String e164 = UTIL.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164);
        String countryCode = String.valueOf(parsed.getCountryCode());
        String nsrn = UTIL.getNationalSignificantNumber(parsed);
        String region = UTIL.getRegionCodeForNumber(parsed);
        PhoneNumber.NumberType type = mapType(UTIL.getNumberType(parsed));
        // TODO: String carrier = carrierResolver.resolve(parsed);

        return ParsedNumber.builder()
                .e164(e164)
                .countryCode(countryCode)
                .nsrn(nsrn)
                .region(region)
                .type(type)
                .build();
    }

    private String sanitize(String raw) {
        if (raw == null)
            throw new InvalidNumberException(null, "Phone number is required");

        String s = raw.trim();
        if (s.isEmpty())
            throw new InvalidNumberException(raw, "Phone number is empty");

        if (INT_PREFIX_00.matcher(s).find())
            s = "+" + s.substring(2);

        return s;
    }

    private PhoneNumber.NumberType mapType(PhoneNumberUtil.PhoneNumberType t) {
        return switch (t) {
            case FIXED_LINE -> PhoneNumber.NumberType.FIXED;
            case MOBILE, FIXED_LINE_OR_MOBILE -> PhoneNumber.NumberType.MOBILE;
            case TOLL_FREE -> PhoneNumber.NumberType.TOLL_FREE;
            case PREMIUM_RATE, SHARED_COST -> PhoneNumber.NumberType.PREMIUM;
            case VOIP -> PhoneNumber.NumberType.VOIP;
            case PERSONAL_NUMBER -> PhoneNumber.NumberType.PERSONAL;
            case PAGER, UAN, VOICEMAIL, UNKNOWN -> PhoneNumber.NumberType.UNKNOWN;
        };
    }

    private String parseErrorMessage(NumberParseException e) {
        return switch (e.getErrorType()) {
            case INVALID_COUNTRY_CODE ->
                    "Invalid or missing country code. Use +<country code><number>";
            case NOT_A_NUMBER -> "Input is not a phone number";
            case TOO_SHORT_AFTER_IDD, TOO_SHORT_NSN -> "Phone number too short";
            case TOO_LONG -> "Phone number too long";
        };
    }
}
