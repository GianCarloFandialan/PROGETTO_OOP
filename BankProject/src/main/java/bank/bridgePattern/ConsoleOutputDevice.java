package bank.bridgePattern;

/**
 * classe concreta che scrive su console
 */
public class ConsoleOutputDevice implements OutputDevice {

    /**
     * metodo che scrive una linea in console
     */
    @Override
    public void writeLine(String text) {
        System.out.println(text);
    }

    /**
     * metodo che chiude il file
     */
    @Override
    public void close() {
        System.out.println("Output completato");
    }
}