package uz.uzcard.controller.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.uzcard.dto.transaction.TransfersDTO;
import uz.uzcard.dto.transaction.TransfersStatusDTO;
import uz.uzcard.service.transaction.TransfersService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransfersController {

    private final TransfersService transfersService;


    @PostMapping()
    @PreAuthorize("hasAnyRole('PROFILE', 'ADMIN')")
    public ResponseEntity<TransfersDTO> makeTransfer(@RequestBody @Valid TransfersDTO dto) {
        log.info("Make Transfer {}", dto);
        return ResponseEntity.ok(transfersService.makeTransfer(dto));
    }

    @PostMapping("/conversion")
    @PreAuthorize("hasAnyRole('PROFILE', 'ADMIN')")
    public ResponseEntity<TransfersDTO> makeConversion(@RequestBody @Valid TransfersDTO dto) {
        log.info("Make Conversion {}", dto);
        return ResponseEntity.ok(transfersService.makeConversion(dto));
    }

    @GetMapping("/commission")
    @PreAuthorize("hasAnyRole('PROFILE', 'ADMIN')")
    public ResponseEntity<Object> getCommission(@RequestParam(value = "amount") Long amount,
                                                @RequestParam(value = "type") String type) {
        log.info("Get Commission amount={} type={}", amount, type);
        return ResponseEntity.ok(transfersService.getCommission(amount, type));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageImpl<TransfersDTO>> transfersPagination(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Transfers Pagination");
        return ResponseEntity.ok(transfersService.transfersPagination(page, size));
    }

    @PostMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageImpl<TransfersDTO>> transfersPaginationByStatus(@RequestBody @Valid TransfersStatusDTO dto,
                                                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Transfers Pagination By Status {}", dto);
        return ResponseEntity.ok(transfersService.transfersPaginationByStatus(page, size, dto));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PROFILE')")
    public ResponseEntity<PageImpl<TransfersDTO>> transfersPaginationByProfileId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Transfers Pagination By Profile Id");
        return ResponseEntity.ok(transfersService.transfersPaginationByProfileId(page, size));
    }

}
