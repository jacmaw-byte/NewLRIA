package org.types;

import java.util.Objects;

public class Vector2I {
    public int x;
    public int y;

    public static Vector2I zero = new Vector2I(0, 0);

    public Vector2I(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Vector2I scale(int scalar){
        return new Vector2I(x * scalar, y * scalar);
    }

    public Vector2f scale(float scalar){
        return new Vector2f(x * scalar, y * scalar);
    }

    public Vector2I add(Vector2I b){
        return new Vector2I(x + b.x, y + b.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2I vector2I = (Vector2I) o;
        return x == vector2I.x && y == vector2I.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
