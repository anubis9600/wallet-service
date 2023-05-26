package anubislab.tech.walletservice.services;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import anubislab.tech.walletservice.dtos.AddWalletRequestDTO;
import anubislab.tech.walletservice.entities.Currency;
import anubislab.tech.walletservice.entities.Wallet;
import anubislab.tech.walletservice.entities.WalletTransaction;
import anubislab.tech.walletservice.enums.TransactionType;
import anubislab.tech.walletservice.repositories.CurrencyRepository;
import anubislab.tech.walletservice.repositories.WalletRepository;
import anubislab.tech.walletservice.repositories.WalletTransactionRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class WalletService {

    private CurrencyRepository currencyRepository;
    private WalletRepository walletRepository;
    private WalletTransactionRepository walletTransactionRepository;
    

    public WalletService(CurrencyRepository currencyRepository,
                            WalletRepository walletRepository,
                            WalletTransactionRepository walletTransactionRepository){

        this.currencyRepository = currencyRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;

    }

    public Wallet save(AddWalletRequestDTO walletDTO){
        Currency currency = currencyRepository.findById(walletDTO.currencyCode()).orElseThrow(()-> new RuntimeException(String.format("identifiant %s non trouve ", walletDTO.currencyCode())));
        Wallet wallet = Wallet.builder()
                .balance(walletDTO.balance())
                .id(UUID.randomUUID().toString())
                .createdAt(System.currentTimeMillis())
                .userId("user1")
                .currency(currency)
                .build();

        return walletRepository.save(wallet);
    }

    public void loadData() throws IOException{
        URI uri = new ClassPathResource("currencies.data.csv").getURI();
        Path path = Paths.get(uri);

        List<String> lines = Files.readAllLines(path);
        for (int i = 1; i < lines.size(); i++) {
            String[] line = lines.get(i).split(";");

            Currency currency = Currency.builder()
                        .code(line[0])
                        .name(line[1])
                        .salePrice(Double.parseDouble(line[2]))
                        .purchasePrice(Double.parseDouble(line[3]))
                        .build();
            currencyRepository.save(currency);
        }
        String[] curenciesCode = currencyRepository.findAllCurrenciesCodes();
        Stream.of(curenciesCode).forEach(c->{
            Currency currency = currencyRepository.findById(c).orElseThrow(()-> new RuntimeException(String.format("Ce code n'Existe pas", c)));

            Wallet wallet = new Wallet();
            wallet.setId(UUID.randomUUID().toString());
            wallet.setBalance(Math.random()*10254);
            wallet.setCreatedAt(System.currentTimeMillis());
            wallet.setUserId("user1");
            wallet.setCurrency(currency);
            walletRepository.save(wallet);
        });
        walletRepository.findAll().forEach(w->{
            for (int i = 0; i < 2; i++) {
                WalletTransaction debitWalletTransaction = WalletTransaction.builder()
                                .timestamp(System.currentTimeMillis())
                                .amount(Math.random()*514)
                                .wallet(w)
                                .type(TransactionType.DEBIT)
                                // .type(Math.random() > 0.5? TransactionType.DEBIT:TransactionType.DEBIT)
                                .build();
                walletTransactionRepository.save(debitWalletTransaction);
                w.setBalance(w.getBalance()-debitWalletTransaction.getAmount());

                WalletTransaction crebitWalletTransaction = WalletTransaction.builder()
                                .timestamp(System.currentTimeMillis())
                                .amount(Math.random()*514)
                                .wallet(w)
                                .type(TransactionType.CREDIT)
                                .build();
                walletTransactionRepository.save(crebitWalletTransaction);
                w.setBalance(w.getBalance()+crebitWalletTransaction.getAmount());

                walletRepository.save(w);
            }

        });
    }
}