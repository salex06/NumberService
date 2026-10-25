package github.salex9206.user.auth.service;

import github.salex9206.user.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthUserDetailsService service;

    @Test
    @DisplayName("loadByUsername: пользователь не найден")
    void loadByUsername_WhenNotFound_ThenThrowException() {
        String username = "abc";
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage( String.format("User not found, or unauthorized %s", username));
    }

    @Test
    @DisplayName("loadByUsername: пользователь найден")
    void loadByUsername_WhenUserExists_TheReturnUserDetails() {
        String username = "ivan";
        String password = "123";
        github.salex9206.user.auth.domain.User repoUser = new github.salex9206.user.auth.domain.User();
        repoUser.setUsername(username);
        repoUser.setPasswordHash(password);
        UserDetails expected = User.builder()
                .username(username)
                .password(password)
                .authorities(new ArrayList<>())
                .build();
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(repoUser));

        UserDetails actual = service.loadUserByUsername(username);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}