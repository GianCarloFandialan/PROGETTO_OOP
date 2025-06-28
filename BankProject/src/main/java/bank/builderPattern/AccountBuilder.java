package bank.builderPattern;

import java.util.logging.Logger;

import bank.BankException;
import bank.account.Account;
import bank.account.AccountFactory;

/**
 * classe contenente il Builder pattern per costruire conti bancari
 */
public class AccountBuilder {

    /** Tipo di conto */
    private String accountType;

    /** L’identificatore univoco per questo conto. */
    private String accountNumber;

    /** Il proprietario del conto */
    private String ownerName;

    /** Il saldo iniziale da depositare */
    private double initialBalance;

    /** Booleano per verificare se il conto è premium */
    private boolean isPremium;

    /** logger statico */
    private static final Logger logger = Logger.getLogger(AccountBuilder.class.getName());

    /**
     * Costruttore per implementare il builder pattern (no costruzione diretta)
     */
    private AccountBuilder() {
        this.initialBalance = 0.0; // 0 di default
        this.isPremium = false;
        logger.info("Nuovo AccountBuilder inizializzato");
    }

    /**
     * Metodo factory per iniziare la costruzione
     */
    public static AccountBuilder newAccount() {
        return new AccountBuilder();
    }

    /**
     * Metodo che imposta il tipo di conto
     */
    public AccountBuilder ofType(String type) {
        if (type == null || type.trim().isEmpty()) {
            logger.warning("Tentativo di impostare tipo di conto null o vuoto");
            throw new IllegalArgumentException("Il tipo di conto non può essere vuoto");
        }

        String normalizedType = type.trim().toLowerCase();

        try {
            AccountFactory.AccountType.valueOf(normalizedType.toUpperCase());

            this.accountType = normalizedType;
            logger.info("Tipo conto impostato: " + this.accountType);
            return this;

        } catch (IllegalArgumentException e) {

            logger.warning("Tipo di conto non supportato dal sistema: " + type);

            throw new IllegalArgumentException(
                    "Tipo di conto non valido: '" + type);
        }
    }

    /**
     * Metodo che imposta il numero identificativo del conto
     */
    public AccountBuilder withNumber(String number) {
        if (number == null || number.trim().isEmpty()) {
            logger.warning("Tentativo di impostare numero conto null o vuoto");
            throw new IllegalArgumentException("Il numero di conto non può essere vuoto");
        }

        this.accountNumber = number.trim();
        logger.info("Numero conto impostato: " + this.accountNumber);
        return this;
    }

    /**
     * Metodo che imposta il nome del proprietario del conto
     */
    public AccountBuilder forOwner(String name) {
        if (name == null || name.trim().isEmpty()) {
            logger.warning("Tentativo di impostare nome proprietario null o vuoto");
            throw new IllegalArgumentException("Il nome del proprietario non può essere vuoto");
        }

        this.ownerName = name.trim();
        logger.info("Proprietario del conto impostato: " + this.ownerName);
        return this;
    }

    /**
     * Metodo che imposta il saldo iniziale del conto
     */
    public AccountBuilder withInitialBalance(double balance) {
        if (balance < 0) {
            logger.warning("Tentativo di impostare saldo iniziale negativo: " + balance);
            throw new IllegalArgumentException("Il saldo iniziale non può essere negativo");
        }

        this.initialBalance = balance;
        logger.info("Saldo iniziale impostato: €" + this.initialBalance);
        return this;
    }

    /**
     * Metodo che imposta il conto come premium
     */
    public AccountBuilder asPremium() {
        this.isPremium = true;
        logger.info("Conto configurato come Premium");
        return this;
    }

    /**
     * Metodo che costruisce il conto
     */
    public Account build() throws BankException {

        validateRequiredFields();

        try {

            AccountFactory.AccountType factoryType = AccountFactory.AccountType.valueOf(
                    accountType.toUpperCase());

            // si utilizza il factory già esistente
            Account account = AccountFactory.createAccount(
                    factoryType,
                    accountNumber,
                    ownerName,
                    initialBalance);

            // Log di successo con dettagli della costruzione
            String accountInfo = String.format(
                    "Account costruito con successo: %s %s per %s con saldo €%.2f%s",
                    account.getAccountType(),
                    account.getAccountNumber(),
                    account.getOwnerName(),
                    account.getBalance(),
                    isPremium ? " (Premium)" : " (Standard)");

            logger.info(accountInfo);
            System.out.println("" + accountInfo);

            return account;

        } catch (IllegalArgumentException e) {
            logger.severe("Errore di conversione tipo durante costruzione: " + accountType);
            throw new BankException(
                    "Errore interno nella creazione del conto",
                    "Tipo non convertibile: " + accountType + " - " + e.getMessage(),
                    e);

        } catch (Exception e) {
            logger.severe("Errore durante la costruzione del conto: " + e.getMessage());
            throw new BankException(
                    "Impossibile creare il conto",
                    "Errore nel builder: " + e.getMessage(),
                    e);
        }
    }

    /**
     * Metodo che controlla che tutti i campi obbligatori siano presenti
     */
    private void validateRequiredFields() {
        StringBuilder missingFields = new StringBuilder();

        if (accountType == null) {
            missingFields.append("tipo di conto, ");
        }
        if (accountNumber == null) {
            missingFields.append("numero di conto, ");
        }
        if (ownerName == null) {
            missingFields.append("nome proprietario, ");
        }

        if (missingFields.length() > 0) {
            // Rimuoviamo l'ultima virgola e spazio
            String missing = missingFields.substring(0, missingFields.length() - 2);
            logger.severe("Campi obbligatori mancanti nel builder: " + missing);
            throw new IllegalStateException("Campi obbligatori mancanti: " + missing);
        }

        logger.info("Validazione builder completata con successo");
    }

    /**
     * Metodo che resetta il builder
     */
    public AccountBuilder reset() {
        this.accountType = null;
        this.accountNumber = null;
        this.ownerName = null;
        this.initialBalance = 0.0;
        this.isPremium = false;

        logger.info("AccountBuilder resettato per riutilizzo");
        return this;
    }

}
