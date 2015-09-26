package com.floydjohn.pizzaplanet.data.posti;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.floydjohn.pizzaplanet.data.persone.Cliente;
import com.floydjohn.pizzaplanet.data.persone.Dipendente;
import com.floydjohn.pizzaplanet.data.prodotti.Magazzino;
import com.floydjohn.pizzaplanet.data.prodotti.Ordine;
import com.floydjohn.pizzaplanet.exceptions.PostoOccupatoException;
import com.floydjohn.pizzaplanet.utils.Logger;
import com.floydjohn.pizzaplanet.utils.Messaggi;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

//TODO Coda in pizzeria

public class Pizzeria implements Telegraph {

    private Magazzino magazzino;
    private BiMap<Dipendente, Posto> dipendenti = HashBiMap.create();
    private List<Dipendente> moving = new ArrayList<>();
    private List<Ordine> ordini = new ArrayList<>();
    private List<Cliente> inAttesa = new ArrayList<>();
    private Ordine ordineInCorso;

    private List<Dipendente> bufferDipendenti = new ArrayList<>();

    public Pizzeria() {
        MessageManager.getInstance().addListeners(this, Messaggi.ClienteInAttesa, Messaggi.Ordine, Messaggi.DipendenteArrivato);
    }

    public void assumi(Posto posto, Dipendente dipendente) throws PostoOccupatoException {
        if (isOccupato(posto)) throw new PostoOccupatoException();
        MessageManager.getInstance().dispatchMessage(this, dipendente, Messaggi.AssumiDipendente, posto);
        moving.add(dipendente);
    }

    public void inverti(Posto posto1, Posto posto2) {
        Logger.debug("Invertendo " + posto1.name() + " con " + posto2.name());
        Dipendente d1 = getDipendente(posto1);
        Dipendente d2 = getDipendente(posto2);

        if (d1 != null && d2 != null) {
            Logger.debug("Muovo entrambi");
            moving.add(dipendenti.inverse().remove(posto1));
            moving.add(dipendenti.inverse().remove(posto2));
            MessageManager.getInstance().dispatchMessage(this, d1, Messaggi.MuoviDipendente, posto2);
            MessageManager.getInstance().dispatchMessage(this, d2, Messaggi.MuoviDipendente, posto1);
        } else if (d1 == null && d2 != null) {
            Logger.debug("Muovo d2");
            moving.add(dipendenti.inverse().remove(posto2));
            MessageManager.getInstance().dispatchMessage(this, d2, Messaggi.MuoviDipendente, posto1);
        } else if (d1 != null) {
            Logger.debug("Muovo d1");
            moving.add(dipendenti.inverse().remove(posto1));
            MessageManager.getInstance().dispatchMessage(this, d1, Messaggi.MuoviDipendente, posto2);
        } else Logger.debug("Non muovo nessuno");
    }

    public boolean isOccupato(Posto posto) {
        return dipendenti.containsValue(posto);
    }

    public Dipendente getDipendente(Posto posto) {
        if (isOccupato(posto)) return dipendenti.inverse().get(posto);
        else return null;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if (msg.message == Messaggi.DipendenteArrivato) {
            dipendenti.put((Dipendente) msg.sender, (Posto) msg.extraInfo);
            if (moving.contains(msg.sender)) moving.remove(msg.sender);
            System.out.println("[PIZZERIA] Swap completato!");
        }
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
        bufferDipendenti.clear();
        bufferDipendenti.addAll(moving);
        bufferDipendenti.addAll(dipendenti.keySet());
        return bufferDipendenti;
    }

    public void update() {
        if (inAttesa.size() > 0 && isOccupato(Posto.Cassa)) {
            System.out.println("[PIZZERIA] Cassa disponibile! Inizio trattazione con cliente...");
            MessageManager.getInstance().dispatchMessage(this, inAttesa.get(0), Messaggi.ProssimoCliente);
            inAttesa.remove(0);
        }
    }
}
