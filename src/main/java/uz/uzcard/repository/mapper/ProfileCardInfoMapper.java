package uz.uzcard.repository.mapper;

import uz.uzcard.dto.BankDTO;
import uz.uzcard.dto.card.CardDTO;
import uz.uzcard.dto.profile.ProfileDTO;
import uz.uzcard.enums.card.CardStatus;
import uz.uzcard.enums.card.BalanceType;
import uz.uzcard.util.NumberUtil;

public interface ProfileCardInfoMapper {

    String getP_id();
    String getP_name();
    String getP_surname();
    String getP_phone_number();

    String getC_id();
    String getC_pan();
    String getC_masked_pan();
    String getC_cvv_code();
    String getC_expired_date();
    Long getC_balance();
    CardStatus getC_status();
    BalanceType getC_type();

    String getB_mfo_code();

    default CardDTO toDTO(){
        CardDTO dto = new CardDTO();

        ProfileDTO profile = new ProfileDTO();
        profile.setId(this.getP_id());
        profile.setName(this.getP_name());
        profile.setSurname(this.getP_surname());
        profile.setPhoneNumber(this.getP_phone_number());

        dto.setProfile(profile);

        dto.setId(this.getC_id());
        dto.setPan(this.getC_pan());
        dto.setMaskedPan(this.getC_masked_pan());
        dto.setCvvCode(this.getC_cvv_code());
        dto.setExpiredDate(this.getC_expired_date());
        dto.setCash(NumberUtil.balanceToType(this.getC_balance(), this.getC_type()));
        dto.setStatus(this.getC_status());

        BankDTO bank = new BankDTO();
        bank.setMfoCode(this.getB_mfo_code());

        dto.setBank(bank);

        return dto;
    }

}
