package uz.uzcard.controller.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.uzcard.dto.card.CardDTO;
import uz.uzcard.dto.card.VisaPanDTO;
import uz.uzcard.service.card.VisaCardService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/visa-card")
@RequiredArgsConstructor
public class VisaCardController {

    private final VisaCardService visaCardService;


    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> create() {
        log.info("Create Visa Card");
        return ResponseEntity.ok(visaCardService.create());
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> updateStatus(@RequestBody @Valid VisaPanDTO dto) {
        log.info("Change Status");
        return ResponseEntity.ok(visaCardService.changeStatus(dto));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageImpl<CardDTO>> getInactiveCardByPage(@RequestParam("page") int page,
                                                                   @RequestParam("size") int size) {
        log.info("Get All");
        return ResponseEntity.ok(visaCardService.getInactiveCardByPage(page, size));
    }

}
