package bank.strategyPattern;

import java.util.logging.Logger;

/**
 * Classe che permette prelievi con scoperto fino a un certo limite.
 */
class OverdraftWithdrawalStrategy implements WithdrawalStrategy {

    /**
     * Importo massimo che la banca permette di prelevare oltre il saldo
     * disponibile(fido)
     */
    private final double overdraftLimit;

    /** logger statico */
    private static final Logger logger = Logger.getLogger(OverdraftWithdrawalStrategy.class.getName());

    /**
     * Costruttore che imposta il limite di fido permesso.
     * 
     * @param overdraftLimit limite massimo di fido
     */
    public OverdraftWithdrawalStrategy(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
        logger.info("Strategia overdraft inizializzata con limite €" + overdraftLimit);
    }

    @Override
    public boolean executeWithdrawal(double[] currentBalance, double requestedAmount, String accountNumber) {

        if (requestedAmount <= 0) {
            logger.warning(
                    "Strategia overdraft: importo non valido €" + requestedAmount + " per conto " + accountNumber);
            System.out.println("Importo non valido per il prelievo");
            return false;
        }

        double totalAvailable = currentBalance[0] + overdraftLimit;

        logger.info("Strategia overdraft: richiesta €" + requestedAmount +
                " su conto " + accountNumber + " (saldo: €" + currentBalance[0] +
                ", fido: €" + overdraftLimit + ", totale disponibile: €" + totalAvailable + ")");

        if (totalAvailable >= requestedAmount) {
            double previousBalance = currentBalance[0];
            currentBalance[0] -= requestedAmount;

            if (previousBalance >= requestedAmount) {
                logger.info("Prelievo normale eseguito: €" + requestedAmount +
                        " (saldo: €" + previousBalance + " → €" + currentBalance[0] + ")");
            } else {
                double overdraftUsed = requestedAmount - previousBalance;
                logger.warning("Prelievo con fido eseguito: €" + requestedAmount +
                        " (scoperto utilizzato: €" + overdraftUsed +
                        ", saldo: €" + previousBalance + " → €" + currentBalance[0] + ")");
            }

            return true;

        } else {
            double shortfall = requestedAmount - totalAvailable;
            logger.warning("Prelievo rifiutato per fondi insufficienti: richiesti €" + requestedAmount +
                    ", disponibili €" + totalAvailable + ", mancanti €" + shortfall);

            System.out.println("Fondi insufficienti");
            return false;
        }
    }
}
