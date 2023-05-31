package anubislab.tech.walletservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Country {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String countryName;
    private int m49Code;
    private String isoCode;
    @ManyToOne
    private Continent continent;
    @ManyToOne
    private Currency currency;
    private double longitude;
    private double latitude;
    private double altitude;
}
