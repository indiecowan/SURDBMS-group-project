/*  SURLY 1
Natalie Norris, Robin Preble, Indie Cowan
1/21/23
*/

public class RelationParser {
    /* Reference to the input string being parsed */
    private String input;
    private boolean validInput = true;
    private final String RELATION_FORMAT = "RELATION [-_A-Za-z0-9]+ *\\(([-_A-Za-z0-9]+ +[-_A-Za-z]+ +[0-9]+,?[ \n]*)*\\);";

    /* initializes the input field and validInput field 
     * checks if input is valid but does NOT carry out instruction
    */
    public RelationParser(String input) {
        // check for correct format: name, parentheses, each attribute has 3 args, semicolon
        this.input = input;
        if (!input.matches(RELATION_FORMAT)) {
          System.err.println("ERROR: Relation has incorrect format.");
          validInput = false;
          return;
        }
    }

    /* returns true if input is in valid format */
    public boolean hasValidInput() {
      return validInput;
    }

    /* Parses and returns the Relation to create
     * relation has name and attributes filled out
    */
    public BaseRelation parseRelation() {
      // account for repeat spaces and newlines in split
      String[] array = input.split("[ *\n*]+");
      BaseRelation rel;
      try {
        rel = new BaseRelation(array[1]);
      } catch (Exception e) {
        System.err.println("ERROR: Could not create relation beacuse creating a base relation with the name of 'CATALOG' is forbidden.");
        return null;
      }

      String attName;
      String datType;
      int leng;
      // starting at 2 bc 0="RELATION" and 1=relation_name as confirmed by validInput
      for (int i = 2; i < array.length; i+=3) {
          attName = removeFluff(array[i]);
          datType = removeFluff(array[i+1]);
          leng = Integer.parseInt(removeFluff(array[i+2]));
          Attribute atty = new Attribute(attName, datType, leng, rel.getName());
          rel.addAttribute(atty);
      }
        return rel;
    }


    /* Parses and returns the TempRelation to create
     * relation has name and attributes filled out
    */
    public TempRelation parseTempRelation() {
      // account for repeat spaces and newlines in split
      String[] array = input.split("[ *\n*]+");
      TempRelation rel;
      try {
        System.out.println(array[1]);
        rel = new TempRelation(array[1]);
      } catch (Exception e) {
        System.err.println("ERROR: Could not create relation beacuse creating a base relation with the name of 'CATALOG' is forbidden.");
        return null;
      }

      String attName;
      String datType;
      int leng;
      // starting at 2 bc 0="RELATION" and 1=relation_name as confirmed by validInput
      for (int i = 2; i < array.length; i+=3) {
          attName = removeFluff(array[i]);
          datType = removeFluff(array[i+1]);
          leng = Integer.parseInt(removeFluff(array[i+2]));
          Attribute atty = new Attribute(attName, datType, leng, rel.getName());
          rel.addAttribute(atty);
      }
        return rel;
    }


    /* Removes commas, parenthases, and semicolons (,); */
    private String removeFluff(String working) {
        String returny;
        if (working.charAt(0) == '(') {
            returny = working.substring(1, working.length());
        } else if (working.length() > 0 && working.charAt(working.length()-1) == ',') {
            returny = working.substring(0, working.length()-1);
        } else if (working.length() > 1 && working.charAt(working.length()-2) == ')') {
            returny = working.substring(0, working.length()-2);
        } else {
          returny = working;
        }
        return returny;
    }

    /* actually carries out command in input IF input has valid format
     * returns true if instruction could be carried out, false otherwise
     */
    public boolean performCommand(SurlyDatabase DB) {
      if (validInput) {
        BaseRelation rel = parseRelation();
        if (rel == null) {
          return false;
        }
        DB.addRelation(rel);
        return true;
      } else {
        System.err.println("ERROR: Relation could not be added to database because format is incorrect.");
        return false;
      }
    }


    /* actually carries out command in input IF input has valid format
     * returns true if instruction could be carried out, false otherwise
     */
    public boolean performTempRel(SurlyDatabase DB) {
      if (validInput) {
        TempRelation rel = parseTempRelation();
        if (rel == null) {
          return false;
        }
        DB.addRelation(rel);
        return true;
      } else {
        System.err.println("ERROR: Relation could not be added to database because format is incorrect.");
        return false;
      }
    }
}
