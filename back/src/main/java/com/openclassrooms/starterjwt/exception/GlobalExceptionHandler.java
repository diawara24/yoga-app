package com.openclassrooms.starterjwt.exception;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
// UsernameNotFoundException handled by service -> throw NotFoundException instead
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        return createProblemDetail(HttpStatus.NOT_FOUND, "error.title.not-found",
                resolve(ex.getCode(), ex.getArgs()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, "error.title.bad-request",
                resolve(ex.getCode(), ex.getArgs()));
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException ex) {
        return createProblemDetail(HttpStatus.UNPROCESSABLE_ENTITY, "error.title.business",
                resolve(ex.getCode(), ex.getArgs()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail handleUsernameNotFound(UsernameNotFoundException ex) {
        return createProblemDetail(HttpStatus.NOT_FOUND, "error.title.not-found",
            resolve("error.username-not-found", ex.getMessage()));
    }

    /**
     * Vérifie la validation au niveau des champs envoyés par le client
     */
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

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Erreur non gérée", ex);
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "error.title.server",  resolve("error.detail.internal"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Violation de contrainte d'intégrité ", ex);
        return createProblemDetail(HttpStatus.CONFLICT, "error.title.conflict",  resolve("error.detail.data-integrity"));
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
