package bank.account;

import java.util.*;
import java.util.logging.Logger;

/**
 * Questa classe implementa un sistema per navigare attraverso collezioni di
 * conti
 */
public class AccountIterator implements Iterator<Account> {
    /** ID univoco per distinguere questo iterator nei log */
    private final String iteratorId;

    /** lista di tutti i conti attraverso cui si naviga */
    private List<Account> accounts;

    /** poszione del conto attuale */
    private int currentPosition;

    /** Contatore per generare ID univoci */
    private static int iteratorCounter = 0;

    /** logger statico */
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    public AccountIterator(List<Account> accounts) {
        // ID univoco per questo iterator
        iteratorCounter++;
        this.iteratorId = "ITER_" + iteratorCounter;

        if (accounts != null) {
            this.accounts = new ArrayList<>(accounts);
            logger.info("Iterator " + iteratorId + " creato con " + accounts.size() + " account");
        } else {
            this.accounts = new ArrayList<>();
            logger.warning("Iterator " + iteratorId + " creato con lista null - inizializzato come vuoto");
        }

        this.currentPosition = 0;
    }

    /**
     * Metodo che verifica se ci sono ancora conti da vedere nella lista.
     * 
     * @return hasMore - true se ci sono ancora conti, false se abbiamo finito
     */
    @Override
    public boolean hasNext() {
        boolean hasMore = currentPosition < accounts.size();

        if (!hasMore && currentPosition > 0) {
            logger.info("Iterator " + iteratorId + " ha raggiunto la fine - " +
                    currentPosition + " conti processati");
        }

        return hasMore;
    }

    /**
     * Metodo che restituisce il conto successivo nella lista e avanza la posizione.
     * 
     * @return Account conto nella posizione corrente
     * @throws NoSuchElementException - Se non ci sono più conti da restituire
     */
    @Override
    public Account next() {
        if (!hasNext()) {
            logger.severe("Errore: next() chiamato su iterator " + iteratorId + " esaurito");
            throw new NoSuchElementException("Non ci sono più conti da iterare");
        }

        Account currentAccount = accounts.get(currentPosition);

        logger.info("Iterator " + iteratorId + " - accesso conto #" + (currentPosition + 1) +
                ": " + currentAccount.getAccountNumber() + " (" + currentAccount.getAccountType() + ")");

        // Incrementiamo la posizione per la prossima chiamata
        currentPosition++;

        return currentAccount;
    }

    /**
     * Metodo dche mostra solo conti di un tipo specifico.
     * 
     * @param accounts    lista completa di conti da filtrare
     * @param accountType tipo di conto che vogliamo vedere
     * @return AccountIterator - nuovo iterator che mostra solo i conti del
     *         tipo specificato
     */
    public static AccountIterator createFilteredIterator(List<Account> accounts, String accountType) {
        List<Account> filteredAccounts = new ArrayList<>();

        if (accounts != null) {
            int totalProcessed = 0;
            int matchingAccounts = 0;

            // si esamina ogni conto nella lista originale
            for (Account account : accounts) {
                totalProcessed++;

                // Controlliamo se il tipo di questo conto corrisponde a quello che cerchiamo
                if (account.getAccountType().equals(accountType)) {
                    filteredAccounts.add(account);
                    matchingAccounts++;
                }
            }

            // Registriamo i risultati del filtering
            logger.info("Filtering completato: " + matchingAccounts + "/" + totalProcessed +
                    " conti selezionati per tipo '" + accountType + "'");
        }

        // Creiamo e restituiamo un nuovo iterator che lavora solo sulla lista filtrata
        return new AccountIterator(filteredAccounts);
    }
}