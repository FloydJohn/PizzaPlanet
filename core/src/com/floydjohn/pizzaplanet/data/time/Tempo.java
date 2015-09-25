package com.floydjohn.pizzaplanet.data.time;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;

public class Tempo implements Telegraph {

    private static final float DELTA_PER_MIN = 0.01f;//15/9f;
    private static Istante tempo = new Istante(0, 17, 0);
    private static float deltaTime = 0;

    public static Istante get() {
        return tempo;
    }

    public static void update() {
        deltaTime += Gdx.graphics.getDeltaTime();
        if (deltaTime > DELTA_PER_MIN) {
            deltaTime = 0;
            tempo.incMin();
        }
    }

    public static boolean passato(Istante istante) {
        if (tempo.getDay() > istante.getDay()) return true;
        else if (tempo.getDay() < istante.getDay()) return false;
        if (tempo.getHrs() > istante.getHrs()) return true;
        else if (tempo.getHrs() < istante.getHrs()) return false;
        return tempo.getMin() > istante.getMin();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }
}
