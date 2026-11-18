package github.salex9206.number.service;

import github.salex9206.number.api.dto.*;
import github.salex9206.number.domain.ParsedNumber;
import github.salex9206.number.domain.PhoneNumber;
import github.salex9206.number.domain.enums.VerdictStatus;
import github.salex9206.number.exception.NumberNotFoundException;
import github.salex9206.number.repository.PhoneNumberRepository;
import github.salex9206.number.utils.PhoneNumberNormalizer;
import github.salex9206.number.utils.converter.NumberDetailsConverter;
import github.salex9206.shared.auth.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class NumberService {
    private final PhoneNumberRepository phoneNumberRepository;
    private final PhoneNumberNormalizer phoneNumberNormalizer;

    private final TransactionTemplate transactionTemplate;

    public GetAllNumbersResult getAllNumbers(/*TODO: UserPrincipal principal*/) {
        var numbers = phoneNumberRepository.findAllByStatusNotIn(List.of(PhoneNumber.NumberStatus.DELETED));

        List<NumberDetails> details = new ArrayList<>();
        numbers.forEach(number -> {
            details.add(NumberDetailsConverter.toDetails(number));
        });

        return GetAllNumbersResult.builder()
                .numberCount(details.size())
                .numbers(details)
                .build();
    }

    public AddNumberResult addNumber(AddNumberRequest request, UserPrincipal principal) {
        ParsedNumber parsed = phoneNumberNormalizer.normalize(request.rawInput());

        var existing = phoneNumberRepository.findByE164NumberAndStatusNot(parsed.getE164(), PhoneNumber.NumberStatus.DELETED);
        if (existing.isPresent()) {
            // TODO: return existing entry
            return AddNumberResult.alreadyExists(null);
        }

        PhoneNumber created = transactionTemplate.execute(status -> {
            PhoneNumber number = PhoneNumber.builder()
                    .id(UUID.randomUUID())
                    .e164Number(parsed.getE164())
                    .countryCode(parsed.getCountryCode())
                    .nationalDestinationCode("todo")
                    .subscriberNumber("todo")
                    .rawNumber(request.rawInput())
                    .numberType(parsed.getType())
                    .regionCode(parsed.getRegion())
                    .originalCarrier("todo")
                    .currentCarrier("todo")
                    .ported(false)
                    .ratingSum(0L).ratingsCount(0).commentsCount(0)
                    .currentVerdict(VerdictStatus.UNKNOWN)
                    .status(PhoneNumber.NumberStatus.PENDING_HLR)
                    .createdBy(principal.userId())
                    .build();

            phoneNumberRepository.save(number);

            return number;
        });

        // TODO: hlr lookup

        // TODO: hlr update


        return AddNumberResult.created(NumberDetailsConverter.toDetails(created));
    }

    public GetSingleNumberResult getNumberByE164(String e164) {
        var phoneNumber = phoneNumberRepository.findByE164Number(e164).orElseThrow(
                () -> new NumberNotFoundException(
                        String.format("Not found number with e164=%s", e164)
                )
        );

        return new GetSingleNumberResult(NumberDetailsConverter.toDetails(phoneNumber));
    }

    public void softDeleteById(String id) {
        Supplier<? extends RuntimeException> notFoundException = () -> new NumberNotFoundException(
                String.format("Not found number with id=%s", id)
        );

        // Extract UUID
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw notFoundException.get();
        }

        // Find phone number
        var phoneNumber = phoneNumberRepository.findById(uuid).orElseThrow(notFoundException);

        // Update phone number
        phoneNumber.setStatus(PhoneNumber.NumberStatus.DELETED);
        phoneNumberRepository.save(phoneNumber);
    }
}
