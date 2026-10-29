package github.salex9206.user.profile.service;

import github.salex9206.user.profile.domain.UserProfile;
import github.salex9206.user.profile.repository.UserProfileRepository;
import github.salex9206.user.shared.UserProfileShared;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileRegistrationService {
    private final UserProfileRepository profileRepository;

    public boolean register(@Valid UserProfileShared profileShared) {
        UserProfile profile = convertToUserProfile(profileShared);

        return profileRepository.save(profile) != null;
    }

    private UserProfile convertToUserProfile(UserProfileShared profileShared) {
        UserProfile profile = new UserProfile();
        profile.setUserId(profileShared.getUserId());
        profile.setName(profileShared.getName());
        profile.setSurname(profileShared.getSurname());

        return profile;
    }
}
