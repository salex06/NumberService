package github.salex9206.number.utils;

import github.salex9206.number.domain.ParsedNumber;
import github.salex9206.number.exception.InvalidNumberException;
import org.junit.jupiter.api.Test;

import static github.salex9206.number.domain.PhoneNumber.NumberType.MOBILE;
import static github.salex9206.number.domain.PhoneNumber.NumberType.TOLL_FREE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberNormalizerTest {
    PhoneNumberNormalizer normalizer = new PhoneNumberNormalizer();

    @Test
    void russianMobile_withPlus() {
        ParsedNumber p = normalizer.normalize("+79161234567");
        assertThat(p.getE164()).isEqualTo("+79161234567");
        assertThat(p.getCountryCode()).isEqualTo("7");
        assertThat(p.getNsrn()).isEqualTo("9161234567");
        assertThat(p.getRegion()).isEqualTo("RU");
        assertThat(p.getType()).isEqualTo(MOBILE);
    }

    @Test
    void russianMobile_withSpacesAndDashes() {
        ParsedNumber p = normalizer.normalize("+7 916 123-45-67");
        assertThat(p.getE164()).isEqualTo("+79161234567");
    }

    @Test
    void russianMobile_withBrackets() {
        ParsedNumber p = normalizer.normalize("+7 (916) 123-45-67");
        assertThat(p.getE164()).isEqualTo("+79161234567");
    }

    @Test
    void ukMobile() {
        ParsedNumber p = normalizer.normalize("+447912345678");
        assertThat(p.getE164()).isEqualTo("+447912345678");
        assertThat(p.getRegion()).isEqualTo("GB");
        assertThat(p.getType()).isEqualTo(MOBILE);
    }

    @Test
    void usNumber_fixedOrMobile() {
        ParsedNumber p = normalizer.normalize("+1 650 253 0000");
        assertThat(p.getRegion()).isEqualTo("US");
        assertThat(p.getType()).isIn(MOBILE);
    }

    @Test
    void tollFree() {
        ParsedNumber p = normalizer.normalize("+1 800 234 5678");
        assertThat(p.getType()).isEqualTo(TOLL_FREE);
    }

    @Test
    void internationalPrefix00_normalized() {
        ParsedNumber p = normalizer.normalize("00447911123456");
        assertThat(p.getE164()).isEqualTo("+447911123456");
    }

    @Test
    void withoutPlus_rejected() {
        assertThatThrownBy(() -> normalizer.normalize("89161234567"))
                .isInstanceOf(InvalidNumberException.class)
                .hasMessageContaining("international format");
    }

    @Test
    void tooShort_rejected() {
        assertThatThrownBy(() -> normalizer.normalize("+7916"))
                .isInstanceOf(InvalidNumberException.class);
    }

    @Test
    void tooLong_rejected() {
        assertThatThrownBy(() -> normalizer.normalize("+7916123456789012345"))
                .isInstanceOf(InvalidNumberException.class);
    }

    @Test
    void invalidCountryCode_rejected() {
        assertThatThrownBy(() -> normalizer.normalize("+9991234567"))
                .isInstanceOf(InvalidNumberException.class)
                .hasMessageContaining("country code");
    }

    @Test
    void notANumber_rejected() {
        assertThatThrownBy(() -> normalizer.normalize("hello"))
                .isInstanceOf(InvalidNumberException.class);
    }

    @Test
    void empty_rejected() {
        assertThatThrownBy(() -> normalizer.normalize("   "))
                .isInstanceOf(InvalidNumberException.class);
    }

    @Test
    void null_rejected() {
        assertThatThrownBy(() -> normalizer.normalize(null))
                .isInstanceOf(InvalidNumberException.class);
    }
}