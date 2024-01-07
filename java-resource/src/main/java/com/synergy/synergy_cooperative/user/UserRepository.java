package com.synergy.synergy_cooperative.user;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByReferralCodeIgnoreCase(String referralCode);

    User findByEmailAddress(String emailAddress);

}
