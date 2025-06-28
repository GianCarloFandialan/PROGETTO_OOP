package bank.observerPattern;

import java.util.logging.Logger;

/**
 * Classe dell'observer concreto che stampa informazioni sulle transazioni.
 */
public class SimpleTransactionObserver implements TransactionObserver {

    private static final Logger logger = Logger.getLogger(SimpleTransactionObserver.class.getName());

    @Override
    public void onTransaction(String accountNumber, String operation, double amount, boolean success) {

        String status = success ? "✓" : "✗";
        String message = String.format("%s %s di €%.2f su conto %s",
                status, operation.toUpperCase(), amount, accountNumber);

        System.out.println("Observer: " + message);
        logger.info("Transaction observed: " + message);
    }
}
