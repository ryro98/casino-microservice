package com.casino;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CashService {
    @Autowired
    private CashRepository cashRepository;
    public List<Cash> getAllCash() {
        return cashRepository.findAll().stream()
                .sorted(Comparator.comparing(Cash::getId))
                .collect(Collectors.toList());
    }

    public Cash getCashById(Integer id) throws CashNotFoundException {
        return cashRepository.findById(id).orElseThrow(CashNotFoundException::new);
    }

    public Cash getCashByUserId(Integer userId) throws CashNotFoundException {
        return cashRepository.findCashByUserId(userId).orElseThrow(CashNotFoundException::new);
    }
}
