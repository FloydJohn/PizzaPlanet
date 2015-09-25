package com.floydjohn.pizzaplanet.data.persone;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.floydjohn.pizzaplanet.GUI.Disegnabile;
import com.floydjohn.pizzaplanet.GUI.Renderer;
import com.floydjohn.pizzaplanet.data.posti.Posto;
import com.floydjohn.pizzaplanet.data.prodotti.Database;
import com.floydjohn.pizzaplanet.data.prodotti.Ordine;
import com.floydjohn.pizzaplanet.data.prodotti.Prodotto;
import com.floydjohn.pizzaplanet.data.time.Istante;
import com.floydjohn.pizzaplanet.data.time.Tempo;
import com.floydjohn.pizzaplanet.utils.FileParser;
import com.floydjohn.pizzaplanet.utils.Messaggi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cliente extends Disegnabile implements Telegraph {

    private Ordine ordine;
    private String nome;
    private Stato statoCorrente = Stato.Fuori;
    private boolean mioTurno = false;

    private int giorniPerOrdinazione;

    public Cliente(int giorniPerOrdinazione) {
        super(Tipo.Cliente);
        MessageManager.getInstance().addListeners(this, Messaggi.OrdineCompletato, Messaggi.ProssimoCliente);
        this.giorniPerOrdinazione = giorniPerOrdinazione;
        this.nome = FileParser.getNameCliente();
        genera();
    }

    @Override
    public void update() {
        switch (statoCorrente) {
            case Fuori:
                if (ordine.isPassato()) statoCorrente = Stato.Cammina;
                break;
            case Cammina:
                System.out.println("[CLIENTE] Sono " + nome + ", cammino in pizzeria...");
                posizioneReale = new Vector2(100, 100);
                statoCorrente = Stato.Coda;
                MessageManager.getInstance().dispatchMessage(this, Messaggi.ClienteInAttesa, this);
                System.out.println("[CLIENTE] Mandato cliente in attesa ;)");
                break;
            case Coda:
                if (mioTurno) {
                    System.out.println("[CLIENTE] E' il mio turno! Invio l'ordine.");
                    statoCorrente = Stato.Ordina;
                    MessageManager.getInstance().dispatchMessage(this, Messaggi.Ordine, ordine);
                    mioTurno = false;
                }
                break;
            case Ordina:
                //Wait for dipendente
                break;
            case Esci:
                System.out.println("[CLIENTE] Ordine completato, yayy! Me ne torno a casa!");
                genera();
                posizioneReale = Renderer.coordinateRealiDi(Posto.Fuori);
                statoCorrente = Stato.Fuori;
                break;
        }
    }

    private void genera() {
        Random generator = new Random();
        int day = Tempo.get().getDay() + giorniPerOrdinazione + (generator.nextInt(giorniPerOrdinazione) - giorniPerOrdinazione / 2);
        int hrs = (int) (20 + generator.nextGaussian());
        int min = generator.nextInt(60);
        Istante ora = new Istante(day, hrs, min);
        List<Prodotto> prodotti = new ArrayList<>();
        for (int i = 0; i < generator.nextInt(5) + 1; i++)
            prodotti.add(Database.pizze[generator.nextInt(Database.pizze.length)]);
        ordine = new Ordine(this, prodotti, ora);
        System.out.println("[CLIENTE] Genero un nuovo ordine! Passerò -> " + ordine.getOra());
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if (statoCorrente == Stato.Coda && msg.message == Messaggi.ProssimoCliente) mioTurno = true;
        if (statoCorrente == Stato.Ordina && msg.message == Messaggi.OrdineCompletato) statoCorrente = Stato.Esci;
        return true;
    }

    public enum Stato {
        Fuori, Cammina, Coda, Ordina, Esci
    }
}
