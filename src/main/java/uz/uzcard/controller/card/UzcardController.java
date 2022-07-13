package uz.uzcard.controller.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.uzcard.dto.card.PanDTO;
import uz.uzcard.dto.card.CardDTO;
import uz.uzcard.service.card.UzcardService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/uzcard")
@RequiredArgsConstructor
public class UzcardController {

    private final UzcardService uzcardService;


    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> create() {
        log.info("Create Uzcard");
        return ResponseEntity.ok(uzcardService.create());
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> updateStatus(@RequestBody @Valid PanDTO dto) {
        log.info("Change Status");
        return ResponseEntity.ok(uzcardService.changeStatus(dto));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageImpl<CardDTO>> getInactiveCardByPage(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", defaultValue = "15") int size) {
        log.info("Get All");
        return ResponseEntity.ok(uzcardService.getInactiveCardByPage(page, size));
    }

}
