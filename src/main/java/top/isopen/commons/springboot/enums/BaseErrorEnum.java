package top.isopen.commons.springboot.enums;

import org.springframework.http.HttpStatus;
import top.isopen.commons.springboot.exception.BaseException;

public enum BaseErrorEnum {

    INVALID_PAGE_CURRENT_ERROR(1, "invalid page current", HttpStatus.BAD_REQUEST),
    INVALID_PAGE_SIZE_ERROR(2, "invalid page size", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_BY_COLUMN_ERROR(3, "invalid order by column", HttpStatus.BAD_REQUEST),

    INVALID_PRIMARY_ID_ERROR(501, "invalid model primary id", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_APPLICATION_CONTEXT_ERROR(502, "invalid application context", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REDIS_RESULT_ERROR(503, "invalid redis result", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_RED_LOCK_ASPECT_PARAMETER_ERROR(504, "invalid red lock aspect parameter", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_RED_LOCK_TRY_ERROR(505, "failed to try lock", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_SP_EL_ERROR(505, "invalid spEl", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    BaseErrorEnum(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public void throwException() {
        throw new BaseException(this.code, this.message, this.httpStatus);
    }

}
