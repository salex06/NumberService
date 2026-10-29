package github.salex9206.user.profile.api;

import github.salex9206.user.profile.service.UserProfileRegistrationService;
import github.salex9206.user.shared.IProfileFacade;
import github.salex9206.user.shared.UserProfileShared;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProfileFacade implements IProfileFacade {
    private final UserProfileRegistrationService userProfileRegistrationService;

    @Override
    public boolean saveUserProfile(@Valid UserProfileShared profileShared) {
        return userProfileRegistrationService.register(profileShared);
    }
}
