package github.salex9206.number.repository;

import github.salex9206.number.domain.PhoneNumberRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PhoneNumberRatingRepository extends JpaRepository<PhoneNumberRating, UUID> {
    Optional<PhoneNumberRating> findByUserIdAndNumberId(UUID userId, UUID numberId);
    void deleteByUserIdAndNumberId(UUID userId, UUID numberId);
}
