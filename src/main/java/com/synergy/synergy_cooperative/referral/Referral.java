package com.synergy.synergy_cooperative.referral;

import com.synergy.synergy_cooperative.user.User;

import javax.persistence.*;

@Entity

public class Referral {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, updatable = false)
    private String code;

    @Column(nullable = false)
    private boolean used;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", updatable = false)
    private User user;

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

    public User getUsers() {
        return user;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setUsers(final User user) {
        this.user = user;
    }

}
