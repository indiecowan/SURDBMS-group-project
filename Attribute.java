

public class Attribute {
    private String parentRelationName;
	private String name;	/* name of the attribute */
	private String dataType;	/* data type of the attribute */
	private int length;		/* length of the attribute */

	/* initializes fields */
	public Attribute(String name, String dataType, int length, String parentRelationName) {
		this.name = name;
		this.dataType = dataType;
		this.length = length;
        this.parentRelationName = parentRelationName;
	}

	public String getName(){
		return name;
	}

    public String getQualifiedName(){
		return parentRelationName + "." + name;
	}

	public String getDataType(){
		return dataType;
	}

	public int getLength(){
		return length;
	}

    public String getParentRelationName() {
        return parentRelationName;
    }

	public void setName(String name){
		this.name = name;
	}

	public void setDataType(String dataType){
		this.dataType = dataType;
	}

	public boolean setLength(int length){
		this.length = length;
		return true;
	}

    public Attribute makeDeepCopy() {
        Attribute copy = new Attribute(name.substring(0), dataType.substring(0), length, parentRelationName);
        return copy;
    }
}
