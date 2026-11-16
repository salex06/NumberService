package github.salex9206.number.domain;

import github.salex9206.number.domain.enums.VerdictStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "number_scoring_results")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneNumberScoringResult {
    public enum ScoringTrigger {
        RATING_CHANGED,
        COMMENT_ADDED,
        HLR_UPDATED,
        MANUAL,
        SCHEDULED
    }

    @Id
    private UUID id;

    @Column(name = "number_id", nullable = false)
    private UUID numberId;

    @Min(0)
    @Max(100)
    @Column(name = "score", nullable = false)
    private Short score;

    @Enumerated(EnumType.STRING)
    @Column(name = "verdict", nullable = false)
    private VerdictStatus verdict;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_val", nullable = false)
    private ScoringTrigger triggerVal;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "factors", columnDefinition = "jsonb")
    private String factors;

    @Generated(event = {EventType.INSERT})
    @Column(name = "calculated_at", nullable = false, insertable = false, updatable = false)
    private Instant calculatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumberScoringResult other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Converter
    public static class ScoringTriggerConverter
            implements AttributeConverter<ScoringTrigger, String> {
        @Override
        public String convertToDatabaseColumn(ScoringTrigger attribute) {
            return attribute == null ? null : attribute.name();
        }

        @Override
        public ScoringTrigger convertToEntityAttribute(String dbData) {
            return dbData == null ? null : ScoringTrigger.valueOf(dbData);
        }
    }
}
