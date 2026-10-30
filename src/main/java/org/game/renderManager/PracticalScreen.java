package org.game.renderManager;

import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.types.Colour;
import org.types.ColourA;

import java.nio.*;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL30C.*;

import static org.Constants.*;
public class PracticalScreen {

    float[] vertices = {
            -1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f,
    };

    int[] indices = {
            0, 2, 1,
            1, 2, 3
    };

    private int VBO;
    private int VAO;
    private int EBO;

    private final ShaderClass shader;

    private final Texture render_texture;

    public PracticalScreen(){

        render_texture = new Texture(GRID_WIDTH, GRID_HEIGHT, 1);

        VBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);

        FloatBuffer vertex_buffer = MemoryUtil.memAllocFloat(vertices.length);
        vertex_buffer.put(vertices).flip();

        glBufferData(
                GL_ARRAY_BUFFER,
                vertex_buffer,
                GL_STATIC_DRAW
        );

        MemoryUtil.memFree(vertex_buffer);

        VAO = glGenVertexArrays();

        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);

        // For the position
        glVertexAttribPointer(
                0,
                2,
                GL11.GL_FLOAT,
                false,
                8,
                0L
        );

        glEnableVertexAttribArray(0);

        EBO = glGenBuffers();

        IntBuffer index_buffer = MemoryUtil.memAllocInt(indices.length);
        index_buffer.put(indices).flip();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, index_buffer, GL_STATIC_DRAW);

        MemoryUtil.memFree(index_buffer);

        glBindVertexArray(0);
        shader = new ShaderClass("src/shaders/vert_screen.glsl", "src/shaders/frag_screen.glsl");
    }

    public void set_blended_pixel(int x, int y, ColourA colour) {
        ColourA background = render_texture.get_pixel(x, y);

        float alpha = colour.a() / 255.0f;
        float inverseAlpha = 1.0f - alpha;

        Colour blended = new Colour(
                Math.round(colour.r() * alpha + background.r() * inverseAlpha),
                Math.round(colour.g() * alpha + background.g() * inverseAlpha),
                Math.round(colour.b() * alpha + background.b() * inverseAlpha)
        );

        render_texture.set_pixel(x, y, blended);
    }

    public void draw(LightingHandler lighting_handler) {
        render_texture.bind();
        render_texture.upload_to_opengl();

        glUseProgram(shader.ID);

        shader.set_int("texture1", render_texture.get_index());

        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0L);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public Texture get_texture(){
        return render_texture;
    }
}
