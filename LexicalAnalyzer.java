
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class LexicalAnalyzer {
    /* Reference to the SurlyDatabase instance */
    private SurlyDatabase database;

    private String getFullCommand(Scanner scan, String currentLine) {
        String fullCommand = currentLine + " ";
        while (currentLine.charAt(currentLine.length() - 1) != ';') {
            currentLine = scan.nextLine();
            fullCommand += currentLine + " ";
        }
        return fullCommand.strip().toUpperCase();
    }

    private String makeKeywordUpper(String s, String keyword) {
        String result = s.replace(keyword, keyword.toUpperCase());
        return result;
    }

    private String getKeyword(String s) {
        String result = s;
        int isEquals = s.indexOf('=');
        if (isEquals != -1) { // SELECT PROJECT JOIN
            result = s.substring(isEquals + 1, s.length());
        }
        result = result.strip();
        int spaceIndex = result.indexOf(' ');
        if (spaceIndex != -1) {
            result = result.substring(0, spaceIndex);
        }
        return result;
    }

    /*
     * Parses the given file into individual commands,
     * passes each to the appropriate parser,
     * and invokes the parsed command on the SURLY database
     */
    public void run(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String fileName = args[i];
            try { // for opening file
                File file = new File(fileName);
                Scanner scan = new Scanner(file);
                SurlyDatabase DB = new SurlyDatabase();
                this.database = DB;

                while (scan.hasNextLine()) {
                    applyNextCommand(scan);
                }
                scan.close();
            } catch (FileNotFoundException e) {
                System.err.println("Error: lexical analyzer could not find file.");
            }
        }
        // Let user do extra commands if they'd like
        Scanner scan = new Scanner(System.in);
        String input = getUserInput(scan);
        System.out.println("input: " + input);
        while (!input.equals("q")) {
            applyCommand(input, database, scan);
            input = getUserInput(scan);
        }
        
        System.out.println("\nThank you for using SURLY. Have a nice day! Goodbye.\n");
    }

    private void applyNextCommand(Scanner scan) {
        SurlyDatabase DB = this.database;
        // Convert first word of the current line to uppercase
        String currentLine = scan.nextLine();
        applyCommand(currentLine, DB, scan);
    }

    private void applyCommand(String command, SurlyDatabase DB, Scanner scan) {
        String keyword = getKeyword(command);
        command = makeKeywordUpper(command, keyword);

        if (command.equals("")) {
            // blank line

        } else if (command.startsWith("#")) {
            // comment line

        } else if (keyword.equalsIgnoreCase("RELATION")) {
            // relation command
            // get rest of command
            String fullCommand = getFullCommand(scan, command);
            RelationParser relationParser = new RelationParser(fullCommand);
            relationParser.performCommand(DB);

        } else if (keyword.equalsIgnoreCase("INSERT")) {
            // insert command
            // get rest of command
            String fullCommand = getFullCommand(scan, command);
            InsertParser insertParser = new InsertParser(fullCommand);
            insertParser.performCommand(DB);

        } else if (keyword.equalsIgnoreCase("PRINT")) {
            // print command
            // get rest of command
            String fullCommand = getFullCommand(scan, command);
            PrintParser printParser = new PrintParser(fullCommand);
            printParser.performCommand(DB);

        } else if (keyword.equalsIgnoreCase("DESTROY")) {
            // destroy command
            // get rest of command
            String fullCommand = getFullCommand(scan, command);
            DestroyParser destroyParser = new DestroyParser(fullCommand);
            destroyParser.performCommand(DB);
        } else if (command.startsWith("DELETE")) {
            // delete command
            // get rest of command
            String fullCommand = getFullCommand(scan, command);
            DeleteParser deleteParser = new DeleteParser(fullCommand);
            deleteParser.performCommand(DB);
        } else { // possibly assignment
            if (keyword.equalsIgnoreCase("SELECT")) {
                // Select Where command
                String fullCommand = getFullCommand(scan, command);
                SelectParser select = new SelectParser(fullCommand);
                select.performCommand(DB);

            } else if (keyword.equalsIgnoreCase("PROJECT")) {
                // Project command
                String fullCommand = getFullCommand(scan, command);
                ProjectParser project = new ProjectParser(fullCommand);
                project.performCommand(DB);

            } else if (keyword.equalsIgnoreCase("JOIN")) {
                // Join command
                String fullCommand = getFullCommand(scan, command);
                JoinParser join = new JoinParser(fullCommand);
                join.performCommand(DB);
            } else {
                // unknown line
                System.err.print("Error: input line not recognized: ");
                System.err.println(command);
            }
        }
    }

    private String getUserInput(Scanner scan) {
        System.out.print("\nEnter additional commands, or 'q' to quit: ");
        String input = scan.nextLine();
        return input;
    }
}
