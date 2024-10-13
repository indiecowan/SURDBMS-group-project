/*  SURLY 1
    Natalie Norris, Robin Preble, Indie Cowan
    1/21/23
*/
import java.lang.Exception;

public class BaseRelation extends ARelation{
	/* initiaizes all fields 
	 * has potential for overloading to make custom catalog data
	*/
	public BaseRelation(String name) throws Exception {
        super(SurlyConstants.relation_indetifier.BASE);
        this.name = name;
		if (name.equals(SurlyConstants.CATALOG_NAME)) {
            Exception e = new Exception("ERROR: You are creating a relation with the name of 'CATALOG'. This is forbidden.");
			throw e;
		}
	}
}
