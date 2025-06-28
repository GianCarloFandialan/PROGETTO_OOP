package bank.singletonPattern;

import java.util.logging.Logger;

/**
 * Classe Singleton che gestisce le configurazioni globali del sistema bancario
 * creato.
 */
public class BankConfiguration {

    /** L'unica istanza della classe (Singleton) */
    private static BankConfiguration instance;

    /** logger statico */
    private static final Logger logger = Logger.getLogger(BankConfiguration.class.getName());

    /** nome del sistema */
    private String systemName;

    /** versione del sistema */
    private String version;

    /** booleano per verificare se il sistema è in debug mode */
    private boolean debugMode;

    /**
     * Costruttore che impedisce istanziazione diretta
     */
    private BankConfiguration() {

        this.systemName = "Sistema Bancario";
        this.version = "1.0.0";
        this.debugMode = false;

        logger.info("BankConfiguration Singleton inizializzato");
    }

    /**
     * Metodo che ottiene l'istanza della classe.
     * 
     * @return BankConfiguration - istanza della classe
     */
    public static synchronized BankConfiguration getInstance() {
        if (instance == null) {
            logger.info("Creando nuova istanza BankConfiguration");
            instance = new BankConfiguration();
        } else {
            logger.fine("Restituendo istanza esistente di BankConfiguration");
        }
        return instance;
    }

    /**
     * Restituisce il nome del sistema.
     * 
     * @return String - Nome del sistema
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * Restituisce la versione del sistema.
     * 
     * @return String - Versione corrente
     */
    public String getVersion() {
        return version;
    }

    /**
     * Verifica se la modalità debug è attiva.
     * 
     * @return boolean - True se debug attivo, false altrimenti
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Attiva o disattiva la modalità debug.
     * 
     * @param debugMode - True per attivare, false per disattivare
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        logger.info("Modalità debug " + (debugMode ? "attivata" : "disattivata"));
    }

    /**
     * Restituisce una stringa con tutte le informazioni di configurazione.
     * 
     * @return String - Informazioni complete del sistema
     */
    public String getSystemInfo() {
        return String.format("%s v%s (Debug: %s)",
                systemName, version, debugMode ? "ON" : "OFF");
    }
}