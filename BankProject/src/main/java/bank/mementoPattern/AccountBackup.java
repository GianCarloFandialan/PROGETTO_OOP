package bank.mementoPattern;

import java.util.HashMap;
import java.util.Map;
import bank.account.Account;

/**
 * classe del "Caretaker" del Pattern Memento
 */
public class AccountBackup {

    /** mappa per conservare gli snapshot */
    private Map<String, AccountSnapshot> snapshots = new HashMap<>();

    /**
     * Metodo che salva lo stato attuale di un conto(Snapshot)
     */
    public boolean save(Account account) {

        if (account == null) {
            System.out.println("Errore: impossibile salvare un conto null");
            return false;
        }

        try {
            AccountSnapshot snapshot = new AccountSnapshot(account);

            String accountNumber = snapshot.getAccountNumber();
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                System.out.println("Errore: snapshot creato ma senza numero account valido");
                return false;
            }

            // si salva lo snapshot
            snapshots.put(accountNumber, snapshot);
            System.out.println("Backup salvato per il conto " + accountNumber +
                    " (saldo: €" + snapshot.getBalance() + ")");
            return true;

        } catch (IllegalArgumentException e) {
            System.out.println("Impossibile salvare backup: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Errore imprevisto durante il salvataggio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Metodo che recupera lo stato salvato di un conto
     */
    public AccountSnapshot restore(String accountNumber) {

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            System.out.println("Impossibile recuperare backup: numero conto non valido");
            return null;
        }

        AccountSnapshot snapshot = snapshots.get(accountNumber.trim());
        if (snapshot == null) {
            System.out.println("Nessun backup trovato per il conto " + accountNumber);
            return null;
        }

        System.out.println("Backup recuperato per il conto " + accountNumber);
        return snapshots.get(accountNumber);
    }

    /**
     * Metodo che controlla se esiste un backup
     */
    public boolean hasBackup(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            System.out.println("Numero del conto non valido per controllo backup");
            return false;
        }

        boolean exists = snapshots.containsKey(accountNumber.trim());
        System.out.println("🔍 Backup per " + accountNumber + ": " + (exists ? "PRESENTE" : "ASSENTE"));
        return exists;
    }

    /**
     * Restituisce il numero totale di backup conservati
     */
    public int getTotalBackups() {
        int total = snapshots.size();
        System.out.println("📊 Totale backup attivi: " + total);
        return total;
    }

    /**
     * Elimina un backup specifico
     */
    public boolean deleteBackup(String accountNumber) {

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            System.out.println("Impossibile eliminare: numero del conto non valido");
            return false;
        }

        AccountSnapshot removed = snapshots.remove(accountNumber.trim());
        if (removed != null) {
            System.out.println("Backup eliminato per il conto " + accountNumber);
            return true;
        } else {
            System.out.println("Nessun backup da eliminare per il conto " + accountNumber);
            return false;
        }
    }
}
