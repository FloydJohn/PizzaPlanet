package com.floydjohn.pizzaplanet.data.persone;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.floydjohn.pizzaplanet.GUI.Persona;
import com.floydjohn.pizzaplanet.GUI.Renderer;
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

public class Cliente extends Persona implements Telegraph {

    private Ordine ordine;
    private String nome;
    private Stato statoCorrente = Stato.Fuori;
    private boolean mioTurno = false;

    private int giorniPerOrdinazione;
    private int postoInCoda;

    public Cliente(int giorniPerOrdinazione) {
        super(Tipo.Cliente);
        this.giorniPerOrdinazione = giorniPerOrdinazione;
        this.nome = FileParser.getNameCliente();
        genera();
    }

    @Override
    public void update() {
        super.updatePosition();

        switch (statoCorrente) {
            case Fuori:
                if (ordine.isPassato()) {
                    statoCorrente = Stato.Cammina;
                    //System.out.println("[" + nome + "] Entro e mando ClienteInPizzeria!");
                    MessageManager.getInstance().dispatchMessage(this, Messaggi.ClienteInPizzeria);
                }
                break;
            case Cammina:
                if (!super.isMoving()) {
                    statoCorrente = Stato.Coda;
                }
                break;
            case Coda:
                if (mioTurno) {
                    //System.out.println("["+nome+"] Mio turno! Ordino");
                    statoCorrente = Stato.Ordina;
                    MessageManager.getInstance().dispatchMessage(this, Messaggi.Ordine, ordine);
                    mioTurno = false;
                }
                break;
            case Ordina:
                //Wait for dipendente
                break;
            case Esci:
                //System.out.println("["+nome+"] Fine, esco!");
                genera();
                super.setNext(new Vector2());
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
        //System.out.println("["+nome+"] Nuovo evento ->" + ordine.getOra());
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if (statoCorrente == Stato.Coda || statoCorrente == Stato.Cammina) {
            switch (msg.message) {
                case Messaggi.PostoInCoda:
                    postoInCoda = (int) msg.extraInfo;
                    //System.out.println("["+nome+"] Posto in coda: "+postoInCoda);
                    setNext(Renderer.coordinateCoda(postoInCoda));
                    if (postoInCoda == 0) mioTurno = true;
                    break;
            }
        }
        if (statoCorrente == Stato.Ordina && msg.message == Messaggi.OrdineCompletato) statoCorrente = Stato.Esci;
        return true;
    }

    public enum Stato {
        Fuori, Cammina, Coda, Ordina, Esci
    }
}
