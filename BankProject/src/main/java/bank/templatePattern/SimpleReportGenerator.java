package bank.templatePattern;

import bank.account.Account;

/**
 * classe che implementa report base
 */
public class SimpleReportGenerator extends AccountReportGenerator {

    /**
     * metodo che genera un header base
     */
    @Override
    protected String generateHeader() {
        return "\n REPORT SEMPLICE \n";

    }

    /**
     * metodo che genera un report base
     */
    @Override
    protected String generateAccountDetails(Account account) {
        if (account == null) {
            return "\n Dettagli non disponibili \n";
        }

        StringBuilder details = new StringBuilder();
        details.append("DETTAGLI FINANZIARI\n");

        if (account.getBalance() > 0) {
            details.append("   Status: In positivo\n");
        } else if (account.getBalance() == 0) {
            details.append("   Status: saldo zero\n");
        } else {
            details.append("   Status: In negativo (possibile scoperto)\n");
        }

        details.append("\n");
        return details.toString();
    }

    /**
     * metodo che genera un footer base
     */
    @Override
    protected String generateFooter() {
        return "\n Report generato dal Sistema Bancario \n";
    }
}
