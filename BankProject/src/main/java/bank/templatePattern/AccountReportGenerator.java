package bank.templatePattern;

import bank.account.Account;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Classe che implementa il Template Method Pattern per la generazione di
 * report bancari.
 */
public abstract class AccountReportGenerator {

    /** logger statico */
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    /**
     * TEMPLATE METHOD
     * 
     * @param account conto per cui generare il report
     * 
     * @return Il report
     */
    public final String generateReport(Account account) {

        logger.info("Iniziando generazione report per il conto: " +
                (account != null ? account.getAccountNumber() : "null"));

        StringBuilder report = new StringBuilder();

        report.append(generateHeader());

        // info di base del conto (uguale per tutti)
        report.append(generateBasicAccountInfo(account));

        // personalizzazione report
        report.append(generateAccountDetails(account));

        // personalizzazione footer
        report.append(generateFooter());

        logger.info("Report generato con successo");
        return report.toString();
    }

    /**
     * ogni sottoclasse deve implementare questo metodo
     */
    protected abstract String generateHeader();

    /**
     * ogni sottoclasse deve implementare questo metodo
     */
    protected abstract String generateAccountDetails(Account account);

    /**
     * ogni sottoclasse deve implementare questo metodo
     */
    protected abstract String generateFooter();

    /**
     * Metodo uguale per tutti i tipi di report!
     * 
     * @param account conto da cui estrarre le informazioni base
     * 
     * @return informazioni di base
     */
    protected final String generateBasicAccountInfo(Account account) {

        if (account == null) {
            return "\n⚠️  ERRORE: conto non disponibile\n\n";
        }

        StringBuilder basicInfo = new StringBuilder();
        basicInfo.append(" INFORMAZIONI CONTO \n");
        basicInfo.append("   Numero: ").append(account.getAccountNumber()).append("\n");
        basicInfo.append("   Proprietario: ").append(account.getOwnerName()).append("\n");
        basicInfo.append("   Tipo: ").append(account.getAccountType()).append("\n");
        basicInfo.append("   Saldo attuale: €").append(String.format("%.2f", account.getBalance())).append("\n");
        basicInfo.append("   Data generazione: ").append(new Date()).append("\n\n");

        return basicInfo.toString();
    }

    /**
     * Metodo hook con implementazione di default
     * 
     * @return eventuale nota aggiuntiva
     */
    protected String generateAdditionalNotes() {
        return "";
    }
}
