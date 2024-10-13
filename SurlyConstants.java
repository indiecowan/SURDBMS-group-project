public class SurlyConstants {
    public static final String CATALOG_NAME = "CATALOG";
    public static final String CATALOG_COLUMN1 = "RELATION";
	public static final String CATALOG_COLUMN2 = "ATTRIBUTES";
    public static final String FIRST_DATATYPE = "CHAR"; // 1st col datatype
    public static final int FIRST_LENGTH = 25; // length of relation attribute (table names)
    public static final String SECOND_DATATYPE = "NUM"; // 2nd col datatype
    public static final int SECOND_LENGTH = 10; // length of attributes attribute (table lengths)

    public static final String CHAR_TYPE_STRING = "char";
    public static final String NUM_TYPE_STRING = "num";

    public static enum relation_indetifier {
        CATALOG,
        BASE,
        TEMP
    }
}
