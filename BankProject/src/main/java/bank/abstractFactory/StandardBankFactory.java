package bank.abstractFactory;

import bank.account.Account;
import bank.account.AccountFactory;

/** Factory concreta per i conti standard */
public class StandardBankFactory implements BankFactory {

    @Override
    public Account createCheckingAccount(String accountNumber, String ownerName, double initialBalance) {
        // Delega alla factory esistente - non tocchiamo il codice originale!
        return AccountFactory.createAccount(
                AccountFactory.AccountType.CHECKING,
                accountNumber,
                ownerName,
                initialBalance);
    }

    @Override
    public Account createSavingsAccount(String accountNumber, String ownerName, double initialBalance) {
        // Delega alla factory esistente - non tocchiamo il codice originale!
        return AccountFactory.createAccount(
                AccountFactory.AccountType.SAVINGS,
                accountNumber,
                ownerName,
                initialBalance);
    }
}
