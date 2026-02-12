package kz.che.xm.crypto.enity;

import jakarta.persistence.*;
import kz.che.xm.crypto.type.CurrencyType;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.UUID;

@Entity
@Table(name = "crypto_rate")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EntityListeners(value = AuditingEntityListener.class)
public class CryptoRateEntity {
    @Id
    @GeneratedValue(strategy = UUID)
    private UUID id;
    private Long ts;
    @Enumerated(STRING)
    private CurrencyType currency;
    private BigDecimal rate;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CryptoRateEntity entity = (CryptoRateEntity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return CryptoRateEntity.class.hashCode();
    }
}
