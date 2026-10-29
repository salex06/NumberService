package github.salex9206.user.auth.service;

import github.salex9206.user.auth.api.dto.RegistrationRequest;
import github.salex9206.user.auth.api.dto.RegistrationResponse;
import github.salex9206.user.auth.domain.User;
import github.salex9206.user.auth.exception.UserAlreadyExistsException;
import github.salex9206.user.auth.exception.UserSavingException;
import github.salex9206.user.auth.repository.UserRepository;
import github.salex9206.user.shared.IProfileFacade;
import github.salex9206.user.shared.UserProfileShared;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IProfileFacade profileFacade;

    @Transactional
    public RegistrationResponse register(RegistrationRequest request) {
        final String username = request.getUsername();
        final String password = request.getPassword();
        final RegistrationRequest.ProfileInfo profileInfo = request.getProfileInfo();

        // Check for duplicate username
        if (userRepository.existsByUsername(username))
            throw new UserAlreadyExistsException(String.format("Имя пользователя %s уже занято", username));

        // Hash password
        String hashedPassword = passwordEncoder.encode(password);

        // Generate User id
        UUID id = UUID.randomUUID();

        // Create new User
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPasswordHash(hashedPassword);

        // Save user and user profile
        userRepository.save(user);
        if (!profileFacade.saveUserProfile(convertToUserProfile(user.getId(), profileInfo)))
            throw new UserSavingException(
                    String.format("Не удалось сохранить пользователя %s", username)
            );

        return RegistrationResponse.builder()
                .result(true)
                .build();
    }

    private UserProfileShared convertToUserProfile(UUID userId, RegistrationRequest.ProfileInfo profileInfo) {

        return UserProfileShared.builder()
                .userId(userId)
                .name(profileInfo.getName())
                .surname(profileInfo.getSurname())
                .build();
    }
}
