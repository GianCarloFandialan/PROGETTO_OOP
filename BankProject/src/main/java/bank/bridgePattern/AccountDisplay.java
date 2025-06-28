package bank.bridgePattern;

import bank.account.Account;

/**
 * Classe di astrazione del Bridge pattern.
 */
public class AccountDisplay {

    /* bridge */
    protected OutputDevice outputDevice;

    /**
     * Costruttore che riceve il dispositivo di output da usare
     * 
     * @param outputDevice il dispositivo che gestirà l'output effettivo
     */
    public AccountDisplay(OutputDevice outputDevice) {
        this.outputDevice = outputDevice;
    }

    /**
     * Metodo principale che mostra le informazioni base di un conto
     * 
     * @param account il conto da visualizzare
     */
    public void showAccount(Account account) {
        if (account == null) {
            outputDevice.writeLine("ERRORE: Conto non valido");
            return;
        }

        outputDevice.writeLine("INFORMAZIONI CONTO");
        outputDevice.writeLine("Numero: " + account.getAccountNumber());
        outputDevice.writeLine("Proprietario: " + account.getOwnerName());
        outputDevice.writeLine("Tipo: " + account.getAccountType());
        outputDevice.writeLine("Saldo: €" + String.format("%.2f", account.getBalance()));
    }

    /**
     * Metdo che cambia il dispositivo di output in runtime
     * 
     * @param newDevice il nuovo dispositivo da utilizzare
     */
    public void changeOutputDevice(OutputDevice newDevice) {
        if (outputDevice != null) {
            outputDevice.close();
        }

        this.outputDevice = newDevice;
    }

    /**
     * Finalizza l'output
     */
    public void finish() {
        if (outputDevice != null) {
            outputDevice.close();
        }
    }
}
