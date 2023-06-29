package top.isopen.commons.springboot.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/**
 * @author TimeChaser
 * @version 1.0
 * @since 2023/5/10 16:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -56551964473301555L;

    private int code;
    private String message;
    private HttpStatus httpStatus;

    protected BaseException() {
        super();
    }

    public BaseException(int code) {
        this.code = code;
    }

    public BaseException(int code, String message) {
        this.code = code;
        this.message = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BaseException(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
