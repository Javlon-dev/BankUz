package uz.uzcard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.uzcard.config.details.EntityDetails;
import uz.uzcard.dto.BankDTO;
import uz.uzcard.entity.BankEntity;
import uz.uzcard.entity.profile.ProfileEntity;
import uz.uzcard.exception.ItemAlreadyExistsException;
import uz.uzcard.exception.ItemNotFoundException;
import uz.uzcard.repository.BankRepository;
import uz.uzcard.service.profile.ProfileService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    private final ProfileService profileService;


    public BankDTO add(BankDTO dto) {

        ProfileEntity profile = EntityDetails.getProfile();

        Optional<BankEntity> optional = getByMfoCodeOptional(dto.getMfoCode());

        if (optional.isPresent()) {
            log.warn("Bank Mfo Code Exists {}", dto.getMfoCode());
            throw new ItemAlreadyExistsException("Bank Mfo Code Exists!");
        }

        BankEntity entity = new BankEntity();
        entity.setMfoCode(dto.getMfoCode());
        entity.setProfileId(profile.getPhoneNumber());

        bankRepository.save(entity);
        entity.setProfile(profile);
        return toDTO(entity);
    }

    public List<BankDTO> getAll() {

        return bankRepository
                .findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public BankEntity getByMfoCodeOrThrow(String mfoCode) {
        return bankRepository
                .findByMfoCode(mfoCode)
                .orElseThrow(() -> {
                    log.warn("Bank Not Found {}", mfoCode);
                    return new ItemNotFoundException("Bank Not Found!");
                });
    }

    public Optional<BankEntity> getByMfoCodeOptional(String mfoCode) {
        return bankRepository
                .findByMfoCode(mfoCode);
    }

    public BankDTO toDTO(BankEntity entity) {
        BankDTO dto = new BankDTO();
        dto.setId(entity.getId());
        dto.setMfoCode(entity.getMfoCode());
        dto.setCreatedDate(entity.getCreatedDate());

        dto.setProfile(profileService.toDTO(entity.getProfile()));

        return dto;
    }

}
