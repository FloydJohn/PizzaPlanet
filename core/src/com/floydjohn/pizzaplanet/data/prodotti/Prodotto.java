package com.floydjohn.pizzaplanet.data.prodotti;

import java.util.ArrayList;
import java.util.List;

public class Prodotto {

    String nome;
    List<Ingrediente> ingredienti = new ArrayList<>();
    float costo;

    public Prodotto(String nome, List<Ingrediente> ingredienti, float costo) {
        this.nome = nome;
        this.ingredienti.addAll(ingredienti);
        this.costo = costo;
    }

    @Override
    public String toString() {
        String out = nome + ": [";
        for (Ingrediente ingrediente : ingredienti) out += ingrediente.getNome() + ",";
        return out + "], costo: " + costo;
    }
}