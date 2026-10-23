package org.types;

public class Colour {
    private byte r;
    private byte g;
    private byte b;

    public Colour(int r, int g, int b){

        r = Math.clamp(r, 0, 255);
        g = Math.clamp(g, 0, 255);
        b = Math.clamp(b, 0, 255);

        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
    }

    public int r(){ return Byte.toUnsignedInt(r); }
    public int g(){ return Byte.toUnsignedInt(g); }
    public int b(){ return Byte.toUnsignedInt(b); }

}
