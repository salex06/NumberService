CREATE TYPE number_type_enum AS ENUM (
    'MOBILE', 'FIXED', 'VOIP', 'TOLL_FREE',
    'PREMIUM', 'PERSONAL', 'SHORT_CODE', 'UNKNOWN'
);

CREATE TYPE number_status_enum AS ENUM (
    'ACTIVE', 'ARCHIVED', 'DELETED'
);

CREATE TYPE verdict_enum AS ENUM (
    'TRUSTED', 'NEUTRAL', 'SUSPICIOUS', 'UNKNOWN'
);

CREATE TYPE scoring_trigger_enum AS ENUM (
    'RATING_CHANGED', 'COMMENT_ADDED', 'HLR_UPDATED',
    'MANUAL', 'SCHEDULED'
);

CREATE TABLE IF NOT EXISTS numbers (
    id                          UUID PRIMARY KEY,

    -- Нормализованное представление
    e164_number                 VARCHAR(20) NOT NULL,

    -- Компоненты номера
    country_code                VARCHAR(3) NOT NULL,
    national_destination_code   VARCHAR(8),
    subscriber_number           VARCHAR(64),
    extension_number            VARCHAR(40),

    -- Пользовательский ввод
    raw_number                  TEXT,

    -- Классификация
    number_type                 number_type_enum NOT NULL DEFAULT 'UNKNOWN',
    region_code                 CHAR(2), -- ISO 3166-1

    -- Операторы
    original_carrier            VARCHAR(100),
    current_carrier             VARCHAR(100),
    is_ported                   BOOLEAN NOT NULL DEFAULT FALSE,

    -- Агрегированные оценки
    rating_sum                  BIGINT NOT NULL DEFAULT 0,
    ratings_count               INTEGER NOT NULL DEFAULT 0,
    comments_count              INTEGER NOT NULL DEFAULT 0,
    current_score               SMALLINT,
    current_verdict             verdict_enum NOT NULL DEFAULT 'UNKNOWN',

    -- HLR-метаданные
    last_hlr_check              TIMESTAMPTZ,

    -- Служебные
    status                      number_status_enum NOT NULL DEFAULT 'ACTIVE',
    created_by                  UUID,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at                  TIMESTAMPTZ,

    CONSTRAINT chk_e164_format CHECK (e164_number ~ '^\+[1-9][0-9]{6,14}$'),
    CONSTRAINT chk_rating_consistency CHECK (
        (ratings_count = 0 AND rating_sum = 0) OR
        (ratings_count > 0 AND rating_sum >= 0)
    ),
    CONSTRAINT chk_score_range CHECK (current_score IS NULL OR current_score BETWEEN 0 AND 100)
);

CREATE UNIQUE INDEX ux_numbers_e164_active
ON numbers (e164_number)
WHERE status <> 'DELETED';

CREATE INDEX ix_numbers_verdict_score
ON numbers (current_verdict, current_score DESC)
WHERE status = 'ACTIVE';

CREATE INDEX ix_numbers_type_region
ON numbers (number_type, region_code)
WHERE status = 'ACTIVE';

CREATE INDEX ix_numbers_updated_at
ON numbers (updated_at DESC);

CREATE INDEX ix_numbers_created_by
ON numbers (created_by)
WHERE created_by IS NOT NULL;