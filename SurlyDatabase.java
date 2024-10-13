/*  SURLY 1
    Natalie Norris, Robin Preble, Indie Cowan
    1/21/23
*/

import java.util.LinkedList;

public class SurlyDatabase {
	/* Collection of relations in the database */
	private LinkedList<ARelation> relations;
	private Catalog catalog;
	

	/* constructor initializes relations linked list */
	public SurlyDatabase() {
		relations = new LinkedList<ARelation>();
		catalog = new Catalog();
		relations.add(catalog);
	}

	/* returns amount of relations in the database including temp tables*/
	public int getRelationCount() {
		return relations.size();
	}

	/* Returns the relation with the specified name */
	public ARelation getRelation(String name) {
        // System.out.printf("getRelation input: -%s-%n", name);
		int i = 0;
		for (; i < relations.size(); i++) {
			if (relations.get(i).getName().equalsIgnoreCase(name)) {
				return relations.get(i);
			}
		}
		return null;
	}

	/* Removes the relation with the specified name from the database */
    public void destroyRelation(String name) {
		ARelation toRemove = getRelation(name);
		catalog.removeTable(toRemove.getName());
		relations.remove(toRemove);
    }

	/* Creates a relation to contain the schemas of all other tables 
	 * has potential to be overloaded for custom attribute lengths
	*/

	/* Adds the given relation to the database */
	public void addRelation(ARelation relation) {
        if (relation.getName() == SurlyConstants.CATALOG_NAME) {
            System.err.println("ERROR: You may not create a table named  " + SurlyConstants.CATALOG_NAME + ".");
        }

        ARelation existingRel = getRelation(relation.getName());
        // IGNORE COMMAND if base relation w that name already exists or destroy temp
        if (existingRel != null) {
            if (existingRel.getType() == SurlyConstants.relation_indetifier.TEMP) {
                destroyRelation(existingRel.getName());
            } else {
                System.err.println("ERROR: Table " + relation.getName() + " cannot be added because a table with that name already exists.");
                return;
            }
		}
        // relation doesn't exist now if it did
        if (relation.getType() == SurlyConstants.relation_indetifier.BASE) {
            catalog.addTableSchema(relation);
        }
        relations.add(relation);
	}
}
