package github.salex9206.user.profile.service;

import github.salex9206.user.profile.domain.UserProfile;
import github.salex9206.user.profile.repository.UserProfileRepository;
import github.salex9206.user.shared.UserProfileShared;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileRegistrationServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String NAME = "Иван";
    private static final String SURNAME = "Петров";

    @Mock private UserProfileRepository profileRepository;

    @InjectMocks private UserProfileRegistrationService service;

    private UserProfileShared createProfileShared() {
        return UserProfileShared.builder()
                .userId(USER_ID)
                .name(NAME)
                .surname(SURNAME)
                .build();
    }

    private UserProfile captureSavedProfile() {
        var captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(profileRepository).save(captor.capture());
        return captor.getValue();
    }

    @Test
    @DisplayName("register: успешное сохранение -> true")
    void register_saveReturnsEntity_returnsTrue() {
        UserProfile saved = new UserProfile();
        when(profileRepository.save(any(UserProfile.class))).thenReturn(saved);

        boolean result = service.register(createProfileShared());

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("register: userId корректно маппится из shared в entity")
    void register_mapsUserIdFromShared() {
        when(profileRepository.save(any(UserProfile.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.register(createProfileShared());

        UserProfile saved = captureSavedProfile();
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("register: name и surname корректно маппятся")
    void register_mapsNameAndSurname() {
        when(profileRepository.save(any(UserProfile.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.register(createProfileShared());

        UserProfile saved = captureSavedProfile();
        assertThat(saved.getName()).isEqualTo(NAME);
        assertThat(saved.getSurname()).isEqualTo(SURNAME);
    }

    @Test
    @DisplayName("register: save вызывается ровно один раз с не-null сущностью")
    void register_callsRepositorySaveExactlyOnce() {
        when(profileRepository.save(any(UserProfile.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.register(createProfileShared());

        verify(profileRepository, times(1)).save(any(UserProfile.class));
        assertThat(captureSavedProfile()).isNotNull();
    }

    @Test
    @DisplayName("register: save вернул null -> false")
    void register_saveReturnsNull_returnsFalse() {
        when(profileRepository.save(any(UserProfile.class))).thenReturn(null);

        boolean result = service.register(createProfileShared());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("register: repository.save бросил исключение -> пробрасывается")
    void register_saveThrows_exceptionPropagates() {
        when(profileRepository.save(any(UserProfile.class)))
                .thenThrow(new RuntimeException("unique constraint violation"));

        assertThatThrownBy(() -> service.register(createProfileShared()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("unique constraint violation");
    }
}