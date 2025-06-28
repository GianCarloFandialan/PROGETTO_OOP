package bank.bridgePattern;

import bank.account.Account;
import bank.account.CheckingAccount;

/**
 * Classe di astrazione che estende AccountDisplay
 */
public class DetailedAccountDisplay extends AccountDisplay {

    /**
     * Costruttore che riceve il dispositivo di output da usare
     * 
     * @param outputDevice il dispositivo che gestirà l'output effettivo
     */
    public DetailedAccountDisplay(OutputDevice outputDevice) {
        super(outputDevice);
    }

    /**
     * Metodo principale che mostra le informazioni dettagliate di un conto
     * 
     * @param account il conto da visualizzare con dettagli extra
     */
    @Override
    public void showAccount(Account account) {
        if (account == null) {
            outputDevice.writeLine("ERRORE: Conto non valido");
            return;
        }

        outputDevice.writeLine("DETTAGLI COMPLETI CONTO");
        outputDevice.writeLine("\nNumero Conto: " + account.getAccountNumber());
        outputDevice.writeLine("\nNome Titolare: " + account.getOwnerName());
        outputDevice.writeLine("\nTipologia: " + account.getAccountType());
        outputDevice.writeLine("\nSaldo Attuale: €" + String.format("%.2f", account.getBalance()));

        // informazioni per tipo di conto
        if (account instanceof CheckingAccount) {
            CheckingAccount checking = (CheckingAccount) account;
            outputDevice.writeLine("Fido Disponibile: €" + String.format("%.2f", checking.getOverdraftLimit()));
            outputDevice.writeLine("Fido Utilizzato: €" + String.format("%.2f", checking.getOverdraftUsed()));
            outputDevice.writeLine("Fondi Totali: €" + String.format("%.2f", checking.getTotalAvailableFunds()));
        }

        // Stato del conto
        if (account.getBalance() > 1000) {
            outputDevice.writeLine("Stato: Saldo ottimo");
        } else if (account.getBalance() > 0) {
            outputDevice.writeLine("Stato: Saldo positivo");
        } else {
            outputDevice.writeLine("Stato: saldo in rosso");
        }
    }

    /**
     * Metodo che mostra il riassunto
     * 
     * @param account conto da riassumere
     */
    public void showSummary(Account account) {
        if (account == null) {
            outputDevice.writeLine("Nessun conto da riassumere");
            return;
        }

        outputDevice.writeLine("RIASSUNTO: " + account.getAccountNumber() +
                " (" + account.getAccountType() + ") - €" +
                String.format("%.2f", account.getBalance()));
    }
}