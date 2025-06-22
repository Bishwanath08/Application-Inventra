package org.productApplication.Inventra.exception;

import java.io.Serializable;

public class CodeException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ErrorCode code;

    public CodeException(ErrorCode code) {
        super();
        this.code = code;
    }

    public CodeException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }

    public CodeException(String message, Throwable cause, ErrorCode code) {
        super(message, cause);
        this.code = code;
    }

    public CodeException(Throwable cause, ErrorCode code) {
        super(cause);
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " (Error Code: " + code + ")";
    }
}
