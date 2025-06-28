package bank.account;

import java.util.*;
import java.util.logging.Logger;

/**
 * Classe che gestisce i gruppi di conti bancari.
 */
public class AccountGroup {
    /** nome identificativo del gruppo */
    private String groupName;

    /** lista di tutti i conti che appartengono direttamente a questo gruppo */
    private List<Account> accounts;

    /** lista di sottogruppi contenuti in questo gruppo */
    private List<AccountGroup> subGroups;

    /** logger statico */
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    /**
     * Costruttore - Costruisce un nuovo gruppo vuoto
     * 
     * @param groupName nome identificativo del gruppo
     */
    public AccountGroup(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            this.groupName = "Gruppo_Senza_Nome_" + System.currentTimeMillis();
            logger.warning("Tentativo di creare gruppo con nome non valido - " +
                    "Assegnato nome automatico: " + this.groupName);
        } else {
            // Se il nome è valido, lo puliamo da eventuali spazi extra
            this.groupName = groupName.trim();
        }
        this.accounts = new ArrayList<>();
        this.subGroups = new ArrayList<>();
        logger.info("Nuovo gruppo creato: '" + this.groupName + "' " +
                "(inizialmente vuoto - capacità conto: illimitata, sottogruppi: illimitati)");
    }

    /**
     * Metodo che aggiunge un conto al gruppo
     * 
     * @param account conto da aggiungere al gruppo
     */
    public void addAccount(Account account) {
        if (account == null) {
            logger.warning("Tentativo di aggiungere conto null al gruppo '" + groupName +
                    "' - Operazione ignorata");
            return;
        } else {
            // Veifica se esiste già un conto con lo stesso numero
            for (Account existingAccount : accounts) {
                if (existingAccount.getAccountNumber().equals(account.getAccountNumber())) {
                    logger.warning("Tentativo di aggiungere conto duplicato '" +
                            account.getAccountNumber() + "' al gruppo '" + groupName +
                            "' - Conto già presente, operazione ignorata");
                    return;
                }
            }

            // Aggiungiamo il conto alla nostra lista
            accounts.add(account);

            // Registriamo il successo dell'operazione
            logger.info("Conto " + account.getAccountNumber() +
                    " aggiunto con successo al gruppo '" + groupName +
                    "' (totale conti diretti nel gruppo: " + accounts.size() + ")");

            System.out.println("Conto " + account.getAccountNumber() +
                    " aggiunto al gruppo " + groupName);
        }
    }

    /**
     * Metodo che aggiunge un sottogruppo
     * 
     * @param subGroup
     */
    public void addSubGroup(AccountGroup subGroup) {
        if (subGroup == null) {
            logger.warning("Tentativo di aggiungere sottogruppo null al gruppo '" + groupName +
                    "' - Operazione ignorata");
            return;
        } else {
            // Verifica che non si stia tentando di aggiungere il gruppo stesso a se stesso
            if (subGroup == this) {
                logger.warning("Tentativo di aggiungere il gruppo " + groupName +
                        " come sottogruppo di se stesso - Operazione ignorata");
                return;
            }

            // Verifica se questo sottogruppo non sia già presente
            for (AccountGroup existingSubGroup : subGroups) {
                if (existingSubGroup.getGroupName().equals(subGroup.getGroupName())) {
                    logger.warning("Tentativo di aggiungere sottogruppo duplicato '" +
                            subGroup.getGroupName() + "' al gruppo '" + groupName +
                            "' - Sottogruppo già presente, operazione ignorata");
                    return;
                }
            }

            // Verifica se questo gruppo non sia un sottogruppo del gruppo che si vuole
            // aggiungere
            if (subGroup.subGroups.contains(this)) {
                logger.warning("Tentativo di aggiungere il gruppo " + subGroup +
                        "' di cui '" + groupName + "' è sottogruppo" +
                        " - Operazione ignorata");
                return;
            }

            subGroups.add(subGroup);

            // Registriamo il successo dell'operazione
            logger.info("Sottogruppo '" + subGroup.getGroupName() +
                    "' aggiunto con successo al gruppo '" + groupName +
                    "' (totale sottogruppi: " + subGroups.size() + ")");

            System.out.println("Sottogruppo " + subGroup.getGroupName() +
                    " aggiunto al gruppo " + groupName);
        }
    }

    /**
     * Metodo che calcola il saldo totale di tutti i conti nel gruppo (inclusi
     * sottogruppi)
     * 
     * @return total la somma di tutti i saldi dei conti nel gruppo e
     *         sottogruppi
     */
    public double getTotalBalance() {
        double total = 0;

        // Somma i saldi dei conti non in sottogruppi
        for (Account account : accounts) {
            total += account.getBalance();
        }

        // Registriamo il subtotale dei conti diretti
        logger.info("Subtotale conti diretti nel gruppo '" + groupName + "': €" + total);

        double subGroupsTotal = 0;

        // Somma i saldi dei sottogruppi (ricorsione)
        for (AccountGroup subGroup : subGroups) {
            double subGroupBalance = subGroup.getTotalBalance();
            subGroupsTotal += subGroupBalance;
        }

        total += subGroupsTotal;

        logger.info("Calcolo completato per gruppo '" + groupName +
                "': €" + (total - subGroupsTotal) + " (diretti) + €" + subGroupsTotal +
                " (sottogruppi) = €" + total + " (totale)");

        return total;
    }

    /**
     * Metodo che conta il numero totale di conti
     * 
     * @return
     */
    public int getTotalAccountCount() {
        int count = accounts.size();
        logger.info("Conti diretti nel gruppo '" + groupName + "': " + count);

        // Poi aggiungiamo il conteggio di ogni sottogruppo (RICORSIONE!)
        int subGroupsCount = 0;
        for (AccountGroup subGroup : subGroups) {
            // Chiamata ricorsiva: il sottogruppo conta i suoi conti
            int subGroupAccountCount = subGroup.getTotalAccountCount();
            subGroupsCount += subGroupAccountCount;
        }

        count += subGroupsCount;

        logger.info("Conteggio completato per gruppo '" + groupName +
                "': " + accounts.size() + " (diretti) + " + subGroupsCount +
                " (sottogruppi) = " + count + " (totale)");

        return count;
    }

    /**
     * Restituisce il nome di questo gruppo
     * 
     * @return String - Il nome del gruppo
     */
    public String getGroupName() {
        logger.finest("Accesso al nome del gruppo: '" + groupName + "'");
        return groupName;
    }

    /**
     * Restituisce la lista dei conti diretti
     * 
     * @return List<Account> - lista contenente gli stessi conti
     */
    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    /**
     * Restituisce la lista dei sottogruppi.
     * 
     * @return List<AccountGroup> -lista contenente gli stessi sottogruppi
     */
    public List<AccountGroup> getSubGroups() {
        return new ArrayList<>(subGroups);
    }

    /**
     * Metodo per verificare se il gruppo è vuoto (nessun conto diretto o
     * sottogruppi).
     * 
     * @return boolean - true se il gruppo non contiene nulla, false altrimenti
     */
    public boolean isEmpty() {
        boolean empty = accounts.isEmpty() && subGroups.isEmpty();

        if (empty) {
            logger.info("Gruppo '" + groupName + "' risulta vuoto (nessun conto o sottogruppo)");
        }

        return empty;
    }

    /**
     * Metodo per verificare se questo gruppo contiene un conto specifico
     * (direttamente o in qualsiasi sottogruppo).
     * 
     * @param accountNumber - Il numero dell'conto da cercare
     * @return boolean - true se l'conto è presente da qualche parte nel gruppo
     */
    public boolean containsAccount(String accountNumber) {

        logger.info("Ricercando conto '" + accountNumber + "' nel gruppo '" + groupName + "'");

        // Cerchiamo nei diretti
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                logger.info("Conto '" + accountNumber + "' trovato direttamente nel gruppo '" + groupName + "'");
                return true;
            }
        }

        // Cerchiamo ricorsivamente nei sottogruppi
        for (AccountGroup subGroup : subGroups) {
            if (subGroup.containsAccount(accountNumber)) {
                logger.info("Conto '" + accountNumber + "' trovato nel sottogruppo '" +
                        subGroup.getGroupName() + "' del gruppo '" + groupName + "'");
                return true;
            }
        }

        logger.info("Conto '" + accountNumber + "' NON trovato nel gruppo '" + groupName + "'");
        return false;
    }
}