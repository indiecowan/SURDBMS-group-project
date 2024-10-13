# SURLY: A Single User Relational Database Management System
Surly is a Single User Relational Database Management System, complete with it's own query language based on SQL. This project contains contributions from Robin Preble and Natalie Morris, but they are not shown on Github because this is a copy of the work to be shared publicly.

## Workflow


## Query Language
### RELATION
- Syntax: RELATION relationname (<attribute format>);
- Creates a new relation in the SURLY database, and adds its schema to the CATALOG. 
- Example: RELATION COURSE (CNUMCHAR8,TITLECHAR30,CREDITSNUM 4);
### INSERT
- Syntax: INSERT relationname <tuple>;
- Adds a new tuple to relationname. 
- Example: INSERT COURSE CSCI241 'DATASTRUCTURES' 4;
### PRINT
- Syntax: PRINT <relexpr>;
- Formats and prints each of the named relations in tabular form. 
- Example: PRINT COURSE, OFFERING, CATALOG;
### DESTROY
- Syntax: DESTROY relationname;
- Removes the specified relation from the SURLY database, and removes its schema from the CATALOG.
- Example: DESTROY COURSE;
### DELETE
- Syntax: DELETE relationname;
- Deletes all tuples from the specified relation. 
- Example: DELETE COURSE;
### DELETE WHERE
- Deletes any tuples from a relation that meet the specified conditions. The where clause should handle multiple conditions, with AND having higher precedence than OR. If no where clause is included, all tuples are deleted from the relation. Operators include: =, !=, <, >, <=, >=
- Syntax: DELETE relationname WHERE <conditions>;
- Examples:
    - DELETE OFFERING WHERE CNUM = CSCI241 and SECTION > 27922;
    - DELETE PREREQ WHERE CNUM = CSCI241 or CNUM = CSCI145 and PNUM != CSCI141;
### SELECT WHERE
- Returns all tuples in a relation that meet the specified conditions in a new, temporary relation. The where clause should handle multiple conditions, with AND having higher precedence than OR. If no where clause is included, all tuples are returned. Operators include: =, !=, <, >, <=, >=
- Syntax: temprelationname = SELECT relationname WHERE <conditions>;
- Examples:
    - C = SELECT COURSE;
    - T1 = SELECT OFFERING WHERE CNUM = CSCI241 and SECTION > 27922;
    - DB_S = SELECT COURSE WHERE TITLE = ‘DATABASE SYSTEMS’ or CNUM != CSCI301 and CREDITS > 3;
### PROJECT
- Returns the specified attributes of a relation in a new, temporary relation. As with relational algebra, duplicate tuples are eliminated.
- Syntax: temprelationname = PROJECT <attribute names> FROM relationname;
- Example:  P = PROJECT CREDITS, CNUM FROM COURSE;
### JOIN
- Inner joins two relations based on the specified join condition and returns the result as a new, temporary relation. The resulting relation should contain all attributes from both relations.
- Syntax: temprelationname = JOIN relationAname, relationBname ON <join condition>;
- Example: J = JOIN COURSE, PREREQ ON COURSE.CNUM = PNUM;


## Other Info
### Heap Storage
- Data is organized using a Linked-List storage structure, where the SURLY database is a linked-list of relations, each relation is a linked-list of tuples, and each tuple is a linked-list of attribute values.
### CATALOG
- A special relation named CATALOG that is automatically created when the program runs and stores the schema of every relation in the database (see the example output). As a relation, it can be printed, but cannot be destroyed, inserted to, or deleted from by user input.
### Temporary Relations
- Results of operations like SELECT, PROJECT, and JOIN produce new, temporary relations. These relations can be printed and can be used as operands for further query operations. Since they are not base relations, however, INSERT, DESTROY, and DELETE operations should not function on temporary relations. Note that SELECT, PROJECT, and JOIN always output to a temporary relation.
- If a query operation names its output the same as a temporary relation that already exists, the existing temp relation should be overwritten. However, a temp relation can NOT overwrite an existing base relation.
### Qualify Attributes
- Because JOIN introduces the possibility of having multiple attributes with the same name in the same relation, you will want to be able to qualify attributes in your query commands.
- Example: T1 = JOIN COURSE, PREREQ ON COURSE.CNUM = PREREQ.CNUM;