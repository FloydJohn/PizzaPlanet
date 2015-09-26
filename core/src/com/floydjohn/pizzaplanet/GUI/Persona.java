package com.floydjohn.pizzaplanet.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public abstract class Persona {
    private static final int SPD = 100;
    protected Texture texture;
    protected Vector2 posizioneReale;
    protected String nome;
    protected Vector2 nextTarget = null;

    public Persona(Tipo tipo) {
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

    protected void updatePosition() {
        if (nextTarget != null) {
            Vector2 move = new Vector2(new Vector2(nextTarget).sub(posizioneReale)).limit(SPD * Gdx.graphics.getDeltaTime());
            posizioneReale.add(move);
            if (posizioneReale.dst(nextTarget) < 1) nextTarget = null;
        }
    }

    public abstract void update();

    public Vector2 getPosizioneReale() {
        return posizioneReale;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setNext(Vector2 next) {
        this.nextTarget = next;
    }

    public String getNome() {
        return nome;
    }

    public boolean isMoving() {
        return nextTarget != null;
    }

    public enum Tipo {
        Dipendente, Cliente
    }
}
