package bank.strategyPattern;

import java.util.logging.Logger;

import bank.account.Account;

/**
 * Classe factory per implementare il prelievo corretto per ogni tipo di conto.
 */
public class WithdrawalStrategyFactory {

    /** logger statico */
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    /**
     * Crea il pirelievo appropriato per un tipo di cono.
     * 
     * @param accountType tipo di conto
     * 
     * @return WithdrawalStrategy prelievo appropriato per il tipo di conto
     */
    public static WithdrawalStrategy createStrategy(String accountType) {
        if ("Conto Corrente".equals(accountType)) {
            logger.info("Creando strategia overdraft per conto corrente");
            return new OverdraftWithdrawalStrategy(100.0);
        } else if ("Conto Risparmio".equals(accountType)) {
            logger.info("Creando strategia no-overdraft per conto risparmio");
            return new NoOverdraftWithdrawalStrategy();

        } else {
            logger.warning("Tipo account non riconosciuto: " + accountType + ", usando strategia no-overdraft");
            return new NoOverdraftWithdrawalStrategy();
        }
    }

}
