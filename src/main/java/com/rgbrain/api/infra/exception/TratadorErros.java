package com.rgbrain.api.infra.exception;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class TratadorErros {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarEntityNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> tratarMethodArgumentNoValidExcpetion(MethodArgumentNotValidException exception) {
        var erros = exception.getFieldErrors();
        
        return ResponseEntity.badRequest().body(
            erros.stream()
                .map(DadosErroValidacao::new)
                .toList()
        );
    }

    private record DadosErroValidacao(
        String campo, 
        String mensagem
    ) {
        public DadosErroValidacao(FieldError error) {
            this(
                error.getField(),
                error.getDefaultMessage()
            );
        }
    }
}
