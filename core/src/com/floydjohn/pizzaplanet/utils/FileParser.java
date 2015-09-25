package com.floydjohn.pizzaplanet.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class FileParser {
    public static String getName() {
        try {
            String result = null;
            Random rand = new Random();
            int n = 0;
            for (Scanner sc = new Scanner(new File("core/assets/dipendenti.txt")); sc.hasNext(); ) {
                ++n;
                String line = sc.nextLine();
                if (rand.nextInt(n) == 0)
                    result = line;
            }

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
            for (Scanner sc = new Scanner(new File("core/assets/clienti.txt")); sc.hasNext(); ) {
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
