package github.salex9206.user.shared;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface IProfileFacade {
    boolean saveUserProfile(UserProfileShared profile);
}
