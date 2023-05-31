package anubislab.tech.walletservice.web;

import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import anubislab.tech.walletservice.dtos.AddWalletRequestDTO;
import anubislab.tech.walletservice.entities.Currency;
import anubislab.tech.walletservice.entities.Wallet;
import anubislab.tech.walletservice.entities.WalletTransaction;
import anubislab.tech.walletservice.repositories.CurrencyRepository;
import anubislab.tech.walletservice.repositories.WalletRepository;
import anubislab.tech.walletservice.services.WalletService;

@Controller
public class WalletGraphQLController {

    private WalletRepository walletRepository;
    private WalletService walletService;
    private CurrencyRepository currencyRepository;

    public WalletGraphQLController(WalletRepository walletRepository, 
                                    WalletService walletService,
                                    CurrencyRepository currencyRepository){
        this.walletRepository = walletRepository;
        this.walletService = walletService;
        this.currencyRepository = currencyRepository;
    }

    @QueryMapping
    public List<Wallet> userWallets(){
        return walletRepository.findAll();
    }

    @QueryMapping
    public Wallet walletById(@Argument String id){
        return walletRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException(String.format("Ce portefeuille %s est introuvable",id)));
    }

    @QueryMapping
    public Currency currencyById(@Argument String code){
        return currencyRepository.findById(code).orElseThrow(()-> new RuntimeException(String.format("Ce devise %s est introuvable",code)));
    }

    @QueryMapping
    public List<Currency> currencies(@Argument String code){
        return currencyRepository.findAll(); //findById(code).orElseThrow(()-> new RuntimeException(String.format("Ce devise %s est introuvable",code)));
    }

    @MutationMapping
    public Wallet addWallet(@Argument AddWalletRequestDTO wallet){
        
        return walletService.save(wallet);
    }

    @MutationMapping
    public List<WalletTransaction> walletTransfer(@Argument String source, @Argument String destination, @Argument double amount){
        return walletService.walletTransfer(source, destination, amount);
    }
    
}