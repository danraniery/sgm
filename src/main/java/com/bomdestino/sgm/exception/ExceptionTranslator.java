package com.bomdestino.sgm.exception;

import com.bomdestino.sgm.exception.exceptions.ExpiredCredentialsException;
import com.bomdestino.sgm.exception.exceptions.NotFoundException;
import com.bomdestino.sgm.util.Translator;
import lombok.AllArgsConstructor;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.bomdestino.sgm.util.TranslateConstants.*;


/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 */
@AllArgsConstructor
@ControllerAdvice
public class ExceptionTranslator implements ProblemHandling, SecurityAdviceTrait {

    private static final String PATH_KEY = "path";
    private static final String ERROR_KEY = "errorKey";
    private static final String MESSAGE_KEY = "message";
    private static final String VIOLATIONS_KEY = "violations";
    private static final String FIELD_ERRORS_KEY = "fieldErrors";
    private static final String EXPIRED_CREDENTIALS_KEY = "expiredCredentials";

    private final Translator translator;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Problem> expiredCredentialsException(NotFoundException e, NativeWebRequest request) {
        return create(e, getNotFoundErrorResponseEntity(e.getMessage()), request);
    }

    @ExceptionHandler(ExpiredCredentialsException.class)
    public ResponseEntity<Problem> expiredCredentialsException(ExpiredCredentialsException e, NativeWebRequest request) {
        return create(e, getExpiredCredentialsErrorResponseEntity(e.getMessage()), request);
    }

    private Problem getNotFoundErrorResponseEntity(String key) {
        return Problem.builder()
                .withStatus(Status.NOT_FOUND)
                .with(ERROR_KEY, key)
                .with(MESSAGE_KEY, String.format(translator.translate(NOT_FOUND_MESSAGE), translator.translate(key)))
                .withDetail(String.format(translator.translate(NOT_FOUND_MESSAGE), translator.translate(key)))
                .build();
    }

    private Problem getExpiredCredentialsErrorResponseEntity(String message) {
        return Problem.builder()
                .withStatus(Status.FORBIDDEN)
                .with(ERROR_KEY, EXPIRED_CREDENTIALS_KEY)
                .with(MESSAGE_KEY, message)
                .withDetail(message)
                .build();
    }

    /**
     * Post-process the Problem payload to add the message key for the front-end if needed.
     */
    @Override
    public ResponseEntity<Problem> process(@Nullable ResponseEntity<Problem> entity, NativeWebRequest request) {
        if (entity == null) {
            return entity;
        }
        Problem problem = entity.getBody();
        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return entity;
        }
        HttpServletRequest httpServletRequest = request.getNativeRequest(HttpServletRequest.class);
        String requestURI = Objects.nonNull(httpServletRequest) ? httpServletRequest.getRequestURI() : "";
        ProblemBuilder builder = Problem.builder()
                .withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? DEFAULT_TYPE : problem.getType())
                .withStatus(problem.getStatus())
                .withTitle(problem.getTitle())
                .with(PATH_KEY, requestURI);

        if (problem instanceof ConstraintViolationProblem) {
            builder
                    .with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations())
                    .with(MESSAGE_KEY, ERR_VALIDATION);
        } else {
            String detail = problem.getDetail();
            builder
                    .withCause(((DefaultProblem) problem).getCause())
                    .withDetail(detail)
                    .withInstance(problem.getInstance());
            problem.getParameters().forEach(builder::with);
            if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
                builder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
            }
        }
        return new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode());
    }

    @Override
    public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @Nonnull NativeWebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldErrorVM> fieldErrors = result.getFieldErrors().stream()
                .map(f -> new FieldErrorVM(f.getObjectName().replaceFirst("DTO$", ""), f.getField(), f.getCode()))
                .collect(Collectors.toList());

        Problem problem = Problem.builder()
                .withType(CONSTRAINT_VIOLATION_TYPE)
                .withTitle("Method argument not valid")
                .withStatus(defaultConstraintViolationStatus())
                .with(MESSAGE_KEY, ERR_VALIDATION)
                .with(FIELD_ERRORS_KEY, fieldErrors)
                .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleConcurrencyFailure(ConcurrencyFailureException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
                .withStatus(Status.CONFLICT)
                .with(MESSAGE_KEY, ERR_CONCURRENCY_FAILURE)
                .build();
        return create(ex, problem, request);
    }

}
