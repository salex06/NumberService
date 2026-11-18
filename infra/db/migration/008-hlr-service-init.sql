CREATE TYPE live_status_enum AS ENUM ('LIVE', 'ABSENT', 'UNKNOWN');

CREATE TABLE hlr_subscribers (
    e164_number     VARCHAR(20) PRIMARY KEY,
    imsi            VARCHAR(20) NOT NULL,
    mcc             VARCHAR(3) NOT NULL,
    mnc             VARCHAR(3) NOT NULL,
    origin_network  VARCHAR(100) NOT NULL,
    current_network VARCHAR(100) NOT NULL,
    msc_address     VARCHAR(100),
    is_ported       BOOLEAN NOT NULL DEFAULT FALSE,
    is_roaming      BOOLEAN NOT NULL DEFAULT FALSE,
    roaming_country CHAR(2),
    live_status     live_status_enum NOT NULL DEFAULT 'LIVE',
    location        VARCHAR(100),
    last_updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    simulated_at    TIMESTAMPTZ,

    CONSTRAINT chk_roaming_country CHECK (
        (is_roaming = TRUE AND roaming_country IS NOT NULL) OR
        (is_roaming = FALSE AND roaming_country IS NULL)
    ),
    CONSTRAINT chk_e164_format CHECK (e164_number ~ '^\+[1-9][0-9]{6,14}$')
);

CREATE INDEX ix_hlr_simulated_at ON hlr_subscribers (simulated_at);