package com.floydjohn.pizzaplanet.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.floydjohn.pizzaplanet.GUI.PizzaPlanet;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960;
        config.height = 640;
        new LwjglApplication(new PizzaPlanet(), config);
    }
}
