package org.gameManager.renderManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.lwjgl.opengl.GL30C.*;

public class ShaderClass implements AutoCloseable {
    private int vertex_shader;
    private int frag_shader;
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
        glDeleteShader(vertex_shader);
        glDeleteShader(frag_shader);
    }

    public void set_int(String name, int value){
        glUniform1i(glGetUniformLocation(ID, name), value);
    }

    public void set_float(String name, float value){
        glUniform1f(glGetUniformLocation(ID, name), value);
    }

    public ShaderClass(String vertex_file_path, String frag_file_path){

        File vertex_file_obj = new File(vertex_file_path);
        File frag_file_obj = new File(frag_file_path);

        StringBuilder vertex_source = get_shader_source(vertex_file_obj);
        StringBuilder frag_source = get_shader_source(frag_file_obj);

        vertex_shader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex_shader, vertex_source);
        glCompileShader(vertex_shader);

        frag_shader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(frag_shader, frag_source);
        glCompileShader(frag_shader);

        ID = glCreateProgram();

        glAttachShader(ID, frag_shader);
        glAttachShader(ID, vertex_shader);
        glLinkProgram(ID);
    }
}
