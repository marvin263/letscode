package com.tntrip.interview;

import java.util.ArrayList;

/**
 * Created by libing2 on 2016/10/11.
 */
public class PrintPet {
    public static void printPets() {
        ArrayList<String> listPets = new ArrayList<>();
        listPets.add("dog");
        listPets.add("cat");

        favorNewPets(listPets);

        System.out.println(listPets);
    }

    private static void favorNewPets(ArrayList<String> listPets) {
        ArrayList<String> listNew = new ArrayList<>();
        listNew.add("rabbit");
        listNew.add("hamster");

        listPets = listNew;
    }

    public static void main(String[] args) {
        printPets();
    }

}
