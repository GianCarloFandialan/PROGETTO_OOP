package bank.observerPattern;

/**
 * Interfaccia per implementare l'observer Pattern
 */
public interface TransactionObserver {

    void onTransaction(String accountNumber, String operation, double amount, boolean success);
}
