package top.isopen.commons.springboot.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.isopen.commons.logging.Log;
import top.isopen.commons.logging.LogFactory;
import top.isopen.commons.springboot.bean.Result;

@RestControllerAdvice
public class BaseExceptionHandler {

    private final Log log = LogFactory.getLog(BaseExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Object>> handleBaseException(BaseException exception) {
        log.error("BaseException ! error: {}, {}", exception.getMessage(), exception.getStackTrace());
        return new ResponseEntity<>(Result.error(exception.getCode(), exception.getMessage()),
                new HttpHeaders(),
                exception.getHttpStatus());
    }

}
