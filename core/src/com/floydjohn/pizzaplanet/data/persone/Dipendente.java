package com.floydjohn.pizzaplanet.data.persone;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.floydjohn.pizzaplanet.GUI.Disegnabile;
import com.floydjohn.pizzaplanet.GUI.ProgressBar;
import com.floydjohn.pizzaplanet.GUI.Renderer;
import com.floydjohn.pizzaplanet.data.posti.Posto;
import com.floydjohn.pizzaplanet.data.time.Timer;
import com.floydjohn.pizzaplanet.utils.FileParser;
import com.floydjohn.pizzaplanet.utils.Logger;

import java.util.TimerTask;

import static com.floydjohn.pizzaplanet.utils.Messaggi.AssumiDipendente;
import static com.floydjohn.pizzaplanet.utils.Messaggi.MuoviDipendente;

public class Dipendente extends Disegnabile implements Telegraph {
    String nome;
    Attributi attributi;
    private Posto posto = Posto.Fuori;
    private Vector2 prossimo = null;
    private Timer timer;
    private ProgressBar progressBar;

    public Dipendente() {
        super(Tipo.Dipendente);
        nome = FileParser.getName();
        attributi = new Attributi();
        timer = new Timer();
        progressBar = new ProgressBar(timer);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public String getNome() {
        return nome;
    }

    public void muovi(Posto posto) {
        Logger.debug(nome + ", mi muovo da " + this.posto + " a " + posto.name());
        //TODO
        this.posto = posto;
        timer.setDelay(15 - attributi.get(posto));
    }

    public void update() {
        timer.update();
        //TODO
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case MuoviDipendente:
                if (msg.extraInfo instanceof Posto) {
                    muovi((Posto) msg.extraInfo);
                    return true;
                } else Logger.debug(nome + ", il posto a cui andare non è nel formato corretto.");
                break;
            case AssumiDipendente:
                if (msg.extraInfo instanceof Posto) {
                    posizioneReale = Renderer.coordinateRealiDi(posto);
                    muovi((Posto) msg.extraInfo);
                    return true;
                } else Logger.debug(nome + ", il posto a cui andare non è nel formato corretto.");
                Logger.debug(nome + ", sono stato assunto in " + msg.extraInfo + ", Yay!");
                break;
        }
        return false;
    }

    public Posto getPosto() {
        return posto;
    }

    public void iniziaLavoro(TimerTask task) {
        timer.stop();
        System.out.println("[DIPENDENTE] " + nome + ": inizio il mio lavoro in " + posto.name() + ": ci metterò " + timer.getDelay() + " secondi! (a = " + attributi.get(posto) + ")");
        timer.start(task);
    }
}
