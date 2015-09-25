package com.floydjohn.pizzaplanet.test;

import com.floydjohn.pizzaplanet.data.persone.Dipendente;
import com.floydjohn.pizzaplanet.data.posti.Pizzeria;
import com.floydjohn.pizzaplanet.data.posti.Posto;
import com.floydjohn.pizzaplanet.exceptions.PostoOccupatoException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PizzeriaTest {

    Pizzeria pizzeria;

    @Before
    public void setUp() throws Exception {
        pizzeria = new Pizzeria();
    }

    @Test
    public void testAssumi() throws Exception {
        pizzeria.assumi(Posto.Cassa, new Dipendente());
        pizzeria.assumi(Posto.Farcitura, new Dipendente());
    }

    @Test(expected = PostoOccupatoException.class)
    public void testAssumiOccupato() throws Exception {
        pizzeria.assumi(Posto.Cassa, new Dipendente());
        pizzeria.assumi(Posto.Cassa, new Dipendente());
    }

    @Test
    public void testIsOccupato() throws Exception {
        assertFalse(pizzeria.isOccupato(Posto.Cassa));
        pizzeria.assumi(Posto.Cassa, new Dipendente());
        assertTrue(pizzeria.isOccupato(Posto.Cassa));
    }

    @Test
    public void testInverti() throws Exception {
        Dipendente dipendente1 = new Dipendente(), dipendente2 = new Dipendente();
        pizzeria.assumi(Posto.Cassa, dipendente1);
        pizzeria.assumi(Posto.Farcitura, dipendente2);
        pizzeria.inverti(Posto.Cassa, Posto.Farcitura);
        assertEquals(dipendente1, pizzeria.getDipendente(Posto.Farcitura));
        assertEquals(dipendente2, pizzeria.getDipendente(Posto.Cassa));
    }
}