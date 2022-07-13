package uz.uzcard.controller.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.uzcard.dto.profile.ProfileBioDTO;
import uz.uzcard.dto.profile.ProfileDTO;
import uz.uzcard.dto.profile.ProfilePhoneNumberDTO;
import uz.uzcard.service.profile.ProfileService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;


    @GetMapping()
    @PreAuthorize("hasAnyRole('PROFILE','ADMIN')")
    public ResponseEntity<ProfileDTO> getProfile() {
        log.info("Get Profile");
        return ResponseEntity.ok(profileService.getProfile());
    }

    @PutMapping()
    @PreAuthorize("hasRole('PROFILE')")
    public ResponseEntity<ProfileDTO> updatePhone(@RequestBody @Valid ProfilePhoneNumberDTO dto) {
        log.info("Update Phone {}", dto);
        return ResponseEntity.ok(profileService.updatePhone(dto));
    }

    @PutMapping("/bio")
    @PreAuthorize("hasAnyRole('PROFILE','ADMIN')")
    public ResponseEntity<ProfileDTO> updateBio(@RequestBody @Valid ProfileBioDTO dto) {
        log.info("Update Bio {}", dto);
        return ResponseEntity.ok(profileService.updateBio(dto));
    }

    @PutMapping("/status/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileDTO> changeStatus(@PathVariable String profileId) {
        log.info("Change Status {}", profileId);
        return ResponseEntity.ok(profileService.changeStatus(profileId));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('PROFILE')")
    public ResponseEntity<Boolean> delete() {
        log.info("Delete Account");
        return ResponseEntity.ok(profileService.delete());
    }


}
