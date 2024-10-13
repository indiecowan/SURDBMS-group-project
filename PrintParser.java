/*  SURLY 1
    Natalie Norris, Robin Preble, Indie Cowan
    1/21/23
*/

public class PrintParser {
    /* Reference to the input string being parsed */
    private String input;
    private final String PRINT_FORMAT = "PRINT +([_-a-zA-Z0-9_]+,[ \n]+)*[_-a-zA-Z0-9_]+;";
    private boolean validInput = true;

    /* Constructor initializes the input and validInput fields
     * checks if command has valid formatting but does NOT carry out instruction
     */
    public PrintParser(String input) {
		// check for correct format: print, name, colon
        this.input = input;
        if (!input.matches(PRINT_FORMAT)) {
          System.err.println("ERROR: Print has incorrect format.");
          validInput = false;
          return;
        }
	}

    /* returns true if input has valid formatting */
    public boolean hasValidInput() {
        return validInput;
    }

    /* Parses and returns the names the relations to print */
    public String[] parseRelationNames() {
        // format: PRINT <relexpr>;
        // remove 'print' from input (know it's already in there bc of validInput)
        input = input.replaceAll("PRINT ", "");
        input = input.replaceAll(";", "");

        // split by commas to get relation names
        input = input.replaceAll(" ", "");  // shouldn't be spaces in relation names
        String[] args = input.split(",");

        return args;

    }
    
    /* actually carries out instruction in input string
     * returns true if instruction could be performed, false otherwise
     */
    public boolean performCommand(SurlyDatabase DB) {
        if (validInput) {
            String[] relations = parseRelationNames();
            for (String relationName : relations) {
                ARelation relation = DB.getRelation(relationName);
                if (relation != null) {
                    relation.print();
                    System.out.println();
                } else {
                    System.err.println("ERROR: " + relationName + " could not be printed because that relation does not exist.");
                }
            }
            return true;
          } else {
            System.err.println("ERROR: Print could not be performed because format is incorrect.");
            return false;
        }
    }
}
