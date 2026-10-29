package github.salex9206.user.auth.service;

import github.salex9206.user.auth.api.dto.RegistrationRequest;
import github.salex9206.user.auth.api.dto.RegistrationResponse;
import github.salex9206.user.auth.domain.User;
import github.salex9206.user.auth.exception.UserAlreadyExistsException;
import github.salex9206.user.auth.exception.UserSavingException;
import github.salex9206.user.auth.repository.UserRepository;
import github.salex9206.user.shared.IProfileFacade;
import github.salex9206.user.shared.UserProfileShared;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    private static final String USERNAME = "ivan";
    private static final String RAW_PASSWORD = "S3cret!pass";
    private static final String HASHED_PASSWORD = "$2a$12$abcdefghijklmnopqrstuv";
    private static final String PROFILE_NAME = "Иван";
    private static final String PROFILE_SURNAME = "Петров";

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private IProfileFacade profileFacade;

    @InjectMocks private UserRegistrationService service;

    private RegistrationRequest createValidRequest() {
        return RegistrationRequest.builder()
                .username(USERNAME)
                .password(RAW_PASSWORD)
                .profileInfo(RegistrationRequest.ProfileInfo.builder()
                        .name(PROFILE_NAME)
                        .surname(PROFILE_SURNAME)
                        .build())
                .build();
    }

    private User captureSavedUser() {
        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        return captor.getValue();
    }

    private UserProfileShared captureSavedProfile() {
        var captor = ArgumentCaptor.forClass(UserProfileShared.class);
        verify(profileFacade).saveUserProfile(captor.capture());
        return captor.getValue();
    }

    @Test
    @DisplayName("register: успешная регистрация -> result=true")
    void register_success_returnsResponseWithResultTrue() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(profileFacade.saveUserProfile(any())).thenReturn(true);

        RegistrationResponse response = service.register(createValidRequest());

        assertThat(response).isNotNull();
        assertThat(response.isResult()).isTrue();
    }

    @Test
    @DisplayName("register: пароль хешируется и сохраняется в БД (не raw)")
    void register_success_savesUserWithHashedPassword() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(profileFacade.saveUserProfile(any())).thenReturn(true);

        service.register(createValidRequest());

        User saved = captureSavedUser();
        assertThat(saved.getPasswordHash())
                .isEqualTo(HASHED_PASSWORD)
                .isNotEqualTo(RAW_PASSWORD);
        assertThat(saved.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    @DisplayName("register: для пользователя генерируется UUID")
    void register_success_generatesUuidForUser() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(profileFacade.saveUserProfile(any())).thenReturn(true);

        service.register(createValidRequest());

        User saved = captureSavedUser();
        assertThat(saved.getId()).isNotNull();
        assertThat(UUID.fromString(saved.getId().toString())).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("register: поля профиля корректно маппятся из запроса")
    void register_success_mapsProfileFieldsFromRequest() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(profileFacade.saveUserProfile(any())).thenReturn(true);

        service.register(createValidRequest());

        UserProfileShared profile = captureSavedProfile();
        assertThat(profile.getName()).isEqualTo(PROFILE_NAME);
        assertThat(profile.getSurname()).isEqualTo(PROFILE_SURNAME);
    }

    @Test
    @DisplayName("register: userId профиля совпадает с id сохранённого User")
    void register_success_passesSameIdToProfile() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(profileFacade.saveUserProfile(any())).thenReturn(true);

        service.register(createValidRequest());

        User savedUser = captureSavedUser();
        UserProfileShared savedProfile = captureSavedProfile();

        assertThat(savedProfile.getUserId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("register: дубликат username -> UserAlreadyExistsException")
    void register_duplicateUsername_throwsUserAlreadyExistsException() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        assertThatThrownBy(() -> service.register(createValidRequest()))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining(USERNAME);
    }

    @Test
    @DisplayName("register: дубликат username -> User и профиль не сохраняются")
    void register_duplicateUsername_doesNotPersistOrCallFacade() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        assertThatThrownBy(() -> service.register(createValidRequest()))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(profileFacade);
    }

    @Test
    @DisplayName("register: при дубликате пароль НЕ хешируется (проверка уникальности идёт первой)")
    void register_duplicateUsername_doesNotHashPassword() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        assertThatThrownBy(() -> service.register(createValidRequest()))
                .isInstanceOf(UserAlreadyExistsException.class);

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("register: saveUserProfile вернул false -> UserSavingException")
    void register_profileFacadeReturnsFalse_throwsUserSavingException() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(profileFacade.saveUserProfile(any())).thenReturn(false);

        assertThatThrownBy(() -> service.register(createValidRequest()))
                .isInstanceOf(UserSavingException.class)
                .hasMessageContaining(USERNAME);
    }

    @Test
    @DisplayName("register: User сохраняется до вызова profileFacade (порядок операций)")
    void register_savesUserBeforeCallingFacade() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(profileFacade.saveUserProfile(any())).thenReturn(true);

        service.register(createValidRequest());

        var inOrder = inOrder(userRepository, profileFacade);
        inOrder.verify(userRepository).save(any(User.class));
        inOrder.verify(profileFacade).saveUserProfile(any());
    }

    @Test
    @DisplayName("register: userRepository.save бросил исключение -> пробрасывается")
    void register_userRepositoryThrows_exceptionPropagates() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("db is down"));

        assertThatThrownBy(() -> service.register(createValidRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("db is down");

        verifyNoInteractions(profileFacade);
    }

    @Test
    @DisplayName("register: profileFacade бросил исключение -> пробрасывается")
    void register_profileFacadeThrows_exceptionPropagates() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(profileFacade.saveUserProfile(any()))
                .thenThrow(new IllegalStateException("profile service unreachable"));

        assertThatThrownBy(() -> service.register(createValidRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("profile service unreachable");
    }
}