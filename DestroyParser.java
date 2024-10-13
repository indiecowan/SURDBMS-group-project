import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DestroyParser {
    /* Reference to the input string being parsed */
    private String input;
    private boolean validInput = true;
    private final String DESTROY_FORMAT = "DESTROY ([-_a-zA-Z0-9]*);";
    
    /* Constructor to initialize the input field */
    public DestroyParser(String input) {
        this.input = input.strip();
        
        if (!input.matches(DESTROY_FORMAT)) {
            System.err.println("ERROR: Destroy has incorrect format.");
            validInput = false;
            return;
        }
	}

    /* returns true if input is in valid format */
    public boolean hasValidInput() {
        return validInput;
    }

    /* Parses and returns the name of the relation to destroy */
    public String parseRelationName() {
        final String NAME_PATTERN = "DESTROY +([-_A-Za-z0-9]*)";
        Pattern p = Pattern.compile(NAME_PATTERN);
        Matcher m = p.matcher(input);
        m.find();
        return m.group(1);
    }

    /* Actually carries out the destroy on database DB */
    public boolean performCommand(SurlyDatabase DB) {
        if (validInput) {
            String relationName = parseRelationName();
            ARelation thisRelation = DB.getRelation(relationName);
            
            if (relationName.equalsIgnoreCase("CATALOG")) {
                System.err.println("ERROR: CATALOG cannot be deleted.");
                return false;
            } else if (thisRelation.getType().equals(SurlyConstants.relation_indetifier.TEMP)) {
                System.err.println("ERROR: Cannot destroy a temporary relation.");
                return false;
            } else if (thisRelation != null) {
                DB.destroyRelation(relationName);
                return true;
            } else {
                System.err.println("ERROR: Destroy could not be performed on database because " + relationName + " is not in the database");
                return false;
            }
        } else {
            System.err.println("ERROR: Destroy could not be performed on database because format is incorrect.");
            return false;
        }
    }
}
