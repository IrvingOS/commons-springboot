package top.isopen.commons.springboot.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.isopen.commons.logging.Log;
import top.isopen.commons.logging.LogFactory;
import top.isopen.commons.springboot.bean.Result;

/**
 * 基础异常处理类
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:35
 */
@RestControllerAdvice
public class BaseExceptionHandler {

    private final Log log = LogFactory.getLog(BaseExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Object>> handleBaseException(BaseException exception) {
        log.error("BaseException ! error: {}, {}, {}",
                exception.getMessage(),
                exception.getDescription(),
                exception.getStackTrace());
        return new ResponseEntity<>(
                Result.error(exception.getCode(), exception.getMessage(), exception.getDescription()),
                new HttpHeaders(),
                exception.getHttpStatus());
    }

}
