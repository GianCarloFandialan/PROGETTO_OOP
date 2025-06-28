package bank.mementoPattern;

import bank.account.Account;

/**
 * classe che implementa il Pattern memento
 */
public class AccountSnapshot {

    /** L’identificatore univoco per questo conto. */
    private final String accountNumber;

    /** Il saldo di questo conto. */
    private final double balance;

    /**
     * Costruttore - costruisce o meglio dire salva lo stato attuale del conto
     * 
     * @param account stato attuale del conto
     */
    public AccountSnapshot(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Non posso fotografare un conto che non esiste!");
        }

        String number = account.getAccountNumber();
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("Conto senza numero non può essere salvato");
        }

        double accountBalance = account.getBalance();
        if (Double.isNaN(accountBalance) || Double.isInfinite(accountBalance)) {
            throw new IllegalArgumentException("Il saldo del conto non è un numero valido");
        }

        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();

        System.out.println("Snapshot creato: " + accountNumber + " con saldo €" + balance);
    }

    /**
     * Restituisce il L’identificatore univoco attuale per questo conto.
     * 
     * @return accountNumber L’identificatore univoco attuale per questo conto.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Restituisce il saldo attuale di questo conto.
     * 
     * @return balance il saldo attuale di questo conto.
     */
    public double getBalance() {
        return balance;
    }
}