package github.salex9206.number.api;

import github.salex9206.number.api.dto.GetSingleNumberResult;
import github.salex9206.number.api.dto.SetScoreRequest;
import github.salex9206.number.service.RatingsService;
import github.salex9206.shared.auth.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/api/numbers/{id}/ratings/me")
    public ResponseEntity<?> deleteScore(@PathVariable("id") String numberId,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        ratingsService.deleteScore(numberId, principal.userId().toString());

        return ResponseEntity.noContent().build();
    }
}
