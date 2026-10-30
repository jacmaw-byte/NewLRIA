package org.types;
import org.joml.Vector2i;
public class Rect {
    public Vector2i min;
    public Vector2i max;

    public Rect(Vector2i min, Vector2i max){
        this.min = min;
        this.max = max;
    }

    public Rect(Vector2i origin, int w, int h){
        this.min = origin;
        this.max = new Vector2i(
                origin.x + w,
                origin.y + h
        );
    }
}
