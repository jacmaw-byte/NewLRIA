
package org.game.scheisseRenderer;

import org.Constants;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryUtil;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBTruetype.*;

// Completely ChatGPT
public class TextRenderer implements AutoCloseable {

    private static final int FIRST_CHAR = 32;
    private static final int CHAR_COUNT = 96;

    private static ShaderClass shader = null;

    private int texture;
    private int vao;
    private int vbo;

    private int font_size;
    private int atlas_size;

    private STBTTBakedChar.Buffer char_data;
    private STBTTAlignedQuad quad;

    private final FloatBuffer x = BufferUtils.createFloatBuffer(1);
    private final FloatBuffer y = BufferUtils.createFloatBuffer(1);

    public TextRenderer(String font_path, int font_size) {

        if (shader == null) {
            shader = new ShaderClass(
                    "src/shaders/vert_text.glsl",
                    "src/shaders/frag_text.glsl"
            );
        }

        this.font_size = font_size;

        atlas_size = compute_atlas_size(font_size);

        load_font(font_path);
        create_buffers();
    }


    private static int compute_atlas_size(int font_size) {
        // ~96 glyphs, each roughly (0.7 * size) x (1.2 * size), plus padding
        double area = CHAR_COUNT * (font_size * 0.7 + 2) * (font_size * 1.2 + 2);
        int size = 128;
        while ((long) size * size < area * 1.3) size *= 2; // 30% slack for packing waste
        return Math.min(size, 4096);
    }

    private void load_font(String font_path) {
        byte[] bytes;

        try {
            bytes = Files.readAllBytes(Path.of(font_path));
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not load font: " + font_path, e
            );
        }

        ByteBuffer font_data = MemoryUtil.memAlloc(bytes.length);
        ByteBuffer bitmap = MemoryUtil.memAlloc(atlas_size * atlas_size);

        try {
            font_data.put(bytes).flip();

            char_data = STBTTBakedChar.malloc(CHAR_COUNT);

            int result = stbtt_BakeFontBitmap(
                    font_data,
                    (float) font_size,
                    bitmap,
                    atlas_size,
                    atlas_size,
                    FIRST_CHAR,
                    char_data
            );

            if (result <= 0) {
                throw new RuntimeException("Failed to bake font!");
            }

            texture = glGenTextures();

            glBindTexture(GL_TEXTURE_2D, texture);

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            glTexImage2D(
                    GL_TEXTURE_2D,
                    0,
                    GL_R8,
                    atlas_size,
                    atlas_size,
                    0,
                    GL_RED,
                    GL_UNSIGNED_BYTE,
                    bitmap
            );

            glTexParameteri(
                    GL_TEXTURE_2D,
                    GL_TEXTURE_MIN_FILTER,
                    GL_LINEAR
            );

            glTexParameteri(
                    GL_TEXTURE_2D,
                    GL_TEXTURE_MAG_FILTER,
                    GL_LINEAR
            );

            glBindTexture(GL_TEXTURE_2D, 0);

        } finally {
            MemoryUtil.memFree(font_data);
            MemoryUtil.memFree(bitmap);
        }

        quad = STBTTAlignedQuad.malloc();
    }

    private void create_buffers() {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glBufferData(
                GL_ARRAY_BUFFER,
                0,
                GL_DYNAMIC_DRAW
        );

        // Position
        glVertexAttribPointer(
                0,
                2,
                GL_FLOAT,
                false,
                4 * Float.BYTES,
                0
        );

        glEnableVertexAttribArray(0);

        // Texture coordinates
        glVertexAttribPointer(
                1,
                2,
                GL_FLOAT,
                false,
                4 * Float.BYTES,
                2 * Float.BYTES
        );

        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private Matrix3f get_projection() {
        return new Matrix3f().set(
                2.0f / Constants.SCR_WIDTH, 0, 0,
                0, 2.0f / Constants.SCR_HEIGHT, 0,
                -1, -1, 1
        );
    }

    public void draw_text(String text, float pos_x, float pos_y) {
        draw_text(text, pos_x, pos_y, Color.WHITE);
    }

    public void draw_text(
            String text,
            float pos_x,
            float pos_y,
            Color color
    ) {
        if (text == null || text.isEmpty()) return;

        FloatBuffer vertices = MemoryUtil.memAllocFloat(
                text.length() * 6 * 4
        );

        // Convert bottom-left screen coordinates
        // into STB's top-left coordinate system.
        x.put(0, pos_x);
        y.put(0, Constants.SCR_HEIGHT - pos_y);

        int vertex_count = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '\n') {
                x.put(0, pos_x);
                y.put(0, y.get(0) + font_size * 1.2f);
                continue;
            }

            if (c < FIRST_CHAR || c >= FIRST_CHAR + CHAR_COUNT) {
                c = '?';
            }

            stbtt_GetBakedQuad(
                    char_data,
                    atlas_size,
                    atlas_size,
                    c - FIRST_CHAR,
                    x,
                    y,
                    quad,
                    true
            );

            float x_0 = quad.x0();
            float x_1 = quad.x1();

            // Convert STB's top-left coordinates
            // into bottom-left screen coordinates.
            float y_0 = Constants.SCR_HEIGHT - quad.y1();
            float y_1 = Constants.SCR_HEIGHT - quad.y0();

            float s_0 = quad.s0();
            float t_0 = quad.t0();
            float s_1 = quad.s1();
            float t_1 = quad.t1();

            // First triangle
            put_vertex(vertices, x_0, y_1, s_0, t_0);
            put_vertex(vertices, x_1, y_1, s_1, t_0);
            put_vertex(vertices, x_1, y_0, s_1, t_1);

            // Second triangle
            put_vertex(vertices, x_0, y_1, s_0, t_0);
            put_vertex(vertices, x_1, y_0, s_1, t_1);
            put_vertex(vertices, x_0, y_0, s_0, t_1);

            vertex_count += 6;
        }

        vertices.flip();

        if (vertex_count == 0) {
            MemoryUtil.memFree(vertices);
            return;
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.use();

        shader.set_mat3("projection", get_projection());

        glUniform4f(
                glGetUniformLocation(shader.ID, "textColor"),
                color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f
        );

        shader.set_int("fontTexture", 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glBufferData(
                GL_ARRAY_BUFFER,
                vertices,
                GL_DYNAMIC_DRAW
        );

        glDrawArrays(GL_TRIANGLES, 0, vertex_count);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glBindTexture(GL_TEXTURE_2D, 0);
        glUseProgram(0);

        MemoryUtil.memFree(vertices);
    }

    private void put_vertex(
            FloatBuffer buffer,
            float pos_x,
            float pos_y,
            float u,
            float v
    ) {
        buffer.put(pos_x);
        buffer.put(pos_y);
        buffer.put(u);
        buffer.put(v);
    }

    public Vector2f get_character_dimensions(char character) {
        if (character < FIRST_CHAR || character >= FIRST_CHAR + CHAR_COUNT) {
            character = '?';
        }

        FloatBuffer char_x = BufferUtils.createFloatBuffer(1);
        FloatBuffer char_y = BufferUtils.createFloatBuffer(1);

        STBTTAlignedQuad char_quad = STBTTAlignedQuad.malloc();

        stbtt_GetBakedQuad(
                char_data,
                atlas_size,
                atlas_size,
                character - FIRST_CHAR,
                char_x,
                char_y,
                char_quad,
                true
        );

        float width = char_quad.x1() - char_quad.x0();
        float height = char_quad.y1() - char_quad.y0();

        char_quad.free();

        return new Vector2f(width, height);
    }

    public Vector2f get_text_dimensions(String text) {
        if (text == null || text.isEmpty()) {
            return new Vector2f(0, 0);
        }

        FloatBuffer text_x = BufferUtils.createFloatBuffer(1);
        FloatBuffer text_y = BufferUtils.createFloatBuffer(1);

        float line_height = font_size * 1.2f; // baseline-to-baseline distance (match your renderer)

        float max_width = 0;
        float min_y = Float.MAX_VALUE;   // top of highest glyph
        float max_y = -Float.MAX_VALUE;  // bottom of lowest glyph
        boolean has_ink = false;

        text_x.put(0, 0f);
        text_y.put(0, 0f);

        STBTTAlignedQuad char_quad = STBTTAlignedQuad.malloc();
        try {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);

                if (c == '\n') {
                    max_width = Math.max(max_width, text_x.get(0));
                    text_x.put(0, 0f);
                    text_y.put(0, text_y.get(0) + line_height);
                    continue;
                }

                if (c < FIRST_CHAR || c >= FIRST_CHAR + CHAR_COUNT) {
                    c = '?';
                }

                stbtt_GetBakedQuad(
                        char_data,
                        atlas_size,
                        atlas_size,
                        c - FIRST_CHAR,
                        text_x,
                        text_y,
                        char_quad,
                        true
                );

                // Skip glyphs with no visible area (e.g. spaces) so they don't skew bounds
                if (char_quad.y1() > char_quad.y0()) {
                    min_y = Math.min(min_y, char_quad.y0());
                    max_y = Math.max(max_y, char_quad.y1());
                    has_ink = true;
                }
            }

            max_width = Math.max(max_width, text_x.get(0));
        } finally {
            char_quad.free();
        }

        float height = has_ink ? (max_y - min_y) : 0f;

        return new Vector2f(max_width, height);
    }

    @Override
    public void close() {
        glDeleteTextures(texture);
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);

        char_data.free();
        quad.free();
    }
}
