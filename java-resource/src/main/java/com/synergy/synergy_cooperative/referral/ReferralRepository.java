package com.synergy.synergy_cooperative.referral;

import com.synergy.synergy_cooperative.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ReferralRepository extends JpaRepository<Referral, String> {

    Referral findFirstByUser(User user);

    Optional<Referral> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByIdIgnoreCase(String id);

}
