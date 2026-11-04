CREATE TABLE number_scoring_results (
    id                UUID PRIMARY KEY,
    number_id         UUID NOT NULL REFERENCES numbers(id) ON DELETE CASCADE,
    score             SMALLINT NOT NULL,
    verdict           verdict_enum NOT NULL,
    trigger_val       scoring_trigger_enum NOT NULL,
    factors           JSONB, -- example: {"spam_reports": 5, "hlr_roaming": true, ...}
    calculated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_scoring_score CHECK (score BETWEEN 0 AND 100)
);

CREATE INDEX ix_scoring_number_calc
ON number_scoring_results (number_id, calculated_at DESC);

CREATE INDEX ix_scoring_latest
ON number_scoring_results (number_id, calculated_at DESC)
INCLUDE (score, verdict);