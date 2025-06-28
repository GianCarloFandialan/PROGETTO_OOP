package bank;

import java.util.*;
import java.util.logging.Logger;

import bank.account.Account;
import bank.account.AccountGroup;
import bank.account.AccountIterator;
import bank.bridgePattern.AccountDisplay;
import bank.bridgePattern.ConsoleOutputDevice;
import bank.bridgePattern.DetailedAccountDisplay;
import bank.bridgePattern.FileOutputDevice;
import bank.builderPattern.AccountBuilder;
import bank.mementoPattern.AccountSnapshot;
import bank.mementoPattern.AccountBackup;
import bank.singletonPattern.BankConfiguration;
import bank.strategyPattern.WithdrawalStrategy;
import bank.strategyPattern.WithdrawalStrategyFactory;
import bank.templatePattern.AccountReportGenerator;
import bank.templatePattern.DetailedReportGenerator;
import bank.templatePattern.SimpleReportGenerator;

/**
 * Classe principale che contiene il metodo main
 */
public class BankApplication {

    /** logger statico */
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    /** Metodo main */
    public static void main(String[] args) {

        logger.info("Sistema bancario in avvio...");

        /** istanza principale del servizio bancario */
        BankService bankService = new BankService();

        /** Scanner per leggere l'input */
        Scanner scanner = new Scanner(System.in);

        logger.info("Sistema bancario inizializzato correttamente");

        System.out.println("Sistema Bancario Semplice");
        System.out.println("Comandi disponibili:");
        System.out.println("1. create <tipo> <numero> <nome> <saldo> - Crea un conto");
        System.out.println("2. createpremium <tipo> <numero> <nome> <saldo> - Crea un conto premium");
        System.out.println("3. build <tipo> <numero> <nome> <saldo> - Crea un conto usando Builder");
        System.out.println("4. deposit <numero> <importo> - Deposita denaro");
        System.out.println("5. withdraw <numero> <importo> - Preleva denaro");
        System.out.println("6. balance <numero> - Mostra saldo");
        System.out.println("7. list - Mostra tutti i conti");
        System.out.println("8. demo - Esegue dimostrazione pattern");
        System.out.println("9. exit - Esci");

        while (true) {

            System.out.print("\n> ");
            String input = scanner.nextLine().trim();

            // Se l'utente preme solo Invio si continua
            if (input.isEmpty())
                continue;

            // Si divide l'input in parole separate da spazi
            String[] parts = input.split("\\s+");

            if (parts.length == 0)
                continue;

            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "create":
                        if (parts.length >= 5) {
                            String type = parts[1];
                            String number = parts[2];
                            String name = String.join(" ", Arrays.copyOfRange(parts, 3, parts.length - 1));
                            double balance = Double.parseDouble(parts[parts.length - 1]);

                            // Chiamiamo il metodo originale - nessun cambiamento
                            bankService.createAccount(type, number, name, balance);
                            System.out.println("Conto creato con successo!");
                            logger.info("Conto creato: " + number + " per " + name);
                        } else {
                            System.out.println("Uso: create <checking|savings> <numero> <nome> <saldo>");
                            logger.warning("Comando CREATE con sintassi errata");
                        }
                        break;

                    // caso aggiunto per implementare l'abstract factory
                    case "createpremium":
                        if (parts.length >= 5) {
                            String type = parts[1];
                            String number = parts[2];
                            String name = String.join(" ", Arrays.copyOfRange(parts, 3, parts.length - 1));
                            double balance = Double.parseDouble(parts[parts.length - 1]);

                            bankService.createAccount(type, number, name, balance, true);
                            System.out.println("Conto Premium creato con successo!");
                            logger.info("Conto Premium creato: " + number + " per " + name);
                        } else {
                            System.out.println("Uso: createpremium <checking|savings> <numero> <nome> <saldo>");
                            logger.warning("Comando CREATEPREMIUM con sintassi errata");
                        }
                        break;

                    // caso aggiunto per dimostrare il Builder pattern
                    case "build":
                        if (parts.length >= 5) {
                            String type = parts[1];
                            String number = parts[2];
                            String name = String.join(" ", Arrays.copyOfRange(parts, 3, parts.length - 1));
                            double balance = Double.parseDouble(parts[parts.length - 1]);

                            try {
                                Account account = AccountBuilder.newAccount()
                                        .ofType(type)
                                        .withNumber(number)
                                        .forOwner(name)
                                        .withInitialBalance(balance)
                                        .build();

                                bankService.registerAccount(account);

                                System.out.println("Conto creato usando Builder pattern!");
                                logger.info("Conto creato via Builder: " + number + " per " + name);

                            } catch (BankException e) {
                                throw e;
                            }
                        } else {
                            System.out.println("Uso: build <checking|savings> <numero> <nome> <saldo>");
                            logger.warning("Comando BUILD con sintassi errata");
                        }
                        break;

                    case "deposit":
                        if (parts.length == 3) {
                            bankService.performTransaction(parts[1], "deposit", Double.parseDouble(parts[2]));
                            System.out.println("Deposito eseguito!");
                            logger.info("Deposito completato sul conto " + parts[1]);

                        } else {
                            System.out.println("Uso: deposit <numero_account> <importo>");
                            logger.warning("Comando DEPOSIT con sintassi errata");
                        }
                        break;

                    case "withdraw":
                        if (parts.length == 3) {
                            bankService.performTransaction(parts[1], "withdraw", Double.parseDouble(parts[2]));
                            System.out.println("Prelievo eseguito!");
                            logger.info("Prelievo completato sul conto " + parts[1]);

                        } else {
                            System.out.println("Uso: withdraw <numero_account> <importo>");
                            logger.warning("Comando WITHDRAW con sintassi errata");
                        }
                        break;

                    case "balance":
                        if (parts.length == 2) {
                            Account account = bankService.getAccount(parts[1]);
                            if (account != null) {
                                System.out.println("Saldo: " + account.getBalance());
                                logger.info("Saldo consultato per conto " + parts[1] + ": €" + account.getBalance());
                            } else {
                                System.out.println("Conto non trovato");
                                logger.warning("Tentativo di consultare conto inesistente: " + parts[1]);
                            }
                        } else {
                            System.out.println("Uso: balance <numero_account>");
                            logger.warning("Comando BALANCE con sintassi errata");
                        }
                        break;

                    case "list":
                        System.out.println("\nLista Conti");
                        Collection<Account> allAccounts = bankService.getAllAccounts();

                        if (allAccounts.isEmpty()) {
                            System.out.println("Nessun conto presente nel sistema");
                            logger.info("Lista conti richiesta - sistema vuoto");
                        } else {
                            for (Account account : allAccounts) {
                                System.out.println(account.getAccountType() + " - " +
                                        account.getAccountNumber() + " - " +
                                        account.getOwnerName() + " - " +
                                        account.getBalance());
                            }
                            logger.info("Lista conti mostrata - totale: " + allAccounts.size());
                        }
                        break;

                    case "demo":
                        logger.info("Processando comando DEMO");
                        runPatternDemo(bankService);
                        break;

                    case "exit":
                        System.out.println("Arrivederci!");
                        logger.info("CHIUSURA APPLICAZIONE BANCARIA");
                        return;

                    default:
                        System.out.println("Comando non riconosciuto");
                        logger.warning("Comando non riconosciuto ricevuto: " + command);

                }

            } catch (BankException e) {
                System.out.println("Errore: " + e.getUserMessage());
                logger.warning("Errore bancario: " + e.getErrorId() + " - " + e.getUserMessage());

            } catch (NumberFormatException e) {
                System.out.println("Formato numero non valido");
                logger.warning("Errore formato numero nell'input: " + input);

            } catch (Exception e) {
                System.out.println("Errore inaspettato: " + e.getMessage());
                logger.severe("Errore inaspettato nell'interfaccia utente: " + e.getMessage());
            }
        }
    }

    /**
     * Metodo che esegue una dimostrazione completa di tutti i pattern implementati.
     * È come un tour guidato del sistema che mostra tutte le funzionalità avanzate.
     * 
     * @param bankService - L'istanza del servizio bancario da usare per la demo
     */
    private static void runPatternDemo(BankService bankService) {

        System.out.println(" Dimostrazione Design Pattern ");

        try {
            // Demo Factory Pattern
            System.out.println("\n1. Factory Pattern:");
            logger.info("Demo: Dimostrando Factory Pattern");

            bankService.createAccount("checking", "DEMO001", "Demo User 1", 1000);
            bankService.createAccount("savings", "DEMO002", "Demo User 2", 2000);

            // Demo Composite Pattern
            System.out.println("\n2. Composite Pattern:");
            logger.info("Demo: Dimostrando Composite Pattern");

            AccountGroup mainGroup = new AccountGroup("Gruppo Demo");
            AccountGroup subGroup = new AccountGroup("Sottogruppo VIP");

            Account account1 = bankService.getAccount("DEMO001");
            Account account2 = bankService.getAccount("DEMO002");

            mainGroup.addAccount(account1);
            subGroup.addAccount(account2);
            mainGroup.addSubGroup(subGroup);

            System.out.println("Saldo totale gruppo: " + mainGroup.getTotalBalance());
            System.out.println("Numero conti totali: " + mainGroup.getTotalAccountCount());
            logger.info("Demo Composite: Saldo totale €" + mainGroup.getTotalBalance() +
                    ", Conti totali: " + mainGroup.getTotalAccountCount());

            // Demo Iterator Pattern
            System.out.println("\n3. Iterator Pattern:");
            List<Account> allAccounts = new ArrayList<>(bankService.getAllAccounts());
            AccountIterator iterator = new AccountIterator(allAccounts);

            System.out.println("Iterazione attraverso tutti i conti:");
            int count = 0;
            while (iterator.hasNext()) {
                Account account = iterator.next();
                System.out.println("- " + account.getAccountNumber() + " (" + account.getAccountType() + ")");
                count++;
            }
            logger.info("Demo Iterator: Processati " + count + " conti");

            // Demo Exception Shielding
            System.out.println("\n4. Exception Shielding:");
            try {
                bankService.createAccount("invalid_type", "DEMO003", "Demo User 3", 100);
            } catch (BankException e) {
                System.out.println("Eccezione gestita: " + e.getUserMessage());
                logger.info("Demo Exception: Gestita correttamente eccezione " + e.getErrorId());

            }

            // Demo dell'Abstract Factory
            System.out.println("\n5. Abstract Factory Pattern:");
            logger.info("Demo: Dimostrando Abstract Factory Pattern");

            // Si mostra la differenza
            System.out.println("Creando conto premium per mostrare la differenza...");
            bankService.createAccount("savings", "PREMIUM001", "Cliente VIP", 5000, true);

            System.out.println("Confronta con conto standard:");
            bankService.createAccount("savings", "STANDARD001", "Cliente Standard", 5000, false);

            System.out.println("Entrambi funzionano identicamente, ma il sistema ora distingue i tipi di cliente!");

            // demo del Builder Pattern
            System.out.println("\n6. Builder Pattern:");
            logger.info("Demo: Dimostrando AccountBuilder Pattern");

            try {
                System.out.println("Creazione di un conto usando AccountBuilder...");

                Account builderAccount = AccountBuilder.newAccount()
                        .ofType("checking")
                        .withNumber("BUILDER001")
                        .forOwner("Utente Builder")
                        .withInitialBalance(1500)
                        .build();

            } catch (BankException e) {
                System.out.println("Errore nella demo builder: " + e.getUserMessage());
                logger.warning("Errore demo builder: " + e.getErrorId());
            }

            // demo dello Strategy Pattern
            System.out.println("\n7. Strategy Pattern:");
            logger.info("Demo: Dimostrando Strategy Pattern per politiche di prelievo");

            try {
                System.out.println("Il Strategy Pattern rende esplicite le politiche di prelievo già esistenti...");

                WithdrawalStrategy checkingStrategy = WithdrawalStrategyFactory.createStrategy("Conto Corrente");
                WithdrawalStrategy savingsStrategy = WithdrawalStrategyFactory.createStrategy("Conto Risparmio");

                System.out.println("\nTest Strategia Conto Corrente (con overdraft) ");

                // Simuliamo un prelievo con scoperto
                double[] checkingBalance = { 50.0 };
                System.out.println("Saldo iniziale: €" + checkingBalance[0]);

                boolean checkingResult = checkingStrategy.executeWithdrawal(checkingBalance, 120.0, "TEST-CHECKING");
                System.out.println("Prelievo €120: " + (checkingResult ? "Autorizzato" : "✗ Rifiutato"));
                System.out.println("Saldo finale: €" + checkingBalance[0]);

                System.out.println("\nTest Strategia Conto Risparmio (no overdraft) ");

                // Stesso prelievo senza scoperto
                double[] savingsBalance = { 50.0 }; // Array per permettere modifica del valore
                System.out.println("Saldo iniziale: €" + savingsBalance[0]);
                boolean savingsResult = savingsStrategy.executeWithdrawal(savingsBalance, 120.0, "TEST-SAVINGS");
                System.out.println("Prelievo €120: " + (savingsResult ? "Autorizzato" : "✗ Rifiutato"));
                System.out.println("Saldo finale: €" + savingsBalance[0]);

                System.out.println("\nStrategy Pattern: Stessa interfaccia dei metodi withdraw() originali!");
                System.out.println("Le strategie restituiscono boolean e modificano il saldo, esattamente come prima.");

            } catch (Exception e) {
                System.out.println("Errore nella demo strategy: " + e.getMessage());
                logger.warning("Errore demo strategy: " + e.getMessage());
            }

            // demo dell'Observer Pattern
            System.out.println("\n8. Observer Pattern:");
            logger.info("Demo: Dimostrando Observer Pattern ");

            try {
                System.out.println("L'observer monitora tutte le transazioni in modo semplice...");

                // Facciamo alcune transazioni per dimostrare l'observer semplice
                if (bankService.getAccount("DEMO001") != null) {
                    System.out.println("\nEseguendo transazioni dimostrative per vedere l'observer:");

                    System.out.println("Deposito di €200:");
                    bankService.performTransaction("DEMO001", "deposit", 200);

                    System.out.println("Prelievo di €50:");
                    bankService.performTransaction("DEMO001", "withdraw", 50);

                    System.out.println("\nNota: l'observer ha stampato un messaggio per ogni transazione!");
                } else {
                    System.out.println("Conto demo non disponibile per la dimostrazione");
                }

            } catch (Exception e) {
                System.out.println("Errore nella demo observer: " + e.getMessage());
                logger.warning("Errore demo observer: " + e.getMessage());
            }

            // Demo del Singleton Pattern
            System.out.println("\n9. Singleton Pattern:");
            logger.info("Demo: Dimostrando Singleton Pattern ");

            BankConfiguration config1 = BankConfiguration.getInstance();
            BankConfiguration config2 = BankConfiguration.getInstance();

            System.out.println("Prima istanza: " + config1.getSystemInfo());
            System.out.println("Seconda istanza: " + config2.getSystemInfo());
            System.out.println("Sono la stessa istanza? " + (config1 == config2)); // Stamperà: true

            // Demo del Memento Pattern
            System.out.println("\n10. Memento Pattern:");

            AccountBackup backup = new AccountBackup();
            Account account = bankService.getAccount("DEMO001");

            if (account != null) {
                // Salva lo stato attuale
                backup.save(account);

                // Fa qualche operazione che cambia il conto
                bankService.performTransaction("DEMO001", "withdraw", 100);
                System.out.println("Saldo dopo prelievo: €" + account.getBalance());

                // Recupera lo stato salvato per vedere com'era prima
                AccountSnapshot oldState = backup.restore("DEMO001");
                System.out.println("Saldo nel backup: €" + oldState.getBalance());
            }

            // Demo del Template Method Pattern
            System.out.println("\n11. Template Method Pattern:");
            logger.info("Demo: Template Method Pattern");

            Account demoAccount = bankService.getAccount("DEMO001");
            if (demoAccount != null) {
                System.out.println("Generando report con Template Method Pattern...");

                // Creiamo i due diversi generatori
                AccountReportGenerator simpleGenerator = new SimpleReportGenerator();
                AccountReportGenerator detailedGenerator = new DetailedReportGenerator();

                System.out.println("\nReport SEMPLICE:");
                String simpleReport = simpleGenerator.generateReport(demoAccount);
                System.out.println(simpleReport);

                System.out.println("Report DETTAGLIATO:");
                String detailedReport = detailedGenerator.generateReport(demoAccount);
                System.out.println(detailedReport);

                System.out.println("Template Method: Stessa struttura, implementazioni diverse!");
                logger.info("Template Method completata per il conto " + demoAccount.getAccountNumber());
            } else {
                System.out.println("Serve un conto per vedere il Template Method Pattern");
                logger.warning("Demo Template Method saltata: nessun conto disponibile");
            }

            // Demo del Bridge Pattern
            System.out.println("\n12. Bridge Pattern:");
            logger.info("Demo: Bridge Pattern per output flessibile");

            Account demoBridgeAccount = bankService.getAccount("DEMO001");
            if (demoAccount != null) {
                System.out.println("Mostrando stesso conto con diversi output...");

                // Output su console
                AccountDisplay consoleDisplay = new AccountDisplay(new ConsoleOutputDevice());
                consoleDisplay.showAccount(demoBridgeAccount);
                consoleDisplay.finish();

                // Output su file
                AccountDisplay fileDisplay = new DetailedAccountDisplay(new FileOutputDevice("conto_report.txt"));
                fileDisplay.showAccount(demoBridgeAccount);
                fileDisplay.finish();

                // Cambio dispositivo a runtime!
                AccountDisplay flexibleDisplay = new AccountDisplay(new ConsoleOutputDevice());
                flexibleDisplay.showAccount(demoBridgeAccount);
                flexibleDisplay.changeOutputDevice(new FileOutputDevice("altro_file.txt"));
                flexibleDisplay.showAccount(demoBridgeAccount); // Ora va su file!
                flexibleDisplay.finish();
            }

        } catch (Exception e) {
            System.out.println("Errore nella demo: " + e.getMessage());
            logger.severe("Errore durante dimostrazione: " + e.getMessage());
        }
    }
}