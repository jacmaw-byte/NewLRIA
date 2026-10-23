package org.types;

public class Rect {
    public Vector2I min;
    public Vector2I max;

    public Rect(Vector2I min, Vector2I max){
        this.min = min;
        this.max = max;
    }

    public Rect(Vector2I origin, int w, int h){
        this.min = origin;
        this.max = new Vector2I(
                origin.x + w,
                origin.y + h
        );
    }
}
