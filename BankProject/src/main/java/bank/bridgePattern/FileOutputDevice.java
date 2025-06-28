package bank.bridgePattern;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * classe concreta che scrive sul file
 */
public class FileOutputDevice implements OutputDevice {

    private PrintWriter writer;
    private String filename;

    /**
     * Costruttore che apre il file per la scrittura
     * 
     * @param filename nome del file dove scrivere
     */
    public FileOutputDevice(String filename) {
        this.filename = filename;
        try {
            this.writer = new PrintWriter(new FileWriter(filename, true)); // append mode
        } catch (IOException e) {
            System.err.println("Errore apertura file: " + e.getMessage());
            this.writer = null;
        }
    }

    /**
     * metodo che scrive una linea sul file
     */
    @Override
    public void writeLine(String text) {
        if (writer != null) {
            writer.println(text);
            writer.flush();
        } else {
            System.out.println("[FILE ERROR] " + text);
        }
    }

    /**
     * metodo che chiude il file
     */
    @Override
    public void close() {
        if (writer != null) {
            writer.close();
            System.out.println("Dati salvati nel file: " + filename);
        }
    }
}
