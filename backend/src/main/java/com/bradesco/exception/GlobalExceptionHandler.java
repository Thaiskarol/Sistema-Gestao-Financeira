package com.bradesco.exception;

import java.util.List;

import com.bradesco.exception.TokenInvalidoException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.bradesco.dto.response.ErrorDetail;
import com.bradesco.dto.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception) {

        List<ErrorDetail> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorDetail(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        ErrorResponse response = new ErrorResponse(
                false,
                "Existem dados inválidos na requisição.",
                null,
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
        }

          @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception) {

        ErrorResponse response = new ErrorResponse(
                false,
                exception.getMessage(),
                null,
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
        @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErrorResponse> handleConflito(ConflitoException ex) {

        ErrorResponse response = new ErrorResponse(
                false,
                ex.getMessage(),
                null,
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleCredenciais(
            CredenciaisInvalidasException ex) {

        ErrorResponse response = new ErrorResponse(
                false,
                ex.getMessage(),
                null,
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }
    
    @ExceptionHandler (Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception exception) {

        ErrorResponse response = new ErrorResponse(
                false,
                "Ocorreu um erro interno no servidor.",
                null, 
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleParametroAusente(
              MissingServletRequestParameterException exception) {

        ErrorResponse response = new ErrorResponse(
                false,
                "O parâmetro '" + exception.getParameterName() + "' é obrigatório.",
                null,
                List.of()
        );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleParametroInvalido(
            MethodArgumentTypeMismatchException exception) {

        ErrorResponse response = new ErrorResponse(
                false,
                "O parâmetro '" + exception.getName() + "' tem um valor inválido.",
                null,
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

        @ExceptionHandler(TokenInvalidoException.class)
        public ResponseEntity<ErrorResponse> handleTokenInvalido(TokenInvalidoException ex) {
        ErrorResponse response = new ErrorResponse(false, ex.getMessage(), null, List.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // JSON malformado ou valor de enum inválido (ex.: tipo de investimento) viram 400, e não 500
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleCorpoIlegivel(HttpMessageNotReadableException ex) {
        ErrorResponse response = new ErrorResponse(false, "Corpo da requisição inválido.", null, List.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
}