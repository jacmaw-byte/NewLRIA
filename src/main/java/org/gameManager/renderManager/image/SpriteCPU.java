package org.gameManager.renderManager.image;

import org.gameManager.GameManager;
import org.gameManager.player.Camera;
import org.gameManager.renderManager.RenderManager;
import org.gameManager.renderManager.Texture;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.types.Colour;
import org.types.ColourA;
import org.types.Rect;
import org.types.Vector2I;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

// This is only cpu side, so it will be written to the render screen from the cpu
public class SpriteCPU {
    public int width;
    public int height;
    public int channels;

    private ByteBuffer image_raw;
    private int desired_channels = 4;

    public SpriteCPU(String path){

        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            image_raw = STBImage.stbi_load(path, width, height, channels, desired_channels);

            if (image_raw == null){
                throw new RuntimeException("Failed to load image");
            }

            this.width = width.get(0);
            this.height = height.get(0);
            this.channels = channels.get(0);

        }

    }

    public SpriteCPU slice(){
        Rect rect = get_used_rect();

        SpriteCPU new_sprite = new SpriteCPU(rect, this);

        destroy();

        return new_sprite;
    }

    public Rect get_used_rect(){
        // Uses width and height so any pixel is less
        Rect rect = new Rect(new Vector2I(width, height), Vector2I.zero);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                boolean solid = get_pixel(x, y).a() > 0;

                if (x < rect.min.x && solid) rect.min.x = x;
                if (y < rect.min.y && solid) rect.min.y = y;
                if (x > rect.max.x && solid) rect.max.x = x + 1;
                if (y > rect.max.y && solid) rect.max.y = y + 1;
            }
        }
        // IDK why this is needed, but it is
        rect.max.y += 1;

        return rect;
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
        int index = (y * width + x) * 4;

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


    // This cuts out a rect from another SpriteCPU
    public SpriteCPU(Rect rect, SpriteCPU original){
        width = rect.max.x - rect.min.x;
        height = rect.max.y - rect.min.y;

        image_raw = MemoryUtil.memAlloc(width * height * original.channels);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * 4;
                int original_index = ((y + rect.min.y) * original.width + (x + rect.min.x)) * 4;

                image_raw.put(index,        original.image_raw.get(original_index));
                image_raw.put(index + 1, original.image_raw.get(original_index + 1));
                image_raw.put(index + 2, original.image_raw.get(original_index + 2));
                image_raw.put(index + 3, original.image_raw.get(original_index + 3));
            }
        }
    }

    public void destroy(){
        STBImage.stbi_image_free(image_raw);
    }
}
