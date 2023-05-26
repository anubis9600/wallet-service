package anubislab.tech.walletservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import anubislab.tech.walletservice.entities.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, String> {

    @Query("select c.code from Currency c")
    String[] findAllCurrenciesCodes();
    
}
