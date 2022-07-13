package uz.uzcard.controller.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.uzcard.dto.card.PanDTO;
import uz.uzcard.dto.card.CardDTO;
import uz.uzcard.dto.card.VisaPanDTO;
import uz.uzcard.service.profile.ProfileCardService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/profile-card")
@RequiredArgsConstructor
public class ProfileCardController {

    private final ProfileCardService profileCardService;


    @PostMapping("/uzcard/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> assignPhoneToUzcard(@RequestBody @Valid PanDTO dto,
                                               @PathVariable String profileId) {
        log.info("Assign Phone To Uzcard {} {}", dto, profileId);
        return ResponseEntity.ok(profileCardService.assignPhoneToUzcard(dto, profileId));
    }

    @PostMapping("/humo/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> assignPhoneToHumoCard(@RequestBody @Valid PanDTO dto,
                                                   @PathVariable String profileId) {
        log.info("Assign Phone To Humo Card {} {}", dto, profileId);
        return ResponseEntity.ok(profileCardService.assignPhoneToHumoCard(dto, profileId));
    }

    @PostMapping("/visa/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> assignPhoneToVisaCard(@RequestBody @Valid VisaPanDTO dto,
                                                   @PathVariable String profileId) {
        log.info("Assign Phone To Visa Card {} {}", dto, profileId);
        return ResponseEntity.ok(profileCardService.assignPhoneToVisaCard(dto, profileId));
    }

    @PutMapping("/uzcard/status")
    @PreAuthorize("hasRole('PROFILE')")
    public ResponseEntity<CardDTO> changeStatusToUzcard(@RequestBody @Valid PanDTO dto) {
        log.info("Change Status To Uzcard {}", dto);
        return ResponseEntity.ok(profileCardService.changeStatusToUzcard(dto));
    }

    @PutMapping("/humo/status")
    @PreAuthorize("hasRole('PROFILE')")
    public ResponseEntity<CardDTO> changeStatusToHumoCard(@RequestBody @Valid PanDTO dto) {
        log.info("Change Status To Humo Card {}", dto);
        return ResponseEntity.ok(profileCardService.changeStatusToHumoCard(dto));
    }

    @PutMapping("/visa/status")
    @PreAuthorize("hasRole('PROFILE')")
    public ResponseEntity<CardDTO> changeStatusToVisaCard(@RequestBody @Valid VisaPanDTO dto) {
        log.info("Change Status To Visa Card {}", dto);
        return ResponseEntity.ok(profileCardService.changeStatusToVisaCard(dto));
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('PROFILE', 'ADMIN')")
    public ResponseEntity<List<CardDTO>> getAllByPhoneNumber() {
        log.info("Get All By PhoneNumber");
        return ResponseEntity.ok(profileCardService.getAllByPhoneNumber());
    }

    @GetMapping("/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardDTO>> getAllByProfileId(@PathVariable String profileId) {
        log.info("Get All By ProfileId");
        return ResponseEntity.ok(profileCardService.getAllByProfileId(profileId));
    }

}
