package com.floydjohn.pizzaplanet.data.prodotti;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

public class Database {

    public static Prodotto[] pizze;
    public static Ingrediente[] ingredienti;

    public static void populate() {
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(Gdx.files.internal("core/assets/db.json"));
        JsonValue jingredienti = root.get("Ingredienti");
        ingredienti = new Ingrediente[jingredienti.asStringArray().length];
        for (int i = 0; i < ingredienti.length; i++) {
            ingredienti[i] = new Ingrediente(jingredienti.asStringArray()[i]);
            //System.out.println(i+": "+ingredienti[i].getNome());
        }
        JsonValue jpizze = root.get("Pizze");
        ArrayList<Prodotto> pizzeList = new ArrayList<>();
        for (JsonValue jpizza : jpizze) {
            String nome = jpizza.get("Nome").asString();
            int[] ingredientiIds = jpizza.get("ingredienti").asIntArray();
            ArrayList<Ingrediente> ingredientiList = new ArrayList<>();
            for (int i : ingredientiIds) ingredientiList.add(ingredienti[i]);
            pizzeList.add(new Prodotto(nome, ingredientiList, 5));
            //System.out.println("Aggiunta pizza: " + pizzeList.get(pizzeList.size()-1).toString());
        }
        pizze = pizzeList.toArray(new Prodotto[pizzeList.size()]);
    }
}
