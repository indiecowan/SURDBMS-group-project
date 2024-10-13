/*  SURLY 1
    Natalie Norris, Robin Preble, Indie Cowan
    1/21/23
*/

import java.util.LinkedList;
import java.util.ArrayList;

public class Tuple {
    private LinkedList<AttributeValue> values;  /* Values of each attribute in the tuple */

    /* initializes the values linked list */
    public Tuple() {
        this.values = new LinkedList<AttributeValue>();
    }

    /* adds an attribute value to the tuple */
    public void addAttributeValue(AttributeValue av) {
        values.add(av);
    }

    /* Returns the value of the specified attribute, null if attribute value not found */
    // delete this and fix dpeeendencies later
    public String getValue(String attributeName) {
        for (AttributeValue value : values) {
            if (value.getName().equalsIgnoreCase(attributeName)) {
                return value.getValue();
            }
        }
        return null;
    }

    /* Returns the value of the specified attribute, null if attribute value not found */
    public ArrayList<AttributeValue> getValues(String attributeName) {
        ArrayList<AttributeValue> attributes = new ArrayList<AttributeValue>();
        for (AttributeValue value : values) {
            if (value.getName().equalsIgnoreCase(attributeName)) {
                attributes.add(value);
            }
        }
        return attributes;
    }

    /* Returns s value of the specified attribute, null if attribute value not found */
    public boolean getValueAndParent(String attributeName, String[] resultsBucket) {
        resultsBucket[0] = "";
        resultsBucket[1] = "";
        for (AttributeValue value : values) {
            if (value.getName().equalsIgnoreCase(attributeName)) {
                resultsBucket[0] = value.getValue();
                resultsBucket[1] = value.getParentRelationName();
                return true;
            }
        }
        return false;
    }

    /* Returns the value of the specified attribute, null if attribute value not found */
    public String getValueWithParent(String attributeName, String parentName) {
        for (AttributeValue value : values) {
            if (value.getName().equalsIgnoreCase(attributeName) && value.getParentRelationName().equalsIgnoreCase(parentName)) {
                return value.getValue();
            }
        }
        return null;
    }

    public LinkedList<AttributeValue> getAVList() {
        return values;
    }

    /* returns amount of values in tuple */
    public int getValueCount() {
        return this.values.size();
    }

    /* prints tuple formatted with attribute length limits + borders */
    public void print(int[] attyLengths, int PRINT_PADDING, LinkedList<Attribute> schema) {
        int i = 0;
        for (Attribute atty : schema) {
            String value = getValueWithParent(atty.getName(), atty.getParentRelationName());
            System.out.format("%-" + (attyLengths[i] + PRINT_PADDING) + "s", "| " + value);
            i++;
        }
        System.out.println("|");
    }

    /* prints tuple unformatted */
    public void print() {
        for (AttributeValue av : values) {
            // format column width based on length of attibute
            System.out.format("%s, ", av.getValue());
        }
        System.out.println();
    }

    public Tuple makeDeepCopy() {
        Tuple copy = new Tuple();
        for (AttributeValue value : values) {
            AttributeValue valueCopy = value.makeDeepCopy();
            copy.addAttributeValue(valueCopy);
        }
        return copy;
    }

    /* truncates attribute value value on either right or left side to length length
     * char argument side is either 'r' or 'l' and is side to cut off
     */
    public void truncateAttributeValue(String attributeName, String attributeParent, int length, char side) {
        for (AttributeValue value : values) {
            if (value.getName().equalsIgnoreCase(attributeName) && value.getParentRelationName().equals(attributeParent)) {
                if (value.getValue().length() > length) {
                    int START_INDEX = 0;
                    int END_INDEX = 0;
                    if (side == 'r') {
                        START_INDEX = 0;
                        END_INDEX = length;
                    } else if (side == 'l') {
                        START_INDEX = value.getValue().length() -length - 1;
                        END_INDEX = value.getValue().length();
                    } else {
                        System.err.println("ERROR: truncateAttributeValue could not work because side is not recognized.");
                        return;
                    }
                    value.setValue(value.getValue().substring(START_INDEX, END_INDEX));
                }
                return;
            }
        }
        System.err.println("ERROR: truncateAttributeValue did not find an attribute matching parameters given to truncate.");
    }
}
