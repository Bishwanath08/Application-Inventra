package org.productApplication.Inventra.exception;

public enum ErrorCode {

    RecordNotFound(1),
    InvalidUUID(2),
    InvalidRecordId(3),
    RecordAlreadyExist(4),
    InvalidParentCategory(5),
    ParameterMissing(6),
    InvalidParameter(7),
    InvalidParentUnit(8),
    InvalidAccess(9),
    // AreaNotProvided(10),
    OutOfStock(10),
    PaymentFailed(11),
    LowBalance(12),
    NoDataFound(13),
    OrderPreparing(51),
    Delivered(52),
    NoDineInAllowed(53),
    ClosedNow(54),
    TableNotFound(55),
    ReservationNotMatched(56),
    NoCafeAllowed(57),
    RoomBookingAllowed(76),
    InSufficientQuantity(58),
    NotPrepared(59),
    NotDispatched(60),
    Error(99),
    FacebookUserNotFound(100),
    WrongCheckInDate(61),
    WrongCheckOutDate(62),
    InvalidOtp(63),
    InvalidPassword(64),
    InSufficientBalance(65),
    InvalidData(66),
    UNIT_ALREADY_EXISTS(67),
    SUPPLIER_ALREADY_EXISTS(68),
    OTP_UPDATE_FAILED(69),

    ;

    int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
