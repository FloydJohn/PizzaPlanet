package com.floydjohn.pizzaplanet.data.posti;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.floydjohn.pizzaplanet.data.persone.Cliente;
import com.floydjohn.pizzaplanet.data.persone.Dipendente;
import com.floydjohn.pizzaplanet.data.persone.DipendenteVuoto;
import com.floydjohn.pizzaplanet.data.prodotti.Magazzino;
import com.floydjohn.pizzaplanet.data.prodotti.Ordine;
import com.floydjohn.pizzaplanet.exceptions.PostoOccupatoException;
import com.floydjohn.pizzaplanet.utils.Logger;
import com.floydjohn.pizzaplanet.utils.Messaggi;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;

//TODO Coda in pizzeria
//TODO Barra del tempo

public class Pizzeria implements Telegraph {
    Magazzino magazzino;
    EnumMap<Posto, Dipendente> posti = new EnumMap<>(Posto.class);
    private List<Ordine> ordini = new ArrayList<>();
    private List<Cliente> inAttesa = new ArrayList<>();
    private Ordine ordineInCorso;

    public Pizzeria() {
        for (Posto posto : Posto.values()) posti.put(posto, new DipendenteVuoto());
        MessageManager.getInstance().addListeners(this, Messaggi.ClienteInAttesa, Messaggi.Ordine);
    }

    public void assumi(Posto posto, Dipendente dipendente) throws PostoOccupatoException {
        if (isOccupato(posto)) throw new PostoOccupatoException();
        posti.put(posto, dipendente);
        MessageManager.getInstance().dispatchMessage(this, dipendente, Messaggi.AssumiDipendente, posto);
    }

    public void inverti(Posto posto1, Posto posto2) {
        Logger.debug("Invertendo " + posto1.name() + " con " + posto2.name());
        Dipendente dipendente1 = getDipendente(posto1);
        Dipendente dipendente2 = getDipendente(posto2);
        posti.put(posto1, dipendente2);
        posti.put(posto2, dipendente1);
        MessageManager.getInstance().dispatchMessage(this, dipendente1, Messaggi.MuoviDipendente, posto2);
        MessageManager.getInstance().dispatchMessage(this, dipendente2, Messaggi.MuoviDipendente, posto1);
    }

    public boolean isOccupato(Posto posto) {
        return !(posti.get(posto) == null || posti.get(posto) instanceof DipendenteVuoto);
    }

    public Dipendente getDipendente(Posto posto) {
        if (isOccupato(posto)) return posti.get(posto);
        else return null;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if (msg.message == Messaggi.ClienteInAttesa) {
            System.out.println("[PIZZERIA] Ohibò, c'è un cliente! Vedo se qualcuno è disponibile in cassa...");
            inAttesa.add((Cliente) msg.extraInfo);
        }
        if (msg.message == Messaggi.Ordine) {
            System.out.println("[PIZZERIA] Un cliente ha inviato l'ordine! Faccio partire il timer...");
            ordineInCorso = (Ordine) msg.extraInfo;
            getDipendente(Posto.Cassa).iniziaLavoro(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("[TASK] Task completato!");
                    MessageManager.getInstance().dispatchMessage(Pizzeria.this, Messaggi.OrdineCompletato);
                    ordini.add(ordineInCorso);
                    ordineInCorso = null;
                }
            });
        }
        return false;
    }

    public List<Dipendente> getDipendenti() {
        return posti.values().stream().filter(dipendente -> dipendente != null && !(dipendente instanceof DipendenteVuoto)).collect(Collectors.toList());
    }

    public void update() {
        if (inAttesa.size() > 0 && isOccupato(Posto.Cassa)) {
            System.out.println("[PIZZERIA] Cassa disponibile! Inizio trattazione con cliente...");
            MessageManager.getInstance().dispatchMessage(this, inAttesa.get(0), Messaggi.ProssimoCliente);
            inAttesa.remove(0);
        }
    }
}
