package com.incubyte.salarymanagement.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationFailure(
            HttpServletRequest request) {
        log.warn("Authentication rejected for request path={}", request.getRequestURI());
        return problem(HttpStatus.UNAUTHORIZED, "Invalid username or password", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            HttpServletRequest request) {
        log.warn("Access denied for request path={}", request.getRequestURI());
        return problem(HttpStatus.FORBIDDEN, "You are not allowed to perform this operation", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleInvalidRequestBody(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .distinct()
                .toList();

        log.warn("Request validation failed for path={} errorCount={}",
                request.getRequestURI(), errors.size());

        ProblemDetail body = createProblem(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                request);
        body.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({
            HandlerMethodValidationException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ProblemDetail> handleMalformedRequest(
            Exception exception,
            HttpServletRequest request) {
        log.warn("Malformed request for path={}: {}",
                request.getRequestURI(), exception.getClass().getSimpleName());
        return problem(HttpStatus.BAD_REQUEST, "The request is invalid or malformed", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleInvalidArgument(
            IllegalArgumentException exception,
            HttpServletRequest request) {
        log.warn("Rejected request for path={}: {}",
                request.getRequestURI(), exception.getMessage());
        return problem(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            NoSuchElementException exception,
            HttpServletRequest request) {
        log.warn("Requested resource was not found for path={}", request.getRequestURI());
        return problem(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleConflict(
            IllegalStateException exception,
            HttpServletRequest request) {
        log.warn("Request conflict for path={}: {}",
                request.getRequestURI(), exception.getMessage());
        return problem(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(
            NoResourceFoundException exception,
            HttpServletRequest request) {
        log.debug("No resource found for path={}", request.getRequestURI());
        return problem(HttpStatus.NOT_FOUND, "The requested resource was not found", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request) {
        log.error("Unexpected error for request path={}", request.getRequestURI(), exception);
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request);
    }

    private ResponseEntity<ProblemDetail> problem(
            HttpStatus status,
            String detail,
            HttpServletRequest request) {
        return ResponseEntity.status(status).body(createProblem(status, detail, request));
    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String detail,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }
}
