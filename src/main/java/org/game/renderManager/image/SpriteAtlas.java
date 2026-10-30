package org.game.renderManager.image;

import org.game.renderManager.Texture;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class SpriteAtlas {
    public Texture texture;

    // The number of sprites across the image is.
    // So if an atlas is 16x16 and each sprite is 8x8, it would be 2x2 sprites across
    public Vector2i sprite_dimensions;

    public SpriteAtlas(String path, Vector2i slice_dimensions, int texture_index){

        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer buffer = STBImage.stbi_load(path, width, height, channels, 4);

            sprite_dimensions = new Vector2i(
                width.get(0) / slice_dimensions.x,
                height.get(0) / slice_dimensions.y
            );

            if (buffer == null) {
                throw new RuntimeException(
                        "Failed to load image: " + path + "\n" +
                                STBImage.stbi_failure_reason()
                );
            }

            try {
                texture = new Texture(width.get(0), height.get(0), texture_index);
                texture.set_data(buffer);
            } finally {
                STBImage.stbi_image_free(buffer);
            }
        }
    }

    public void bind(){
        texture.bind();
    }

    public void upload_to_opengl(){
        texture.upload_to_opengl();
    }

    public Vector2f[] get_tex_coords(Vector2i atlas_position) {
        float tex_size_x = 1.0f / sprite_dimensions.x;
        float tex_size_y = 1.0f / sprite_dimensions.y;

        float x1 = tex_size_x * atlas_position.x;
        float x2 = x1 + tex_size_x;

        float y1 = tex_size_y * atlas_position.y;
        float y2 = y1 + tex_size_y;

        return new Vector2f[]{
                new Vector2f(x1, y1),
                new Vector2f(x2, y2),
        };
    }
}
