package com.floydjohn.pizzaplanet.data.posti;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.floydjohn.pizzaplanet.data.persone.Cliente;
import com.floydjohn.pizzaplanet.data.persone.Dipendente;
import com.floydjohn.pizzaplanet.data.prodotti.Magazzino;
import com.floydjohn.pizzaplanet.data.prodotti.Ordine;
import com.floydjohn.pizzaplanet.exceptions.PostoOccupatoException;
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
    private List<Cliente> coda = new ArrayList<>();
    private Cliente inAvvicinamento = null;
    private Ordine ordineInCorso;

    private List<Dipendente> bufferDipendenti = new ArrayList<>();

    public Pizzeria() {
        MessageManager.getInstance().addListeners(this, Messaggi.ClienteInPizzeria, Messaggi.Ordine, Messaggi.DipendenteArrivato);
    }

    public void assumi(Posto posto, Dipendente dipendente) throws PostoOccupatoException {
        if (isOccupato(posto)) throw new PostoOccupatoException();
        MessageManager.getInstance().dispatchMessage(this, dipendente, Messaggi.AssumiDipendente, posto);
        moving.add(dipendente);
    }

    public void inverti(Posto posto1, Posto posto2) {
        Dipendente d1 = getDipendente(posto1);
        Dipendente d2 = getDipendente(posto2);

        if (d1 != null && d2 != null) {
            moving.add(dipendenti.inverse().remove(posto1));
            moving.add(dipendenti.inverse().remove(posto2));
            MessageManager.getInstance().dispatchMessage(this, d1, Messaggi.MuoviDipendente, posto2);
            MessageManager.getInstance().dispatchMessage(this, d2, Messaggi.MuoviDipendente, posto1);
        } else if (d1 == null && d2 != null) {
            moving.add(dipendenti.inverse().remove(posto2));
            MessageManager.getInstance().dispatchMessage(this, d2, Messaggi.MuoviDipendente, posto1);
        } else if (d1 != null) {
            moving.add(dipendenti.inverse().remove(posto1));
            MessageManager.getInstance().dispatchMessage(this, d1, Messaggi.MuoviDipendente, posto2);
        }
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
        }
        if (msg.message == Messaggi.ClienteInPizzeria) {
            coda.add((Cliente) msg.sender);
            MessageManager.getInstance().dispatchMessage(this, msg.sender, Messaggi.PostoInCoda, coda.size());
        }
        if (msg.message == Messaggi.Ordine) {
            System.out.println("[PIZZERIA] Un cliente ha inviato l'ordine! Faccio partire il timer...");
            ordineInCorso = (Ordine) msg.extraInfo;
            Cliente cliente = (Cliente) msg.sender;
            getDipendente(Posto.Cassa).iniziaLavoro(new TimerTask() {
                @Override
                public void run() {
                    MessageManager.getInstance().dispatchMessage(Pizzeria.this, cliente, Messaggi.OrdineCompletato);
                    ordini.add(ordineInCorso);
                    ordineInCorso = null;
                    scorri(true);
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

    public boolean isWorking(Posto posto) {
        Dipendente d = getDipendente(posto);
        return d != null && !d.inPausa();
    }

    public void update() {
        getDipendenti().forEach(com.floydjohn.pizzaplanet.data.persone.Dipendente::update);
        if (coda.size() > 0 && getDipendente(Posto.Cassa) != null && !isWorking(Posto.Cassa)) {
            //System.out.println("[PIZZERIA] Cassa disponibile! Scorro!");
            scorri(false);
        }
    }

    public void scorri(boolean removeFirst) {
        if (coda.size() >= 1) {
            if (removeFirst) coda.remove(0);
            for (int i = 0; i < coda.size(); i++)
                MessageManager.getInstance().dispatchMessage(this, coda.get(i), Messaggi.PostoInCoda, i);
        }
    }
}
