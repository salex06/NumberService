package github.salex9206.hlr.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "hlr_subscribers")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HlrSubscriber {
    public enum LiveStatus {
        LIVE,
        ABSENT,
        UNKNOWN
    }

    @Id
    private String e164Number;

    @Column(nullable = false)
    private String imsi;

    @Column(nullable = false, length = 3)
    private String mcc;

    @Column(nullable = false, length = 3)
    private String mnc;

    @Column(nullable = false)
    private String originNetwork;

    @Column(nullable = false)
    private String currentNetwork;

    @Column
    private String mscAddress;

    @Column (nullable = false)
    private boolean isPorted;

    @Column (nullable = false)
    private boolean isRoaming;

    @Column(columnDefinition = "CHAR(2)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private String roamingCountry;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private LiveStatus liveStatus;

    @Column
    private String location;

    @Column(nullable = false, insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private Instant lastUpdatedAt;

    @Column
    private Instant simulatedAt;
}
