package uz.uzcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.uzcard.entity.rest.rate.ExchangeRateEntity;
import uz.uzcard.enums.card.BalanceType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    @Query(value = "select * from exchange_rate er " +
            "where cast(er.created_date as date) = ?1 ", nativeQuery = true)
    List<ExchangeRateEntity> findByCreatedDate(LocalDate date);

    @Query(value = "select * from exchange_rate er " +
            "where cast(er.created_date as date) = ?1 and type = ?2", nativeQuery = true)
    Optional<ExchangeRateEntity> findByCreatedDateAndType(LocalDate date, String type);

}