package github.salex9206.number.api;

import github.salex9206.number.api.dto.AddNumberRequest;
import github.salex9206.number.api.dto.AddNumberResult;
import github.salex9206.number.api.dto.GetAllNumbersResult;
import github.salex9206.number.api.dto.GetSingleNumberResult;
import github.salex9206.number.service.NumberService;
import github.salex9206.shared.auth.principal.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NumberController {
    private final NumberService numberService;

    @PostMapping("/api/numbers")
    public ResponseEntity<?> create(
            @Valid @RequestBody AddNumberRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        AddNumberResult result = numberService.addNumber(request, principal);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/api/numbers")
    public ResponseEntity<?> getAll() {
        GetAllNumbersResult result = numberService.getAllNumbers();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/api/numbers/by-e164/{e164}")
    public ResponseEntity<?> getByE164(@PathVariable("e164") String e164) {
        GetSingleNumberResult result = numberService.getNumberByE164(e164);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
