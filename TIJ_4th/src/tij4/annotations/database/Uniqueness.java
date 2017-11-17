//: annotations/database/Uniqueness.java
// Sample of nested annotations
package tij4.annotations.database;

public @interface Uniqueness {
	Constraints constraints() default @Constraints(unique = true);
} // /:~
