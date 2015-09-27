package com.floydjohn.pizzaplanet.data.persone;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.floydjohn.pizzaplanet.GUI.Persona;
import com.floydjohn.pizzaplanet.GUI.ProgressBar;
import com.floydjohn.pizzaplanet.GUI.Renderer;
import com.floydjohn.pizzaplanet.data.posti.Posto;
import com.floydjohn.pizzaplanet.data.time.Timer;
import com.floydjohn.pizzaplanet.utils.FileParser;
import com.floydjohn.pizzaplanet.utils.Logger;
import com.floydjohn.pizzaplanet.utils.Messaggi;

import java.util.TimerTask;

import static com.floydjohn.pizzaplanet.utils.Messaggi.AssumiDipendente;
import static com.floydjohn.pizzaplanet.utils.Messaggi.MuoviDipendente;

public class Dipendente extends Persona implements Telegraph {

    Attributi attributi;
    private Posto posto = Posto.Fuori;
    private Posto nextPosto = null;
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

    public void muovi(Posto posto) {
        //Logger.debug(nome + ", mi muovo da " + this.posto + " a " + posto.name());
        super.setNext(Renderer.coordinateRealiDi(posto));
        nextPosto = posto;
    }

    public void update() {
        super.updatePosition();
        if (nextPosto != null && !super.isMoving()) {
            posto = nextPosto;
            timer.setDelay(15 - attributi.get(posto));
            nextPosto = null;
            //System.out.println("[DIP] Arrivato a " + posto);
            send(Messaggi.DipendenteArrivato, null, posto);
        }
        timer.update();
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
                    //Logger.debug(nome + ", sono stato assunto in " + msg.extraInfo + ", Yay!");
                    muovi((Posto) msg.extraInfo);
                    return true;
                } else Logger.debug(nome + ", il posto a cui andare non è nel formato corretto.");
                break;
        }
        return false;
    }

    public void iniziaLavoro(TimerTask task) {
        timer.stop();
        System.out.println("[DIPENDENTE] " + nome + ": inizio il mio lavoro in " + posto.name() + ": ci metterò " + timer.getDelay() + " secondi! (a = " + attributi.get(posto) + ")");
        timer.start(task);
    }

    private void send(int msg, Telegraph target, Object extraInfo) {
        if (target == null) MessageManager.getInstance().dispatchMessage(this, msg, extraInfo);
        else MessageManager.getInstance().dispatchMessage(this, target, msg, extraInfo);
    }

    public boolean inPausa() {
        return !timer.isRunning();
    }
}
