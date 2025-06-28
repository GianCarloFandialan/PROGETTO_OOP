package bank.strategyPattern;

import java.util.logging.Logger;

/**
 * Classe che permette prelievi senza scoperti.
 */
class NoOverdraftWithdrawalStrategy implements WithdrawalStrategy {

    /** logger statico */
    private static final Logger logger = Logger.getLogger(NoOverdraftWithdrawalStrategy.class.getName());

    @Override
    public boolean executeWithdrawal(double[] currentBalance, double requestedAmount, String accountNumber) {
        if (requestedAmount <= 0) {
            logger.warning(
                    "Strategia no-overdraft: importo non valido €" + requestedAmount + " per conto " + accountNumber);
            System.out.println("Importo non valido per il prelievo");
            return false;
        }

        logger.info("Strategia no-overdraft: richiesta €" + requestedAmount +
                " su conto " + accountNumber + " (saldo: €" + currentBalance[0] + ")");

        if (currentBalance[0] >= requestedAmount) {
            double previousBalance = currentBalance[0];
            currentBalance[0] -= requestedAmount;

            logger.info("Prelievo autorizzato: €" + requestedAmount +
                    " (saldo: €" + previousBalance + " → €" + currentBalance[0] + ")");

            return true;

        } else {
            logger.warning("Prelievo rifiutato: richiesti €" + requestedAmount +
                    ", disponibili €" + currentBalance[0] + " (no overdraft policy)");

            System.out.println("Fondi insufficienti");
            return false;
        }
    }
}
