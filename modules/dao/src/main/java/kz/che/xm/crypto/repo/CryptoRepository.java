package kz.che.xm.crypto.repo;

import kz.che.xm.crypto.enity.CryptoRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CryptoRepository extends JpaRepository<CryptoRateEntity, UUID>,
        JpaSpecificationExecutor<CryptoRateEntity> {
}