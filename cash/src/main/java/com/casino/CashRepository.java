package com.casino;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashRepository extends JpaRepository<Cash, Integer> {
    boolean existsCashByUserName(String name);
    Optional<Cash> findCashByUserName(String name);
}
