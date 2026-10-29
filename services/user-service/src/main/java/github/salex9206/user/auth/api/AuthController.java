package github.salex9206.user.auth.api;

import github.salex9206.user.auth.api.dto.*;
import github.salex9206.user.auth.service.UserRegistrationService;
import github.salex9206.user.auth.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Аутентификация и выдача токенов")
public class AuthController {
    private final TokenService tokenService;
    private final UserRegistrationService userRegistrationService;

    @Operation(
            summary = "Получить JWT по логину и паролю",
            description = "Проверяет credentials и возвращает access-токен. "
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токен успешно выдан",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (пустые поля)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/api/auth/login")
    public TokenResponse login(@Valid @RequestBody TokenRequest loginRequest) {
        return tokenService.generateToken(loginRequest);
    }

    @Operation(
            summary = "Зарегистрировать пользователя",
            description = "Получает данные пользователя, проверяет уникальность и сохраняет в БД"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = RegistrationRequest.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (пустые поля)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/api/auth/register")
    public RegistrationResponse register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return userRegistrationService.register(registrationRequest);
    }
}
