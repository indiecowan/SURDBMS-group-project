import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DeleteParser {
    /* Reference to the input string being parsed */
    private String input;
    private boolean validInput = true;
    private boolean DeleteWhere = false;
    private final String DELETE_FORMAT = "DELETE ([-_a-zA-Z0-9]*);";
    private final String DELETE_WHERE_FORMAT = "DELETE ([-_a-zA-Z0-9]+) WHERE ([-_a-zA-Z0-9]+) (=|!=|>|<|>=|<=) (([-_a-zA-Z0-9]+)|'([ -_.a-zA-Z0-9]+?)')( (AND|OR|and|or) ([-_a-zA-Z0-9]+) (=|!=|>|<|>=|<=) (([-_a-zA-Z0-9]+)|'([ -_.a-zA-Z0-9]+?)'))*;";

    /* Constructor to initialize the input field */
    public DeleteParser(String input) {
        this.input = input;
        if (!(input.matches(DELETE_FORMAT) || input.matches(DELETE_WHERE_FORMAT))) {
            System.err.println("ERROR: Delete has incorrect format.");
            validInput = false;
            return;
        } else if (input.matches(DELETE_WHERE_FORMAT)) {
            DeleteWhere = true;
        }
    }

    /* Returns true if input is in valid format */
    public boolean hasValidInput() {
        return validInput;
    }

    /* Parses and returns the name of the relation for delete */
    public String parseRelationName() {
        final String NAME_PATTERN = "DELETE +([-_A-Za-z0-9]*)";
        Pattern p = Pattern.compile(NAME_PATTERN);
        Matcher m = p.matcher(input);
        m.find();
        return m.group(1);
    }

    /* Parses and returns the qualification string */
    public String parseQualificationString() {
        final String where_string = "WHERE ";
        int start = input.indexOf(where_string) + where_string.length();
        return input.substring(start, input.length()-1);
    }

    /* Actually carries out the delete on database DB */
    public boolean performCommand(SurlyDatabase DB) {
        if (validInput) {
            String relationName = parseRelationName();
            ARelation thisRelation = DB.getRelation(relationName);
            if (thisRelation == null) {
                System.err.println("ERROR: Can't delete from " + relationName + " because it doesn't exist");
                return false;
            }
            if (DeleteWhere) {
                try {
                    thisRelation.deleteWhere(parseQualificationString());  
                } catch (Exception e) {
                    // error already printed
                    return false;
                } 
                return true;
            } else if (thisRelation != null) {
                thisRelation.delete();
                return true;
            }
            return false;
            
        } else {
            System.err.println("ERROR: Delete could not be performed on database because format is incorrect.");
            return false;
        }
    }
}