package com.floydjohn.pizzaplanet.data.persone;

import com.floydjohn.pizzaplanet.data.posti.Posto;

import java.util.EnumMap;
import java.util.Random;

public class Attributi {
    EnumMap<Posto, Integer> attributi = new EnumMap<>(Posto.class);
    int MAX = 10;

    public Attributi() {
        Random random = new Random();
        for (Posto posto : Posto.values()) attributi.put(posto, random.nextInt(MAX) + 1);
    }

    public int get(Posto posto) {
        return attributi.get(posto);
    }
}
