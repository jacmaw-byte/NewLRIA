package org.types;
import java.math.*;

public class Vector2f {
    public float x;
    public float y;

    public static Vector2f zero = new Vector2f(0.0f, 0.0f);

    public Vector2f(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2f add(Vector2I b){
        return new Vector2f(x + b.x, y + b.y);
    }

    public Vector2f add(Vector2f b){
        return new Vector2f(x + b.x, y + b.y);
    }

    public Vector2f scale(float scalar){
        return new Vector2f(x * scalar, y * scalar);
    }

    public Vector2I floor(){
        return new Vector2I((int)Math.floor(x), (int)Math.floor(y));
    }
}
