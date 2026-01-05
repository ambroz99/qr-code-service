package qrcodeapi.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ValidationHandler {
    private static final List<String> REQUEST_PARAMETERS = List.of("contents", "size", "correction", "type");

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(ConstraintViolationException ex) {
        String errorMessage = null;

        for (String field : REQUEST_PARAMETERS) {
            errorMessage = ex.getConstraintViolations().stream()
                    .filter(cv -> cv.getPropertyPath().toString().contains(field))
                    .findFirst()
                    .map(ConstraintViolation::getMessage)
                    .orElse(null);

            if (errorMessage != null) break;
        }

        if (errorMessage == null) {
            errorMessage = ex.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .findFirst()
                    .orElse("Invalid request parameters");
        }

        Map<String, String> response = new HashMap<>();
        response.put("error", errorMessage);

        return ResponseEntity.badRequest().body(response);
    }
}
