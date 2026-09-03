package com.openclassrooms.starterjwt.exception;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        log.info("Ressource introuvable : code={}, args={}", ex.getCode(), ex.getArgs());
        return createProblemDetail(HttpStatus.NOT_FOUND, "error.title.not-found",
                resolve(ex.getCode(), ex.getArgs()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex) {
        log.info("Requête invalide : code={}, args={}", ex.getCode(), ex.getArgs());
        return createProblemDetail(HttpStatus.BAD_REQUEST, "error.title.bad-request",
                resolve(ex.getCode(), ex.getArgs()));
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException ex) {
        log.warn("Erreur métier : code={}, args={}", ex.getCode(), ex.getArgs());
        return createProblemDetail(HttpStatus.UNPROCESSABLE_ENTITY, "error.title.business",
                resolve(ex.getCode(), ex.getArgs()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail handleUsernameNotFound(UsernameNotFoundException ex) {
        log.warn("Utilisateur non trouvé lors de l'authentification : {}", ex.getMessage());
        return createProblemDetail(HttpStatus.NOT_FOUND, "error.title.not-found",
                resolve("error.username-not-found", ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Échec d'authentification sur {} : identifiants invalides", request.getRequestURI());
        return createProblemDetail(HttpStatus.UNAUTHORIZED, "error.title.unauthenticated",
                resolve("error.security.unauthenticated"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining());

        log.warn("Validation échouée sur {} : {}", request.getRequestURI(), details);

        ProblemDetail problem = createProblemDetail(HttpStatus.BAD_REQUEST, "error.title.validation", details);
        problem.setProperty("fieldErrors", details);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Corps de requête illisible ou manquant sur {} : {}", request.getRequestURI(), ex.getMessage());
        return createProblemDetail(HttpStatus.BAD_REQUEST, "error.title.bad-request",
                resolve("error.detail.bad-request"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Paramètre invalide sur {} : '{}' n'est pas convertible pour '{}'",
                request.getRequestURI(), ex.getValue(), ex.getName());
        return createProblemDetail(HttpStatus.BAD_REQUEST, "error.title.bad-request",
                resolve("error.detail.bad-request"));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        log.warn("Accès refusé sur {} : code={}, args={}", request.getRequestURI(), ex.getCode(), ex.getArgs());
        return createProblemDetail(HttpStatus.FORBIDDEN, "error.title.access-denied",
                resolve(ex.getCode(), ex.getArgs()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Violation de contrainte d'intégrité ", ex);
        return createProblemDetail(HttpStatus.CONFLICT, "error.title.conflict",  resolve("error.detail.data-integrity"));
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Erreur non gérée sur {}", request.getRequestURI(), ex);
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "error.title.server",  resolve("error.detail.internal"));
    }

    private String resolve(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String titleCode, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(resolve(titleCode));
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

}