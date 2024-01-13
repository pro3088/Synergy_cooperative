package com.synergy.synergy_cooperative.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByReferralCodeIgnoreCase(String referralCode);

    Optional<User> findByEmailAddress(String emailAddress);

}
