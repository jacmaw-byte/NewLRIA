package org.game.renderManager;

import org.Constants;
import org.joml.*;
import org.joml.Math;


public class Transform {
    private Vector2f position;
    private float rotation;
    private Vector2f scale;
    private Matrix3f model;

    public Transform(){
        position = new Vector2f(0, 0);
        rotation = 0.0f;
        scale = new Vector2f(1, 1);

        model = new Matrix3f();
    }

    public Transform(Vector2f position, Vector2f scale){
        this.position = position;
        this.rotation = 0.0f;
        this.scale = scale;

        model = new Matrix3f();
        make_matrix();
    }

    public void make_matrix(){

        float sin_theta = Math.sin(rotation);
        float cos_theta = Math.cos(rotation);


        float normalised_x =
                2.0f * (position.x + scale.x / 2.0f)
                        / Constants.SCR_WIDTH - 1.0f;

        float normalised_y =
                2.0f * (position.y + scale.y / 2.0f)
                        / Constants.SCR_HEIGHT - 1.0f;

        float scale_x = 2.0f * scale.x / Constants.SCR_WIDTH;
        float scale_y = 2.0f * scale.y / Constants.SCR_HEIGHT;

        Vector3f row1 = new Vector3f(
                scale_x * cos_theta,
                -scale_y * sin_theta,
                normalised_x
        );

        Vector3f row2 = new Vector3f(
                scale_x * sin_theta,
                scale_y * cos_theta,
                normalised_y
        );

        Vector3f row3 = new Vector3f(
                0,
                0,
                1
        );

        model.setRow(0, row1);
        model.setRow(1, row2);
        model.setRow(2, row3);
    }

    public void set_rotation(float rotation) {
        this.rotation = Math.toRadians(rotation);
        make_matrix();
    }

    public void set_position(Vector2f position) {
        this.position = position;
        make_matrix();
    }

    public void set_scale(Vector2f scale) {
        this.scale = scale;
        make_matrix();
    }

    public Vector2f get_scale() { return scale; }
    public Vector2f get_position() { return position; }
    public float get_rotation() { return Math.toDegrees(rotation); }
    public Matrix3f get_matrix() { return model; }
}
