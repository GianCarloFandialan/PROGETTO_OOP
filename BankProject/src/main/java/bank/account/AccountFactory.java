package bank.account;

import java.util.logging.Logger;

/**
 * Classe che si occupa di creare diversi tipi di conti bancari.
 */
public class AccountFactory {

    /**
     * Definizione dei tipi di conti che la factory può creare.
     */
    public enum AccountType {
        CHECKING, // Rappresenta un conto corrente
        SAVINGS // Rappresenta un conto di risparmio
    }

    /** logger statico */
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    /**
     * Questo metodo crea conti di diversi tipi.
     * 
     * @param type           tipo di conto da creare (CHECKING o SAVINGS)
     * @param accountNumber  numero identificativo dell'conto
     * @param ownerName      nome completo del proprietario
     * @param initialBalance Il saldo iniziale da depositare
     * 
     * @return Account - Restituisce un nuovo conto del tipo richiesto
     * 
     * @throws IllegalArgumentException - Se i parametri non sono validi
     */
    public static Account createAccount(AccountType type, String accountNumber, String ownerName,
            double initialBalance) {

        // Verifica se il numero del conto è valido
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            logger.severe("Creazione conto fallita: numero conto non valido");
            throw new IllegalArgumentException("Numero conto non valido");
        }

        // Verifica se il nome del proprietario del conto è valido
        if (ownerName == null || ownerName.trim().isEmpty()) {
            logger.severe("Creazione account fallita: nome proprietario non valido");
            throw new IllegalArgumentException("Nome proprietario non valido");
        }

        Account createdAccount = null;

        switch (type) {
            // Crea e restituisce una nuova istanza di CheckingAccount
            case CHECKING:
                logger.info("Creando conto corrente per " + ownerName);
                System.out.println("Creando conto corrente per " + ownerName);
                createdAccount = new CheckingAccount(accountNumber, ownerName, initialBalance);
                break;
            // Crea e restituisce una nuova istanza di SavingsAccount
            case SAVINGS:
                logger.info("Creando conto risparmio per " + ownerName);
                System.out.println("Creando conto risparmio per " + ownerName);
                createdAccount = new SavingsAccount(accountNumber, ownerName, initialBalance);
                break;
            default:
                logger.severe("Tipo di account non supportato: " + type);
                throw new IllegalArgumentException("Tipo di conto non supportato");
        }

        logger.info("Account creato con successo: " + createdAccount.getAccountType() +
                " " + createdAccount.getAccountNumber() + " per " + createdAccount.getOwnerName());

        return createdAccount;
    }
}