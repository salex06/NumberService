package github.salex9206.hlr.mapper;

import github.salex9206.hlr.entity.HlrSubscriber;
import github.salex9206.hlr.api.dto.LookupResponse;
import org.springframework.stereotype.Component;

@Component
public class HlrMapper {
    public LookupResponse toLookupResponse(HlrSubscriber subscriber, boolean changed) {
        return LookupResponse.builder()
                .e164(subscriber.getE164Number())
                .imsi(subscriber.getImsi())
                .mcc(subscriber.getMcc())
                .mnc(subscriber.getMnc())
                .originNetwork(subscriber.getOriginNetwork())
                .currentNetwork(subscriber.getCurrentNetwork())
                .mscAddress(subscriber.getMscAddress())
                .isPorted(subscriber.isPorted())
                .isRoaming(subscriber.isRoaming())
                .roamingCountry(subscriber.getRoamingCountry())
                .liveStatus(String.valueOf(subscriber.getLiveStatus()))
                .location(subscriber.getLocation())
                .fetchedAt(subscriber.getLastUpdatedAt().toString())
                .changed(changed)
                .build();
    }
}
