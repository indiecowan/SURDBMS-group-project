/*  SURLY 1
    Natalie Norris, Robin Preble, Indie Cowan
    1/21/23
*/

import java.lang.Exception;

public class TempRelation extends ARelation{
	/* initiaizes all fields 
	 * has potential for overloading to make custom catalog data
	*/
	public TempRelation(String name) throws Exception {
        super(SurlyConstants.relation_indetifier.TEMP);
        this.name = name;
		if (name.equals(SurlyConstants.CATALOG_NAME)) {
            Exception e = new Exception("ERROR: You are creating a relation with the name of 'CATALOG'. This is forbidden.");
			throw e;
		}
	}

    /* blocks user from performing delete like you could do on other relations */
    public void delete() {
        System.err.println("ERROR: Cannot perform DELETE on a Temporary Relation.");
    }

    /* returns an array of all attribute lengths 
	 * made for tuple print (for formatted column width)
	*/
	public int[] getAttributeLengths() {
		int[] result = new int[schema.size()];
		int i = 0;
		for (Attribute atty : schema) {
            String qualifiedName = atty.getParentRelationName() + "." + atty.getName();
			if (atty.getLength() > qualifiedName.length()) {
				result[i] = atty.getLength();
			} else {
				result[i] = qualifiedName.length();
			}
			i++;
		}
		return result;
	}

    /* Formats and prints the relation's name, schema, and tuples */
	public void print() {
		final int PRINT_PADDING = 3;
		int tableWidth = calcWidth(PRINT_PADDING)+1;

		// make border line and tableLine
		String borderLine = "";
		String tableLine = "";
		for (int i = 0; i < tableWidth; i++) {
			borderLine += "*";
			tableLine += "-";
		}

		System.out.println(borderLine);
		// print name
        int width;
        if (calcWidth(PRINT_PADDING) < 1) {
            width = name.length();
        } else {
            width = calcWidth(PRINT_PADDING);
        }
		System.out.format("%-" + width + "s|\n", "| " + name);
		System.out.println(tableLine);

		// print attribute name row
		int[] attyLengths = getAttributeLengths();
		int i = 0;
		for (Attribute atty : schema) {
			// format column width based on length of attibute
            String qualifiedAttyName = atty.getParentRelationName() + "." + atty.getName();
			System.out.format("%-" + (attyLengths[i] + PRINT_PADDING) + "s", "| " + qualifiedAttyName);
			i++;
		}
		System.out.println("|");
		System.out.println(tableLine);

		// print tuples
		for (Tuple tupy : tuples) {
			tupy.print(attyLengths, PRINT_PADDING, schema);
		}
		System.out.println(borderLine);
	}
}
