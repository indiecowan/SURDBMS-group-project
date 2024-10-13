import java.util.ArrayList;
import java.util.LinkedList;

public class JoinParser {
    private String input;
    private boolean validInput = true;
    // table name, =, JOIN, 'table1, table2', on, single condition
    private String JOIN_FORMAT = "[_-a-zA-Z0-9]+ += +JOIN +([_-a-zA-Z0-9]+)+, +([_-a-zA-Z0-9]+)+ +ON +([_-a-zA-Z0-9.]+) (=|!=|>|<|>=|<=) ([_-a-zA-Z0-9.]+)+;";

    private String newTableName;
    private ARelation tableA; // first table in join condition
    private ARelation tableB; // second table in join condition
    private JoinCondition joinCondition;

    public JoinParser(String input) {
        this.input = input;
        if (!input.matches(JOIN_FORMAT)) {
            System.err.println("ERROR: Join has incorrect format.");
            validInput = false;
            return;
        }
    }

    /* returns true if input is in valid format */
    public boolean hasValidInput() {
        return validInput;
    }

    private void processInput(String input, SurlyDatabase DB) throws Exception {
        String workingString = input.strip();

        // capture new table name
        int spaceIndex = workingString.indexOf(" ");
        this.newTableName = workingString.substring(0, spaceIndex);

        // strip string of the '= JOIN' + extra spaces
        workingString = workingString.substring(spaceIndex + 1).replaceFirst("=", "");
        workingString = workingString.replaceFirst("JOIN", "").strip();
        // System.out.println("working string: " + workingString);

        // capture tableA
        int commaIndex = workingString.indexOf(",");
        // System.out.println("commaIndex: " + commaIndex);
        String tableAName = workingString.substring(0, commaIndex).strip();
        // System.out.println("tableAname: " + tableAName);
        this.tableA = DB.getRelation(tableAName);

        if (this.tableA == null) {
            System.err.printf("ERROR: Table you are trying to select from (%s) does not exist.%n", tableAName);
            Exception e = new Exception("ERROR: Table you are trying to select from does not exist.");
            throw e;
        }

        // strip string of tableA name
        workingString = workingString.substring(commaIndex + 1).strip();

        // capture tableB
        spaceIndex = workingString.indexOf(" ");
        String tableBName = workingString.substring(0, spaceIndex).strip();
        this.tableB = DB.getRelation(tableBName);

        if (this.tableB == null) {
            System.err.printf("ERROR: Table you are trying to select from (%s) does not exist.%n", tableBName);
            Exception e = new Exception("ERROR: Table you are trying to select from does not exist.");
            throw e;
        }

        // strip string of tableB name
        workingString = workingString.substring(spaceIndex + 1).strip();

        // strip the 'on' and remove the ;
        workingString = workingString.replaceFirst("ON|on", "").strip();
        workingString = workingString.substring(0, workingString.length() - 1);

        // capture condition
        String[] bits = workingString.split(" +");
        JoinCondition jc = new JoinCondition(bits[0], bits[1], bits[2]);
        this.joinCondition = jc;
    }

    public boolean performCommand(SurlyDatabase DB) {
        if (validInput) {
            // exctract data
            try {
                processInput(input, DB);
            } catch (Exception e) {
                // a table trying to join with doesn't exist, error message already printed
                System.out.println(e);
                return false;
            }

            if (tableA.name.equals(tableB.name)) {
                System.err.println("ERROR: This program cannot handle joining the same table to itself. Join cannot be performed.");
                return false;
            }

            // create new table with all attributes
            TempRelation newRelation;
            try {
                newRelation = new TempRelation(newTableName);
            } catch (Exception e) {
                // tried to name table catalog- cancel transaction (error message already printed)
                return false;
            }

            // add needed attributes to new table
            LinkedList<Attribute> attributesA = tableA.getSchema();
            for (Attribute attribute : attributesA) {
                newRelation.addAttribute(attribute);
            }
            LinkedList<Attribute> attributesB = tableB.getSchema();
            for (Attribute attribute : attributesB) {        
                newRelation.addAttribute(attribute);
            }

            // for each pair of tuples see if they pass join condition and if so add them conjoined into the new table
            ArrayList<Tuple> passingTuples;
            try {
                if (joinCondition == null) {
                    System.out.println("jc is null");
                }
                passingTuples = joinCondition.getPassingTuples(tableA, tableB);
            } catch (Exception e) {
                // error message already printed
                return false;
            }

            for (Tuple tuple : passingTuples) {
                newRelation.insert(tuple);
            }

            DB.addRelation(newRelation);

        } else {
            System.err.println("ERROR: Join could not be performed because format is incorrect.");
            return false;
        }
        return true;
    }
}
