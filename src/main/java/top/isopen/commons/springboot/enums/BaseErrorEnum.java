package top.isopen.commons.springboot.enums;

import top.isopen.commons.springboot.exception.BaseException;

public enum BaseErrorEnum {

    INVALID_PAGE_CURRENT_ERROR(1, "invalid_page_current", HttpStatusEnum.BAD_REQUEST),
    INVALID_PAGE_SIZE_ERROR(2, "invalid_page_size", HttpStatusEnum.BAD_REQUEST),
    INVALID_ORDER_BY_COLUMN_ERROR(3, "invalid_order_by_column", HttpStatusEnum.BAD_REQUEST),

    INVALID_PRIMARY_ID_ERROR(501, "invalid_model_primary_id", HttpStatusEnum.INTERNAL_SERVER_ERROR),
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
