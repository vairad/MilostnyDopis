package gui;

import java.awt.*;

import core.TypKarty;


/**
 * Created by Radek VAIS on 26.10.15.
 */
public class ZobrazeniKarty extends Canvas {

    private TypKarty typKarty;
    private boolean cela;

    public ZobrazeniKarty(TypKarty typKarty, boolean cela) {
        this.typKarty = typKarty;
        this.cela = cela;
    }

    @Override
    public void paint(Graphics g){

    }

}
