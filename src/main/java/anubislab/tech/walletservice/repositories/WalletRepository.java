package anubislab.tech.walletservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import anubislab.tech.walletservice.entities.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    
}
