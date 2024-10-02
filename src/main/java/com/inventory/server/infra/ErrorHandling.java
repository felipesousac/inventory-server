package com.inventory.server.infra;

import com.inventory.server.infra.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandling extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("One or more fields are invalid");
        problemDetail.setType(URI.create("https://inventory.com/errors/invalid-fields")); // Doesn't need to be an existing URI

        Map<String, String> fields = ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(error -> ((FieldError) error).getField(),
                        DefaultMessageSourceResolvable::getDefaultMessage));

        problemDetail.setProperty("fields", fields);

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(ObjectAlreadyCreatedException.class)
    public ProblemDetail handleItemAlreadyCreated(ObjectAlreadyCreatedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle(ex.getMessage());
        problemDetail.setType(URI.create("https://inventory.com/errors/object-already-exists"));

        return problemDetail;
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ProblemDetail handleItemNotFound(ObjectNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle(ex.getMessage());
        problemDetail.setType(URI.create("https://inventory.com/errors/object-does-not-exist"));

        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleRecordInUse(DataIntegrityViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Record in use");
        problemDetail.setType(URI.create("https://inventory.com/errors/record-in-use"));

        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle(ex.getMessage());
        problemDetail.setType(URI.create("https://inventory.com/errors/invalid-credentials"));

        return problemDetail;
    }

    @ExceptionHandler(FileNotSupportedException.class)
    public ProblemDetail handleFileNotSupported(FileNotSupportedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        problemDetail.setTitle("Invalid file type, only images allowed");
        problemDetail.setType(URI.create("https://inventory.com/errors/file-not-supported"));

        return problemDetail;
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ProblemDetail handleFileNotFoundException(FileNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("File not found");
        problemDetail.setType(URI.create("https://inventory.com/errors/file-not-found"));

        return problemDetail;
    }

    @ExceptionHandler(PasswordChangeIllegalArgumentException.class)
    public ProblemDetail handlePasswordChangeIllegalArgument(PasswordChangeIllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle(ex.getMessage());
        problemDetail.setType(URI.create("https://inventory.com/errors/passwords-do-not-match"));

        return problemDetail;
    }
}
