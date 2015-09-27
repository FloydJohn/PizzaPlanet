package com.floydjohn.pizzaplanet.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.floydjohn.pizzaplanet.data.persone.Cliente;
import com.floydjohn.pizzaplanet.data.persone.Dipendente;
import com.floydjohn.pizzaplanet.data.posti.Paese;
import com.floydjohn.pizzaplanet.data.posti.Posto;
import com.floydjohn.pizzaplanet.data.time.Tempo;

import java.util.EnumMap;
import java.util.Map;

//TODO Caricare immagini dinamiche

public class Renderer {

    private static TiledMap currentMap;
    private static TiledMapRenderer mapRenderer;
    private static Map<Posto, Vector2> coordinatePosti = new EnumMap<>(Posto.class);
    private static BitmapFont font = new BitmapFont();
    private Paese paese;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    public Renderer(Paese paese) {
        this.paese = paese;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font.setColor(Color.RED);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 30, 20);
        camera.update();
        currentMap = new TmxMapLoader().load("core/assets/maps/pizzeria.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(currentMap, 1 / 32f);
        for (Posto posto : Posto.values()) coordinatePosti.put(posto, coordinateRealiDi(posto));
    }

    public static float getTileWidth() {
        return currentMap.getProperties().get("tilewidth", Integer.class);
    }

    public static float getTileHeight() {
        return currentMap.getProperties().get("tileheight", Integer.class);
    }

    public static Vector2 coordinateRealiDi(Posto posto) {
        return coordinateMappaDi(posto).scl(32);
    }

    public static Vector2 coordinateMappaDi(Posto posto) {
        String coords = currentMap.getProperties().get(posto.name(), String.class);
        String[] parts = coords.split(",");
        return new Vector2(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    public static Posto postoACoordinate(int x, int y) {
        for (final Map.Entry<Posto, Vector2> entry : coordinatePosti.entrySet()) {
            if (x > entry.getValue().x && x < entry.getValue().x + getTileWidth() && y > entry.getValue().y && y < entry.getValue().y + getTileHeight())
                return entry.getKey();
        }
        return null;
    }

    public static Vector2 coordinateCoda(int postoInCoda) {
        return new Vector2(Math.max((postoInCoda + 3) * 32, 288), Math.max(320 - (postoInCoda * 32), 128));
    }

    public void render(Posto postoSelezionato) {

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.begin();
        for (Dipendente dipendente : paese.getPizzeria().getDipendenti()) dipendente.draw(batch);
        for (Cliente cliente : paese.getClienti()) cliente.draw(batch);

        drawTempo();
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (postoSelezionato != null) {
            Vector2 start = coordinateRealiDi(postoSelezionato).add(getTileWidth() / 2, getTileHeight() / 2);
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.line(start.x, start.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        }
        for (Dipendente dipendente : paese.getPizzeria().getDipendenti())
            dipendente.getProgressBar().draw(shapeRenderer, dipendente.getPosizioneReale());
        shapeRenderer.end();

    }

    private void drawTempo() {
        font.draw(batch, "Day: " + Tempo.get().getDay(), 0, 600);
        font.draw(batch, Tempo.get().getHrs() + ":" + Tempo.get().getMinString(), 0, 580);
    }
}
