package pointer.Pointer_Spring.validation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity handleCustomException(CustomException customException){
        return ResponseEntity
                .status(customException.getExceptionCode().getStatus().getValue())
                .body(
                        new ErrorResponse(customException.getExceptionCode())
                );

    }
}
