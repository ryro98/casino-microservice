package com.casino;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("api/v1/cash")
public class CashController {
    @Autowired
    private CashRepository cashRepository;
    @Autowired
    private CashService cashService;

    @GetMapping
    @ResponseStatus(value = OK)
    public ResponseEntity<?> getAllCash() {
        if (cashRepository.count() > 0) {
            List<Cash> allCash = cashService.getAllCash();
            return ResponseEntity.status(OK).body(allCash);
        } else {
            return ResponseEntity.status(NOT_FOUND).body("Cash not found.");
        }
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = OK)
    public ResponseEntity<?> getCashById(@PathVariable(name = "id") Integer id) throws CashNotFoundException {
        if (cashRepository.existsById(id)) {
            Cash cash = cashService.getCashById(id);
            return ResponseEntity.status(OK).body(cash);
        } else {
            return ResponseEntity.status(NOT_FOUND).body("Cash with given id does not exist.");
        }
    }

    @GetMapping("/user/{id}")
    @ResponseStatus(value = OK)
    public ResponseEntity<?> getCashByUserId(@PathVariable(name = "id") Integer id) throws CashNotFoundException {
        if (cashRepository.existsCashByUserId(id)) {
            Cash cash = cashService.getCashByUserId(id);
            return ResponseEntity.status(OK).body(cash);
        } else {
            return ResponseEntity.status(NOT_FOUND).body("Cash with given user id does not exist.");
        }
    }

    @PostMapping
    @ResponseStatus(value = CREATED)
    public ResponseEntity<?> createCash(@RequestParam Integer userId) {
        if (cashRepository.existsCashByUserId(userId)) {
            return ResponseEntity.status(CONFLICT).body("Cash with given user id already exists.");
        }
        Cash cash = cashService.createCash(userId);
        return ResponseEntity.status(CREATED).body(cash);
    }

    @PutMapping("/user/{id}")
    @ResponseStatus(value = OK)
    public ResponseEntity<?> addCash(@PathVariable(name = "id") Integer userId) throws CashNotFoundException {
        if (cashRepository.existsCashByUserId(userId)) {
            Cash oldCash = Cash.builder()
                    .cash(cashService.getCashByUserId(userId).getCash())
                    .build();
            Cash newCash = cashService.addCash(userId);
            if (oldCash.getCash() != newCash.getCash()) {
                return ResponseEntity.status(OK).body(newCash);
            } else {
                return ResponseEntity.status(BAD_REQUEST).body(newCash);
            }
        } else {
            return ResponseEntity.status(NOT_FOUND).body("Cash with given user id does not exist.");
        }
    }

    @PutMapping("/gamble/{userId}")
    @ResponseStatus(value = OK)
    public ResponseEntity<?> gambleCash(
            @PathVariable(name = "userId") Integer userId,
            @RequestParam Integer money) throws CashNotFoundException {
        if (cashRepository.existsCashByUserId(userId)) {
            Cash oldCash = Cash.builder()
                    .cash(cashService.getCashByUserId(userId).getCash())
                    .build();
            Cash newCash = cashService.gambleCash(userId, money);
            if (newCash.getCash() != oldCash.getCash()) {
                return ResponseEntity.status(OK).body(newCash);
            } else {
                if (money > oldCash.getCash() * 0.5) {
                    return ResponseEntity.status(BAD_REQUEST).body("You bet too much cash, try again.");
                }
                LocalDateTime time = LocalDateTime.now();
                LocalDateTime customer_timer = newCash.getGetCashTimer();
                int timer_now = time.getHour() * 3600 + time.getMinute() * 60 + time.getSecond();
                int timer_customer = customer_timer.getHour() * 3600 + customer_timer.getMinute() * 60 + customer_timer.getSecond() + 300;
                return ResponseEntity.status(BAD_REQUEST).body(String.format("Wait %d seconds", timer_customer-timer_now));
            }
        } else {
            return ResponseEntity.status(NOT_FOUND).body("Cash with given user id does not exist.");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = NO_CONTENT)
    public ResponseEntity<String> deleteCash(@PathVariable(name = "id") Integer id) {
        if (cashRepository.existsById(id)) {
            cashService.deleteCash(id);
            return ResponseEntity.status(NO_CONTENT).body("");
        } else {
            return ResponseEntity.status(NOT_FOUND).body("Cash with given id does not exist.");
        }

    }
}
