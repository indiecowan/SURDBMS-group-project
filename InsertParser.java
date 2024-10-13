import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class InsertParser {
    /* Reference to the input string being parsed */
    private String input;
    private boolean validInput = true;
    private final String INSERT_FORMAT = "INSERT +[-_a-zA-Z0-9]+ +((('[-_a-zA-Z0-9 &:.]+')|([-_a-zA-Z0-9:&.]+))[ \n]+)+(('[-_a-zA-Z0-9 &:.]+')|([-_a-zA-Z0-9:&.]+));";

    /* initializes the input field and validInput field
     * checks if input was valid but does not perform command
     */
    public InsertParser(String input) {
        // check for correct format: insert, title, and valid attributes ending with ;
        this.input = input;
        if (!input.matches(INSERT_FORMAT)) {
            System.err.println("ERROR: Insert has incorrect format.");
            validInput = false;
            return;
        }
	}

    /* returns true if input is a valid insert command */
    public boolean hasValidInput() {
        return validInput;
    }

    /* Parses and returns the name of the relation to insert into
     * preconditions: isValidInput must be true
     * returns: name of relation to be inserted to or null if input is invalid
    */
    public String parseRelationName() {
        if (!validInput) {
            System.err.println("ERROR: parseRelationName could not be performed because INSERT command has incorrect formatting.");
            return null;
        }
        final String NAME_PATTERN = "INSERT +([A-Za-z0-9]*)";
        Pattern p = Pattern.compile(NAME_PATTERN);
        Matcher m = p.matcher(input);
        m.find();
        // know there will be a match bc of precondition validInput
        return m.group(1);
    }

    /* Parses and returns the Tuple to insert 
     * arguments: toBeAddedTo is relation that will be modified (needed to get attribute names)
     * tuple might have attributes that have invalid data, the relation will handle for this when attempting to insert the tuple
     * returns: new tuple or NULL if tuple has incorrect amount of attributes for table
    */
    public Tuple parseTuple(BaseRelation toBeAddedTo) {
        // isolate attributes part of string in tupleString
        final String RELATION_NAME = toBeAddedTo.getName();
        String tupleString = input;
        tupleString = tupleString.replaceFirst("(INSERT|insert) +[A-Za-z0-9]+ +", "");
        tupleString = tupleString.replace(";", "");
        tupleString = tupleString.trim();

        // use a matcher to find each value one at a time
        // ATTY_PATTERN catches spaces as well so find can work back to back 
        final String ATTY_PATTERN = "([A-Za-z0-9&:.]+ *|'[A-Za-z0-9 &:.]+' *)";
        Pattern p = Pattern.compile(ATTY_PATTERN);
        // matcher m compares tupleString with ATTY_PATTERN to find next value
        Matcher m = p.matcher(tupleString);
        Tuple newTuple = new Tuple();  // tuple to be returned
        // get attribute names from relation so names can be assigned to AttributeValues
        String[] attyNames = toBeAddedTo.getAttributeNames();
        int i = 0;
        boolean found = m.find();
        while (found) {
            if (i >= attyNames.length) {
                System.err.printf("ERROR: Tuple to be inserted into %s has too many values so INSERT could not be completed.\n", RELATION_NAME);
                return null;
            }
            String value = m.group(1);
            // remove single quotes 
            value = value.replaceAll("'", "");
            value = value.trim(); // pattern catches space so must remove
            String attyName = attyNames[i];
            AttributeValue newVal = new AttributeValue(attyName, value, RELATION_NAME);
            newTuple.addAttributeValue(newVal);
            i++;
            found = m.find();
        }
        if (i < attyNames.length) {
            System.err.printf("ERROR: Tuple to be inserted into %s has too few values so INSERT could not be completed.\n", RELATION_NAME);
            return null;
        }
        return newTuple;
    }

    /* actually carries out the insert on database DB */
    public boolean performCommand(SurlyDatabase DB) {
        if (validInput) {
            String relationName = parseRelationName();
            ARelation toBeAddedToA = DB.getRelation(relationName);
            if (toBeAddedToA == null) {
                System.err.printf("ERROR: Insert could not be performed on database because table '%s' does not exist.\n", relationName);
                return false;
            }
            // check relation type
            if (toBeAddedToA.getType() != SurlyConstants.relation_indetifier.BASE) {
                System.err.println("ERROR: You cannot insert into an array that is temporary or the catalog.");
                return false;
            }

            BaseRelation toBeAddedTo = (BaseRelation) toBeAddedToA;
            // parseTuple will catch if attribute count is incorrect
            Tuple newTuple = parseTuple(toBeAddedTo);
            if (newTuple == null) return false;
            // attribute validity is checked in Relation class insert method!
            // check that table exists
            if (toBeAddedTo.insert(newTuple)) return true;
            else return false;
        } else {
            System.err.println("ERROR: Insert could not be performed on database because format is incorrect.");
            return false;
        }
    }
}
