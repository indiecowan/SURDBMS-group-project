import java.util.ArrayList;
import java.util.LinkedList;

public class SelectParser {
    private String input;
    private boolean validInput = true;
    private final String SELECT_FORMAT = "([a-zA-Z0-9_-]+ += +SELECT [a-zA-Z0-9_-]+ +WHERE +[a-zA-Z0-9._-]+ +(=|!=|>|<|>=|<=) +([a-zA-Z0-9_-]+|'[ a-zA-Z0-9_-]+')( +(AND|OR|and|or) +[a-zA-Z0-9._-]+ +(=|!=|>|<|>=|<=) +([a-zA-Z0-9._-]+)|('[ a-zA-Z0-9._-]+'))*;)|([a-zA-Z0-9_-]+ += +SELECT [a-zA-Z0-9_-]+;)";
    private final String EMPTY_SELECT_FORMAT = "[a-zA-Z0-9_-]+ += +SELECT [a-zA-Z0-9_-]+;";

    private String newTableName;
    private ARelation inputRelation;
    private ArrayList<ArrayList<Condition>> conditions; // grouped by ones that share ands
    private boolean emptySelect;

    public SelectParser(String input) {
        this.input = input;
        if (!input.matches(SELECT_FORMAT)) {
            System.err.println("ERROR: Select has incorrect format.");
            validInput = false;
            return;
        }
    }

    /* returns true if input is in valid format */
    public boolean hasValidInput() {
        return validInput;
    }

    private void processInput(String input, SurlyDatabase DB) throws Exception {
        this.emptySelect = input.matches(EMPTY_SELECT_FORMAT);

        String workingString = input.strip();

        // capture new table name
        int spaceIndex = workingString.indexOf(" ");
        this.newTableName = workingString.substring(0, spaceIndex);

        // strip string of the '= SELECT' + extra spaces
        workingString = workingString.substring(spaceIndex + 1).replaceFirst("=", "");
        workingString = workingString.replaceFirst("SELECT", "").strip();

        // capture table we are selecting from
        spaceIndex = workingString.indexOf(" ");
        if (spaceIndex == -1) {
            spaceIndex = workingString.indexOf(";");
        }
        String inputRelationName = workingString.substring(0, spaceIndex).strip();
        this.inputRelation = DB.getRelation(inputRelationName);

        if (this.inputRelation == null) {
            Exception e = new Exception("ERROR: Table you are trying to select from (" + inputRelationName + ")does not exist.");
            throw e;
        }

        this.conditions = new ArrayList<ArrayList<Condition>>();
        
        if (!emptySelect) {
            // strip string of table name and where
            workingString = workingString.substring(spaceIndex + 1).replaceFirst(inputRelationName, "");
            workingString = workingString.replaceFirst("WHERE|where", "").strip();

            // capture conditions
            String[] conditionStrings = workingString.split("or|OR");
            for (int i = 0; i < conditionStrings.length; i++) {
                String currentBlock = conditionStrings[i];
                String[] joinedConditionStrings = currentBlock.split("and|AND");
                ArrayList<Condition> conditionGroup = new ArrayList<Condition>();
                for (int j = 0; j < joinedConditionStrings.length; j++) {
                    String condition = joinedConditionStrings[j].strip();
                    String[] bits = condition.split(" +");
                    String value = bits[2];
                    if (value.startsWith("'")) {
                        for (int k = 3; k < bits.length; k++) {
                            value += " " + bits[k];
                        }
                    }
                    if (value.endsWith(";")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    Condition newCondition = new Condition(bits[0], bits[1], value);
                    conditionGroup.add(newCondition);
                }
                this.conditions.add(conditionGroup);
            }
        }
    }

    
    public boolean performCommand(SurlyDatabase DB) {
        if (validInput) {
            // process input into field data
            try {
                processInput(input, DB);
            } catch (Exception e) {
                System.out.println(e.toString());
                return false;
            }
            // create new table to put in database
            TempRelation newRelation;
            try {
                newRelation = new TempRelation(newTableName);
            } catch (Exception e) {
                // tried to create table named catalog, error already printed
                System.out.println(e);
                return false;
            }
            // for every tuple in input table check if conditions are valid and then add that tuple to new table if it passes conditions
            ArrayList<Tuple> passingTuples = new ArrayList<Tuple>();
            try {
                passingTuples = inputRelation.getPassingTuples(this.conditions); 
            } catch (Exception e) {
                System.out.println(e.toString() + " Select could not be performed.");
            }

            LinkedList<Attribute> attributes = inputRelation.getSchema();
            for (Attribute attribute : attributes) {
                newRelation.addAttribute(attribute);
            }

            for (Tuple tuple : passingTuples) {
                newRelation.insert(tuple);
            }

            DB.addRelation(newRelation);

            return true;

        } else {
            System.err.println("ERROR: Select could not be performed because format is incorrect.");
            return false;
        }
    }
}
