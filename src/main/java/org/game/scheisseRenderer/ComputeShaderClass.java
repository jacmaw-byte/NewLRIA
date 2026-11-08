package org.game.scheisseRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.lwjgl.opengl.GL43C.*;

public class ComputeShaderClass implements AutoCloseable {
    private int shader;
    public int ID;

    private StringBuilder get_shader_source(File shader_file_obj){
        StringBuilder shader_source = new StringBuilder();

        try (Scanner reader = new Scanner(shader_file_obj)){
            while(reader.hasNextLine()){
                shader_source.append(reader.nextLine()).append('\n');
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        return shader_source;
    }

    @Override
    public void close(){
        glDeleteShader(shader);
    }

    public void use(){
        glUseProgram(ID);
    }

    public void set_int(String name, int value){
        glUniform1i(glGetUniformLocation(ID, name), value);
    }

    public void set_float(String name, float value){
        glUniform1f(glGetUniformLocation(ID, name), value);
    }

    public ComputeShaderClass(String shader_path){

        File compute_file_obj = new File(shader_path);

        StringBuilder compute_source = get_shader_source(compute_file_obj);

        shader = glCreateShader(GL_COMPUTE_SHADER);
        glShaderSource(shader, compute_source);
        glCompileShader(shader);


        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException(
                    "Compute shader compilation failed:\n" +
                            glGetShaderInfoLog(shader)
            );
        }

        ID = glCreateProgram();

        glAttachShader(ID, shader);
        glLinkProgram(ID);

        if (glGetProgrami(ID, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException(
                    "Compute shader linking failed:\n" +
                            glGetProgramInfoLog(ID)
            );
        }
    }
}
