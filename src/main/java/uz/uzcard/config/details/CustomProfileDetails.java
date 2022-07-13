package uz.uzcard.config.details;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.uzcard.entity.profile.ProfileEntity;
import uz.uzcard.enums.profile.ProfileStatus;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class CustomProfileDetails implements UserDetails {

    private ProfileEntity profileEntity;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(profileEntity.getRole().name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return profileEntity.getPhoneNumber();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return profileEntity.getStatus().equals(ProfileStatus.ACTIVE);
    }

    public ProfileEntity getProfile() {
        return profileEntity;
    }

}
