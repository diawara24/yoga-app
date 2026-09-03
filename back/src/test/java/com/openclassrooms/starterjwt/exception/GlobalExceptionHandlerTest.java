package com.openclassrooms.starterjwt.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock private MessageSource messageSource;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    void handleNotFound_returns404WithResolvedMessage() {
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Session non trouvée");

        ProblemDetail result = handler.handleNotFound(new NotFoundException("error.session.not-found", 1L));

        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getDetail()).isEqualTo("Session non trouvée");
    }

    @Test
    void handleUnauthorized_returns403() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Accès refusé");

        ProblemDetail result = handler.handleUnauthorized(new UnauthorizedException("error.security.access-denied"), request);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void handleBadRequest_returns400() {
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Requête invalide");

        ProblemDetail result = handler.handleBadRequest(new BadRequestException("error.detail.bad-request"));

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleBusiness_returns422() {
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Erreur métier");

        ProblemDetail result = handler.handleBusiness(new BusinessException("error.business.rule"));

        assertThat(result.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(result.getDetail()).isEqualTo("Erreur métier");
    }

    @Test
    void handleUsernameNotFound_returns404() {
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Utilisateur non trouvé");

        ProblemDetail result = handler.handleUsernameNotFound(new UsernameNotFoundException("unknown@example.com"));

        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void handleBadCredentials_returns401() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Identifiants invalides");

        ProblemDetail result = handler.handleBadCredentials(new BadCredentialsException("bad credentials"), request);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void handleValidation_returns400WithFieldErrors() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/session");
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Validation échouée");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(
                List.of(new FieldError("sessionRequest", "name", "ne doit pas être vide")));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ProblemDetail result = handler.handleValidation(ex, request);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getProperties()).containsKey("fieldErrors");
    }

    @Test
    void handleMessageNotReadable_returns400() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/session");
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Requête invalide");

        ProblemDetail result = handler.handleMessageNotReadable(
                new HttpMessageNotReadableException("corps manquant"), request);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleTypeMismatch_returns400() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/session/abc");
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Requête invalide");

        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getValue()).thenReturn("abc");
        when(ex.getName()).thenReturn("id");

        ProblemDetail result = handler.handleTypeMismatch(ex, request);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleDataIntegrityViolation_returns409() {
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Violation de contrainte");

        ProblemDetail result = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("contrainte unique violée"));

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    void handleGeneric_returns500() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/session");
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Erreur serveur");

        ProblemDetail result = handler.handleGeneric(new RuntimeException("boom"), request);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}