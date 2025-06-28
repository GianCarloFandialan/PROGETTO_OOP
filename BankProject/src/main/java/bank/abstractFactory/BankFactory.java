package bank.abstractFactory;

import bank.account.Account;

// Interfaccia per l'Abstract Factory 
public interface BankFactory {

    Account createCheckingAccount(String accountNumber, String ownerName, double initialBalance);

    Account createSavingsAccount(String accountNumber, String ownerName, double initialBalance);
}
