package bank;

import java.util.logging.Logger;

import bank.account.Account;

/**
 * classe che gestisce gli errori del sistema bancario
 */
public class BankException extends Exception {

    /** Messaggio destinato all'utente finale */
    private String userMessage;

    /** Dettagli dell'errore */
    private String technicalDetails;

    /** ID univoco per tracciare gli errori */
    private final String errorId;

    /** Contatore per generare ID univoci */
    private static long errorCounter = 0;

    /** logger statico */
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    /**
     * Costruttore - crea un'eccezione con entrambi i tipi di messaggio.
     * 
     * @param userMessage
     * @param technicalDetails
     */
    public BankException(String userMessage, String technicalDetails) {
        super(userMessage);

        // ID univoco per questo errore
        errorCounter++;
        this.errorId = "ERR_" + errorCounter;

        this.userMessage = userMessage != null ? userMessage : "Errore di sistema";
        this.technicalDetails = technicalDetails != null ? technicalDetails : "Nessun dettaglio tecnico";

        logger.severe("ERRORE BANCARIO [" + errorId + "] - " +
                "Messaggio utente: \"" + this.userMessage + "\" | " +
                "Dettagli tecnici: " + this.technicalDetails);
    }

    /**
     * Costruttore - crea un'eccezione con entrambi i tipi di messaggio includendo
     * l'eccezione
     * 
     * @param userMessage      - Messaggio comprensibile per l'utente finale
     * @param technicalDetails - Dettagli tecnici per il debugging
     * @param cause            - L'eccezione originale che ha scatenato questo
     *                         errore
     */
    public BankException(String userMessage, String technicalDetails, Throwable cause) {
        super(userMessage, cause);

        errorCounter++;
        this.errorId = "ERR_" + errorCounter;
        this.userMessage = userMessage != null ? userMessage : "Errore di sistema";
        this.technicalDetails = technicalDetails != null ? technicalDetails : "Nessun dettaglio tecnico";

        logger.severe("ERRORE BANCARIO CON CAUSA [" + errorId + "] - " +
                "Messaggio utente: \"" + this.userMessage + "\" | " +
                "Dettagli tecnici: " + this.technicalDetails + " | " +
                "Causa originale: " + (cause != null ? cause.getClass().getSimpleName() : "null"));
    }

    /**
     * Restituisce il messaggio sicuro destinato all'utente finale.
     * 
     * @return userMessage - messaggio per l'utente
     */
    public String getUserMessage() {
        logger.info("Messaggio utente richiesto per errore " + errorId);
        return userMessage;
    }

    /**
     * Restituisce i dettagli tecnici
     * 
     * @return technicalDetails - dettagli tecnici
     */
    String getTechnicalDetails() {
        logger.info("Dettagli tecnici richiesti per errore " + errorId);
        return technicalDetails;
    }

    /**
     * Restituisce l'ID univoco di questo errore.
     * 
     * @return errorId - ID dell'errore
     */
    public String getErrorId() {
        return errorId;
    }
}