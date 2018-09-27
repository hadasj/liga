package cz.icz.ping.pong.liga;

import cz.icz.ping.pong.liga.service.ImportService;

import java.io.Console;
import java.io.File;
import java.io.PrintStream;

public class Commander {
    private static final String DERBY_DB = "/home/honza/Documents/pp-liga";
    private static final String DERBY_USER = "hadasj";
    private static final String DERBY_PASSWORD = "liga123";

    private enum Command {
        HELP('h'), IMPORT('i'), GENERATE('g'), PRINT('p'), EXPORT('e'), QUIT('q'), UNKNOWN(null);

        private Character code;
        Command(Character code) {
            this.code = code;
        }
        public static Command codeOf(char code) {
            for (Command command : values()) {
                if (command.code != null && command.code == code)
                    return command;
            }
            return UNKNOWN;
        }
    }


    public static void main(String[] args) {
        PrintStream out = System.out;
        Console console = System.console();

        printCommands(out);

        boolean run = true;
        do {
            String line = console.readLine();
            if (line != null && !line.trim().isEmpty()) {
                char code = line.trim().toLowerCase().charAt(0);
                Command command = Command.codeOf(code);
                switch (command) {
                    case HELP:
                        printCommands(out);
                        break;
                    case QUIT:
                        run = false;
                        break;
                    case IMPORT:
                        importFile(line, out);
                        break;
                    case UNKNOWN:
                        out.println("Neznámý příkaz: " + line);
                        break;
                    case PRINT:
                        // TODO
                        break;
                    case GENERATE:
                        // TODO
                        break;
                }
            }
        } while (run);
    }

    private static void printCommands(PrintStream out) {
        out.println();
        out.println("Ping pong liga");
        out.println("Příkazy: ");
        out.println("h = help");
        out.println("i = import file");
        out.println("g = generate");
        out.println("p = print");
        out.println("e = export to file");
        out.println("q = quit");
    }

    private static void importFile(String line, PrintStream out) {
        String parts[] = line.split(" ");
        if (parts.length < 2) {
            out.println("Chybí název souboru");
            return;
        }
        String filename = parts[1];
        File file = new File(filename);
        if (!file.exists() || !file.canRead()) {
            out.println("Soubor " + filename + " nejde otevřít");
            return;
        }

        out.println("Importuji soubor " + filename);
        ImportService importService = new ImportService();
        try {
            importService.importFile(file, DERBY_DB, DERBY_USER, DERBY_PASSWORD);
        } catch (Exception e) {
            out.println("Chyba importu " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
