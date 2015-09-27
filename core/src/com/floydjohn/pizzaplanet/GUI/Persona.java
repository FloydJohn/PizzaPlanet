package com.floydjohn.pizzaplanet.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.floydjohn.pizzaplanet.data.posti.Posto;

public abstract class Persona {
    private static final int SPD = 100;
    protected Texture texture;
    protected Vector2 posizioneReale;
    protected String nome;
    private Vector2 nextTarget = null;
    private Facing arrivalFacing = Facing.Down;
    private Facing lastFacing = Facing.Up;
    private Vector2 move = new Vector2();

    public Persona(Tipo tipo) {
        posizioneReale = new Vector2(-1, -1);
        switch (tipo) {
            case Dipendente:
                texture = new Texture(Gdx.files.internal("core/assets/dipendente.png"));
                break;
            case Cliente:
                texture = new Texture(Gdx.files.internal("core/assets/cliente.png"));
                break;
        }
    }

    public static Facing getFacing(Posto posto) {
        if (posto == null) return Facing.Up;
        switch (posto) {
            case Forno:
                return Facing.Left;
            case Stesura:
                return Facing.Down;
            case Farcitura:
                return Facing.Down;
            case Cassa:
                return Facing.Down;
            case Consegna:
                return Facing.Down;
            case Lavaggio:
                return Facing.Up;
            case Scatole:
                return Facing.Right;
            case Fuori:
                return Facing.Up;
            default:
                return Facing.Up;
        }
    }

    protected void updatePosition() {
        if (nextTarget != null) {
            move = new Vector2(new Vector2(nextTarget).sub(posizioneReale)).limit(SPD * Gdx.graphics.getDeltaTime());
            if (Math.abs(move.y) >= Math.abs(move.x)) {
                if (move.y >= 0) lastFacing = Facing.Up;
                else lastFacing = Facing.Down;
            } else {
                if (move.x >= 0) lastFacing = Facing.Right;
                else lastFacing = Facing.Left;
            }
            posizioneReale.add(move);
            if (posizioneReale.dst(nextTarget) < 1) {
                posizioneReale = new Vector2(nextTarget);
                nextTarget = null;
                lastFacing = arrivalFacing;
                arrivalFacing = null;
            }
        }
    }

    public abstract void update();

    public Vector2 getPosizioneReale() {
        return posizioneReale;
    }

    public void draw(SpriteBatch batch) {
        int x = 0, y = 32;
        switch (lastFacing) {
            case Up:
                x = 0;
                y = 32;
                break;
            case Down:
                x = 0;
                y = 0;
                break;
            case Left:
                x = 32;
                y = 32;
                break;
            case Right:
                x = 32;
                y = 0;
                break;
        }
        //batch.draw(texture, posizioneReale.x, posizioneReale.y, x, y, 32, 32, 1, 1, 0, 0, 0, 32, 32, false, false);
        batch.draw(texture, posizioneReale.x, posizioneReale.y, x, y, 32, 32);

    }

    public void setNext(Vector2 next) {
        this.nextTarget = next;
        arrivalFacing = getFacing(Renderer.postoACoordinate(Math.round(next.x) + 2, Math.round(next.y) + 2));
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

    public enum Facing {
        Up, Down, Left, Right
    }
}
