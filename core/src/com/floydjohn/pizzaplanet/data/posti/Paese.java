package com.floydjohn.pizzaplanet.data.posti;

import com.floydjohn.pizzaplanet.data.persone.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Paese {
    List<Cliente> clienti = new ArrayList<>();
    Pizzeria pizzeria;

    public Paese() {
        pizzeria = new Pizzeria();
        for (int i = 0; i < 2; i++) clienti.add(new Cliente(new Random().nextInt(4) + 1));
    }

    public Pizzeria getPizzeria() {
        return pizzeria;
    }

    public void update() {
        pizzeria.update();
        for (Cliente cliente : clienti) cliente.update();
    }

    public List<Cliente> getClienti() {
        return clienti;
    }
}
