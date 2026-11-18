package github.salex9206.number.utils.converter;

import github.salex9206.number.api.dto.HlrInfo;
import github.salex9206.number.api.dto.NumberDetails;
import github.salex9206.number.api.dto.NumberSummary;
import github.salex9206.number.api.dto.ScoreInfo;
import github.salex9206.number.domain.PhoneNumber;

import java.util.List;

public class NumberDetailsConverter {
    public static NumberDetails toDetails(PhoneNumber n) {
        HlrInfo hlrInfo = null; // todo

        ScoreInfo scoreInfo = ScoreInfo.builder()
                .score(n.getCurrentScore() == null ? 0 : n.getCurrentScore())
                .build();

        NumberSummary summary = NumberSummary.builder()
                .id(String.valueOf(n.getId()))
                .e164(n.getE164Number())
                .numberType(n.getNumberType().toString())
                .regionCode(n.getRegionCode())
                .countryCode(n.getCountryCode())
                .verdict(n.getCurrentVerdict().toString())
                .score(n.getCurrentScore() == null ? 0 : n.getCurrentScore())
                .averageRating(n.getRatingsCount() == 0 ? 0 : 1.0 * n.getRatingSum() / n.getRatingsCount())
                .ratingCount(n.getRatingsCount())
                .commentCount(n.getCommentsCount())
                .tags(List.of())
                .lastHlrCheckAt(n.getLastHlrCheck() == null ? null : n.getLastHlrCheck().toString())
                .createdAt(n.getCreatedAt() == null ? null : n.getCreatedAt().toString())
                .updatedAt(n.getUpdatedAt() == null ? null : n.getUpdatedAt().toString())
                .build();

        return NumberDetails.builder()
                .commonInfo(summary)
                .nationalDestinationCode(n.getNationalDestinationCode())
                .subscriberNumber(n.getSubscriberNumber())
                .rawInput(n.getRawNumber())
                .originalCarrier(n.getOriginalCarrier())
                .isPorted(n.isPorted())
                .status(n.getStatus().toString())
                .hlrInfo(hlrInfo)
                .scoreInfo(scoreInfo)
                .myRating(0) // todo;
                .build();
    }
}
