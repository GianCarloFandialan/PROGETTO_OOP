package bank.account;

import java.util.logging.Logger;

/**
 * Classe base di ogni conto bancario
 * 
 * * Ogni operazione viene registrata con il livello appropriato:
 * - INFO: operazioni normali riuscite
 * - WARNING: tentativi di prelievo che sfiorano i limiti
 * - SEVERE: errori gravi o superamenti dei limiti
 */
public abstract class Account {
    /** L’identificatore univoco per questo conto. */
    protected String accountNumber;

    /** Il proprietario del conto */
    protected String ownerName;

    /** Il saldo di questo conto. */
    protected double balance;

    /** logger statico */
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    /**
     * Costruttore - Costruisce un nuovo conto bancario con il numero di conto
     * specificato, il proprietario del conto ed il saldo iniziale.
     * 
     * @param accountNumber  L’identificatore univoco per questo conto.
     * @param ownerName      Il proprietario del conto
     * @param initialBalance Il saldo iniziale per questo conto.(Se negativo si
     *                       considera 0)
     */
    public Account(String accountNumber, String ownerName, double initialBalance) {
        this.accountNumber = clearInput(accountNumber);
        this.ownerName = clearInput(ownerName);

        if (initialBalance < 0) {
            logWarning("Tentativo di creare account con saldo negativo: " + initialBalance);
            throw new IllegalArgumentException("Il saldo iniziale non può essere negativo");
        }

        this.balance = Math.max(0, initialBalance);

        logger.info("Account creato: " + this.accountNumber + " per " + this.ownerName + " con saldo iniziale "
                + this.balance);
    }

    /**
     * "Pulisce" le stringhe che arrivano dall'esterno per evitare possibili errori
     * e segnala i caratteri rimossi
     * 
     * @param input Stringa in input da "ripulire"
     * @return Stringa "ripulita"
     */
    private String clearInput(String input) {
        if (input == null) {
            // Se l'input è null, registriamo un warning e restituiamo una stringa vuota
            logger.warning("Input null rilevato durante la sanificazione");
            return "";
        }

        String cleaned = input.replaceAll("[<>\"'%;()&+]", "");

        String result = cleaned.trim();

        if (!input.equals(result)) {
            logger.warning("Caratteri pericolosi rimossi dall'input: '" + input + "' -> '" + result + "'");
        }

        return result;
    }

    /**
     * Metodo astratto per prelevare che ogni tipo di conto deve implementare
     * 
     * @param amount Importo da prelevare
     * @return
     */
    public abstract boolean withdraw(double amount);

    /**
     * Il metodo per depositare uguale per tutti i tipi di conto
     * 
     * @param amount Importo da depositare
     * @return boolean - Valore di ritorno che comunica l'esito dell'operazione
     */
    public boolean deposit(double amount) {
        if (amount <= 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
            logWarning("Tentativo di deposito con importo non valido: " + amount);
            return false;
        }

        if (balance + amount < balance) {
            logError("Tentativo di deposito che causerebbe overflow: balance=" + balance + ", amount=" + amount);
            throw new ArithmeticException("Il deposito causerebbe un overflow del saldo");
        }

        double previousBalance = balance;
        balance += amount;

        logger.info("Deposito eseguito su account " + accountNumber + ": €" + amount + " (saldo: €" + previousBalance
                + " → €" + balance + ")");
        return true;
    }

    /**
     * Scrive un messaggio di log per tenere traccia delle operazioni.
     * Accessibile alle classi figlie ma non accessibile dall'esterno.
     * 
     * @param message Messaggio contenuto nel log
     * @return
     */
    protected void logOperation(String message) {
        logger.info(accountNumber + " - " + message);
    }

    /**
     * Scrive un messaggio di log per tenere traccia delle operazioni sospette o
     * problematiche.
     * Accessibile alle classi figlie ma non accessibile dall'esterno.
     * 
     * @param message Messaggio contenuto nel log
     */
    protected void logWarning(String message) {
        logger.warning(accountNumber + " - " + message);
    }

    /**
     * Scrive un messaggio di log per tenere traccia delle operazioni con errori
     * 
     * @param message Messaggio contenuto nel log
     */
    protected void logError(String message) {
        logger.severe(accountNumber + " - " + message);
    }

    /**
     * Restituisce il numero identificativo di questo conto.
     * 
     * @return accountNumber - Il numero del conto
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Restituisce il nome del proprietario di questo conto.
     * 
     * @return ownerName - Il nome del proprietario del conto
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Restituisce il saldo di questo conto.
     * 
     * @return balance - Il saldo attuale del conto
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Metodo per restituire il tipo specifico di conto.
     */
    public abstract String getAccountType();
}