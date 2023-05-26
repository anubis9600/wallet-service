package anubislab.tech.walletservice.dtos;

public record AddWalletRequestDTO(
    Double balance,
    String currencyCode
) {}
