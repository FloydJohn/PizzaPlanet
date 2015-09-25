package com.floydjohn.pizzaplanet.GUI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public abstract class Disegnabile {
    protected Texture texture;
    protected Vector2 posizioneReale;

    public Disegnabile(Tipo tipo) {
        posizioneReale = new Vector2(-1, -1);
        switch (tipo) {
            case Dipendente:
                texture = new Texture("core/assets/dipendente.png");
                break;
            case Cliente:
                texture = new Texture("core/assets/cliente.png");
                break;
        }
    }

    public abstract void update();

    public Vector2 getPosizioneReale() {
        return posizioneReale;
    }

    public Texture getTexture() {
        return texture;
    }

    public enum Tipo {
        Dipendente, Cliente
    }
}
