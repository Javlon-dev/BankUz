package uz.uzcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzcard.entity.BankEntity;

import java.util.Optional;

public interface BankRepository extends JpaRepository<BankEntity, String> {

    Optional<BankEntity> findByMfoCode(String mfoCode);

}