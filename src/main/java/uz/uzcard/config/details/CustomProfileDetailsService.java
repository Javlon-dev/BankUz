package uz.uzcard.config.details;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import uz.uzcard.entity.profile.ProfileEntity;
import uz.uzcard.enums.profile.ProfileStatus;
import uz.uzcard.repository.profile.ProfileRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomProfileDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    @Override
    public CustomProfileDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        ProfileEntity profileEntity = profileRepository
                .findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> {
                    log.warn("Profile Not Found {}", id);
                    return new UsernameNotFoundException("Profile Not Found!");
                });

        if (profileEntity.getStatus().equals(ProfileStatus.BLOCK)) {
            log.warn("Profile Blocked {}", id);
            throw new BadCredentialsException("Profile Blocked!");
        }

        return new CustomProfileDetails(profileEntity);
    }

}
