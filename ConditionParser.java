public class ConditionParser {
    private String attribute;
    private String operator;
    private String value;

    /* constructor */
    public ConditionParser(String condString) {
        String[] splitBySpace = condString.split(" ");

        this.attribute = splitBySpace[0];
        this.operator = splitBySpace[1];
        String val = "";
        for (int i = 2; i < splitBySpace.length; i++) {
            val += splitBySpace[i].replace("'", "") + " ";
        }
        this.value = val.strip();
    }

    /* Returns true if the tuple's value for the this.attribute evaluates to true
       when compared to this.value using this.operator */
    public boolean testTuple(Tuple tuple) throws Exception{
        final String numFormat = "[0-9]+";
        String attValue = tuple.getValue(attribute);

        if (attValue == null) {
            System.err.println("ERROR: " + attribute + " isn't an attribute of this tuple");
            Exception e = new Exception("ERROR: Attribute not found in tuple.");
            throw e;
        } else if (attValue.matches(numFormat) && value.matches(numFormat)) {
            return compareNums(Integer.parseInt(attValue), Integer.parseInt(value));
        } else {
            return compareStrings(attValue);
        }
    }

    /* Compares attValue and value lexicographically and returns true or false depending on 
     whether <attValue> <this.operator> <this.value> evaluates to true */
    private boolean compareStrings(String attValue) {
        int compVal = attValue.compareTo(value);
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

    /* Compares attValue and value and returns true or false depending on
       whether <attValue> <this.operator> <this.value> evaluates to true */
    private boolean compareNums(int attribute, int numValue) {
        switch (operator) {
            case "=":
                return attribute == numValue;
            case "!=": 
                return attribute != numValue;
            case "<":
                return attribute < numValue;
            case ">":
                return attribute > numValue;
            case "<=":
                return attribute <= numValue;
            case ">=":
                return attribute >= numValue;
            default:
                return false;
        }
    }

    /* Prints the class attributes */
    public void print() {
        System.out.println(this.attribute + " " + this.operator + " " + this.value);
    }
}
