package github.salex9206.number.service;

import github.salex9206.number.api.dto.GetSingleNumberResult;
import github.salex9206.number.api.dto.SetScoreRequest;
import github.salex9206.number.domain.PhoneNumber;
import github.salex9206.number.domain.PhoneNumberRating;
import github.salex9206.number.exception.NumberNotFoundException;
import github.salex9206.number.repository.PhoneNumberRatingRepository;
import github.salex9206.number.repository.PhoneNumberRepository;
import github.salex9206.number.utils.converter.NumberDetailsConverter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingsService {
    private final PhoneNumberRatingRepository phoneNumberRatingRepository;
    private final PhoneNumberRepository phoneNumberRepository;

    @Transactional
    public GetSingleNumberResult setScore(SetScoreRequest req, String numberId, String userId) {
        final int score = req.score();

        // Конвертируем идентификаторы в UUID TODO: handle invalid ids
        final UUID numberUUID = UUID.fromString(numberId);
        final UUID userUUID = UUID.fromString(userId);

        // Получаем номер по numberId
        PhoneNumber number = phoneNumberRepository.findById(numberUUID).orElseThrow(
                () -> new NumberNotFoundException(
                        String.format("Not found number with id=%s", numberId)
                )
        );

        // Получаем существующий рейтинг или генерируем новый
        PhoneNumberRating rating = phoneNumberRatingRepository.findByUserIdAndNumberId(
                userUUID, numberUUID
        ).orElse(newRating(numberUUID, userUUID));
        int prevScore = rating.getScore() == null ? 0 : rating.getScore();

        // Обновляем агрегированные метрики для номера и сохраняем номер
        recalcPhoneNumberMetrics(number, score, prevScore);
        phoneNumberRepository.save(number);

        // Устанавливаем оценку и сохраняем в БД
        rating.setScore((short) score);
        phoneNumberRatingRepository.save(rating);

        // TODO: transactional outbox number.rating.changed

        return new GetSingleNumberResult(
                NumberDetailsConverter.toDetails(number)
        );
    }

    private PhoneNumberRating newRating(UUID numberId, UUID userId) {
        return PhoneNumberRating.builder()
                .id(UUID.randomUUID())
                .numberId(numberId)
                .userId(userId)
                .build();
    }

    private void recalcPhoneNumberMetrics(PhoneNumber number, int currRating, int prevRating) {
        long ratingSum = number.getRatingSum();
        int ratingCount = number.getRatingsCount();

        number.setRatingSum(ratingSum - prevRating + currRating);
        if (prevRating == 0)
            number.setRatingsCount(ratingCount + 1);
    }
}
