package github.salex9206.hlr.api;

import github.salex9206.hlr.api.dto.LookupRequest;
import github.salex9206.hlr.api.dto.LookupResponse;
import github.salex9206.hlr.service.HlrLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HlrInternalController {
    private final HlrLookupService hlrLookupService;

    @PostMapping("/api/internal/hlr/lookup")
    public ResponseEntity<?> lookup(@RequestBody @Validated LookupRequest request) {
        LookupResponse resp = hlrLookupService.lookup(request);

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
