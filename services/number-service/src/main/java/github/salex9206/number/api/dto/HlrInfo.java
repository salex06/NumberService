package github.salex9206.number.api.dto;

public record HlrInfo(
        String liveStatus,
        String mcc,
        String mnc,
        String currentNetwork,
        String originalNetwork,
        boolean isRoaming,
        String roamingCountry,
        String location,
        String fetchedAt
) {
}
