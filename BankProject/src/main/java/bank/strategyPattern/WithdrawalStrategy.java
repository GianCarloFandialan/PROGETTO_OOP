package bank.strategyPattern;

/**
 * interfaccia strategy per il prevlievo
 */
public interface WithdrawalStrategy {

    /**
     * Esegue un prelievo secondo la strategia specifica implementata.
     * 
     * @param currentBalance  il saldo attuale dell'account
     * @param requestedAmount l'importo che si vuole prelevare
     * @param accountNumber   numero dell'account per logging
     * @return WithdrawalResult risultato dell'operazione con dettagli
     */
    boolean executeWithdrawal(double[] currentBalance, double requestedAmount, String accountNumber);
}
