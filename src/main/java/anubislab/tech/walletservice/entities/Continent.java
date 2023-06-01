package anubislab.tech.walletservice.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Continent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String continentName;
    @OneToMany(mappedBy = "continent")
    private List<Country> countries;
}