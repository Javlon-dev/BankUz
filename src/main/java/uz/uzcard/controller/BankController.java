package uz.uzcard.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.uzcard.dto.BankDTO;
import uz.uzcard.service.BankService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/bank")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BankDTO> add(@RequestBody @Valid BankDTO dto) {
        log.info("Add Bank {}", dto);
        return ResponseEntity.ok(bankService.add(dto));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BankDTO>> getAll() {
        log.info("Get All Bank");
        return ResponseEntity.ok(bankService.getAll());
    }

}
