package anubislab.tech.walletservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import anubislab.tech.walletservice.entities.WalletTransaction;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, String> {
    
}