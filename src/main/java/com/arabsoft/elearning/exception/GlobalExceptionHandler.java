package com.arabsoft.elearning.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        HttpStatus status = switch (ex.getMessage()) {
            case "Utilisateur non trouvé", "Mot de passe incorrect" -> HttpStatus.UNAUTHORIZED;
            case "Email déjà utilisé" -> HttpStatus.CONFLICT;
            case "Compte désactivé" -> HttpStatus.FORBIDDEN;
            case "Compte en attente d'approbation par un administrateur" -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.BAD_REQUEST;
        };
        return ResponseEntity.status(status)
                .body(Map.of("error", ex.getMessage()));
    }
}