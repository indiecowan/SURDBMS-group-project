
public class AttributeValue {
    private String parentRelationName;
    private String name; 	/* name of the attribute */
	private String value;   /* value of the attribute */

	public AttributeValue(String name, String value, String parentRelationName) {
		this.name = name;
		this.value = value;
        this.parentRelationName = parentRelationName;
	}

	public String getName(){
		return name;
	}

    public String getQualifiedName(){
		return parentRelationName + "." + name;
	}

	public String getValue(){
		return value;
	}
    
    public String getParentRelationName() {
        return parentRelationName;
    }

	public boolean setName(String name){
		this.name = name;
		return true;
	}

	public boolean setValue(String value){
		this.value = value;
		return true;
	}

    public AttributeValue makeDeepCopy() {
        AttributeValue copy = new AttributeValue(name.substring(0), value.substring(0), parentRelationName);
        return copy;
    }
}
