CREATE TABLE IF NOT EXISTS number_ratings (
    id              UUID PRIMARY KEY,
    number_id       UUID NOT NULL REFERENCES numbers(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL,
    score           SMALLINT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_rating_score CHECK (score BETWEEN 1 AND 5),
    CONSTRAINT ux_rating_user_number UNIQUE (number_id, user_id)
);

