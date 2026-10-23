package org.gameManager.renderManager;

import org.lwjgl.system.MemoryUtil;
import org.types.Colour;
import org.types.ColourA;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL43C.*;

public class Texture {
    private final int width;
    private final int height;

    private final ByteBuffer data;
    public int id;

    private final int texture_index;

    public Texture(int width, int height, int texture_index){
        this.width = width;
        this.height = height;
        this.texture_index = texture_index;

        data = MemoryUtil.memAlloc(width * height * 3);

        glActiveTexture(GL_TEXTURE0 + texture_index);
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    }

    public void bind(){
        glActiveTexture(GL_TEXTURE0 + texture_index);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void destroy() {
        MemoryUtil.memFree(data);
    }

    public int get_width(){
        return width;
    }

    public int get_height(){
        return height;
    }

    public int get_index(){
        return texture_index;
    }

    public void set_pixel(int x, int y, Colour colour){
        int index = (y * width + x) * 3;

        data.put(index, (byte) colour.r());
        data.put(index + 1, (byte) colour.g());
        data.put(index + 2, (byte) colour.b());
    }

    public void set_pixel(int x, int y, ColourA colour){
        int index = (y * width + x) * 3;

        data.put(index, (byte) colour.r());
        data.put(index + 1, (byte) colour.g());
        data.put(index + 2, (byte) colour.b());
    }

    public ByteBuffer get_raw(){
        return data;
    }

    public Colour get_pixel(int x, int y){
        int index = (y * width + x) * 3;

        return new Colour(
                data.get(index),
                data.get(index + 1),
                data.get(index + 2)
        );
    }


    public void clear(){
        for (int i = 0; i < width * height * 3; i++) {
            data.put(i, (byte)0);
        }
    }

    public void upload_to_opengl(){
        data.position(0);
        data.limit(data.capacity());

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
    }
}
