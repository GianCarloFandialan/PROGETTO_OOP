package bank;

import java.util.*;
import java.io.*;
import java.util.logging.Logger;

import bank.abstractFactory.BankFactory;
import bank.abstractFactory.BankFactoryProvider;
import bank.abstractFactory.StandardBankFactory;
import bank.account.Account;
import bank.account.AccountFactory;
import bank.observerPattern.SimpleTransactionObserver;
import bank.observerPattern.TransactionObserver;

/**
 * Classe principale per gestire tutte le operazioni bancarie.
 */
public class BankService {
    /**
     * Mappa che memorizza tutti i conti, usando il numero di conto come chiave
     */
    private Map<String, Account> accounts;

    /** Percorso del file dove si salvano i dati */
    private String dataFilePath;

    /** Factory di default */
    private BankFactory defaultFactory;

    /** logger statico */
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    /** AGGIUNTO PER IMPLEMENTARE L'OBSERVER PATTERN */
    private TransactionObserver observer;

    /**
     * Costruttore - crea il servizio bancario.
     */
    public BankService() {
        this.accounts = new HashMap<>();
        this.dataFilePath = "bank_data.txt";
        this.defaultFactory = new StandardBankFactory();
        this.observer = new SimpleTransactionObserver();
        logger.info("BankService inizializzato con factory standard - File dati: " + dataFilePath);
        loadAccounts();
    }

    /**
     * Metodo ORIGINALE che crea un nuovo conto.
     * 
     * @param type           tipo di conto da creare (CHECKING o SAVINGS)
     * @param accountNumber  numero identificativo dell'conto
     * @param ownerName      nome completo del proprietario
     * @param initialBalance Il saldo iniziale da depositare
     * @param isPremium      booleano per validare se il conto è premium
     * 
     * @throws BankException Errore durante la creazione
     */
    public void createAccount(String type, String accountNumber, String ownerName, double initialBalance)
            throws BankException {
        try {
            // Verifichiamo che il conto non esista già (logica originale)
            if (accounts.containsKey(accountNumber)) {
                throw new BankException("Conto già esistente",
                        "Tentativo di creare conto duplicato: " + accountNumber);
            }

            // Usiamo la factory STANDARD per mantenere il comportamento originale identico
            AccountFactory.AccountType accountType;
            if ("checking".equalsIgnoreCase(type)) {
                accountType = AccountFactory.AccountType.CHECKING;
            } else if ("savings".equalsIgnoreCase(type)) {
                accountType = AccountFactory.AccountType.SAVINGS;
            } else {
                throw new BankException("Tipo di conto non valido", "Tipo richiesto: " + type);
            }

            // Creiamo il conto usando la factory originale per mantenere compatibilità
            // totale
            Account account = AccountFactory.createAccount(accountType, accountNumber, ownerName, initialBalance);
            accounts.put(accountNumber, account);
            saveAccounts();
            logger.info("Conto creato con successo: " + accountNumber);

        } catch (IllegalArgumentException e) {
            logger.severe("Errore nella creazione del conto: " + e.getMessage());
            throw new BankException("Dati inseriti non validi", e.getMessage(), e);
        } catch (Exception e) {
            logger.severe("Errore imprevisto nella creazione del conto: " + e.getMessage());
            throw new BankException("Errore interno del sistema",
                    "Errore durante la creazione del conto: " + e.getMessage(), e);
        }
    }

    /** ABSTRACT FACTORY */
    /**
     * Metodo AGGIUNTO che crea un nuovo conto per incorporare l'abstarct factory.
     * 
     * @param type           tipo di conto da creare (CHECKING o SAVINGS)
     * @param accountNumber  numero identificativo dell'conto
     * @param ownerName      nome completo del proprietario
     * @param initialBalance Il saldo iniziale da depositare
     * @param isPremium      booleano per validare se il conto è premium
     * 
     * @throws BankException Errore durante la creazione
     */
    public void createAccount(String type, String accountNumber, String ownerName, double initialBalance,
            boolean isPremium)
            throws BankException {
        try {
            if (accounts.containsKey(accountNumber)) {
                throw new BankException("Conto già esistente",
                        "Tentativo di creare conto duplicato: " + accountNumber);
            }

            // facory in base al tipo di client
            BankFactory factoryToUse = BankFactoryProvider.getFactory(isPremium);

            Account account;

            if ("checking".equalsIgnoreCase(type)) {
                account = factoryToUse.createCheckingAccount(accountNumber, ownerName, initialBalance);
            } else if ("savings".equalsIgnoreCase(type)) {
                account = factoryToUse.createSavingsAccount(accountNumber, ownerName, initialBalance);
            } else {
                throw new BankException("Tipo di conto non valido",
                        "Tipo richiesto: " + type);
            }

            accounts.put(accountNumber, account);
            saveAccounts();

            String customerType = isPremium ? "Premium" : "Standard";
            logger.info("Conto " + customerType + " creato con successo: " + accountNumber);

        } catch (IllegalArgumentException e) {
            logger.severe("Errore nella creazione del conto: " + e.getMessage());
            throw new BankException("Dati inseriti non validi", e.getMessage(), e);
        } catch (Exception e) {
            logger.severe("Errore imprevisto nella creazione del conto: " + e.getMessage());
            throw new BankException("Errore interno del sistema",
                    "Errore durante la creazione del conto: " + e.getMessage(), e);
        }
    }

    /**
     * Metodo per cambiare la factory di default
     */
    public void setDefaultFactory(BankFactory factory) {
        if (factory != null) {
            this.defaultFactory = factory;
            logger.info("Factory di default cambiata a: " + factory.getClass().getSimpleName());
        }
    }

    /**
     * Metodo per ottenere informazioni sulla factory corrente
     */
    public String getCurrentFactoryType() {
        return defaultFactory.getClass().getSimpleName();
    }

    /** BUILDER PATTERN */
    /**
     * metodo per includere il builder pattern nel sistema bancario
     * 
     * @param account conto già esistente che deve essere integrato nel sistema
     * 
     * @throws BankException errore nell'inclusione del conto
     */
    public void registerAccount(Account account) throws BankException {
        try {

            if (account == null) {
                throw new BankException("Conto non valido", "Tentativo di registrare conto null");
            }

            if (accounts.containsKey(account.getAccountNumber())) {
                throw new BankException("Conto già esistente",
                        "Tentativo di registrare conto duplicato: " + account.getAccountNumber());
            }

            accounts.put(account.getAccountNumber(), account);

            saveAccounts();

            logger.info("Conto pre-costruito registrato con successo: " + account.getAccountNumber() +
                    " (" + account.getAccountType() + ") per " + account.getOwnerName());

        } catch (Exception e) {
            if (e instanceof BankException) {
                throw e; // Rilanciamo le BankException così come sono
            } else {
                logger.severe("Errore imprevisto durante registrazione del conto: " + e.getMessage());
                throw new BankException("Errore interno del sistema",
                        "Errore durante la registrazione: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Metodo che esegue una transazione (deposito o prelievo) su un conto
     * esistente.
     * 
     * @param accountNumber numero identificativo dell'conto
     * @param operation     tipologia dell' operazione ("deposit" o "withdraw")
     * @param amount        importo della transazione
     * @throws BankException errore durante la transizione
     */
    public void performTransaction(String accountNumber, String operation, double amount) throws BankException {
        try {
            Account account = accounts.get(accountNumber);
            // Si verifica l'esistenza del conto
            if (account == null) {
                logger.warning("Tentativo di operazione su un conto inesistente: " + accountNumber);
                throw new BankException("Conto non trovato",
                        "Conto richiesto: " + accountNumber);
            }

            boolean success;

            if ("deposit".equalsIgnoreCase(operation)) {
                success = account.deposit(amount);
            } else if ("withdraw".equalsIgnoreCase(operation)) {
                success = account.withdraw(amount);
            } else {
                logger.warning("Operazione non riconosciuta: " + operation);
                throw new BankException("Operazione non valida",
                        "Operazione richiesta: " + operation);
            }

            // AGGIUNTO PER IMPLEMENTARE L'OBSERVER PATTERN
            if (observer != null) {
                observer.onTransaction(accountNumber, operation.toLowerCase(), amount, success);
            }

            if (success) {
                saveAccounts();
                logger.info("Transazione completata con successo sul conto " + accountNumber);
            } else {
                logger.warning("Transazione fallita sul conto " + accountNumber +
                        " - Operazione: " + operation + ", Importo: €" + amount);
                throw new BankException("Operazione non riuscita",
                        "Fallimento " + operation + " su conto " + accountNumber);
            }

        } catch (Exception e) {
            if (e instanceof BankException) {
                throw e;
            } else {
                logger.severe("Errore imprevisto durante transazione: " + e.getMessage());
                throw new BankException("Errore durante l'operazione",
                        "Errore tecnico: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Metodo che salva tutti i conti attuali su file in formato CSV
     */
    private void saveAccounts() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFilePath))) {

            int accountsSaved = 0;

            for (Account account : accounts.values()) {
                writer.println(account.getAccountType() + "," +
                        account.getAccountNumber() + "," +
                        account.getOwnerName() + "," +
                        account.getBalance());

                accountsSaved++;
            }

            logger.info("Salvataggio completato: " + accountsSaved + " conti salvati su " + dataFilePath);

        } catch (IOException e) {
            logger.severe("Errore durante il salvataggio: " + e.getMessage());
            System.out.println("Errore nel salvataggio: " + e.getMessage());
        }
    }

    /**
     * Metodo che carica i conti dal file CSV all'avvio del programma.
     */
    private void loadAccounts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFilePath))) {

            String line;
            int accountsLoaded = 0;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");

                if (parts.length == 4) {
                    String type = parts[0];
                    String number = parts[1];
                    String owner = parts[2];
                    double balance = Double.parseDouble(parts[3]);

                    AccountFactory.AccountType accountType = "Conto Corrente".equals(type)
                            ? AccountFactory.AccountType.CHECKING
                            : AccountFactory.AccountType.SAVINGS;

                    Account account = AccountFactory.createAccount(accountType, number, owner, balance);
                    accounts.put(number, account);

                    accountsLoaded++;
                }
            }

            logger.info("Caricamento completato: " + accountsLoaded + " conti caricati da " + dataFilePath);

        } catch (FileNotFoundException e) {
            logger.info("File dati non trovato - inizializzazione con database vuoto");
            System.out.println("File dati non trovato, si inizia con database vuoto");
        } catch (IOException e) {
            logger.warning("Errore durante il caricamento: " + e.getMessage());
            System.out.println("Errore nel caricamento: " + e.getMessage());
        }
    }

    /**
     * Restituisce un conto specifico
     * 
     * @param accountNumber il numero del conto da cercare
     * @return Account conto trovato, o null se non esiste
     */
    public Account getAccount(String accountNumber) {
        logger.info("Richiesta conto: " + accountNumber);
        return accounts.get(accountNumber);
    }

    /**
     * Restituisce tutti i conti presenti nel sistema.
     * 
     * @return Collection<Account> - Una collezione di tutti i conti
     */
    public Collection<Account> getAllAccounts() {
        logger.info("Richiesta lista completa dei conti (totale: " + accounts.size() + ")");
        return accounts.values();
    }
}