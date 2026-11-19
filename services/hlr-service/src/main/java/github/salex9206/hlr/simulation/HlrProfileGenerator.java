package github.salex9206.hlr.simulation;

import github.salex9206.hlr.entity.HlrSubscriber;
import github.salex9206.hlr.simulation.reference.CountryInfoStorage;
import github.salex9206.hlr.simulation.reference.OperatorInfoStorage;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Random;

@Component
public class HlrProfileGenerator {
    public HlrSubscriber generate(String e164) {
        long seed = e164.hashCode();
        Random rnd = new Random(seed);

        CountryInfoStorage.CountryInfo country = CountryInfoStorage.getCountry(e164);
        OperatorInfoStorage.OperatorInfo operator = OperatorInfoStorage.randomOperator(country.countryShortName());

        String imsi = operator.mcc() + operator.mnc()
                + String.format("%010d", Math.abs(rnd.nextLong()) % 10_000_000_000L);
        String msc = "mss-" + country.countryShortName().toLowerCase() + "-"
                + String.format("%02d", rnd.nextInt(20)) + "." + operator.domain();

        return HlrSubscriber.builder()
                .e164Number(e164)
                .imsi(imsi)
                .mcc(operator.mcc())
                .mnc(operator.mnc())
                .originNetwork(operator.name())
                .currentNetwork(operator.name())
                .mscAddress(msc)
                .isPorted(false)
                .isRoaming(false)
                .liveStatus(HlrSubscriber.LiveStatus.LIVE)
                .location(country.defaultCity())
                .lastUpdatedAt(Instant.now())
                .build();
    }
}
