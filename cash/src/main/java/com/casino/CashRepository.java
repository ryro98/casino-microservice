package com.casino;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashRepository extends JpaRepository<Cash, Integer> {
    boolean existsCashByUserId(Integer id);
    Optional<Cash> findCashByUserId(Integer userId);
}
