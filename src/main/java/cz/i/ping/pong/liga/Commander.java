package cz.i.ping.pong.liga;

import cz.i.ping.pong.liga.service.ExportService;
import cz.i.ping.pong.liga.service.GenerateService;
import cz.i.ping.pong.liga.service.ImportService;
import cz.i.ping.pong.liga.service.PrintService;

import java.io.Console;
import java.io.File;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Commander {
    private static final String DEFAULT_DERBY_DB = "/home/honza/Documents/pp-liga";
    private static final String DEFAULT_DERBY_USER = "hadasj";
    private static final String DEFAULT_DERBY_PASSWORD = "liga123";
    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("d.M.yyyy");

    public static void main(String[] args) {
        Commander commander = new Commander(args);
        commander.process();
    }

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

    public enum Import {
        HRACI("ih"), ZAPASY("iz");
        private String code;
        Import(String code) {
            this.code = code;
        }
        public static Import codeOf(String code) {
            for (Import imp : values())
                if (imp.code.equalsIgnoreCase(code))
                    return imp;
            throw new IllegalArgumentException("Neznamy prikaz: " + code);
        }
    }

    private String db;
    private String user;
    private String password;

    private Commander(String[] arguments) {
        db = DEFAULT_DERBY_DB;
        user = DEFAULT_DERBY_USER;
        password = DEFAULT_DERBY_PASSWORD;

        if (arguments.length >= 1) {
            db = arguments[0];
        }
        if (arguments.length >= 3) {
            user = arguments[1];
            password = arguments[2];
        }
        File derbyDbDirectory = new File(db);
        // check db path
        if (!derbyDbDirectory.exists() || !derbyDbDirectory.isDirectory() || !derbyDbDirectory.canWrite())
            throw new IllegalStateException("Db " + db + " neni pristupna! " + derbyDbDirectory.exists()  + ", " +
                    derbyDbDirectory.isDirectory() + ", " + derbyDbDirectory.canWrite());
    }

    private void process() {
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
                    case PRINT:
                        print(out);
                        break;
                    case GENERATE:
                        generate(line, out);
                        break;
                    case EXPORT:
                        export(line, out);
                        break;
                    case UNKNOWN:
                        out.println("Neznámý příkaz: " + line);
                        break;
                }
            }
        } while (run);
    }

    private void printCommands(PrintStream out) {
        out.println();
        out.println("Ping pong liga");
        out.println("Příkazy: ");
        out.println("h = help");
        out.println("ih = import hracu");
        out.println("iz = import zapasu");
        out.println("g = generate zacatek konec");
        out.println("p = print");
        out.println("e = export to file");
        out.println("q = quit");
    }

    private void importFile(String line, PrintStream out) {
        String parts[] = line.split(" ");
        if (parts.length < 2) {
            out.println("Chybí název souboru");
            return;
        }
        Import command = Import.codeOf(parts[0]);
        String filename = parts[1];
        File file = new File(filename);
        if (!file.exists() || !file.canRead()) {
            out.println("Soubor " + filename + " nejde otevřít");
            return;
        }

        out.println("Importuji soubor " + filename);
        try {
            ImportService importService = new ImportService(db, user, password);
            importService.importFile(command, file);
            out.println("Import souboru " + filename + " proběhl úspěšně.");
        } catch (Exception e) {
            out.println("Chyba importu " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(out);
        }
    }

    private void print(PrintStream out) {
        try {
            PrintService printService = new PrintService(db, user, password);
            printService.print(out);
        }catch (Exception e) {
            out.println("Chyba tisku " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(out);
        }
    }

    private void generate(String line, PrintStream out) {
        String parts[] = line.split(" ");
        if (parts.length < 3) {
            out.println("Chybí začátek konec");
            return;
        }
        try {
            LocalDate zacatek = LocalDate.parse(parts[1], FORMAT);
            LocalDate konec = LocalDate.parse(parts[2], FORMAT);
            GenerateService generateService = new GenerateService(db, user, password);
            generateService.generate(zacatek, konec);
            out.println("Generování soupeřů pro kolo " + zacatek + " - " + konec + " proběhl úspěšně.");
        }catch (Exception e) {
            out.println("Chyba generovani " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(out);
        }
    }

    private void export(String line, PrintStream out) {
        try {
            ExportService exportService = new ExportService(db, user, password);

            String parts[] = line.split(" ");
            long kolo;
            if (parts.length >= 2)
                kolo = Long.parseLong(parts[1]);
            else
                kolo = exportService.getLastKolo();

            out.println("Exportuji kolo: " + kolo);
            exportService.exportKolo(kolo);
            out.println("Export proběhl úspěšně");
        } catch (Exception e) {
            out.println("Chyba exportu " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(out);
        }
    }
}
