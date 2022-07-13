package uz.uzcard.controller.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.uzcard.dto.card.CardDTO;
import uz.uzcard.dto.card.PanDTO;
import uz.uzcard.service.card.HumoCardService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/humo-card")
@RequiredArgsConstructor
public class HumoCardController {

    private final HumoCardService humoCardService;


    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> create() {
        log.info("Create Humo Card");
        return ResponseEntity.ok(humoCardService.create());
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> updateStatus(@RequestBody @Valid PanDTO dto) {
        log.info("Change Status");
        return ResponseEntity.ok(humoCardService.changeStatus(dto));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageImpl<CardDTO>> getInactiveCardByPage(@RequestParam("page") int page,
                                                                   @RequestParam("size") int size) {
        log.info("Get All");
        return ResponseEntity.ok(humoCardService.getInactiveCardByPage(page, size));
    }

}
