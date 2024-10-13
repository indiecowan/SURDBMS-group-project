public class Catalog extends ARelation {
    /* invokes super constructor and sets relations LL up to have
     * two columns of datatypes and names defined in fields (potential for overloading for customization)
    */
    public Catalog() {
        super(SurlyConstants.relation_indetifier.CATALOG);
        this.name = SurlyConstants.CATALOG_NAME;
        Attribute firstAtty = new Attribute(SurlyConstants.CATALOG_COLUMN1, 
            SurlyConstants.FIRST_DATATYPE, SurlyConstants.FIRST_LENGTH, this.name);
		addAttribute(firstAtty);
		Attribute secondAtty = new Attribute(SurlyConstants.CATALOG_COLUMN2, 
            SurlyConstants.SECOND_DATATYPE, SurlyConstants.SECOND_LENGTH, this.name);
		addAttribute(secondAtty);
    }

    /* blocks user from performing delete like you could do on other relations */
    public void delete() {
		System.err.println("ERROR: Cannot perform DELETE on a Catalog.");
	}

    /* alias for getTupleCount but with more clear contextual meaning */
    public int getTableCount() {
		return getTupleCount();
	}

    /* alias for getAttributeNames but with more clear contextual meaning */
    public String[] getTableNames() {
		return getAttributeNames();
	}

    /* Finds and deletes tuple in catalog with relation name
	 * Returns true if the table is found, false otherwise  */
	public boolean removeTable(String relationName) {
        for (Tuple t : tuples) {
            if (t.getValue(SurlyConstants.CATALOG_COLUMN1).equalsIgnoreCase(relationName)) {
                tuples.remove(t);
                return true;
            }
        }
        return false;
	}

    /* prevents users from adding an attribute to catalog like they could a normal relation */
    public boolean addAttribute() {
        System.err.println("ERROR: You may not add an attribute to a Catalog.");
        return false;
    }

    /* takes a relation, translates it into a catalog tuple and adds it to catalog */
    public boolean addTableSchema(ARelation relation) {
		Tuple newTableSchema = new Tuple();
        AttributeValue av = new AttributeValue(SurlyConstants.CATALOG_COLUMN1, relation.getName(), this.name);
        newTableSchema.addAttributeValue(av);
        av = new AttributeValue(SurlyConstants.CATALOG_COLUMN2, Integer.toString(relation.getLength()), this.name);
        newTableSchema.addAttributeValue(av);
        return insert(newTableSchema);
    }
}
