package org.game.scheisseRenderer.image;

import org.game.scheisseRenderer.ShaderClass;
import org.game.scheisseRenderer.Texture;
import org.game.scheisseRenderer.Transform;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public class Sprite {
    private Texture texture;

    Transform transform;

    int[] indices = {
            0, 2, 1,
            1, 2, 3
    };

    private int VBO;
    private int VAO;
    private int EBO;

    private ShaderClass shader;

    public Sprite(Texture texture, Vector2f position, Vector2f size){
        transform = new Transform(position, size);

        this.texture = texture;

        generate_mesh(new Vector2f[]{
                new Vector2f(0f, 0f),
                new Vector2f(1f, 1f)
        });

        texture.bind();
        texture.upload_to_opengl();
    }

    public Sprite(String path, Vector2f position, Vector2f size){

        float w = 0.5f;
        float h = 0.5f;

        transform = new Transform(position, size);

        try(MemoryStack stack = MemoryStack.stackPush()){

            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer buffer = STBImage.stbi_load(path, width, height, channels, 4);

            if (buffer == null) {
                throw new RuntimeException(
                        "Failed to load image: " + path + "\n" +
                                STBImage.stbi_failure_reason()
                );
            }

            try {
                texture = new Texture(width.get(0), height.get(0), 2);
                texture.set_data(buffer);
            } finally {
                STBImage.stbi_image_free(buffer);
            }

            generate_mesh(new Vector2f[]{
                    new Vector2f(0f, 0f),
                    new Vector2f(1f, 1f)
            });
        }

        texture.bind();
        texture.upload_to_opengl();
    }

    public Sprite(Vector2f position, Vector2f size, SpriteAtlas atlas, Vector2i atlas_position){

        transform = new Transform(position, size);
        Vector2f[] tex_coords = atlas.get_tex_coords(atlas_position);
        texture = atlas.texture;

        generate_mesh(tex_coords);
    }

    private void generate_mesh(Vector2f[] tex_coords){
        float w = 0.5f;
        float h = 0.5f;

        try(MemoryStack stack = MemoryStack.stackPush()){

            VBO = glGenBuffers();

            glBindBuffer(GL_ARRAY_BUFFER, VBO);

            FloatBuffer vertex_buffer = stack.mallocFloat(16);

            vertex_buffer.put(new float[]{
                    -w, -h, tex_coords[0].x, tex_coords[0].y,
                    -w, h, tex_coords[0].x, tex_coords[1].y,
                    w, -h, tex_coords[1].x, tex_coords[0].y,
                    w, h, tex_coords[1].x, tex_coords[1].y
            }).flip();

            glBufferData(
                    GL_ARRAY_BUFFER,
                    vertex_buffer,
                    GL_STATIC_DRAW
            );

            VAO = glGenVertexArrays();

            glBindVertexArray(VAO);
            glBindBuffer(GL_ARRAY_BUFFER, VBO);

            // For the position
            glVertexAttribPointer(
                    0,
                    2,
                    GL11.GL_FLOAT,
                    false,
                    16,
                    0L
            );

            // For the tex_coords
            glVertexAttribPointer(
                    1,
                    2,
                    GL11.GL_FLOAT,
                    false,
                    16,
                    8L
            );

            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            EBO = glGenBuffers();

            IntBuffer index_buffer = stack.mallocInt(8);
            index_buffer.put(indices).flip();

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, index_buffer, GL_STATIC_DRAW);

            glBindVertexArray(0);

            shader = new ShaderClass("src/shaders/vert_sprite.glsl", "src/shaders/frag_sprite.glsl");
        }
    }

    public void draw(){
        texture.bind();

        glUseProgram(shader.ID);

        shader.set_int("texture0", texture.get_index());
        shader.set_mat3("model", transform.get_matrix());

        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0L);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public Transform get_transform() { return transform; }
}
