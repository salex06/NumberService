package github.salex9206.number.repository;

import github.salex9206.number.domain.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, UUID> {
    Optional<PhoneNumber> findByE164NumberAndStatusNot(String e164, PhoneNumber.NumberStatus status);
}
