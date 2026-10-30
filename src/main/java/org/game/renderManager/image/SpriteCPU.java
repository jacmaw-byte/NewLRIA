package org.game.renderManager.image;

import org.game.GameManager;
import org.game.renderManager.PracticalScreen;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.types.ColourA;
import org.types.Rect;

import org.joml.Vector2i;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

// This is only cpu side, so it will be written to the render screen from the cpu
public class SpriteCPU {
    public int width;
    public int height;

    private ByteBuffer image_raw;
    private int desired_channels = 4;

    private boolean flipped_x = false;

    public SpriteCPU(String path){

        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            image_raw = STBImage.stbi_load(path, width, height, channels, desired_channels);
            STBImage.stbi_set_flip_vertically_on_load(false);
            if (image_raw == null){
                throw new RuntimeException("Failed to load image");
            }

            this.width = width.get(0);
            this.height = height.get(0);
        }

    }

    // This cuts out a rect from another SpriteCPU
    public SpriteCPU(Rect rect, SpriteCPU original){
        width = rect.max.x - rect.min.x;
        height = rect.max.y - rect.min.y;

        image_raw = MemoryUtil.memAlloc(width * height * desired_channels);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * desired_channels;
                int original_index = ((y + rect.min.y) * original.width + (x + rect.min.x)) * desired_channels;

                image_raw.put(index,        original.image_raw.get(original_index));
                image_raw.put(index + 1, original.image_raw.get(original_index + 1));
                image_raw.put(index + 2, original.image_raw.get(original_index + 2));
                image_raw.put(index + 3, original.image_raw.get(original_index + 3));
            }
        }
    }

    public void set_flipped_x(boolean flipped_x){
        this.flipped_x = flipped_x;
    }

    public void draw(int draw_x, int draw_y){
        PracticalScreen screen = GameManager.get_render_manager().get_screen();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int sprite_x = flipped_x ? (width - 1) - x : x;

                ColourA colour = get_pixel(sprite_x, y);

                if (colour.a() > 0)
                    screen.set_blended_pixel(draw_x + x, draw_y + y, colour);
            }
        }
    }

    public SpriteCPU slice(){
        Rect rect = get_used_rect();

        SpriteCPU new_sprite = new SpriteCPU(rect, this);

        destroy();

        return new_sprite;
    }

    public Rect get_used_rect(){
        Rect rect = new Rect(new Vector2i(width, height), new Vector2i(0, 0));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (get_pixel(x, y).a() == 0) continue;
                rect.min.x = Math.min(rect.min.x, x);
                rect.min.y = Math.min(rect.min.y, y);
                rect.max.x = Math.max(rect.max.x, x + 1);
                rect.max.y = Math.max(rect.max.y, y + 1);
            }
        }
        return rect;   // no +1 needed
    }

    public void flip_vertical() {
        int bytesPerPixel = 4;
        int rowSize = width * bytesPerPixel;

        for (int y = 0; y < height / 2; y++) {
            int oppositeY = height - 1 - y;

            for (int x = 0; x < rowSize; x++) {
                int index1 = y * rowSize + x;
                int index2 = oppositeY * rowSize + x;

                byte temp = image_raw.get(index1);
                image_raw.put(index1, image_raw.get(index2));
                image_raw.put(index2, temp);
            }
        }
    }

    public ColourA get_pixel(int x, int y){
        int index = (y * width + x) * desired_channels;

        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException(
                    "Accessed pixel out of bounds in sprite CPU"
            );
        }

        return new ColourA(
                Byte.toUnsignedInt(image_raw.get(index)),
                Byte.toUnsignedInt(image_raw.get(index + 1)),
                Byte.toUnsignedInt(image_raw.get(index + 2)),
                Byte.toUnsignedInt(image_raw.get(index + 3))
        );
    }

    public void set_pixel(int x, int y, ColourA colour){
        int index = (y * width + x) * desired_channels;

        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException(
                    "Accessed pixel out of bounds in sprite CPU"
            );
        }

        image_raw.put(index, colour.r_raw());
        image_raw.put(index + 1, colour.g_raw());
        image_raw.put(index + 2, colour.b_raw());
        image_raw.put(index + 3, colour.a_raw());
    }

    public void destroy(){
        STBImage.stbi_image_free(image_raw);
    }
}
