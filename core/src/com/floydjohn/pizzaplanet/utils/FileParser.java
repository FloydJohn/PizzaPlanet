package com.floydjohn.pizzaplanet.utils;

import com.badlogic.gdx.Gdx;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

//TODO Unique name

public class FileParser {

    private static List<String> alreadyUsed = new LinkedList<>();

    public static String getName() {
        try {
            String result = null;
            Random rand = new Random();
            int n = 0;
            for (Scanner sc = new Scanner((Gdx.files.internal("core/assets/dipendenti.txt").file())); sc.hasNext(); ) {
                ++n;
                String line = sc.nextLine();
                boolean used = false;
                for (String s : alreadyUsed) if (line.equals(s)) used = true;
                if (rand.nextInt(n) == 0 && !used)
                    result = line;
            }
            alreadyUsed.add(result);
            return result;
        } catch (FileNotFoundException e) {
            System.out.println("Could not read dipendenti.txt");
            return "Default";
        }
    }

    public static String getNameCliente() {
        try {
            String result = null;
            Random rand = new Random();
            int n = 0;
            for (Scanner sc = new Scanner(Gdx.files.internal("core/assets/clienti.txt").file()); sc.hasNext(); ) {
                ++n;
                String line = sc.nextLine();
                if (rand.nextInt(n) == 0)
                    result = line;
            }

            return result;
        } catch (FileNotFoundException e) {
            System.out.println("Could not read clienti.txt");
            return "Default";
        }
    }
}
