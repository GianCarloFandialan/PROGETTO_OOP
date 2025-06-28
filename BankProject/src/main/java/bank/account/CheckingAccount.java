package bank.account;

import java.util.logging.Logger;

/**
 * Classe rappresentante un conto corrente che estende Account
 * Si può prelevare più denaro di quello che si ha, fino a un limite
 * prestabilito(fido)
 * 
 * Ogni operazione viene registrata con il livello appropriato:
 * - INFO: operazioni normali riuscite
 * - WARNING: tentativi di prelievo che sfiorano i limiti
 * - SEVERE: errori gravi o superamenti dei limiti
 */
public class CheckingAccount extends Account {

    /**
     * Importo massimo che la banca permette di prelevare oltre il saldo
     * disponibile(fido)
     */
    private double overdraftLimit;

    /** logger statico */
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    /**
     * Costruttore - Costruisce un nuovo conto corrente con il numero di conto
     * specificato, il proprietario del conto ed il saldo iniziale
     * 
     * @param accountNumber  L’identificatore univoco per questo conto
     * @param ownerName      Il proprietario del conto
     * @param initialBalance Il saldo iniziale per questo conto(Se negativo si
     *                       considera 0)
     */
    public CheckingAccount(String accountNumber, String ownerName, double initialBalance) {
        super(accountNumber, ownerName, initialBalance);
        this.overdraftLimit = 100.0;
        logger.info("Conto Corrente inizializzato: " + accountNumber + " con limite scoperto di €" + overdraftLimit);
    }

    /**
     * Modalità di prelievo specifico per il conto corrente
     * 
     * @param amount - importo richiesto per il prelievo(deve essere positivo)
     * @return boolean - Valore di ritorno che comunica l'esito dell'operazione
     */
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            logWarning("Tentativo di prelievo con importo non valido: €" + amount);
            System.out.println("Importo non valido per il prelievo");
            return false;
        }

        double totalAvailable = balance + overdraftLimit;

        // Stato del conto prima del prelievo
        logger.info("Richiesta prelievo di €" + amount + " su account " + getAccountNumber() + " (saldo: €" + balance
                + ", fido: €" + overdraftLimit + ", totale disponibile: €" + totalAvailable + ")");

        if (totalAvailable >= amount) {
            double previousBalance = balance;
            balance -= amount;

            // Si determina se il prelievo è normale o usa il fido
            if (previousBalance >= amount) {
                // Prelievo normale senza usare fido
                logger.info("Prelievo normale eseguito: €" + amount + " (saldo: €" + previousBalance + " → €" + balance
                        + ")");
            } else {
                // Prelievo che utilizza il fido
                double overdraftUsed = amount - previousBalance;
                logWarning("Prelievo con fido eseguito: €" + amount + " (scoperto utilizzato: €" + overdraftUsed
                        + ", saldo: €" + previousBalance + " → €" + balance + ")");
            }

            // Registriamo l'operazione anche nel sistema base
            logOperation("Prelievo di €" + amount + " eseguito");

            return true;

        } else {
            double shortfall = amount - totalAvailable;

            logWarning("Prelievo rifiutato per fondi insufficienti: richiesti €" + amount + ", disponibili €"
                    + totalAvailable + ", mancanti €" + shortfall);

            System.out.println("Fondi insufficienti");
            return false;
        }
    }

    /**
     * Restituisce il tipo di conto
     * 
     * @return "Conto Corrente" per tutte le istanze di CheckingAccount
     */
    @Override
    public String getAccountType() {
        return "Conto Corrente";
    }

    /**
     * Restituisce l'importo di fido massimo.
     * 
     * @return overdraftLimit - Il limite fido
     */
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    /**
     * Restituisce l'importo di fido attualmente utilizzato.
     * 
     * @return double - L'importo di fido attualmente in uso (0 se il saldo è
     *         positivo)
     */
    public double getOverdraftUsed() {
        return balance < 0 ? Math.abs(balance) : 0;
    }

    /**
     * Restituisce i fondi totali disponibili.
     * 
     * @return double - La somma di saldo + l'importo di fido
     */
    public double getTotalAvailableFunds() {
        return balance + overdraftLimit;
    }

}