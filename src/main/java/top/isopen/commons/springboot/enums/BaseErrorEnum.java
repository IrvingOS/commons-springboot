package top.isopen.commons.springboot.enums;

import top.isopen.commons.springboot.exception.BaseException;

public enum BaseErrorEnum {

    INVALID_PAGE_CURRENT_ERROR(1, "invalid page current", HttpStatusEnum.BAD_REQUEST),
    INVALID_PAGE_SIZE_ERROR(2, "invalid page size", HttpStatusEnum.BAD_REQUEST),
    INVALID_ORDER_BY_COLUMN_ERROR(3, "invalid order by column", HttpStatusEnum.BAD_REQUEST),

    INVALID_PRIMARY_ID_ERROR(501, "invalid model primary id", HttpStatusEnum.INTERNAL_SERVER_ERROR),
    INVALID_APPLICATION_CONTEXT_ERROR(502, "invalid application context", HttpStatusEnum.INTERNAL_SERVER_ERROR),
    INVALID_REDIS_RESULT_ERROR(503, "invalid redis result", HttpStatusEnum.INTERNAL_SERVER_ERROR),

    ;

    private final int code;
    private final String message;
    private final HttpStatusEnum httpStatus;

    BaseErrorEnum(int code, String message, HttpStatusEnum httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public void throwException() {
        throw new BaseException(this.code, this.message, this.httpStatus);
    }

}
