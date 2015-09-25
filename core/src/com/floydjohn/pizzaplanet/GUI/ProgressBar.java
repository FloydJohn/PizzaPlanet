package com.floydjohn.pizzaplanet.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.floydjohn.pizzaplanet.utils.Percentage;

public class ProgressBar {
    private final static Color empty = new Color(1, 0, 0, 1);
    private final static Color full = new Color(0, 1, 0, 1);
    private final static int W = 32;
    private final static int H = 8;
    private final Percentage percentage;

    public ProgressBar(Percentage percentage) {
        this.percentage = percentage;
    }

    public void draw(ShapeRenderer renderer, Vector2 pos) {
        if (percentage.getPercentage() == 0) return;
        float x = pos.x;
        float y = pos.y + 48;
        renderer.setColor(empty);
        renderer.rect(x, y, W, H);
        renderer.setColor(full);
        renderer.rect(x, y, W * percentage.getPercentage(), H);
    }
}
