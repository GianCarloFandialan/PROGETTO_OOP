# Sistema Bancario - Design Pattern

## Overview

Questo progetto rappresenta un sistema bancario completo e didattico che implementa **dodici design pattern fondamentali** della programmazione orientata agli oggetti. Il sistema simula operazioni bancarie reali mentre serve come catalogo vivente dei design pattern più importanti nell'ingegneria del software.

Il sistema è caratterizzato da una gestione avanzata degli errori, logging multi-livello, persistenza automatica dei dati, monitoraggio delle transazioni in tempo reale, e un sistema di output flessibile che può adattarsi a diverse modalità di visualizzazione.

## Architettura del Sistema

Il sistema è organizzato:

```mermaid
graph TB
    subgraph "Presentation Layer"
        BA[BankApplication<br/>CLI Interface] --> BS[BankService<br/>Business Facade]
    end

    subgraph "Business Logic Layer"
        BS --> AF[AccountFactory<br/>Object Creation]
        BS --> ABF[BankFactory Hierarchy<br/>Family Creation]
        BS --> AC[AccountBackup<br/>Memento Caretaker]
        BS --> OBS[TransactionObserver<br/>Event Monitoring]
    end

    subgraph "Domain Model Layer"
        AF --> ACC[Account Hierarchy<br/>Template Method]
        ACC --> CA[CheckingAccount]
        ACC --> SA[SavingsAccount]
        AG[AccountGroup<br/>Composite] --> ACC
        AI[AccountIterator<br/>Collection Navigation] --> ACC
    end

    subgraph "Configuration Layer"
        BC[BankConfiguration<br/>Singleton] -.-> BS
    end

    subgraph "Strategy Layer"
        WS[WithdrawalStrategy<br/>Algorithm Family] --> CA
        WS --> SA
        WSF[WithdrawalStrategyFactory] --> WS
    end

    subgraph "Builder Layer"
        AB[AccountBuilder<br/>Flexible Construction] --> AF
    end

    subgraph "Bridge Layer"
        AD[AccountDisplay<br/>Abstraction] --> OD[OutputDevice<br/>Implementor]
        DAD[DetailedAccountDisplay<br/>Refined Abstraction] --> AD
        COD[ConsoleOutputDevice] --> OD
        FOD[FileOutputDevice] --> OD
    end

    subgraph "Template Method Layer"
        TM[ReportGenerator Hierarchy] --> ACC
        STM[SimpleReportGenerator] --> TM
        DTM[DetailedReportGenerator] --> TM
    end

    subgraph "Exception Layer"
        BE[BankException<br/>Exception Shielding] -.-> ALL[All Components]
    end

    style BA fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    style BS fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    style ACC fill:#e8f5e8,stroke:#388e3c,stroke-width:2px
    style BC fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    style AD fill:#fce4ec,stroke:#ad1457,stroke-width:2px
    style OD fill:#f3e5f5,stroke:#8e24aa,stroke-width:2px
    style BE fill:#ffebee,stroke:#d32f2f,stroke-width:2px
```

## Catalogo Completo dei Design Pattern

### Pattern Creazionali (Creational Patterns)

#### 1. Factory Pattern - Creazione Centralizzata

Il Factory Pattern fornisce un'interfaccia per creare oggetti senza specificare le loro classi concrete. Nel nostro sistema bancario, rappresenta la fondazione del sistema di creazione degli account.

**Implementazione:** `AccountFactory`

**Problema Risolto:** Come creare diversi tipi di conti bancari (correnti, risparmio) senza che il codice client debba conoscere i dettagli di costruzione specifici di ogni tipo.

**Caratteristiche Chiave:**

- Validazione centralizzata e rigorosa dei parametri di input
- Enum `AccountType` per type-safety a compile-time
- Gestione degli errori con messaggi descriptivi
- Logging dettagliato per audit e debugging

```mermaid
classDiagram
    class AccountFactory {
        <<factory>>
        +createAccount(AccountType type, String number, String owner, double balance) Account
    }

    class AccountType {
        <<enumeration>>
        CHECKING
        SAVINGS
    }

    class Account {
        <<abstract>>
    }

    class CheckingAccount {
        -overdraftLimit : double
    }

    class SavingsAccount {
        -interestRate : double
    }

    AccountFactory --> AccountType : uses
    AccountFactory ..> Account : creates
    Account <|-- CheckingAccount
    Account <|-- SavingsAccount

    style AccountFactory fill:#ffeb3b,stroke:#f57c00,stroke-width:2px
    style AccountType fill:#fff3e0,stroke:#f57c00,stroke-width:2px
```

#### 2. Abstract Factory Pattern - Famiglie di Oggetti Correlati

L'Abstract Factory Pattern fornisce un'interfaccia per creare famiglie di oggetti correlati senza specificare le loro classi concrete. Nel sistema bancario, gestisce la differenziazione tra clienti standard e premium.

**Implementazione:** `BankFactory`, `StandardBankFactory`, `PremiumBankFactory`, `BankFactoryProvider`

**Problema Risolto:** Come creare famiglie coerenti di oggetti (account standard vs premium) mantenendo la flessibilità di cambiare l'intera famiglia senza modificare il codice client.

```mermaid
classDiagram
    class BankFactory {
        <<interface>>
        +createCheckingAccount(String number, String owner, double balance) Account
        +createSavingsAccount(String number, String owner, double balance) Account
    }

    class StandardBankFactory {
        +createCheckingAccount(String number, String owner, double balance) Account
        +createSavingsAccount(String number, String owner, double balance) Account
    }

    class PremiumBankFactory {
        +createCheckingAccount(String number, String owner, double balance) Account
        +createSavingsAccount(String number, String owner, double balance) Account
    }

    class BankFactoryProvider {
        +getFactory(boolean isPremiumCustomer) BankFactory
    }

    BankFactory <|.. StandardBankFactory
    BankFactory <|.. PremiumBankFactory
    BankFactoryProvider ..> BankFactory : provides
    StandardBankFactory --> AccountFactory : delegates to
    PremiumBankFactory --> AccountFactory : delegates to

    style BankFactory fill:#ff9800,stroke:#e65100,stroke-width:2px
    style StandardBankFactory fill:#ffcc02,stroke:#f57c00,stroke-width:2px
    style PremiumBankFactory fill:#ffd54f,stroke:#f57c00,stroke-width:2px
    style BankFactoryProvider fill:#fff3e0,stroke:#f57c00,stroke-width:2px
```

**Insight Progettuale:** Il pattern mantiene compatibilità totale con il sistema esistente delegando alla `AccountFactory` originale, dimostrando come i pattern possano essere introdotti incrementalmente in sistemi esistenti.

#### 3. Builder Pattern - Costruzione Flessibile di Oggetti Complessi

Il Builder Pattern separa la costruzione di un oggetto complesso dalla sua rappresentazione, permettendo allo stesso processo di costruzione di creare rappresentazioni diverse.

**Implementazione:** `AccountBuilder`

**Problema Risolto:** Come costruire account con molti parametri opzionali mantenendo un'interfaccia pulita e permettendo validazione incrementale durante la costruzione.

```mermaid
classDiagram
    class AccountBuilder {
        -accountType : String
        -accountNumber : String
        -ownerName : String
        -initialBalance : double
        -isPremium : boolean
        +newAccount() AccountBuilder
        +ofType(String type) AccountBuilder
        +withNumber(String number) AccountBuilder
        +forOwner(String name) AccountBuilder
        +withInitialBalance(double balance) AccountBuilder
        +asPremium() AccountBuilder
        +build() Account
        +reset() AccountBuilder
        -validateRequiredFields() void
    }

    AccountBuilder ..> Account : builds
    AccountBuilder --> AccountFactory : uses for final creation

    style AccountBuilder fill:#4caf50,stroke:#2e7d32,stroke-width:2px
```

**Caratteristiche Avanzate:**

- Interfaccia fluida per leggibilità del codice
- Validazione incrementale con messaggi di errore specifici
- Metodo `reset()` per riutilizzo dello stesso builder
- Gestione di configurazioni premium attraverso il metodo `asPremium()`

#### 4. Singleton Pattern - Istanza Unica Globale

Il Singleton Pattern assicura che una classe abbia una sola istanza e fornisce un punto di accesso globale ad essa.

**Implementazione:** `BankConfiguration`

**Problema Risolto:** Come garantire che le configurazioni globali del sistema (nome sistema, versione, modalità debug) siano consistenti in tutto l'applicativo.

```mermaid
classDiagram
    class BankConfiguration {
        -instance : BankConfiguration
        -systemName : String
        -version : String
        -debugMode : boolean
        -BankConfiguration()
        +getInstance() BankConfiguration
        +getSystemName() String
        +getVersion() String
        +isDebugMode() boolean
        +setDebugMode(boolean debugMode) void
        +getSystemInfo() String
    }

    BankConfiguration : +getInstance() returns same instance

    style BankConfiguration fill:#ff9800,stroke:#e65100,stroke-width:2px
```

**Implementazione Thread-Safe:** Il metodo `getInstance()` è sincronizzato per prevenire race conditions in ambienti multi-threaded.

### Pattern Strutturali (Structural Patterns)

I pattern strutturali si occupano della composizione di classi e oggetti per formare strutture più grandi, mantenendo la flessibilità e l'efficienza.

#### 5. Composite Pattern - Strutture Gerarchiche Uniforme

Il Composite Pattern permette di comporre oggetti in strutture ad albero per rappresentare gerarchie parte-tutto. Permette ai client di trattare uniformemente oggetti singoli e composizioni di oggetti.

**Implementazione:** `AccountGroup`

**Problema Risolto:** Come gestire gruppi di account e sottogruppi in modo che le operazioni (calcolo saldi, conteggi) funzionino sia su singoli account che su interi gruppi.

```mermaid
classDiagram
    class AccountGroup {
        -groupName : String
        -accounts : List~Account~
        -subGroups : List~AccountGroup~
        +AccountGroup(String groupName)
        +addAccount(Account account) void
        +addSubGroup(AccountGroup subGroup) void
        +getTotalBalance() double
        +getTotalAccountCount() int
        +containsAccount(String accountNumber) boolean
        +isEmpty() boolean
        +getAccounts() List~Account~
        +getSubGroups() List~AccountGroup~
    }

    AccountGroup o-- Account : contains
    AccountGroup o-- AccountGroup : composes

    style AccountGroup fill:#e91e63,stroke:#ad1457,stroke-width:2px
```

**Algoritmi Ricorsivi:** Le operazioni `getTotalBalance()` e `getTotalAccountCount()` utilizzano ricorsione per navigare l'intera gerarchia, dimostrando l'eleganza del pattern Composite.

#### 6. Bridge Pattern - Separazione Astrazione/Implementazione

Il Bridge Pattern separa un'astrazione dalla sua implementazione in modo che entrambe possano variare indipendentemente.

**Implementazione:** `OutputDevice`, `AccountDisplay`, `DetailedAccountDisplay`, `ConsoleOutputDevice`, `FileOutputDevice`

**Problema Risolto:** Come separare COSA visualizzare (informazioni account) da COME visualizzarlo (console, file, futuro: email, database) permettendo di variare entrambe le dimensioni indipendentemente.

```mermaid
classDiagram
    class OutputDevice {
        <<interface>>
        +writeLine(String text) void
        +initialize() void
        +cleanup() void
        +isReady() boolean
    }

    class ConsoleOutputDevice {
        -isInitialized : boolean
        -lineCounter : int
        +initialize() void
        +writeLine(String text) void
        +cleanup() void
        +isReady() boolean
    }

    class FileOutputDevice {
        -filename : String
        -writer : PrintWriter
        -isInitialized : boolean
        -totalLines : int
        -sessionStart : LocalDateTime
        +FileOutputDevice(String filename)
        +initialize() void
        +writeLine(String text) void
        +cleanup() void
        +isReady() boolean
        -calculateSessionDuration() String
    }

    class AccountDisplay {
        #outputDevice : OutputDevice
        +AccountDisplay(OutputDevice outputDevice)
        +showAccount(Account account) void
        +changeOutputDevice(OutputDevice newDevice) void
        +finish() void
        -analyzeBalance(double balance) void
    }

    class DetailedAccountDisplay {
        +DetailedAccountDisplay(OutputDevice outputDevice)
        +showAccount(Account account) void
        +showExecutiveSummary(Account account) void
        -addAccountSpecificDetails(Account account) void
        -assessRiskLevel(Account account) void
        -generateRecommendations(Account account) void
    }

    OutputDevice <|.. ConsoleOutputDevice
    OutputDevice <|.. FileOutputDevice
    AccountDisplay --> OutputDevice : uses (bridge)
    DetailedAccountDisplay --|> AccountDisplay

    style OutputDevice fill:#9c27b0,stroke:#6a1b9a,stroke-width:2px
    style ConsoleOutputDevice fill:#ba68c8,stroke:#8e24aa,stroke-width:2px
    style FileOutputDevice fill:#ba68c8,stroke:#8e24aa,stroke-width:2px
    style AccountDisplay fill:#ce93d8,stroke:#ab47bc,stroke-width:2px
    style DetailedAccountDisplay fill:#e1bee7,stroke:#9c27b0,stroke-width:2px
```

**Flessibilità Runtime:** Il metodo `changeOutputDevice()` permette di cambiare la modalità di output durante l'esecuzione, dimostrando la potenza del bridge pattern.

### Pattern Comportamentali (Behavioral Patterns)

I pattern comportamentali si concentrano sulla comunicazione tra oggetti e sull'assegnazione di responsabilità tra di essi.

#### 7. Strategy Pattern - Algoritmi Intercambiabili

Il Strategy Pattern definisce una famiglia di algoritmi, li incapsula e li rende intercambiabili. Permette all'algoritmo di variare indipendentemente dai client che lo utilizzano.

**Implementazione:** `WithdrawalStrategy`, `OverdraftWithdrawalStrategy`, `NoOverdraftWithdrawalStrategy`, `WithdrawalStrategyFactory`

**Problema Risolto:** Come gestire diverse politiche di prelievo (con/senza scoperto) senza duplicare codice e permettendo di aggiungere nuove politiche facilmente.

```mermaid
classDiagram
    class WithdrawalStrategy {
        <<interface>>
        +executeWithdrawal(double[] currentBalance, double requestedAmount, String accountNumber) boolean
    }

    class OverdraftWithdrawalStrategy {
        -overdraftLimit : double
        +OverdraftWithdrawalStrategy(double overdraftLimit)
        +executeWithdrawal(double[] currentBalance, double requestedAmount, String accountNumber) boolean
    }

    class NoOverdraftWithdrawalStrategy {
        +executeWithdrawal(double[] currentBalance, double requestedAmount, String accountNumber) boolean
    }

    class WithdrawalStrategyFactory {
        +createStrategy(String accountType) WithdrawalStrategy
    }

    WithdrawalStrategy <|.. OverdraftWithdrawalStrategy
    WithdrawalStrategy <|.. NoOverdraftWithdrawalStrategy
    WithdrawalStrategyFactory ..> WithdrawalStrategy : creates

    style WithdrawalStrategy fill:#ff5722,stroke:#d84315,stroke-width:2px
    style OverdraftWithdrawalStrategy fill:#ff7043,stroke:#ff5722,stroke-width:2px
    style NoOverdraftWithdrawalStrategy fill:#ff7043,stroke:#ff5722,stroke-width:2px
    style WithdrawalStrategyFactory fill:#ffab91,stroke:#ff5722,stroke-width:2px
```

**Design Insight:** L'uso di `double[]` per il balance permette di modificare il valore per riferimento, simulando un parametro di input/output in Java.

#### 8. Observer Pattern - Notifica Automatica di Eventi

L'Observer Pattern definisce una dipendenza uno-a-molti tra oggetti in modo che quando un oggetto cambia stato, tutti i suoi dipendenti vengono notificati e aggiornati automaticamente.

**Implementazione:** `TransactionObserver`, `SimpleTransactionObserver`

**Problema Risolto:** Come monitorare automaticamente tutte le transazioni bancarie per logging, audit, e notifiche senza accoppiare il sistema di monitoraggio alla logica di business.

```mermaid
classDiagram
    class TransactionObserver {
        <<interface>>
        +onTransaction(String accountNumber, String operation, double amount, boolean success) void
    }

    class SimpleTransactionObserver {
        +onTransaction(String accountNumber, String operation, double amount, boolean success) void
    }

    class BankService {
        -observer : TransactionObserver
        +performTransaction(String accountNumber, String operation, double amount) void
    }

    TransactionObserver <|.. SimpleTransactionObserver
    BankService --> TransactionObserver : notifies

    style TransactionObserver fill:#673ab7,stroke:#512da8,stroke-width:2px
    style SimpleTransactionObserver fill:#9575cd,stroke:#7e57c2,stroke-width:2px
    style BankService fill:#b39ddb,stroke:#9575cd,stroke-width:2px
```

**Estensibilità:** Possono essere facilmente aggiunti nuovi observer (EmailNotificationObserver, SMSAlertObserver) senza modificare il BankService.

#### 9. Iterator Pattern - Navigazione Sicura delle Collezioni

L'Iterator Pattern fornisce un modo per accedere sequenzialmente agli elementi di un oggetto aggregato senza esporre la sua rappresentazione sottostante.

**Implementazione:** `AccountIterator`

**Problema Risolto:** Come navigare collezioni di account in modo sicuro, con possibilità di filtering, senza esporre la struttura interna delle collezioni.

```mermaid
classDiagram
    class Iterator {
        <<interface>>
        +hasNext() boolean
        +next() Object
    }

    class AccountIterator {
        -iteratorId : String
        -accounts : List~Account~
        -currentPosition : int
        +AccountIterator(List~Account~ accounts)
        +hasNext() boolean
        +next() Account
        +createFilteredIterator(List~Account~ accounts, String accountType) AccountIterator
    }

    Iterator <|.. AccountIterator
    AccountIterator --> Account : iterates over

    style Iterator fill:#607d8b,stroke:#455a64,stroke-width:2px
    style AccountIterator fill:#78909c,stroke:#546e7a,stroke-width:2px
```

**Funzionalità Avanzate:** Il metodo statico `createFilteredIterator()` permette di creare iterator che mostrano solo account di un tipo specifico.

#### 10. Template Method Pattern - Struttura Algoritmica Fissa

Il Template Method Pattern definisce lo scheletro di un algoritmo in una operazione, rimandando alcuni passi alle sottoclassi. Permette alle sottoclassi di ridefinire certi passi di un algoritmo senza cambiarne la struttura.

**Implementazione:** `AccountReportGenerator`, `SimpleReportGenerator`, `DetailedReportGenerator`

**Problema Risolto:** Come standardizzare la struttura dei report bancari (header, contenuto, footer) permettendo personalizzazione dei dettagli specifici.

```mermaid
classDiagram
    class AccountReportGenerator {
        <<abstract>>
        +generateReport(Account account) String
        #generateHeader() String*
        #generateAccountDetails(Account account) String*
        #generateFooter() String*
        #generateBasicAccountInfo(Account account) String
        #generateAdditionalNotes() String
    }

    class SimpleReportGenerator {
        +generateHeader() String
        +generateAccountDetails(Account account) String
        +generateFooter() String
    }

    class DetailedReportGenerator {
        +generateHeader() String
        +generateAccountDetails(Account account) String
        +generateFooter() String
        +generateAdditionalNotes() String
    }

    AccountReportGenerator <|-- SimpleReportGenerator
    AccountReportGenerator <|-- DetailedReportGenerator

    style AccountReportGenerator fill:#4caf50,stroke:#388e3c,stroke-width:2px
    style SimpleReportGenerator fill:#66bb6a,stroke:#4caf50,stroke-width:2px
    style DetailedReportGenerator fill:#81c784,stroke:#66bb6a,stroke-width:2px
```

**Template Method in Action:** Il metodo `generateReport()` definisce la sequenza fissa: header → informazioni base → dettagli personalizzati → footer, mentre ogni sottoclasse personalizza i dettagli.

#### 11. Memento Pattern - Cattura e Ripristino di Stati

Il Memento Pattern cattura e esternalizza lo stato interno di un oggetto senza violare l'incapsulamento, permettendo di ripristinare l'oggetto a questo stato in seguito.

**Implementazione:** `AccountSnapshot`, `AccountBackup`

**Problema Risolto:** Come implementare funzionalità di backup e audit per account bancari, catturando stati in momenti specifici per analisi successive.

```mermaid
classDiagram
    class AccountSnapshot {
        -accountNumber : String
        -balance : double
        +AccountSnapshot(Account account)
        +getAccountNumber() String
        +getBalance() double
    }

    class AccountBackup {
        -snapshots : Map~String, AccountSnapshot~
        +save(Account account) boolean
        +restore(String accountNumber) AccountSnapshot
        +hasBackup(String accountNumber) boolean
        +deleteBackup(String accountNumber) boolean
        +getTotalBackups() int
    }

    class Account {
        <<abstract>>
    }

    AccountBackup --> AccountSnapshot : manages
    AccountSnapshot --> Account : captures state of

    style AccountSnapshot fill:#03a9f4,stroke:#0288d1,stroke-width:2px
    style AccountBackup fill:#29b6f6,stroke:#03a9f4,stroke-width:2px
```

**Principio di Incapsulamento:** Il `AccountSnapshot` è immutabile e cattura solo lo stato essenziale, mentre `AccountBackup` funge da caretaker senza accedere ai dettagli interni.

### Pattern Architetturali

#### 12. Exception Shielding Pattern - Protezione delle Informazioni Sensibili

L'Exception Shielding Pattern separa le informazioni di errore destinate agli utenti finali da quelle destinate agli sviluppatori, proteggendo informazioni sensibili del sistema.

**Implementazione:** `BankException`

**Problema Risolto:** Come fornire messaggi di errore utili agli utenti finali senza esporre dettagli tecnici sensibili che potrebbero compromettere la sicurezza del sistema.

```mermaid
classDiagram
    class BankException {
        -userMessage : String
        -technicalDetails : String
        -errorId : String
        +BankException(String userMessage, String technicalDetails)
        +BankException(String userMessage, String technicalDetails, Throwable cause)
        +getUserMessage() String
        +getTechnicalDetails() String
        +getErrorId() String
    }

    class Exception {
        <<Java Standard>>
    }

    Exception <|-- BankException

    style BankException fill:#f44336,stroke:#d32f2f,stroke-width:2px
    style Exception fill:#ef5350,stroke:#f44336,stroke-width:2px
```

**Security by Design:** Ogni eccezione riceve un ID univoco per correlazione nei log, mentre l'utente vede solo messaggi sicuri e comprensibili.

## Comandi dell'Interfaccia Utente

Il sistema fornisce un'interfaccia a riga di comando completa e intuitiva:

### Comandi di Gestione Account

- `create <tipo> <numero> <nome> <saldo>` - Crea account standard
- `createpremium <tipo> <numero> <nome> <saldo>` - Crea account premium (Abstract Factory)
- `build <tipo> <numero> <nome> <saldo>` - Costruzione via Builder Pattern
- `balance <numero>` - Consultazione saldo con logging
- `list` - Visualizzazione completa degli account

### Comandi Transazionali

- `deposit <numero> <importo>` - Deposito con validazione
- `withdraw <numero> <importo>` - Prelievo con gestione scoperto
- Monitoraggio automatico via Observer Pattern
- Persistenza automatica delle modifiche

### Comandi di Sistema

- `demo` - Dimostrazione interattiva di tutti i 12 pattern
- `exit` - Chiusura sicura con cleanup risorse

Ogni comando include:

- Validazione automatica dell'input
- Messaggi di errore user-friendly via Exception Shielding
- Logging dettagliato per audit e debugging
- Feedback immediato sullo stato delle operazioni
