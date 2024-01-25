package com.synergy.synergy_cooperative.user;

import com.synergy.synergy_cooperative.transaction.Transaction;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity(name = "users")
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String referralCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private String roles;

    @OneToMany(mappedBy = "user")
    private Set<Transaction> transactions;

    @CreationTimestamp
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @CreationTimestamp
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(final String referralCode) {
        this.referralCode = referralCode;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(final UserStatus status) {
        this.status = status;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(final Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
