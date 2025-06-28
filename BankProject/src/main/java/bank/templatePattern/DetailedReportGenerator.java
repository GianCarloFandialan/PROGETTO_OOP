package bank.templatePattern;

import bank.account.Account;
import java.util.Date;

/**
 * classe che implementa report dettagliato
 */
public class DetailedReportGenerator extends AccountReportGenerator {

    /**
     * metodo che genera un header base
     */
    @Override
    protected String generateHeader() {
        return "\n REPORT DETTAGLIATO \n";
    }

    /**
     * metodo che genera un report piu dettagliato
     */
    @Override
    protected String generateAccountDetails(Account account) {
        if (account == null) {
            return "\n Dettagli non disponibili \n";
        }

        StringBuilder details = new StringBuilder();

        details.append("ANALISI FINANZIARIA DETTAGLIATA\n");
        details.append("Saldo corrente: €").append(String.format("%.2f", account.getBalance())).append("\n");

        // si classificano i saldi
        double balance = account.getBalance();
        if (balance >= 10000) {
            details.append("Account Premium (alta liquidità)\n");
        } else if (balance >= 1000) {
            details.append("Account Standard (buona liquidità)\n");
        } else if (balance == 0) {
            details.append("Account a saldo zero\n");
        } else {
            details.append("Account in scoperto\n");
        }

        // tipo di conto
        details.append("Tipo conto: ").append(account.getAccountType()).append("\n\n");

        details.append("ANALISI TIPO CONTO\n");
        if ("Conto Corrente".equals(account.getAccountType())) {
            details.append("Funzionalità: Prelievi con possibile scoperto\n");
            details.append("Flessibilità: Alta (operazioni frequenti)\n");
            details.append(" Interessi: Non applicabili\n\n");
        } else if ("Conto Risparmio".equals(account.getAccountType())) {
            details.append(" Funzionalità: Solo prelievi con fondi disponibili\n");
            details.append(" Flessibilità: Limitata (preservazione capitale)\n");
            details.append(" Interessi: Maturazione automatica\n\n");
        }

        // raccomandazioni possibili
        details.append("RACCOMANDAZIONI\n");
        if (balance < 100) {
            details.append("Si consigia un deposito per migliorare la liquidità\n");
        }
        if ("Conto Risparmio".equals(account.getAccountType()) && balance > 5000) {
            details.append("Saldo elevato: si consigliano investimenti a lungo termine\n");
        }
        details.append("Si consiglia di monitorare regolarmente le transazioni\n");

        return details.toString();
    }

    /**
     * metodo che genera un footer "dettagliato"
     */
    @Override
    protected String generateFooter() {
        return "Report Dettagliato\n" +
                "Generato il: " + new Date() + "\n" +
                "Le informazioni riportate sono accurate al momento della generazione\n" +
                "per richiedere assistenza, contattare il servizio clienti\n";
    }

    /**
     * Metodo che aggiunge note extra tramite l'hook
     */
    @Override
    protected String generateAdditionalNotes() {
        return "NOTE AGGIUNTIVE:\n" +
                "Questo report include analisi avanzate\n" +
                "Le raccomandazioni sono basate su algoritmi automatici\n";
    }
}
