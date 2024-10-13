import java.util.LinkedList;
import java.util.ArrayList;

public abstract class ARelation {
    protected String name; /* name of the relation */
	protected LinkedList<Attribute> schema;	/* Schema of the relation */
	protected LinkedList<Tuple> tuples;	/* Tuples stored on the relation */
    protected SurlyConstants.relation_indetifier type;

	/* constructtor
     * initiaizes all fields but name */
	public ARelation(SurlyConstants.relation_indetifier type) {
        this.type = type;
		schema = new LinkedList<Attribute>();
		tuples = new LinkedList<Tuple>();
	}

    /* getType
     * returns type
    */
    public SurlyConstants.relation_indetifier getType() {
        return this.type;
    }

	/* addAttribute
     * adds an attibute to the schema and checks if the fields are valid */
	public boolean addAttribute(Attribute attribute) {
		// TODO: put check dattype is valid and length is valid in seperate func
		String type = attribute.getDataType();
		if (!(type.equalsIgnoreCase(SurlyConstants.CHAR_TYPE_STRING) || type.equalsIgnoreCase(SurlyConstants.NUM_TYPE_STRING))) {
			System.err.printf("ERROR: Attempting to add an attribute other than CHAR or NUM, skipping adding it to the relation.\n");
			return false;
		}
		// TODO: put this in seperate func
		String newAttyName = attribute.getName();
		String newAttyParent = attribute.getParentRelationName();
		for (Attribute atty : schema) {
			if ((atty.getName()).equals(newAttyName) && atty.getParentRelationName().equals(newAttyParent)) {
				return false;
			}
		}
		schema.add(attribute);
		return true;
	}

	/* insert
     * Checks tuple works for table and then adds the specified tuple to the relation
	 * returns true if tuple values are valid and tuple was added
	 * returns false if tuple values are incorrect and tuple was not added
	*/
    public boolean insert(Tuple tuple) {
		if (isTupleValid(tuple)) {
			tuples.add(tuple);
			return true;
		} else {
			System.err.printf("ERROR: Tuple to add has unfit data for relation %s's attributes. Please check your command and try again.\n", this.name);
			return false;
		}
    }

	/* delete
     * Remove all tuples from the relation */
	public void delete() {
		while (tuples.size() != 0) {
			tuples.removeLast();
		}
	}

    /* delete
     * Remove all tuples from the relation that meet condition given in string */
	public void deleteWhere(String qualificationString) throws Exception{
		QualificationParser q = new QualificationParser(qualificationString);
		LinkedList<Tuple> toDelete = new LinkedList<Tuple>();
		for (Tuple t : tuples) {
			if (q.testTuple(t)) {
				toDelete.add(t);
			} 
		}
		while (!toDelete.isEmpty()) {
			tuples.remove(toDelete.pop());
		}

	}

	/* getName
     * returns name of table */
	public String getName() {
		return name;
	}

	/* getLength
     * returns amount of attributes in table */
	public int getLength() {
		return schema.size();
	}

	/* getTupleCount
     * returns amount of tuples in table */
	public int getTupleCount() {
		return tuples.size();
	}

    /* getSchema */
    public LinkedList<Attribute> getSchema() {
        return schema;
    }

	/* returns an array of all attribute names 
	 * made for tuple creation
	*/
	public String[] getAttributeNames() {
		String[] result = new String[schema.size()];
		int i = 0;
		for (Attribute atty : schema) {
			result[i] = atty.getName();
			i++;
		}
		return result;
	}

	/* returns an array of all attribute lengths 
	 * made for tuple print (for formatted column width)
	*/
	public int[] getAttributeLengths() {
		int[] result = new int[schema.size()];
		int i = 0;
		for (Attribute atty : schema) {
			if (atty.getLength() > atty.getName().length()) {
				result[i] = atty.getLength();
			} else {
				result[i] = atty.getName().length();
			}
			i++;
		}
		return result;
	}

	/* returns the type of an attribute in the table given it's name 
	 * returns null if attribute is not found
	 * made for isTupleValid
	*/
	public String getAttributeType(String attributeName) {
        for (Attribute atty : schema) {
            if (atty.getName().equalsIgnoreCase(attributeName)) {
                return atty.getDataType();
            }
        }
        return null;
    }

	/* returns the length of a single attribute in the table given it's name
	 * returns 0 if attribute not found
	 * made for ProjectParser 
	 */
	public int getSingularAttributeLength(String attributeName) {
        for (Attribute atty : schema) {
            if (atty.getName().equalsIgnoreCase(attributeName)) {
                return atty.getLength();
            }
        }
        return 0;
    }

	/* checks if a tuple to be added has valid data in each column 
	 * truncates values that are too long given length and datatype
	 * returns: true if tuple can be added to table after length formatting
		false if tuple cannot be added (invalid data for datatypes or incorrect value amount)
	*/
	public boolean isTupleValid(Tuple t) {
		if (t.getValueCount() != schema.size()) {
			// tuple has too few/many values
			return false;
		}

        Tuple workingTuple = t.makeDeepCopy();
        LinkedList<AttributeValue> tupleValues = workingTuple.getAVList();

		for (Attribute atty : schema) {
			final String attributeName = atty.getName();
			final String attyDataType = atty.getDataType();
            final String attyParent = atty.getParentRelationName();
            boolean validAVFound = false;
            for (AttributeValue tupleValue : tupleValues) {
                if (tupleValue.getName().equals(attributeName) && tupleValue.getParentRelationName().equals(attyParent)) {
                    // check datatype
                    if (attyDataType.equalsIgnoreCase(SurlyConstants.CHAR_TYPE_STRING)) {
                        // requires at least 1 char
                        final String VALID_CHAR = "[A-Za-z0-9:& .]+";
                        if (!tupleValue.getValue().matches(VALID_CHAR)) {
                            continue;
                        }

                        validAVFound = true;

                        //check length, OVERWRITE LENGTH IF TOO LONG
                        int length = atty.getLength();
                        t.truncateAttributeValue(attributeName, tupleValue.getParentRelationName(), length, 'r');
                    } else { // datatype == NUM
                        // allows for ints and decimals
                        final String VALID_NUM = "([0-9]*\\.[0-9]+|[0-9]+)";
                        if (!tupleValue.getValue().matches(VALID_NUM)) {
                            continue;
                        }
                        validAVFound = true;

                        //check length, OVERWRITE LENGTH IF TOO LONG
                        int length = atty.getLength();
                        t.truncateAttributeValue(attributeName, tupleValue.getParentRelationName(), length, 'r');
                    }

                    // keep track of tuples checked
                    tupleValues.remove(tupleValue);
                    break;
                }
            }
            if (!validAVFound) {
                return false;
            }
		}
		return true;
	}

	/* returns width of a table given print padding for each attribute
	 * not including the last vertical line
	 * made for the use of the print method
	*/
	protected int calcWidth(int PRINT_PADDING) {
		int[] attyLengths = getAttributeLengths();
		int width = getLength()*PRINT_PADDING;  // column lines will be in width of attribute + padding not including the last line
		for (int i = 0; i < attyLengths.length; i++) {
			width += attyLengths[i];
		}
		return width;
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
		System.out.format("%-" + calcWidth(PRINT_PADDING) + "s|\n", "| " + name);
		System.out.println(tableLine);

		// print attribute name row
		int[] attyLengths = getAttributeLengths();
		int i = 0;
		for (Attribute atty : schema) {
			// format column width based on length of attibute
			System.out.format("%-" + (attyLengths[i] + PRINT_PADDING) + "s", "| " + atty.getName());
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

    public ArrayList<Tuple> getPassingTuples(ArrayList<ArrayList<Condition>> conditions) throws Exception{
        ArrayList<Tuple> passingTuples = new ArrayList<Tuple>();
        if (conditions.size() == 0) {
            for (Tuple tuple : this.tuples) {
                passingTuples.add(tuple);
            }
        } else {
            for (Tuple tuple : this.tuples) {
                boolean tupleResult = false;
                for (ArrayList<Condition> andGroup : conditions) {
                    boolean andResult = true;
                    for (Condition condition : andGroup) {
                        // throws exception
                        andResult = andResult && condition.testTuple(tuple);
                    }
                    tupleResult = tupleResult || andResult;
                    if (tupleResult) {
                        break;
                    }
                }
                if (tupleResult) {
                    passingTuples.add(tuple);
                }
            }
        }
        return passingTuples;
    }

    public LinkedList<Tuple> getTuples() {
        return tuples;
    }
}
