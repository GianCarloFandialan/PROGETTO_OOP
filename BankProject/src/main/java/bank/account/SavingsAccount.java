package bank.account;

import java.util.logging.Logger;

/**
 * Classe rappresentante un conto di risparmio che estende Account
 * 
 * Ogni operazione viene registrata con il livello appropriato:
 * - INFO: operazioni normali riuscite
 * - WARNING: tentativi di prelievo che sfiorano i limiti
 * - SEVERE: errori gravi o superamenti dei limiti
 */
public class SavingsAccount extends Account {
    /** Tasso di interesse annuale applicato al saldo */
    private double interestRate;

    /** logger statico */
    private static final Logger logger = Logger.getLogger(SavingsAccount.class.getName());

    /**
     * Costruttore - Costruisce un nuovo conto risparmio con il numero di conto
     * 
     * @param accountNumber  L’identificatore univoco per questo conto
     * @param ownerName      Il proprietario del conto
     * @param initialBalance Il saldo iniziale per questo conto(Se negativo si
     *                       considera 0)
     */
    public SavingsAccount(String accountNumber, String ownerName, double initialBalance) {
        super(accountNumber, ownerName, initialBalance);
        this.interestRate = 0.02;
        logger.info("Conto Risparmio inizializzato: " + accountNumber + " con tasso interesse annuale del "
                + (interestRate * 100) + "%");
    }

    /**
     * Modalità di prelievo specifico per il conto risparmio
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

        // I conti di risparmio non permettono scoperto
        if (balance >= amount) {
            balance -= amount;
            logOperation("Prelievo di " + amount + " eseguito");
            return true;
        } else {
            logWarning("Prelievo rifiutato per fondi insufficienti: richiesti €" + amount + ", disponibili €"
                    + balance);
            System.out.println("Fondi insufficienti");
            return false;
        }
    }

    /**
     * Metodo per calcolare e aggiungere interessi
     */
    public void addInterest() {
        double interest = balance * interestRate;
        balance += interest;
        logOperation("Interessi di " + interest + " aggiunti");
    }

    /**
     * Restituisce il tipo di conto
     * 
     * @return "Conto Risparmio" per tutte le istanze di SavingsAccount
     */
    @Override
    public String getAccountType() {
        return "Conto Risparmio";
    }
}