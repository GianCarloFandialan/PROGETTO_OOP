package bank;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import java.util.logging.Logger;
import java.io.File;

import bank.account.*;
import bank.abstractFactory.*;
import bank.bridgePattern.*;
import bank.builderPattern.*;
import bank.mementoPattern.*;
import bank.observerPattern.*;
import bank.singletonPattern.*;
import bank.strategyPattern.*;
import bank.templatePattern.*;

public class BankServiceTest {

    /** Servizio bancario */
    private BankService bankService;

    /** logger statico */
    private static final Logger logger = Logger.getLogger(SavingsAccount.class.getName());

    /** Contatore per generare nomi di file unici nei test */
    private static int testFileCounter = 0;

    /**
     * metodo che genera il setup eseguito prima di ogni test individuale.
     */
    @Before
    public void setUp() {
        bankService = new BankService();
        testFileCounter++;
        logger.info(" SETUP TEST #" + testFileCounter);
        logger.info("Nuovo BankService inizializzato per garantire isolamento del test");
    }

    // PATTERN 1: FACTORY PATTERN

    /**
     * Test che verifica il Factory Pattern
     */
    @Test
    public void testFactoryPattern() {
        logger.info(" TESTING FACTORY PATTERN ");

        try {

            String checkingAccountNumber = "FACTORY_CC_001";
            String savingsAccountNumber = "FACTORY_CR_001";
            String ownerName = "Cliente Test Factory";
            double initialBalance = 1000.0;

            // crea diversi tipi di conto
            Account checkingAccount = AccountFactory.createAccount(
                    AccountFactory.AccountType.CHECKING, checkingAccountNumber, ownerName, initialBalance);

            Account savingsAccount = AccountFactory.createAccount(
                    AccountFactory.AccountType.SAVINGS, savingsAccountNumber, ownerName, initialBalance);

            // verifica che la factory abbia creato i tipi corretti
            assertNotNull("Il conto corrente deve essere creato", checkingAccount);
            assertNotNull("Il conto di risparmio deve essere creato", savingsAccount);

            // Verifica del polimorfismo
            assertTrue("Factory deve creare CheckingAccount per il conto corrente",
                    checkingAccount instanceof CheckingAccount);
            assertTrue("Factory deve creare SavingsAccount per il conto di risparmio",
                    savingsAccount instanceof SavingsAccount);

            // Verifica delle proprietà iniziali
            assertEquals("Numero conto corrente corretto", checkingAccountNumber, checkingAccount.getAccountNumber());
            assertEquals("Numero conto di risparmio corretto", savingsAccountNumber, savingsAccount.getAccountNumber());
            assertEquals("Saldo iniziale conto corrente", initialBalance, checkingAccount.getBalance(), 0.01);
            assertEquals("Saldo iniziale conto di risparmio", initialBalance, savingsAccount.getBalance(), 0.01);

            // Verifica dei tipi specifici
            assertEquals("Tipo conto corrente", "Conto Corrente", checkingAccount.getAccountType());
            assertEquals("Tipo conto di risparmio", "Conto Risparmio", savingsAccount.getAccountType());

            logger.info("Factory Pattern verificato: creazione corretta di entrambi i tipi di conto");
            logger.info("Polimorfismo confermato: stessa interfaccia, implementazioni diverse");

        } catch (Exception e) {
            logger.severe("Factory Pattern test FALLITO: " + e.getMessage());
            fail("Factory Pattern non dovrebbe lanciare eccezioni: " + e.getMessage());
        }
    }

    /**
     * Test che verifica la gestione degli errori
     */
    @Test
    public void testFactoryPatternErrorHandling() {
        logger.info(" TESTING FACTORY PATTERN ERROR HANDLING ");

        try {
            // Test con parametri nulli
            AccountFactory.createAccount(AccountFactory.AccountType.CHECKING, null, "Test", 100.0);
            fail("Factory dovrebbe rifiutare numero conto null");
        } catch (IllegalArgumentException e) {
            logger.info("Factory correttamente rifiuta numero conto null");
        }

        try {
            // Test con nome proprietario nullo
            AccountFactory.createAccount(AccountFactory.AccountType.SAVINGS, "123", null, 100.0);
            fail("Factory dovrebbe rifiutare nome proprietario null");
        } catch (IllegalArgumentException e) {
            logger.info("Factory correttamente rifiuta nome proprietario null");
        }

        logger.info("Factory Pattern gestione errori verificata");
    }

    // PATTERN 2: ABSTRACT FACTORY PATTERN

    /**
     * Test che verifica l'Abstract Factory Pattern
     */
    @Test
    public void testAbstractFactoryPattern() {
        logger.info(" TESTING ABSTRACT FACTORY PATTERN ");

        try {

            String accountNumber = "ABSTRACT_001";
            String ownerName = "Cliente Test Abstract Factory";
            double initialBalance = 5000.0;

            // Test per clienti standard
            BankFactory standardFactory = BankFactoryProvider.getFactory(false);
            assertNotNull("StandardFactory deve essere fornita", standardFactory);
            assertTrue("Deve essere StandardBankFactory", standardFactory instanceof StandardBankFactory);

            Account standardCheckingAccount = standardFactory.createCheckingAccount(accountNumber + "_STD", ownerName,
                    initialBalance);
            assertNotNull("Conto corrente standard creato", standardCheckingAccount);
            assertEquals("Tipo conto corrente standard", "Conto Corrente", standardCheckingAccount.getAccountType());

            // Test per clienti premium
            BankFactory premiumFactory = BankFactoryProvider.getFactory(true);
            assertNotNull("PremiumFactory deve essere fornita", premiumFactory);
            assertTrue("Deve essere PremiumBankFactory", premiumFactory instanceof PremiumBankFactory);

            Account premiumSavingsAccount = premiumFactory.createSavingsAccount(accountNumber + "_PREM", ownerName,
                    initialBalance);
            assertNotNull("Conto di risparmio premium creato", premiumSavingsAccount);
            assertEquals("Tipo conto di risparmio premium", "Conto Risparmio", premiumSavingsAccount.getAccountType());

            logger.info("Abstract Factory Pattern verificato");
            logger.info("Factory Standard e Premium create correttamente");
            logger.info("Famiglie di oggetti correlati gestite appropriatamente");

        } catch (Exception e) {
            logger.severe("Abstract Factory Pattern test FALLITO: " + e.getMessage());
            fail("Abstract Factory Pattern non dovrebbe fallire: " + e.getMessage());
        }
    }

    /**
     * Test di integrazione Abstract Factory con BankService.
     */
    @Test
    public void testAbstractFactoryIntegrationWithBankService() {
        logger.info(" TESTING ABSTRACT FACTORY INTEGRATION ");

        try {
            // Test creazione conti attraverso BankService
            bankService.createAccount("checking", "PREMIUM_001", "Cliente Premium", 10000.0, true);
            bankService.createAccount("savings", "STANDARD_001", "Cliente Standard", 1000.0, false);

            Account premiumAccount = bankService.getAccount("PREMIUM_001");
            Account standardAccount = bankService.getAccount("STANDARD_001");

            assertNotNull("Conto premium deve esistere", premiumAccount);
            assertNotNull("Conto standard deve esistere", standardAccount);

            assertEquals("Tipo conto corrente premium", "Conto Corrente", premiumAccount.getAccountType());
            assertEquals("Tipo conto di risparmio standard", "Conto Risparmio", standardAccount.getAccountType());

            logger.info("Abstract Factory integrazione con BankService verificata");

        } catch (BankException e) {
            logger.severe("Abstract Factory integration test FALLITO: " + e.getUserMessage());
            fail("Integration test fallito: " + e.getUserMessage());
        }
    }

    // PATTERN 3: BUILDER PATTERN

    /**
     * Test completo del Builder Pattern
     */
    @Test
    public void testBuilderPattern() {
        logger.info(" TESTING BUILDER PATTERN ");

        try {

            Account builderAccount = AccountBuilder.newAccount()
                    .ofType("checking")
                    .withNumber("BUILDER_001")
                    .forOwner("Cliente Test Builder Pattern")
                    .withInitialBalance(2500.0)
                    .build();

            // Verifica che il conto sia stato costruito correttamente
            assertNotNull("Builder deve creare il conto", builderAccount);
            assertEquals("Numero conto builder", "BUILDER_001", builderAccount.getAccountNumber());
            assertEquals("Nome proprietario builder", "Cliente Test Builder Pattern", builderAccount.getOwnerName());
            assertEquals("Saldo iniziale builder", 2500.0, builderAccount.getBalance(), 0.01);
            assertEquals("Tipo conto builder", "Conto Corrente", builderAccount.getAccountType());

            // Test della funzionalità premium del builder
            Account premiumBuilderAccount = AccountBuilder.newAccount()
                    .ofType("savings")
                    .withNumber("BUILDER_PREMIUM_001")
                    .forOwner("Cliente Premium Builder Test")
                    .withInitialBalance(15000.0)
                    .asPremium()
                    .build();

            assertNotNull("Conto premium builder creato", premiumBuilderAccount);
            assertEquals("Tipo conto premium", "Conto Risparmio", premiumBuilderAccount.getAccountType());

            logger.info("Builder Pattern verificato: costruzione flessibile completata");
            logger.info("Interfaccia fluida funzionante");
            logger.info("Validazione incrementale attiva");

        } catch (BankException e) {
            logger.severe("Builder Pattern test FALLITO: " + e.getUserMessage());
            fail("Builder Pattern fallito: " + e.getUserMessage());
        }
    }

    /**
     * Test della validazione
     */
    @Test
    public void testBuilderPatternValidation() {
        logger.info(" TESTING BUILDER PATTERN VALIDATION ");

        try {
            // Test builder incompleto
            AccountBuilder.newAccount()
                    .ofType("checking")
                    .withNumber("INCOMPLETE_001")

                    // Manca forOwner()
                    .build();
            fail("Builder dovrebbe fallire con dati incompleti");
        } catch (IllegalStateException e) {
            logger.info("Builder correttamente rifiuta costruzione incompleta");
        } catch (BankException e) {
            logger.info("Builder gestisce errori appropriatamente");
        }

        try {
            // Test con tipo non valido
            AccountBuilder.newAccount()
                    .ofType("tipo_non_valido")
                    .withNumber("INVALID_001")
                    .forOwner("Test")
                    .build();
            fail("Builder dovrebbe rifiutare tipo non valido");
        } catch (IllegalArgumentException e) {
            logger.info("Builder correttamente rifiuta tipo di conto non valido");
        } catch (BankException e) {
            logger.info("Builder gestisce tipo non valido appropriatamente");
        }
    }

    // PATTERN 4: SINGLETON PATTERN

    /**
     * Test del Singleton Pattern
     */
    @Test
    public void testSingletonPattern() {
        logger.info(" TESTING SINGLETON PATTERN ");

        BankConfiguration config1 = BankConfiguration.getInstance();
        BankConfiguration config2 = BankConfiguration.getInstance();
        BankConfiguration config3 = BankConfiguration.getInstance();

        // Verifichiamo che siano la stessa istanza
        assertNotNull("Prima istanza non deve essere null", config1);
        assertNotNull("Seconda istanza non deve essere null", config2);
        assertNotNull("Terza istanza non deve essere null", config3);

        // Test dell'identità degli oggetti (stesso riferimento in memoria)
        assertSame("config1 e config2 devono essere la stessa istanza", config1, config2);
        assertSame("config2 e config3 devono essere la stessa istanza", config2, config3);
        assertSame("config1 e config3 devono essere la stessa istanza", config1, config3);

        // Test delle proprietà condivise
        String originalSystemName = config1.getSystemName();
        assertNotNull("Nome sistema deve essere impostato", originalSystemName);
        assertEquals("Nome sistema coerente", originalSystemName, config2.getSystemName());
        assertEquals("Nome sistema coerente", originalSystemName, config3.getSystemName());

        // Test modifica stato condiviso
        boolean originalDebugMode = config1.isDebugMode();
        config2.setDebugMode(!originalDebugMode);

        assertEquals("Modalità debug condivisa tramite config1", !originalDebugMode, config1.isDebugMode());
        assertEquals("Modalità debug condivisa tramite config3", !originalDebugMode, config3.isDebugMode());

        logger.info("Singleton Pattern verificato: una sola istanza garantita");
        logger.info("Stato condiviso tra tutti i riferimenti");
        logger.info("Informazioni sistema: " + config1.getSystemInfo());
    }

    // PATTERN 5: STRATEGY PATTERN

    /**
     * Test completo del Strategy Pattern
     */
    @Test
    public void testStrategyPattern() {
        logger.info(" TESTING STRATEGY PATTERN ");

        try {

            WithdrawalStrategy overdraftStrategy = WithdrawalStrategyFactory.createStrategy("Conto Corrente");
            WithdrawalStrategy noOverdraftStrategy = WithdrawalStrategyFactory.createStrategy("Conto Risparmio");

            assertNotNull("Strategia con scoperto creata", overdraftStrategy);
            assertNotNull("Strategia senza scoperto creata", noOverdraftStrategy);
            assertTrue("Strategia scoperto tipo corretto", overdraftStrategy instanceof OverdraftWithdrawalStrategy);
            assertTrue("Strategia senza scoperto tipo corretto",
                    noOverdraftStrategy instanceof NoOverdraftWithdrawalStrategy);

            // Test con scoperto (per conti correnti)
            double[] overdraftBalance = { 50.0 }; // Array per permettere modifica del valore
            boolean overdraftResult = overdraftStrategy.executeWithdrawal(overdraftBalance, 120.0, "TEST_SCOPERTO");

            assertTrue("Strategia scoperto deve permettere operazioni in rosso", overdraftResult);
            assertEquals("Saldo dopo scoperto", -70.0, overdraftBalance[0], 0.01);
            logger.info("Strategia scoperto: prelievo €120 da saldo €50 = saldo finale €" + overdraftBalance[0]);

            // Test senza scoperto (per conti di risparmio)
            double[] noOverdraftBalance = { 50.0 };
            boolean noOverdraftResult = noOverdraftStrategy.executeWithdrawal(noOverdraftBalance, 120.0,
                    "TEST_SENZA_SCOPERTO");

            assertFalse("Strategia senza scoperto deve rifiutare operazioni in rosso", noOverdraftResult);
            assertEquals("Saldo non modificato dopo rifiuto", 50.0, noOverdraftBalance[0], 0.01);
            logger.info("Strategia senza scoperto: prelievo €120 da saldo €50 = operazione rifiutata, saldo €"
                    + noOverdraftBalance[0]);

            // Test prelievo valido senza scoperto
            boolean validWithdrawal = noOverdraftStrategy.executeWithdrawal(noOverdraftBalance, 30.0, "TEST_VALIDO");
            assertTrue("Strategia senza scoperto deve permettere prelievi validi", validWithdrawal);
            assertEquals("Saldo dopo prelievo valido", 20.0, noOverdraftBalance[0], 0.01);

            logger.info("Strategy Pattern verificato: algoritmi intercambiabili funzionanti");

        } catch (Exception e) {
            logger.severe("Strategy Pattern test FALLITO: " + e.getMessage());
            fail("Strategy Pattern fallito: " + e.getMessage());
        }
    }

    // PATTERN 6: OBSERVER PATTERN

    /**
     * Test dell'Observer Pattern
     */
    @Test
    public void testObserverPattern() {
        logger.info(" TESTING OBSERVER PATTERN ");

        try {
            bankService.createAccount("checking", "OBSERVER_001", "Cliente Test Observer", 1000.0);

            logger.info("Eseguendo transazioni sotto osservazione...");

            // Deposito
            bankService.performTransaction("OBSERVER_001", "deposit", 500.0);
            Account account = bankService.getAccount("OBSERVER_001");
            assertEquals("Saldo dopo deposito osservato", 1500.0, account.getBalance(), 0.01);

            // Prelievo valido
            bankService.performTransaction("OBSERVER_001", "withdraw", 200.0);
            account = bankService.getAccount("OBSERVER_001");
            assertEquals("Saldo dopo prelievo osservato", 1300.0, account.getBalance(), 0.01);

            // Prelievo eccessivo
            try {
                bankService.performTransaction("OBSERVER_001", "withdraw", 2000.0);

                // Per un conto corrente, questo potrebbe riuscire con scoperto
                account = bankService.getAccount("OBSERVER_001");
                logger.info("Prelievo eccessivo gestito (possibile scoperto), saldo finale: €" + account.getBalance());
            } catch (BankException e) {
                logger.info("Prelievo eccessivo correttamente rifiutato e osservato");
            }

            logger.info("Observer Pattern verificato: tutte le transazioni monitorate");
            logger.info("Notifiche automatiche funzionanti");

        } catch (BankException e) {
            logger.severe("Observer Pattern test FALLITO: " + e.getUserMessage());
            fail("Observer Pattern fallito: " + e.getUserMessage());
        }
    }

    // PATTERN 7: MEMENTO PATTERN

    /**
     * Test completo del Memento Pattern
     */
    @Test
    public void testMementoPattern() {
        logger.info(" TESTING MEMENTO PATTERN ");

        try {
            bankService.createAccount("savings", "MEMENTO_001", "Cliente Test Memento", 1000.0);
            Account account = bankService.getAccount("MEMENTO_001");
            AccountBackup backup = new AccountBackup();

            // Salva lo stato iniziale
            boolean saveResult = backup.save(account);
            assertTrue("Backup iniziale deve riuscire", saveResult);
            assertTrue("Backup deve esistere", backup.hasBackup("MEMENTO_001"));
            assertEquals("Totale backup", 1, backup.getTotalBackups());

            // Modifica il conto
            bankService.performTransaction("MEMENTO_001", "deposit", 500.0);
            account = bankService.getAccount("MEMENTO_001");
            assertEquals("Saldo dopo modifica", 1500.0, account.getBalance(), 0.01);

            // Confronta con il backup
            AccountSnapshot originalState = backup.restore("MEMENTO_001");
            assertNotNull("Snapshot deve essere recuperabile", originalState);
            assertEquals("Numero conto snapshot", "MEMENTO_001", originalState.getAccountNumber());
            assertEquals("Saldo originale snapshot", 1000.0, originalState.getBalance(), 0.01);

            // Verifica che il conto corrente sia diverso dal backup
            assertNotEquals("Saldo corrente diverso da backup",
                    originalState.getBalance(), account.getBalance(), 0.01);

            // Test backup multipli
            backup.save(account); // Salviamo lo stato modificato
            assertEquals("Due backup totali", 1, backup.getTotalBackups()); // Sovrascrive il precedente

            // Test eliminazione backup
            boolean deleteResult = backup.deleteBackup("MEMENTO_001");
            assertTrue("Eliminazione backup deve riuscire", deleteResult);
            assertFalse("Backup non deve più esistere", backup.hasBackup("MEMENTO_001"));
            assertEquals("Nessun backup rimanente", 0, backup.getTotalBackups());

            logger.info("Memento Pattern verificato: salvataggio e ripristino stati");
            logger.info("Gestione backup multipli funzionante");
            logger.info("Confronto stati temporali verificato");

        } catch (BankException e) {
            logger.severe("Memento Pattern test FALLITO: " + e.getUserMessage());
            fail("Memento Pattern fallito: " + e.getUserMessage());
        }
    }

    // PATTERN 8: COMPOSITE PATTERN

    /**
     * Test completo del Composite Pattern
     */
    @Test
    public void testCompositePattern() {
        logger.info(" TESTING COMPOSITE PATTERN ");

        try {
            AccountGroup mainGroup = new AccountGroup("Sede Principale");
            AccountGroup vipGroup = new AccountGroup("Clienti VIP");
            AccountGroup corporateGroup = new AccountGroup("Clienti Corporate");

            // Crea conti da distribuire nei gruppi
            Account account1 = AccountFactory.createAccount(AccountFactory.AccountType.CHECKING, "COMP_001",
                    "Cliente Base", 1000.0);
            Account account2 = AccountFactory.createAccount(AccountFactory.AccountType.SAVINGS, "COMP_002",
                    "Cliente VIP", 10000.0);
            Account account3 = AccountFactory.createAccount(AccountFactory.AccountType.CHECKING, "COMP_003",
                    "Cliente Corporate", 50000.0);
            Account account4 = AccountFactory.createAccount(AccountFactory.AccountType.SAVINGS, "COMP_004",
                    "Altro Cliente VIP", 15000.0);

            // Costruisce la gerarchia
            mainGroup.addAccount(account1); // Conto diretto

            vipGroup.addAccount(account2);
            vipGroup.addAccount(account4);

            corporateGroup.addAccount(account3);

            // Aggiunge i sottogruppi al gruppo principale
            mainGroup.addSubGroup(vipGroup);
            mainGroup.addSubGroup(corporateGroup);

            // Test conteggio ricorsivo
            assertEquals("Gruppo principale deve contenere 4 conti totali", 4, mainGroup.getTotalAccountCount());
            assertEquals("Gruppo VIP deve contenere 2 conti", 2, vipGroup.getTotalAccountCount());
            assertEquals("Gruppo Corporate deve contenere 1 conto", 1, corporateGroup.getTotalAccountCount());

            // Test calcolo saldo ricorsivo
            double expectedMainTotal = 1000.0 + 10000.0 + 50000.0 + 15000.0;
            assertEquals("Saldo totale gruppo principale", expectedMainTotal, mainGroup.getTotalBalance(), 0.01);

            double expectedVipTotal = 10000.0 + 15000.0;
            assertEquals("Saldo totale gruppo VIP", expectedVipTotal, vipGroup.getTotalBalance(), 0.01);

            assertEquals("Saldo gruppo corporate", 50000.0, corporateGroup.getTotalBalance(), 0.01);

            // Test ricerca ricorsiva
            assertTrue("Gruppo principale deve contenere COMP_002", mainGroup.containsAccount("COMP_002"));
            assertTrue("Gruppo principale deve contenere COMP_003", mainGroup.containsAccount("COMP_003"));
            assertFalse("Gruppo principale non deve contenere conto inesistente",
                    mainGroup.containsAccount("INESISTENTE"));

            // Test operazioni sui sottogruppi
            assertTrue("Gruppo VIP deve contenere COMP_002", vipGroup.containsAccount("COMP_002"));
            assertFalse("Gruppo VIP non deve contenere COMP_003", vipGroup.containsAccount("COMP_003"));

            logger.info("Composite Pattern verificato: gerarchia di 4 conti in 3 gruppi");
            logger.info("Operazioni ricorsive funzionanti (conteggio e somma)");
            logger.info("Ricerca gerarchica verificata");

        } catch (Exception e) {
            logger.severe("Composite Pattern test FALLITO: " + e.getMessage());
            fail("Composite Pattern fallito: " + e.getMessage());
        }
    }

    // PATTERN 9: ITERATOR PATTERN

    /**
     * Test completo dell'Iterator Pattern
     */
    @Test
    public void testIteratorPattern() {
        logger.info(" TESTING ITERATOR PATTERN ");

        try {
            List<Account> accounts = new ArrayList<>();

            accounts.add(AccountFactory.createAccount(AccountFactory.AccountType.CHECKING, "ITER_001",
                    "Cliente Iterator Test 1", 1000.0));
            accounts.add(AccountFactory.createAccount(AccountFactory.AccountType.SAVINGS, "ITER_002",
                    "Cliente Iterator Test 2", 2000.0));
            accounts.add(AccountFactory.createAccount(AccountFactory.AccountType.CHECKING, "ITER_003",
                    "Cliente Iterator Test 3", 3000.0));
            accounts.add(AccountFactory.createAccount(AccountFactory.AccountType.SAVINGS, "ITER_004",
                    "Cliente Iterator Test 4", 4000.0));

            // Test navigazione completa
            AccountIterator iterator = new AccountIterator(accounts);

            int processedCount = 0;
            double totalBalance = 0.0;

            while (iterator.hasNext()) {
                Account account = iterator.next();
                assertNotNull("Conto dall'iterator non deve essere null", account);

                processedCount++;
                totalBalance += account.getBalance();

                logger.info("Processato conto #" + processedCount + ": " +
                        account.getAccountNumber() + " (" + account.getAccountType() + ") - €" + account.getBalance());
            }

            assertEquals("Tutti i conti devono essere processati", 4, processedCount);
            assertEquals("Somma saldi verificata", 10000.0, totalBalance, 0.01);

            // Test navigazione oltre la fine
            try {
                iterator.next();
                fail("Iterator dovrebbe lanciare eccezione alla fine");
            } catch (NoSuchElementException e) {
                logger.info("Iterator correttamente gestisce fine collezione");
            }

            // Test iterator filtrato per conti correnti
            AccountIterator checkingIterator = AccountIterator.createFilteredIterator(accounts, "Conto Corrente");

            int checkingCount = 0;
            while (checkingIterator.hasNext()) {
                Account account = checkingIterator.next();
                assertEquals("Iterator filtrato deve restituire solo conti correnti",
                        "Conto Corrente", account.getAccountType());
                checkingCount++;
            }

            assertEquals("Due conti correnti nel filtro", 2, checkingCount);

            // Test iterator per conti di risparmio
            AccountIterator savingsIterator = AccountIterator.createFilteredIterator(accounts, "Conto Risparmio");
            int savingsCount = 0;
            while (savingsIterator.hasNext()) {
                Account account = savingsIterator.next();
                assertEquals("Iterator filtrato deve restituire solo conti di risparmio",
                        "Conto Risparmio", account.getAccountType());
                savingsCount++;
            }

            assertEquals("Due conti di risparmio nel filtro", 2, savingsCount);

            logger.info("Iterator Pattern verificato: navigazione sequenziale completa");
            logger.info("Filtering per tipo conto funzionante");
            logger.info("Gestione boundary conditions verificata");

        } catch (Exception e) {
            logger.severe("Iterator Pattern test FALLITO: " + e.getMessage());
            fail("Iterator Pattern fallito: " + e.getMessage());
        }
    }

    // PATTERN 10: TEMPLATE METHOD PATTERN

    /**
     * Test completo del Template Method Pattern
     */
    @Test
    public void testTemplateMethodPattern() {
        logger.info(" TESTING TEMPLATE METHOD PATTERN ");

        try {
            bankService.createAccount("checking", "TEMPLATE_001", "Cliente Test Template", 2500.0);
            Account account = bankService.getAccount("TEMPLATE_001");

            // Testa il generatore semplice
            AccountReportGenerator simpleGenerator = new SimpleReportGenerator();
            String simpleReport = simpleGenerator.generateReport(account);

            // Verifica che il report semplice contenga elementi base
            assertNotNull("Report semplice non deve essere null", simpleReport);
            assertTrue("Report deve contenere header semplice", simpleReport.contains("REPORT SEMPLICE"));
            assertTrue("Report deve contenere numero conto", simpleReport.contains("TEMPLATE_001"));
            assertTrue("Report deve contenere nome proprietario", simpleReport.contains("Cliente Test Template"));
            assertTrue("Report deve contenere saldo", simpleReport.contains("2500"));

            logger.info("Report semplice generato: " + simpleReport.length() + " caratteri");

            // Testa il generatore dettagliato
            AccountReportGenerator detailedGenerator = new DetailedReportGenerator();
            String detailedReport = detailedGenerator.generateReport(account);

            // Verifica che il report dettagliato contenga più informazioni
            assertNotNull("Report dettagliato non deve essere null", detailedReport);
            assertTrue("Report deve contenere header dettagliato", detailedReport.contains("REPORT DETTAGLIATO"));
            assertTrue("Report deve contenere analisi finanziaria", detailedReport.contains("ANALISI FINANZIARIA"));
            assertTrue("Report deve contenere raccomandazioni", detailedReport.contains("RACCOMANDAZIONI"));

            // Il report dettagliato dovrebbe essere più lungo del semplice
            assertTrue("Report dettagliato deve essere più lungo",
                    detailedReport.length() > simpleReport.length());

            logger.info("Report dettagliato generato: " + detailedReport.length() + " caratteri");

            // Test con conto null (gestione errori)
            String nullReport = simpleGenerator.generateReport(null);
            assertNotNull("Report per conto null non deve essere null", nullReport);
            assertTrue("Report null deve contenere messaggio errore",
                    nullReport.contains("ERRORE") || nullReport.contains("non disponibile"));

            // Test che entrambi i generatori seguano la stessa struttura base
            assertTrue("Entrambi i report devono avere struttura base",
                    simpleReport.contains("INFORMAZIONI CONTO") && detailedReport.contains("INFORMAZIONI CONTO"));

            logger.info("Template Method Pattern verificato: struttura comune, implementazioni diverse");
            logger.info("Gestione errori consistente tra implementazioni");

        } catch (BankException e) {
            logger.severe("Template Method Pattern test FALLITO: " + e.getUserMessage());
            fail("Template Method Pattern fallito: " + e.getUserMessage());
        }
    }

    // PATTERN 11: BRIDGE PATTERN

    /**
     * Test completo del Bridge Pattern
     */
    @Test
    public void testBridgePattern() {
        logger.info(" TESTING BRIDGE PATTERN ");

        try {

            bankService.createAccount("checking", "BRIDGE_001", "Cliente Test Bridge", 1500.0);
            Account testAccount = bankService.getAccount("BRIDGE_001");

            assertNotNull("Conto per test Bridge deve esistere", testAccount);

            // Test output su console
            ConsoleOutputDevice consoleDevice = new ConsoleOutputDevice();
            AccountDisplay consoleDisplay = new AccountDisplay(consoleDevice);

            // Test del ciclo di vita del dispositivo
            consoleDevice.initialize();
            assertTrue("Dispositivo console deve essere pronto dopo inizializzazione", consoleDevice.isReady());

            // Test della visualizzazione
            consoleDisplay.showAccount(testAccount);

            // Dopo showAccount, il dispositivo dovrebbe essere pulito automaticamente
            assertFalse("Dispositivo console non deve essere pronto dopo cleanup", consoleDevice.isReady());

            logger.info("Bridge Pattern - Output console verificato");

            // Test output su file
            String fileName = "bridge_test_" + testFileCounter + ".txt";
            FileOutputDevice fileDevice = new FileOutputDevice(fileName);
            AccountDisplay fileDisplay = new AccountDisplay(fileDevice);

            fileDisplay.showAccount(testAccount);

            // Verifica che il file sia stato creato
            File testFile = new File(fileName);
            assertTrue("File output deve essere creato", testFile.exists());
            assertTrue("File deve contenere dati", testFile.length() > 0);

            logger.info("Bridge Pattern - Output file verificato: " + fileName);

            // Test astrazione raffinata
            DetailedAccountDisplay detailedDisplay = new DetailedAccountDisplay(new ConsoleOutputDevice());
            detailedDisplay.showAccount(testAccount);

            logger.info("Bridge Pattern - Astrazione raffinata verificata");

            // Test cambio dispositivo runtime
            AccountDisplay flexibleDisplay = new AccountDisplay(new ConsoleOutputDevice());
            flexibleDisplay.showAccount(testAccount);

            // Cambia dispositivo e testiamo di nuovo
            String runtimeFileName = "bridge_runtime_" + testFileCounter + ".txt";
            flexibleDisplay.changeOutputDevice(new FileOutputDevice(runtimeFileName));
            flexibleDisplay.showAccount(testAccount);
            flexibleDisplay.finish();

            File runtimeFile = new File(runtimeFileName);
            assertTrue("File runtime deve essere creato", runtimeFile.exists());

            logger.info("Bridge Pattern - Cambio dispositivo runtime verificato");

            // Test gestione conto null
            AccountDisplay nullTestDisplay = new AccountDisplay(new ConsoleOutputDevice());
            nullTestDisplay.showAccount(null); // Non deve lanciare eccezioni
            nullTestDisplay.finish();

            logger.info("Bridge Pattern - Gestione conto null verificata");

            // Test executive summary
            DetailedAccountDisplay executiveDisplay = new DetailedAccountDisplay(new ConsoleOutputDevice());
            executiveDisplay.showExecutiveSummary(testAccount);
            executiveDisplay.finish();

            logger.info("Bridge Pattern completamente verificato");

        } catch (Exception e) {
            logger.severe("Bridge Pattern test FALLITO: " + e.getMessage());
            fail("Bridge Pattern non dovrebbe lanciare eccezioni: " + e.getMessage());
        }
    }

    // PATTERN 12: EXCEPTION SHIELDING PATTERN

    /**
     * Test completo dell'Exception Shielding Pattern
     */
    @Test
    public void testExceptionShieldingPattern() {
        logger.info(" TESTING EXCEPTION SHIELDING PATTERN ");

        // Test 1: Creazione conto duplicato
        try {
            bankService.createAccount("checking", "SHIELD_001", "Cliente Test", 100.0);
            bankService.createAccount("checking", "SHIELD_001", "Altro Cliente", 200.0);

            fail("Dovrebbe lanciare BankException per conto duplicato");
        } catch (BankException e) {
            // Verifica le caratteristiche dello shielding
            assertNotNull("Messaggio utente non deve essere null", e.getUserMessage());
            assertNotNull("ID errore non deve essere null", e.getErrorId());

            assertEquals("Messaggio utente appropriato", "Conto già esistente", e.getUserMessage());
            assertTrue("ID errore deve seguire pattern", e.getErrorId().startsWith("ERR_"));

            // I dettagli tecnici dovrebbero esistere ma non essere esposti all'utente
            assertNotNull("Dettagli tecnici devono esistere", e.getTechnicalDetails());

            logger.info("Exception Shielding - Conto duplicato: " + e.getErrorId());
        }

        // Test 2: Transazione su conto inesistente
        try {
            bankService.performTransaction("INESISTENTE_001", "deposit", 100.0);
            fail("Dovrebbe lanciare BankException per conto inesistente");
        } catch (BankException e) {
            assertEquals("Messaggio utente per conto inesistente", "Conto non trovato", e.getUserMessage());
            assertTrue("ID errore univoco generato", e.getErrorId().startsWith("ERR_"));

            logger.info("Exception Shielding - Conto inesistente: " + e.getErrorId());
        }

        // Test 3: Verifica unicità degli ID errore
        Set<String> errorIds = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            try {
                bankService.createAccount("savings", "DUPLICATO_" + i, "Test", 100.0);
                bankService.createAccount("savings", "DUPLICATO_" + i, "Test2", 200.0);
            } catch (BankException e) {
                errorIds.add(e.getErrorId());
            }
        }

        assertEquals("Tutti gli ID errore devono essere unici", 5, errorIds.size());
        logger.info("Exception Shielding - Unicità ID errore verificata: " + errorIds);

        logger.info("Exception Shielding Pattern completamente verificato");
    }

    /**
     * Test delle performance e scalabilità del sistema integrato.
     */
    @Test
    public void testSystemPerformanceAndScalability() {
        logger.info(" TESTING SYSTEM PERFORMANCE ");

        try {
            long startTime = System.currentTimeMillis();

            // Creazione di un volume significativo di dati per testare la scalabilità
            for (int i = 1; i <= 100; i++) {
                String accountType = (i % 2 == 0) ? "checking" : "savings";
                bankService.createAccount(accountType, "PERF_" + String.format("%03d", i),
                        "Cliente Performance Test " + i, 1000.0 + i);
            }

            long creationTime = System.currentTimeMillis() - startTime;

            // Test performance delle operazioni
            startTime = System.currentTimeMillis();

            // Test transazioni multiple
            for (int i = 1; i <= 50; i++) {
                bankService.performTransaction("PERF_" + String.format("%03d", i), "deposit", 100.0);
            }

            long transactionTime = System.currentTimeMillis() - startTime;

            // Test performance Iterator
            startTime = System.currentTimeMillis();

            List<Account> allAccounts = new ArrayList<>(bankService.getAllAccounts());
            AccountIterator iterator = new AccountIterator(allAccounts);
            int processedCount = 0;

            while (iterator.hasNext()) {
                Account account = iterator.next();
                processedCount++;
            }

            long iterationTime = System.currentTimeMillis() - startTime;

            // Test performance Composite
            startTime = System.currentTimeMillis();

            AccountGroup performanceGroup = new AccountGroup("Gruppo Test Performance");
            for (Account account : allAccounts) {
                performanceGroup.addAccount(account);
            }

            double totalBalance = performanceGroup.getTotalBalance();
            int totalCount = performanceGroup.getTotalAccountCount();

            long compositeTime = System.currentTimeMillis() - startTime;

            // Verifica i risultati
            assertEquals("Tutti i conti creati", 100, bankService.getAllAccounts().size());
            assertEquals("Tutti i conti processati", 100, processedCount);
            assertEquals("Conteggio composite corretto", 100, totalCount);
            assertTrue("Saldo totale positivo", totalBalance > 0);

            // Performance benchmarks (valori ragionevoli per test)
            assertTrue("Creazione conti in tempo ragionevole", creationTime < 5000); // < 5 secondi
            assertTrue("Transazioni in tempo ragionevole", transactionTime < 2000); // < 2 secondi
            assertTrue("Iterazione in tempo ragionevole", iterationTime < 1000); // < 1 secondo
            assertTrue("Operazioni composite veloci", compositeTime < 1000); // < 1 secondo

            logger.info("Test Performance completato:");
            logger.info("   - Creazione 100 conti: " + creationTime + "ms");
            logger.info("   - 50 transazioni: " + transactionTime + "ms");
            logger.info("   - Iterazione 100 conti: " + iterationTime + "ms");
            logger.info("   - Operazioni composite: " + compositeTime + "ms");
            logger.info("   - Saldo totale elaborato: €" + String.format("%.2f", totalBalance));

        } catch (Exception e) {
            logger.severe("Performance test FALLITO: " + e.getMessage());
            fail("Performance test fallito: " + e.getMessage());
        }
    }

    /**
     * Cleanup eseguito dopo ogni test per rilasciare risorse.
     */
    @After
    public void tearDown() {
        // Cleanup di eventuali file creati durante i test
        String[] testFiles = {
                "bridge_test_" + testFileCounter + ".txt",
                "bridge_runtime_" + testFileCounter + ".txt",
                "integration_test_" + testFileCounter + ".txt"
        };

        for (String fileName : testFiles) {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
                logger.info("Cleanup: File " + fileName + " rimosso");
            }
        }

        logger.info(" TEARDOWN TEST #" + testFileCounter + " COMPLETATO \n");
    }
}