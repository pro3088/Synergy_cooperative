package com.synergy.synergy_cooperative.transaction.shares;

import com.synergy.synergy_cooperative.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShareRepository extends JpaRepository<Share, String> {

    List<Share>  findAllByUser(User user);
}
