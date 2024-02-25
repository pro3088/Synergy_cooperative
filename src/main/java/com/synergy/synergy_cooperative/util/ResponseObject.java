package com.synergy.synergy_cooperative.util;

public class ResponseObject<T> {
    private final boolean success;
    private final T data;
    private final String errorMessage;

    public ResponseObject(boolean b, T data, String errorMessage) {
        this.success = b;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> ResponseObject<T> success(T data) {
        return new ResponseObject<>(true, data, null);
    }

    public static <T> ResponseObject<T> error(String errorMessage) {
        return new ResponseObject<>(false, null, errorMessage);
    }
}
