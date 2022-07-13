package uz.uzcard.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uz.uzcard.entity.profile.ProfileCardEntity;
import uz.uzcard.enums.card.CardStatus;
import uz.uzcard.repository.mapper.ProfileCardInfoMapper;

import java.util.List;
import java.util.Optional;

public interface ProfileCardRepository extends JpaRepository<ProfileCardEntity, Long> {

    @Query(value = "select p.id as p_id, p.name as p_name, p.surname as p_surname, p.phoneNumber as p_phone_number, " +
            "c.id as c_id, c.pan as c_pan, c.maskedPan as c_masked_pan, c.cvvCode as c_cvv_code, " +
            "c.expiredDate as c_expired_date, c.balance as c_balance, c.status as c_status, c.type as c_type, " +
            "b.mfoCode as b_mfo_code " +
            "from ProfileCardEntity pc " +
            "inner join pc.profile p " +
            "inner join pc.card c " +
            "inner join BankEntity b on c.bankId = b.mfoCode " +
            "where p.deletedDate is null " +
            "and c.deletedDate is null " +
            "and p.phoneNumber = :phoneNumber ")
    List<ProfileCardInfoMapper> findByProfilePhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query(value = "select p.id as p_id, p.name as p_name, p.surname as p_surname, p.phoneNumber as p_phone_number, " +
            "c.id as c_id, c.pan as c_pan, c.maskedPan as c_masked_pan, c.cvvCode as c_cvv_code, " +
            "c.expiredDate as c_expired_date, c.balance as c_balance, c.status as c_status, c.type as c_type, " +
            "b.mfoCode as b_mfo_code " +
            "from ProfileCardEntity pc " +
            "inner join pc.profile p " +
            "inner join pc.card c " +
            "inner join BankEntity b on c.bankId = b.mfoCode " +
            "where p.deletedDate is null " +
            "and c.deletedDate is null " +
            "and pc.id = :id ")
    Optional<ProfileCardInfoMapper> findByIdMapper(@Param("id") Long id);

    @Query(value = "select pc " +
            "from ProfileCardEntity pc " +
            "inner join pc.profile p " +
            "inner join pc.card c " +
            "inner join BankEntity b on c.bankId = b.mfoCode " +
            "where p.deletedDate is null " +
            "and c.deletedDate is null " +
            "and p.id = :id " +
            "and c.pan = :pan ")
    Optional<ProfileCardEntity> findByProfileIdAndPan(@Param("id") String id, @Param("pan") String pan);

    @Query(value = "select pc " +
            "from ProfileCardEntity pc " +
            "inner join pc.profile p " +
            "inner join pc.card c " +
            "inner join BankEntity b on c.bankId = b.mfoCode " +
            "where p.deletedDate is null " +
            "and c.deletedDate is null " +
            "and c.pan = :pan ")
    Optional<ProfileCardEntity> findByPan(@Param("pan") String pan);

    @Modifying
    @Transactional
    @Query(value = "update CardEntity set balance = balance + :amount where deletedDate is null and pan = :pan")
    void plusAmount(@Param("pan") String pan, @Param("amount") Long amount);

    @Modifying
    @Transactional
    @Query(value = "update CardEntity set balance = balance - :amount where deletedDate is null and pan = :pan")
    void minusAmount(@Param("pan") String pan, @Param("amount") Long amount);

    @Modifying
    @Transactional
    @Query(value = "update card set status = :status where deleted_date is null and id in " +
            "(select c.id from profile_card pc " +
            "   inner join profile p on pc.profile_id = p.id " +
            "   inner join card c on pc.card_id = c.id " +
            "   inner join bank b on c.bank_id = b.mfo_code " +
            "       where p.deleted_date is null  " +
            "           and c.deleted_date is null   " +
            "           and c.status = :oldStatus    " +
            "           and c.expired_date = :date );",
            nativeQuery = true)
    void checkExpiredDate(@Param("status") String status, @Param("date") String date, @Param("oldStatus") String oldStatus);


    @Modifying
    @Transactional
    @Query(value = "update CardEntity set status = :status where deletedDate is null and status = 'ACTIVE' and balance < 0")
    void checkBalanceForMinus(@Param("status") CardStatus status);

    @Modifying
    @Transactional
    @Query(value = "update CardEntity set status = :status where deletedDate is null and status = 'NOT_ALLOWED' and balance >= 0")
    void checkBalanceForPlus(@Param("status") CardStatus status);

}