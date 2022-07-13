package uz.uzcard.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzcard.dto.auth.ProfileDetailDTO;
import uz.uzcard.dto.auth.RegistrationDTO;
import uz.uzcard.dto.profile.ProfileDTO;
import uz.uzcard.service.AuthService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/send-sms")
    public ResponseEntity<ProfileDetailDTO> sendSms(@RequestBody @Valid ProfileDetailDTO dto) {
        log.info("Send Sms {}", dto);
        return ResponseEntity.ok(authService.sendSms(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<ProfileDTO> login(@RequestBody @Valid ProfileDetailDTO dto) {
        log.info("Login {}", dto);
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/registration/{token}")
    public ResponseEntity<ProfileDTO> registration(@RequestBody @Valid RegistrationDTO dto,
                                                   @PathVariable("token") String token) {
        log.info("Registration {}", dto);
        return ResponseEntity.ok(authService.registration(dto, token));
    }

}
