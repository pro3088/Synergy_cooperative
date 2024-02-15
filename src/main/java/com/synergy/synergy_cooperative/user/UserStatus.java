package com.synergy.synergy_cooperative.user;


public enum UserStatus {
    ADMIN("ADM"),
    FINANCIAL_MEMBER("FIN"),
    MEMBER("MEM");

    final String code;

    UserStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static UserStatus getByCode(String code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No constant with code " + code + " found");
    }
}
