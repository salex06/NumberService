CREATE OR REPLACE FUNCTION trg_set_updated_at() RETURNS trigger AS
$$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER numbers_set_updated_at
BEFORE UPDATE ON numbers
FOR EACH ROW EXECUTE FUNCTION trg_set_updated_at();

CREATE TRIGGER ratings_set_updated_at
BEFORE UPDATE ON number_ratings
FOR EACH ROW EXECUTE FUNCTION trg_set_updated_at();