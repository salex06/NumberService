package github.salex9206.number.api.dto;

public record AddNumberResult(
    boolean created,
    NumberDetails number
) {
    public static AddNumberResult created(NumberDetails n)      { return new AddNumberResult(true, n); }
    public static AddNumberResult alreadyExists(NumberDetails n) { return new AddNumberResult(false, n); }
}
