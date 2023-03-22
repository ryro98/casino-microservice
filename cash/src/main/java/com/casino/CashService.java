package com.casino;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
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

    public Cash createCash(Integer userId) {
        Cash cash = Cash.builder()
                .userId(userId)
                .cash(0)
                .getCashTimer(LocalDateTime.now().minusDays(1))
                .gambleTimer(LocalDateTime.now().minusDays(1))
                .build();
        cashRepository.saveAndFlush(cash);
        return cash;
    }

    public Cash addCash(Integer userId) throws CashNotFoundException {
        Cash cash = cashRepository.findCashByUserId(userId).orElseThrow(CashNotFoundException::new);

        LocalDateTime time = LocalDateTime.now();
        LocalDateTime customer_timer = cash.getGetCashTimer();
        int timer_now = time.getHour() * 3600 + time.getMinute() * 60 + time.getSecond();
        int timer_customer = customer_timer.getHour() * 3600 + customer_timer.getMinute() * 60 + customer_timer.getSecond() + 300;
        if (customer_timer == null || time.getDayOfMonth() != customer_timer.getDayOfMonth() || timer_now > timer_customer) {
            cash.setCash(cash.getCash() + 500);
            cash.setGetCashTimer(LocalDateTime.now());
            cashRepository.saveAndFlush(cash);
        }
        return cash;
    }

    public Cash gambleCash(Integer userId, Integer money) throws CashNotFoundException {
        Cash cash = cashRepository.findCashByUserId(userId).orElseThrow(CashNotFoundException::new);

        LocalDateTime time = LocalDateTime.now();
        LocalDateTime customer_timer = cash.getGambleTimer();
        int timer_now = time.getHour() * 3600 + time.getMinute() * 60 + time.getSecond();
        int timer_customer = customer_timer.getHour() * 3600 + customer_timer.getMinute() * 60 + customer_timer.getSecond() + 300;
        if (customer_timer == null || time.getDayOfMonth() != customer_timer.getDayOfMonth() || timer_now > timer_customer) {
            cash = gamble(cash, money);
        }
        return cash;
    }

    private Cash gamble(Cash cash, Integer money) {
        if (money <= cash.getCash() * 0.5) {
            int random = new Random().ints(1, 10)
                    .findFirst()
                    .getAsInt();
            if (random > 5) {
                cash.setCash(cash.getCash() + money);
            } else {
                cash.setCash(cash.getCash() - money);
            }
            cash.setGambleTimer(LocalDateTime.now());
            cashRepository.saveAndFlush(cash);
        }
        return cash;
    }

    public void deleteCash(Integer id) {
        cashRepository.deleteById(id);
    }
}
