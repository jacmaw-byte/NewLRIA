package org.utilities;

import org.joml.Vector2f;

public class ExtraMaths {
    public static Vector2f lerp(Vector2f p0, Vector2f p1, double t) {
        return new Vector2f(
                (float)(p0.x * (1 - t) + p1.x * t),
                (float)(p0.y * (1 - t) + p1.y * t)
        );
    }
}
