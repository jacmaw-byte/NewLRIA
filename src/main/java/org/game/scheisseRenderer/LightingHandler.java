package org.game.scheisseRenderer;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL43C.*;

import static org.Constants.*;
public class LightingHandler {
    public int texture;
    public Texture occlusion_map;
    private ComputeShaderClass compute_shader;

    public LightingHandler(){
        compute_shader = new ComputeShaderClass("src/shaders/lighting.comp");
        occlusion_map = new Texture(GRID_WIDTH, GRID_HEIGHT, 2);

        texture = glGenTextures();
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA8,
                GRID_WIDTH,
                GRID_HEIGHT,
                0,
                GL_RGBA,
                GL_FLOAT,
                (ByteBuffer) null
        );

        glBindImageTexture(
                0,
                texture,
                0,
                false,
                0,
                GL_WRITE_ONLY,
                GL_RGBA8
        );
    }

    public void run() {
        compute_shader.use();

        occlusion_map.bind();
        occlusion_map.upload_to_opengl();
        compute_shader.set_int("occlusion", 1);

        int groupsX = (GRID_WIDTH + 63) / 64;
        int groupsY = GRID_HEIGHT + 7;

        glDispatchCompute(groupsX, 1, 1);

        glMemoryBarrier(
                GL_SHADER_IMAGE_ACCESS_BARRIER_BIT |
                        GL_TEXTURE_FETCH_BARRIER_BIT
        );
    }
}
