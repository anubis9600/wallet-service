package anubislab.tech.walletservice.web;

import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import anubislab.tech.walletservice.dtos.AddWalletRequestDTO;
import anubislab.tech.walletservice.entities.Wallet;
import anubislab.tech.walletservice.repositories.WalletRepository;
import anubislab.tech.walletservice.services.WalletService;

@Controller
public class WalletGraphQLController {

    private WalletRepository walletRepository;
    private WalletService walletService;

    public WalletGraphQLController(WalletRepository walletRepository, 
                                    WalletService walletService){
        this.walletRepository = walletRepository;
        this.walletService = walletService;
    }

    @QueryMapping
    public List<Wallet> userWallets(){
        return walletRepository.findAll();
    }

    @QueryMapping
    public Wallet walletById(@Argument String id){
        return walletRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException(String.format("identifiant %s non trouve ",id)));
    }

    @MutationMapping
    public Wallet addWallet(@Argument AddWalletRequestDTO walletRequestDTO){
        return walletService.save(walletRequestDTO);
    }
    
}