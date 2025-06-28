package bank.abstractFactory;

/**
 * Factory Provider che restituisce la factory appropriata in base al tipo di
 * cliente
 */
public class BankFactoryProvider {

    public static BankFactory getFactory(boolean isPremiumCustomer) {
        if (isPremiumCustomer) {
            return new PremiumBankFactory();
        } else {
            return new StandardBankFactory();
        }
    }
}