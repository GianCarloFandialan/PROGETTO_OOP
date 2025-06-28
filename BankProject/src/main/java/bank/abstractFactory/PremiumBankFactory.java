package bank.abstractFactory;

import bank.account.Account;
import bank.account.AccountFactory;
import bank.account.CheckingAccount;
import bank.account.SavingsAccount;

/** Factory concreta per i conti premium */
public class PremiumBankFactory implements BankFactory {

    @Override
    public Account createCheckingAccount(String accountNumber, String ownerName, double initialBalance) {
        // Usa la factory esistente ma aggiunge logica premium
        CheckingAccount account = (CheckingAccount) AccountFactory.createAccount(
                AccountFactory.AccountType.CHECKING,
                accountNumber,
                ownerName,
                initialBalance);

        // I clienti premium non vedono differenze immediate,
        // ma internamente potrebbero avere trattamenti diversi
        System.out.println("Account Premium creato con privilegi aggiuntivi");
        return account;
    }

    @Override
    public Account createSavingsAccount(String accountNumber, String ownerName, double initialBalance) {
        // Usa la factory esistente ma aggiunge logica premium
        SavingsAccount account = (SavingsAccount) AccountFactory.createAccount(
                AccountFactory.AccountType.SAVINGS,
                accountNumber,
                ownerName,
                initialBalance);

        System.out.println("Conto Risparmio Premium creato con tasso preferenziale");
        return account;
    }
}