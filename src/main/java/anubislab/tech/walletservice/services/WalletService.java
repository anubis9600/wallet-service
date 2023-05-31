package anubislab.tech.walletservice.services;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
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
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class WalletService {

    private CurrencyRepository currencyRepository;
    private WalletRepository walletRepository;
    private WalletRepository walletRepository2;
    private WalletTransactionRepository walletTransactionRepository;
    

    public WalletService(CurrencyRepository currencyRepository,
                            WalletRepository walletRepository,
                            WalletTransactionRepository walletTransactionRepository,
                            WalletRepository walletRepository2){

        this.currencyRepository = currencyRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletRepository2 = walletRepository2;

    }

    public Wallet save(AddWalletRequestDTO walletDTO){
        Currency currency = currencyRepository.findById(walletDTO.currencyCode()).orElseThrow(()-> new RuntimeException(String.format("La devise %s est introuvable", walletDTO.currencyCode())));
        
        Wallet wallet = Wallet.builder()
                .balance(walletDTO.balance())
                .id(UUID.randomUUID().toString())
                .createdAt(System.currentTimeMillis())
                .userId("user1")
                .currency(currency)
                .build();

        return walletRepository.save(wallet);
    }

    public List<WalletTransaction> walletTransfer(String sourceWalletid, String destinationWalletId, Double amount){
        Wallet sourceWallet = walletRepository.findById(sourceWalletid).orElseThrow(()-> new RuntimeException(String.format("Ce wallet est introuvable")));
        Wallet destinationWallet = walletRepository2.findById(destinationWalletId).orElseThrow(()-> new RuntimeException(String.format("Ce wallet de destination est introuvable")));
        
        if(sourceWallet.getBalance() < amount) throw new RuntimeException(String.format("Le montant est insuffisant"));

        WalletTransaction sourceWalletTransaction = WalletTransaction.builder()
                .timestamp(System.currentTimeMillis())
                .amount(amount)
                .currentSaleCurrencyPrice(sourceWallet.getCurrency().getSalePrice())
                .currentPurchaseCurrencyPrice(sourceWallet.getCurrency().getPurchasePrice())
                .wallet(destinationWallet)
                .type(TransactionType.DEBIT)
                .build();

        walletTransactionRepository.save(sourceWalletTransaction);
        sourceWallet.setBalance(sourceWallet.getBalance() - amount);
        walletRepository.save(sourceWallet);

        double convertedAmount=amount*(sourceWallet.getCurrency().getSalePrice()/destinationWallet.getCurrency().getPurchasePrice());
        Double isNaN = Double.isNaN(convertedAmount)?amount:convertedAmount;
        // log.info(":::::::::: AMOUNT INITIAL :::::::::: "+amount);
        // log.info(":::::::::: CONVERTED AMOUNT :::::::::: "+convertedAmount);
        // log.info(":::::::::: IS NAN :::::::::: "+isNaN);
        WalletTransaction destinataionWalletTransaction = WalletTransaction.builder()
                .timestamp(System.currentTimeMillis())
                .amount(isNaN)
                .currentSaleCurrencyPrice(destinationWallet.getCurrency().getSalePrice())
                .currentPurchaseCurrencyPrice(destinationWallet.getCurrency().getPurchasePrice())
                .wallet(destinationWallet)
                .type(TransactionType.CREDIT)
                .build();

        walletTransactionRepository.save(destinataionWalletTransaction);
        destinationWallet.setBalance(isNaN + destinationWallet.getBalance());
        walletRepository2.save(destinationWallet);

        return Arrays.asList(sourceWalletTransaction, destinataionWalletTransaction);
    }

    @Bean
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