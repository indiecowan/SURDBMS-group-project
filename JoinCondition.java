import java.util.ArrayList;
import java.util.LinkedList;

public class JoinCondition {
    private String attribute1;
    private String attribute1Parent;
    private ARelation tableA; // contains atty1
    private String attribute2;
    private String attribute2Parent;
    private ARelation tableB; // contains atty2
    private String operator;

    private String attributeType;

    /* constructor */
    public JoinCondition(String attribute1, String operator, String attribute2) {
        this.attribute1 = attribute1;
        this.operator = operator; 
        this.attribute2 = attribute2; 
    }

    public String[] getContents() {
        String[] bits = {attribute1, operator, attribute2};
        return bits;
    }

    private boolean compareStrings(String value1, String value2) {
        int compVal = value1.compareTo(value2);
        switch (operator) {
            case "=":
                return compVal == 0;
            case "!=": 
                return compVal != 0;
            case "<":
                return compVal < 0;
            case ">":
                return compVal > 0;
            case "<=":
                return compVal <= 0;
            case ">=":
                return compVal >= 0;
            default:
                return false;
        }
    }

    private boolean compareNums(String value1, String value2) {
        int value1Int = Integer.parseInt(value1);
        int value2Int = Integer.parseInt(value2);

        switch (operator) {
            case "=":
                return value1Int == value2Int;
            case "!=": 
                return value1Int != value2Int;
            case "<":
                return value1Int < value2Int;
            case ">":
                return value1Int > value2Int;
            case "<=":
                return value1Int <= value2Int;
            case ">=":
                return value1Int >= value2Int;
            default:
                return false;
        }
    }

    public void print() {
        System.out.println(this.attribute1 + " " + this.operator + " " + this.attribute2);
    }

    /* tests if two tuples pass a join condition, throws exception if an atribute cannot be found in either tuple or is in both and not qualified */
    private boolean testTuplePair(Tuple tupleA, Tuple tupleB) throws Exception {
        String attribute1Value = tupleA.getValueWithParent(attribute1, attribute1Parent);
        String attribute2Value = tupleB.getValueWithParent(attribute2, attribute2Parent);

        if (attribute1Value == null) {
            System.out.println("ATTY1 NULL");
        }
        if (attribute2Value == null) {
            System.out.println("ATTY2 NULL");
        }

        boolean tuplePairPasses = false;
        if (attributeType.equals(SurlyConstants.NUM_TYPE_STRING)) {
            tuplePairPasses = compareNums(attribute1Value, attribute2Value);
        } else {
            tuplePairPasses = compareStrings(attribute1Value, attribute2Value);
        }

        return tuplePairPasses;
    }

    private Attribute getAttributeInSchema(ARelation relation, String attyName, String attyParent) {
        LinkedList<Attribute> schema = relation.getSchema();
        for (Attribute atty : schema) {
            if (atty.getName().equals(attyName) && atty.getParentRelationName().equals(attyParent)) {
                return atty;
            }
        }
        return null;
    }

    /* makes tableA associated with first atty in condition, tableB with the second
     * populates 
        attribute1;
        attribute1Parent;
        tableA; // contains atty1
        attribute2;
        attribute2Parent;
        tableB;
     */
    private void alignAndAssignTables(ARelation firstTable, ARelation secondTable) throws Exception{
        LinkedList<Attribute> firstTableSchema = firstTable.getSchema();
        LinkedList<Attribute> secondTableSchema = secondTable.getSchema();

        String attribute1Type = "";
        String attribute2Type = "";

        // find attribute parents
        // attribute1
        int periodIndex = attribute1.indexOf(".");
        if (periodIndex != -1) {
            // parent is given
            attribute1Parent = attribute1.substring(0, periodIndex);
            attribute1 = attribute1.substring(periodIndex + 1, attribute1.length());
            Attribute table1Atty = getAttributeInSchema(firstTable, attribute1, attribute1Parent);
            Attribute table2Atty = getAttributeInSchema(secondTable, attribute1, attribute1Parent);
            if (table1Atty != null) {
                this.tableA = firstTable;
                attribute1Type = table1Atty.getDataType();

            } else if (table2Atty != null) {
                this.tableA = secondTable;
                attribute1Type = table2Atty.getDataType();
            } else {
                // attribute not found in either table
                Exception e = new Exception("ERROR: Trying to use attribute that is not found in either table. Join cannot be performed.");
                throw e;
            }
        } else {
            // find parent
            // is atty in firstTable
            int matchesIn1 = 0;
            for (Attribute atty : firstTableSchema) {
                if (atty.getName().equals(attribute1)) {
                    if (matchesIn1 > 0) {
                        Exception e = new Exception("ERROR: Trying to use ambiguous tuple without qualifying. Join cannot be performed.");
                        throw e;
                    } else {
                        matchesIn1++;
                        attribute1Parent = atty.getParentRelationName();
                        attribute1Type = atty.getDataType();
                    }
                }
            }

            // is atty in second table
            int matchesIn2 = 0;
            for (Attribute atty : secondTableSchema) {
                if (atty.getName().equals(attribute1)) {
                    if (matchesIn2 > 0) {
                        Exception e = new Exception("ERROR: Trying to use ambiguous tuple without qualifying. Join cannot be performed.");
                        throw e;
                    } else {
                        matchesIn2++;
                        attribute1Parent = atty.getParentRelationName();
                        attribute1Type = atty.getDataType();
                    }
                }
            }

            if (matchesIn1 > 0 && matchesIn2 > 0) {
                // attribute is in both tables but not qualified
                Exception e = new Exception("ERROR: Trying to use ambiguous tuple without qualifying. Join cannot be performed.");
                throw e;
            } else if (matchesIn1 > 0) {
                this.tableA = firstTable;
            } else if (matchesIn2 > 0) {
                this.tableA = secondTable;
            } else {
                // attribute not found in either table
                Exception e = new Exception("ERROR: Trying to use attribute that is not found in either table. Join cannot be performed.");
                throw e;
            }

        }

        // attribute2
        periodIndex = attribute2.indexOf(".");
        if (periodIndex != -1) {
            // parent is given
            attribute2Parent = attribute2.substring(0, periodIndex);
            attribute2 = attribute2.substring(periodIndex + 1, attribute2.length());
            Attribute table1Atty = getAttributeInSchema(firstTable, attribute2, attribute2Parent);
            Attribute table2Atty = getAttributeInSchema(secondTable, attribute2, attribute2Parent);
            if (table1Atty != null) {
                this.tableB = firstTable;
                attribute2Type = table1Atty.getDataType();
            } else if (table2Atty != null) {
                this.tableB = secondTable;
                attribute2Type = table2Atty.getDataType();
            } else {
                // attribute not found in either table
                Exception e = new Exception("ERROR: Trying to use attribute that is not found in either table. Join cannot be performed.");
                throw e;
            }
        } else {
            // find parent
            // is atty in firstTable
            int matchesIn1 = 0;
            for (Attribute atty : firstTableSchema) {
                if (atty.getName().equals(attribute2)) {
                    if (matchesIn1 > 0) {
                        Exception e = new Exception("ERROR: Trying to use ambiguous tuple without qualifying. Join cannot be performed.");
                        throw e;
                    } else {
                        matchesIn1++;
                        attribute2Parent = atty.getParentRelationName();
                        attribute2Type = atty.getDataType();
                    }
                }
            }

            // is atty in second table
            int matchesIn2 = 0;
            for (Attribute atty : secondTableSchema) {
                if (atty.getName().equals(attribute2)) {
                    if (matchesIn2 > 0) {
                        Exception e = new Exception("ERROR: Trying to use ambiguous tuple without qualifying. Join cannot be performed.");
                        throw e;
                    } else {
                        matchesIn2++;
                        attribute2Parent = atty.getParentRelationName();
                        attribute2Type = atty.getDataType();
                    }
                }
            }

            if (matchesIn1 > 0 && matchesIn2 > 0) {
                // attribute is in both tables but not qualified
                Exception e = new Exception("ERROR: Trying to use ambiguous tuple without qualifying. Join cannot be performed.");
                throw e;
            } else if (matchesIn1 > 0) {
                this.tableB = firstTable;
            } else if (matchesIn2 > 0) {
                this.tableB = secondTable;
            } else {
                // attribute not found in either table
                Exception e = new Exception("ERROR: Trying to use attribute that is not found in either table. Join cannot be performed.");
                throw e;
            }

            // check if both attributes have same parent relation
            if (this.tableA == this.tableB) {
                // attributes are both from same table
                Exception e = new Exception("ERROR: Trying to use attributes that are both from same table to join on. Join cannot be performed.");
                throw e;
            }

            // check that both attributes have the same type
            if (attribute1Type.equalsIgnoreCase(attribute2Type)) {
                this.attributeType = attribute1Type;
            } else {
                Exception e = new Exception("ERROR: Attempting to join on attributes of different types. Join cannot be performed.");
                throw e;
            }
        }
    }

    private Tuple joinTuples(Tuple tupleA, Tuple tupleB) {
        Tuple joinedTuple = new Tuple();
        for (AttributeValue av : tupleA.getAVList()) {
            joinedTuple.addAttributeValue(av);
        }
        for (AttributeValue av : tupleB.getAVList()) {
            joinedTuple.addAttributeValue(av);
        }
        
        return joinedTuple;
    }

    public ArrayList<Tuple> getPassingTuples(ARelation tableA, ARelation tableB) throws Exception {
        // tableA -> atty 1, tableB -> atty2

        alignAndAssignTables(tableA, tableB);  // throws exception

        ArrayList<Tuple> passingTuples = new ArrayList<Tuple>();
        for (Tuple tupleA : this.tableA.getTuples()) {
            for (Tuple tupleB : this.tableB.getTuples()) {
                boolean tupleResult = testTuplePair(tupleA, tupleB);
                if (tupleResult) {
                    Tuple joinedTuple = joinTuples(tupleA, tupleB);
                    passingTuples.add(joinedTuple);
                }
            }
        }
        return passingTuples;
    }
}
