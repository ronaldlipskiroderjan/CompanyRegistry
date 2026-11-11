package br.com.Ronald.DesingPatternsProject.handler;

import br.com.Ronald.DesingPatternsProject.Exception.AlreadyExistsException;
import br.com.Ronald.DesingPatternsProject.Exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ExceptionDTO(message, HttpStatus.BAD_REQUEST.value())
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleNotFoundException(NotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ExceptionDTO(
                    ex.getMessage(),
                    HttpStatus.NOT_FOUND.value()
        ));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ExceptionDTO> handleAlreadyExistsException(AlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ExceptionDTO(
                        ex.getMessage(),
                        HttpStatus.CONFLICT.value()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handlerException(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ExceptionDTO(
                        ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }
}
