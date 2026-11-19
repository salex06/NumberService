package github.salex9206.hlr.api.dto;

import lombok.Builder;

@Builder
public record LookupResponse(
        String e164,
        String imsi,
        String mcc,
        String mnc,
        String originNetwork,
        String currentNetwork,
        String mscAddress,
        boolean isPorted,
        boolean isRoaming,
        String roamingCountry,
        String liveStatus,
        String location,
        String fetchedAt,
        boolean changed
) {
}
