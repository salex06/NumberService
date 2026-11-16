package github.salex9206.number.domain;

import github.salex9206.number.domain.enums.VerdictStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "numbers")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneNumber {
    public enum NumberType {
        MOBILE,
        FIXED,
        VOIP,
        TOLL_FREE,
        PREMIUM,
        PERSONAL,
        SHORT_CODE,
        UNKNOWN
    }

    public enum NumberStatus {
        ACTIVE,
        ARCHIVED,
        DELETED,
        PENDING_HLR
    }

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "e164_number", nullable = false, length = 20, unique = true)
    private String e164Number;

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;

    @Column(name = "national_destination_code")
    private String nationalDestinationCode;

    @Column(name = "subscriber_number")
    private String subscriberNumber;

    @Column(name = "extension_number")
    private String extension;

    @Column(name = "raw_number")
    private String rawNumber;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "number_type", nullable = false)
    private NumberType numberType;

    @Column(name = "region_code", columnDefinition = "CHAR(2)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private String regionCode;

    @Column(name = "original_carrier")
    private String originalCarrier;

    @Column(name = "current_carrier")
    private String currentCarrier;

    @Column(name = "is_ported", nullable = false)
    private boolean ported = false;

    @Column(name = "rating_sum", nullable = false)
    private Long ratingSum;

    @Column(name = "ratings_count", nullable = false)
    private Integer ratingsCount;

    @Column(name = "comments_count", nullable = false)
    private Integer commentsCount;

    @Column(name = "current_score")
    private Short currentScore;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "current_verdict", nullable = false)
    private VerdictStatus currentVerdict;

    // TODO: add to application.yaml: "spring.jpa.properties.hibernate.jdbc.time_zone=UTC".
    @Column(name = "last_hlr_check")
    private Instant lastHlrCheck;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private NumberStatus status;

    @Column(name = "created_by")
    private UUID createdBy;

    @org.hibernate.annotations.Generated(event = EventType.INSERT)
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Converter
    public static class NumberTypeConverter
            implements AttributeConverter<NumberType, String> {

        @Override
        public String convertToDatabaseColumn(NumberType attribute) {
            return attribute == null ? null : attribute.name();
        }

        @Override
        public NumberType convertToEntityAttribute(String dbData) {
            return dbData == null ? null : NumberType.valueOf(dbData);
        }
    }

    @Converter
    public static class NumberStatusConverter
            implements AttributeConverter<NumberStatus, String> {
        @Override
        public String convertToDatabaseColumn(NumberStatus attribute) {
            return attribute == null ? null : attribute.name();
        }

        @Override
        public NumberStatus convertToEntityAttribute(String dbData) {
            return dbData == null ? null : NumberStatus.valueOf(dbData);
        }
    }
}