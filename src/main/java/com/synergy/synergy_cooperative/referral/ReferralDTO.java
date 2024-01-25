package com.synergy.synergy_cooperative.referral;

import javax.validation.constraints.Size;

public class ReferralDTO {

    @Size(max = 255)
    private String id;

    @Size(max = 255)
    private String code;

    private boolean used;

    private String users;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(final String users) {
        this.users = users;
    }

}
