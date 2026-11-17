package github.salex9206.number.api;

import github.salex9206.number.api.dto.GetSingleNumberResult;
import github.salex9206.number.api.dto.SetScoreRequest;
import github.salex9206.number.service.RatingsService;
import github.salex9206.shared.auth.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RatingsController {
    private final RatingsService ratingsService;

    @PutMapping("/api/numbers/{id}/ratings/me")
    public ResponseEntity<?> setScore(@PathVariable("id") String numberId,
                                      @AuthenticationPrincipal UserPrincipal principal,
                                      @RequestBody SetScoreRequest request) {
        GetSingleNumberResult resp = ratingsService.setScore(
                request, numberId, principal.userId().toString()
        );

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
