package com.tntrip.understand.putogether;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by libing2 on 2015/12/13.
 */
public class Evolution {
    // Sort the people list by last name
    public static void sortEvolution(List<Person> people) {
        // 0. Today, verbose
        Collections.sort(people, new Comparator<Person>() {
            @Override
            public int compare(Person x, Person y) {
                return x.getLastName().compareTo(y.getLastName());
            }
        });

        // 1. lambda expression
        // more concise, but not any more abstract. Programmer does actual comparison
        Collections.sort(people,
                (Person x, Person y) -> x.getLastName().compareTo(y.getLastName()));

        // 2. use existing method
        Collections.sort(people, Comparator.comparing((Person p) -> p.getLastName()));
        // above lambda is just a forwarder for existing p.getLastName()
        // reuse the existing method in place of the lambda expression. By method reference
        Collections.sort(people, Comparator.comparing(Person::getLastName));

        // 3. undermine the value（价值）of List interface.
        // User cannot see sort by only inspecting List interface
        // This reads much more like to the problem statement: "sort the people list by last name"
        people.sort(Comparator.comparing(Person::getLastName));

    }
}
