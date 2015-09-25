package com.floydjohn.pizzaplanet.data.prodotti;

import com.floydjohn.pizzaplanet.data.persone.Cliente;
import com.floydjohn.pizzaplanet.data.time.Istante;
import com.floydjohn.pizzaplanet.data.time.Tempo;

import java.util.List;

public class Ordine {
    private List<Prodotto> prodotti;
    private Istante ora;
    private Cliente cliente;

    public Ordine(Cliente cliente, List<Prodotto> prodotti, Istante ora) {
        this.cliente = cliente;
        this.prodotti = prodotti;
        this.ora = ora;
    }

    public List<Prodotto> getProdotti() {
        return prodotti;
    }

    public Istante getOra() {
        return ora;
    }

    public boolean isPassato() {
        return Tempo.passato(ora);
    }

    @Override
    public String toString() {
        String out = "{" + ora.toString() + "; ";
        for (Prodotto prodotto : prodotti) out += prodotto.toString() + ", ";
        return out + "}";
    }

    public Cliente getCliente() {
        return cliente;
    }
}
