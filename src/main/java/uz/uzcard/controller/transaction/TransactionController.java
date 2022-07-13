package uz.uzcard.controller.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.uzcard.dto.transaction.TransactionDTO;
import uz.uzcard.dto.transaction.TransactionStatusDTO;
import uz.uzcard.service.transaction.TransactionService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageImpl<TransactionDTO>> transactionPagination(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                          @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Transaction Pagination page={} size={}", page, size);
        return ResponseEntity.ok(transactionService.transactionPagination(page, size));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageImpl<TransactionDTO>> transactionPaginationByStatus(@RequestBody @Valid TransactionStatusDTO dto,
                                                                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Transaction Pagination By Status page={} size={} dto={}", page, size, dto);
        return ResponseEntity.ok(transactionService.transactionPaginationByStatus(page, size, dto));
    }

}
