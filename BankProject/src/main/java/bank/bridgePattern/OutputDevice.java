package bank.bridgePattern;

/**
 * Interfaccia che definisce come i dati vengono effettivamente outputati.
 */
public interface OutputDevice {

    void writeLine(String text);

    void close();
}
