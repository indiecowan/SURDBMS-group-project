import java.util.LinkedList;
import java.util.ArrayList;

public class ProjectParser {
    private String input;
    private boolean validInput = true;
    private String PROJECT_FORMAT = "[-_a-zA-Z0-9]+ += +PROJECT +[-_a-zA-Z0-9. ]+(, +[-_a-zA-Z0-9. ]+)* +FROM +([-_a-zA-Z0-9]+);";

    public ProjectParser(String input) {
        this.input = input;
        if (!input.matches(PROJECT_FORMAT)) {
            System.err.println("ERROR: PROJECT has incorrect format.");
            System.out.println("setting validInput to false");
            validInput = false;
            return;
        }
    }

    /* returns true if input is in valid format */
    public boolean hasValidInput() {
        return validInput;
    }

    /* Removes commas from the initial command */
    public String[] removeCommas(String[] vars) {
        for (int i = 3; i < vars.length-2; i++) {
            String[] temp = vars[i].split(",");
            vars[i] = temp[0];
        }
        return vars;
    }

    /* Breaks up the command into a string array */
    public String[] breakUpCommand() {
        String[] vars = input.split(" ");
        vars = removeCommas(vars);
        return vars;
    }

    /* Gets the old relation, aka the relation we are projecting from */
    public ARelation getBase(SurlyDatabase DB, String[] vars) {
        int len = vars.length;
        String namey[] = vars[len-1].split(";");
        ARelation relation = DB.getRelation(namey[0]);
        return relation;
    }

    /* Check if the attributes actually exist */
    public ArrayList<Attribute> getAttys(ArrayList<String> wantedAttys, ARelation baseRelation) throws Exception{
        LinkedList<Attribute> baseAttributes = baseRelation.getSchema();
        ArrayList<Attribute> wantedAttributes = new ArrayList<Attribute>();
        for (String wantedAttyName : wantedAttys) {
            String parentName = null;
            int periodIndex = wantedAttyName.indexOf(".");
            if (periodIndex != -1) {
                parentName = wantedAttyName.substring(0, periodIndex);
                wantedAttyName = wantedAttyName.substring(periodIndex + 1, wantedAttyName.length());
            }
            int foundAttyCount = 0;
            for (Attribute atty : baseAttributes) {
                if (parentName != null) {
                    if (atty.getName().equals(wantedAttyName) && atty.getParentRelationName().equals(parentName)) {
                        foundAttyCount++;
                        wantedAttributes.add(atty);
                        break;
                    }
                } else {
                    if (atty.getName().equals(wantedAttyName)) {
                        if (foundAttyCount > 0) {
                            Exception e = new Exception("ERROR: Trying to project on a duplicate attribute that is not qualified. Project will not be performed.");
                            throw e;
                        } else {
                            foundAttyCount++;
                            wantedAttributes.add(atty);
                        }
                    }
                }
            }
            if (foundAttyCount == 0) {
                Exception e = new Exception("ERROR: Trying to project on an attribute (" + wantedAttyName + ") that is not in the provided table. Project will not be performed.");
                throw e;
            }
        }
        return wantedAttributes;
    }

    public String[][] index(String[] list, String[] vars, ARelation relation) {
        // List how many attributes we are truncating to.
        int numAtts = 0;
        for (int i = 3; i < vars.length-2; i++) {
            numAtts++;
        }
        // need a list of all tuples (already made in relation, fetch)
        LinkedList<Tuple> tuples = relation.getTuples();
        // need how many tuples there are (int)
        int len = relation.getTupleCount();

        // Make an array for each of the attribute values
        String[][] values;
        values = new String[numAtts][len];


        
        for (int i = 3; i < vars.length-2; i++) {
            String atty = vars[i];
            // for each tuple, go through each in the listy list to get the value associated with the appropriate attribute name

        }
        return values;
    }

    private ArrayList<Tuple> getPrunedTuples(ArrayList<Attribute> attributes, ARelation baseRelation) {
        ArrayList<Tuple> newTuples = new ArrayList<Tuple>();
        for (Tuple baseTuple : baseRelation.getTuples()) {
            Tuple prunedTuple = new Tuple();
            for (AttributeValue av : baseTuple.getAVList()) {
                for (Attribute atty : attributes) {
                    if (av.getName().equals(atty.getName()) && av.getParentRelationName().equals(atty.getParentRelationName())) {
                        prunedTuple.addAttributeValue(av);
                        break;
                    }
                }
            }
            newTuples.add(prunedTuple);
        }
        return newTuples;
    }

    /* returns string arraylist that includes qualifiers in atty names */
    private ArrayList<String> getWantedAttys() {
        String workingString = input;
        int equalsIndex = workingString.indexOf("=");
        workingString = workingString.substring(equalsIndex + 1, workingString.length()).strip();
        int spaceIndex = workingString.indexOf(" ");
        workingString = workingString.substring(spaceIndex + 1, workingString.length()).strip();
        int fromIndex = workingString.indexOf("from");
        if (fromIndex == -1) {
            fromIndex = workingString.indexOf("FROM");
        }
        workingString = workingString.substring(0, fromIndex);

        // working string is now just attributes
        String[] wantedAttyNames = workingString.split(",");
        ArrayList<String> wantedAttys = new ArrayList<String>();
        for (String attyName : wantedAttyNames) {
            wantedAttys.add(attyName.strip());
        }

        return wantedAttys;
    }

    public boolean performCommand(SurlyDatabase DB) {
        if (validInput) {
            // split command
            String[] vars = input.split(" +");
            String newRelationName = vars[0].strip();

            // fetch old relation
            String baseName = vars[vars.length - 1].replaceFirst(";", "").strip();
            ARelation baseRelation = DB.getRelation(baseName);

            if (baseRelation == null) {
                System.err.println("ERROR: Trying to project from a table that does not exist (" + baseName + "). Project cannot be performed.");
                return false;
            }

            // get attributes to project from input
            ArrayList<String> attyNames = getWantedAttys();

            TempRelation newRelation;
            try {
                newRelation = new TempRelation(newRelationName);
            } catch (Exception e) {
                // error already printed, tried to name it catalog
                return false;
            }

            //check to make sure all the attributes we're trying to get actually exist
            ArrayList<Attribute> attributes = new ArrayList<Attribute>();
            try {
                attributes = getAttys(attyNames, baseRelation);
            } catch (Exception e) {
                System.err.println(e);
                return false;
            }

            for (Attribute atty : attributes) {
                newRelation.addAttribute(atty);
            }

            ArrayList<Tuple> prunedTuples = getPrunedTuples(attributes, baseRelation);

            for (Tuple tuple : prunedTuples) {
                newRelation.insert(tuple);
            }

            DB.addRelation(newRelation);
            return true;

        } else {
            System.out.println("validInput is false");
            System.err.println("ERROR: Project could not be performed because format is incorrect.");
            return false;
        }
    }
}
