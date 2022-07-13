package uz.uzcard.config.details;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.uzcard.entity.profile.ProfileEntity;

public class EntityDetails {

    private static CustomProfileDetails getEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomProfileDetails) authentication.getPrincipal();
    }

    public static ProfileEntity getProfile() {
        return getEntity().getProfile();
    }

}
