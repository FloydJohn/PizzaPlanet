package com.floydjohn.pizzaplanet.GUI;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.floydjohn.pizzaplanet.data.persone.Dipendente;
import com.floydjohn.pizzaplanet.data.posti.Paese;
import com.floydjohn.pizzaplanet.data.posti.Posto;
import com.floydjohn.pizzaplanet.data.prodotti.Database;
import com.floydjohn.pizzaplanet.data.time.Tempo;

public class PizzaPlanet extends ApplicationAdapter implements InputProcessor {

    private Renderer renderer;
    private Paese paese;
    private Posto postoSelezionato = null;

    @Override
    public void create() {
        Database.populate();
        Gdx.input.setInputProcessor(this);
        paese = new Paese();
        renderer = new Renderer(paese.getPizzeria());
        paese.getPizzeria().assumi(Posto.Cassa, new Dipendente());
    }

    @Override
    public void render() {
        MessageManager.getInstance().update(Gdx.graphics.getDeltaTime());
        Tempo.update();
        paese.update();
        renderer.render(postoSelezionato);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            postoSelezionato = Renderer.postoACoordinate(screenX, Gdx.graphics.getHeight() - screenY);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            Posto postoCliccato = Renderer.postoACoordinate(screenX, Gdx.graphics.getHeight() - screenY);
            if (postoCliccato == null || postoSelezionato == null || postoSelezionato.equals(postoCliccato)) {
                System.out.println("Rilasciato a vuoto");
                postoSelezionato = null;
                return false;
            } else {
                paese.getPizzeria().inverti(postoSelezionato, postoCliccato);
                postoSelezionato = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
