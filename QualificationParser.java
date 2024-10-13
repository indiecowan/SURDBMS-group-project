import java.util.LinkedList;

public class QualificationParser {
    private LinkedList<ConditionParser[]> andGroups = new LinkedList<ConditionParser[]>();

    /* constructor */
    public QualificationParser(String qualification) {
        String[] splitByOr = qualification.split(" OR ");
        for (String a : splitByOr) {
            LinkedList<ConditionParser> andGroup = new LinkedList<ConditionParser>();
            String[] splitByAnd = a.split(" AND ");

            for (String b : splitByAnd) {
                ConditionParser cond = new ConditionParser(b);
                andGroup.add(cond);
            }
            andGroups.add(andGroup.toArray(new ConditionParser[0]));
        }
    }

    /* Returns true if tuple meets all of the conditions in at least 1 andGroup */
    public boolean testTuple(Tuple tuple) throws Exception{
        for (ConditionParser[] andGroup : andGroups) {
            if (meetsAndGroupConditions(tuple, andGroup)) {
                return true;
            }
        }
        return false;
    }

    /* Returns true if tuple meets all of the conditions in andGroup */
    private boolean meetsAndGroupConditions(Tuple tuple, ConditionParser[] andGroup) throws Exception {
        for (ConditionParser cond : andGroup) {
            if (!cond.testTuple(tuple)) {
                return false;
            }
        }
        return true;
    }
}
