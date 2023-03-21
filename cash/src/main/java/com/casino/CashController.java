package com.casino;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

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
}
