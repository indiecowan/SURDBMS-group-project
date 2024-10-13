import java.util.ArrayList;

public class Condition {
    private String attributeName;
    private String parentRelationName = null;
    private String operator;
    private String value;

    private final String NUM_FORMAT = "[0-9]+";

    /* constructor */
    public Condition(String attributeName, String operator, String value) {
         this.attributeName = attributeName;
        int periodIndex = attributeName.indexOf(".");
        if (periodIndex != -1) {
            this.attributeName = attributeName.substring(periodIndex + 1, attributeName.length());
            this.parentRelationName = attributeName.substring(0, periodIndex);
        }
        this.operator = operator; 
        this.value = value; 
    }

    public boolean testTuple(Tuple tuple) throws Exception {
        ArrayList<AttributeValue> attyValues = tuple.getValues(attributeName);
        AttributeValue av = new AttributeValue(attributeName, value, parentRelationName);
        if (attyValues.size() == 0) {
            System.err.println("ERROR: " + attributeName + " isn't an attribute of this tuple.");
            Exception e = new Exception("ERROR: Attribute not found in tuple.");
            throw e;
        } else if (attyValues.size() > 1) {
            if (parentRelationName == null) {
                Exception e = new Exception("ERROR: Failed to qualify ambiguous attribute.");
                throw e; // print error in calling function
            } else {
                for (AttributeValue currentAV : attyValues) {
                    if (currentAV.getParentRelationName().equals(parentRelationName)) {
                        av = currentAV;
                        break;
                    }
                }
            }
        } else {
            av = attyValues.get(0);
        }

        if (av.getValue().matches(NUM_FORMAT) && this.value.matches(NUM_FORMAT)) {
            return compareNums(Integer.parseInt(av.getValue()), Integer.parseInt(value));
        } else {
            return compareStrings(av.getValue());
        }
    }

    private boolean compareStrings(String givenValue) {
        int compVal = givenValue.compareTo(value);
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

    private boolean compareNums(int givenValue, int baseValue) {
        switch (operator) {
            case "=":
                return givenValue == baseValue;
            case "!=": 
                return givenValue != baseValue;
            case "<":
                return givenValue < baseValue;
            case ">":
                return givenValue > baseValue;
            case "<=":
                return givenValue <= baseValue;
            case ">=":
                return givenValue >= baseValue;
            default:
                return false;
        }
    }

    public void print() {
        System.out.println(this.attributeName + " " + this.operator + " " + this.value);
    }
}
