package uz.uzcard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import uz.uzcard.dto.auth.ProfileDetailDTO;
import uz.uzcard.dto.auth.RegistrationDTO;
import uz.uzcard.dto.profile.ProfileDTO;
import uz.uzcard.entity.profile.ProfileDetailEntity;
import uz.uzcard.entity.profile.ProfileEntity;
import uz.uzcard.exception.AppBadRequestException;
import uz.uzcard.service.profile.ProfileDetailService;
import uz.uzcard.service.profile.ProfileService;
import uz.uzcard.util.JwtUtil;
import uz.uzcard.util.NumberUtil;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfileService profileService;

    private final ProfileDetailService profileDetailService;


    public ProfileDetailDTO sendSms(ProfileDetailDTO dto) {
        String code = NumberUtil.generateRandomSmsCode();

        dto.setSmsCode(code);
        dto.setSms("Your verification code is " + code +
                ", You have 3 minutes to use this code");

        profileDetailService.create(dto);

        return dto;
    }

    public ProfileDTO login(ProfileDetailDTO dto) {

        ProfileDetailEntity entity = profileDetailService.getByPhoneNumberOrNull(dto.getPhoneNumber());

        if (!Optional.ofNullable(entity).isPresent() ||
                !entity.getSmsCode().equals(dto.getSmsCode()) ||
                LocalDateTime.now().isAfter(entity.getUpdatedDate().plusSeconds(180))) {
            log.warn("Bad Request {}", dto);
            throw new AppBadRequestException("Bad Request!, You need a confirmation sms code!");
        }

        Optional<ProfileEntity> optional = profileService.getByPhoneNumber(entity.getPhoneNumber());
        if (optional.isPresent()) {
            ProfileDTO profileDTO = profileService.toDTO(optional.get());
            profileDTO.setToken(JwtUtil.encodeAfterRegistration(profileDTO.getId()));
            return profileDTO;
        }

        return new ProfileDTO(dto.getPhoneNumber(), JwtUtil.encodeBeforeRegistration(String.valueOf(entity.getId())));
    }

    public ProfileDTO registration(RegistrationDTO dto, String token) {

        Optional<ProfileDetailEntity> optional;

        try {
            long id = Long.parseLong(JwtUtil.decode(token).getSubject());
            optional = profileDetailService.findById(id);
        } catch (RuntimeException e) {
            log.warn("Incorrect Token {}", token);
            throw new AppBadRequestException("Incorrect Token!");
        }

        if (!optional.isPresent()) {
            log.warn("Not Login {}", token);
            throw new BadCredentialsException("You're Not Login!" +
                    ", You need a confirmation sms code!");
        }

        ProfileDetailEntity entity = optional.get();

        ProfileDTO profileDTO = profileService.registration(new ProfileDTO(dto.getName(), dto.getSurname(), entity.getPhoneNumber()));
        profileDTO.setToken(JwtUtil.encodeAfterRegistration(profileDTO.getId()));
        return profileDTO;
    }

}
