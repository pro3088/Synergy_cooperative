package com.synergy.synergy_cooperative.transaction;

import com.synergy.synergy_cooperative.user.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Transaction findFirstByUser(User user);

    List<Transaction> findAllByType(Type type);

    List<Transaction> findAllByTypeAndUser(Type type, User user);

    boolean existsByBankId(String id);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.type = :type " +
            "AND YEAR(t.dateCreated) = :year " +
            "AND MONTH(t.dateCreated) = :month")
    Integer countWithinMonthByType(
            @Param("type") Type type,
            @Param("year") int year,
            @Param("month") int month
    );

}
