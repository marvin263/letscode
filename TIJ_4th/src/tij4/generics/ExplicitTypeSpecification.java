package tij4.generics;
//: generics/ExplicitTypeSpecification.java
import tij4.typeinfo.pets.*;
import java.util.*;

import net.mindview.util.*;

public class ExplicitTypeSpecification {
  static void f(Map<Person, List<Pet>> petPeople) {}
  public static void main(String[] args) {
    f(New.<Person, List<Pet>>map());
  }
} ///:~
