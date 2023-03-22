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

    @GetMapping("/user/{name}")
    @ResponseStatus(value = OK)
    public ResponseEntity<?> getCashByUserName(@PathVariable(name = "name") String name) throws CashNotFoundException {
        if (cashRepository.existsCashByUserName(name)) {
            Cash cash = cashService.getCashByUserName(name);
            return ResponseEntity.status(OK).body(cash);
        } else {
            return ResponseEntity.status(NOT_FOUND).body("Cash with given user id does not exist.");
        }
    }

    @GetMapping("/top10")
    @ResponseStatus(value = OK)
    public ResponseEntity<?> getCashTop10() {
        if (cashRepository.count() > 0) {
            List<Cash> allCash = cashService.getCashTop10();
            StringBuilder response = new StringBuilder();
            for (Cash cash : allCash) {
                response.append(allCash.indexOf(cash)+1 + ". " + cash.getUserName() + ": " + cash.getCash() + "\n");
            }
            return ResponseEntity.status(OK).body(response);
        } else {
            return ResponseEntity.status(NOT_FOUND).body("Cash not found.");
        }

    }

    @PostMapping
    @ResponseStatus(value = CREATED)
    public ResponseEntity<?> createCash(@RequestParam String name) {
        if (cashRepository.existsCashByUserName(name)) {
            return ResponseEntity.status(CONFLICT).body("Cash with given username already exists.");
        }
        Cash cash = cashService.createCash(name);
        return ResponseEntity.status(CREATED).body(cash);
    }

    @PutMapping("/user/{name}")
    @ResponseStatus(value = OK)
    public ResponseEntity<?> addCash(@PathVariable(name = "name") String name) throws CashNotFoundException {
        if (cashRepository.existsCashByUserName(name)) {
            Cash oldCash = Cash.builder()
                    .cash(cashService.getCashByUserName(name).getCash())
                    .build();
            Cash newCash = cashService.addCash(name);
            if (oldCash.getCash() != newCash.getCash()) {
                return ResponseEntity.status(OK).body(newCash);
            } else {
                LocalDateTime time = LocalDateTime.now();
                LocalDateTime customer_timer = newCash.getGambleTimer();
                int timer_now = time.getHour() * 3600 + time.getMinute() * 60 + time.getSecond();
                int timer_customer = customer_timer.getHour() * 3600 + customer_timer.getMinute() * 60 + customer_timer.getSecond() + 300;
                return ResponseEntity.status(BAD_REQUEST).body(String.format("Wait %d seconds.", timer_customer-timer_now));
            }
        } else {
            return ResponseEntity.status(NOT_FOUND).body("Cash with given user id does not exist.");
        }
    }

    @PutMapping("/gamble/{name}")
    @ResponseStatus(value = OK)
    public ResponseEntity<?> gambleCash(
            @PathVariable(name = "name") String name,
            @RequestParam Integer money) throws CashNotFoundException {
        if (money == 0) {
            return ResponseEntity.status(BAD_REQUEST).body("You cannot bet 0$");
        }
        if (cashRepository.existsCashByUserName(name)) {
            Cash oldCash = Cash.builder()
                    .cash(cashService.getCashByUserName(name).getCash())
                    .build();
            Cash newCash = cashService.gambleCash(name, money);
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

    @DeleteMapping
    @ResponseStatus(value = NO_CONTENT)
    public ResponseEntity<String> deleteAllCash() {
        cashRepository.deleteAll();
        return ResponseEntity.status(NO_CONTENT).body("");
    }
}
