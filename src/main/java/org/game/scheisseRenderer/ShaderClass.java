package org.game.scheisseRenderer;

import org.joml.Matrix3f;

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
    public void close() {
        glDeleteShader(vertex_shader);
        glDeleteShader(frag_shader);
        glDeleteProgram(ID);
    }

    public void set_int(String name, int value){
        glUniform1i(glGetUniformLocation(ID, name), value);
    }

    public void set_mat3(String name, Matrix3f matrix){
        float[] matrix_array = new float[9];

        matrix.get(matrix_array);

        glUniformMatrix3fv(glGetUniformLocation(ID, name), false, matrix_array);
    }

    public void set_float(String name, float value){
        glUniform1f(glGetUniformLocation(ID, name), value);
    }

    private void checkShaderCompilation(int shader, String shaderName) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String infoLog = glGetShaderInfoLog(shader);

            glDeleteShader(shader);

            throw new RuntimeException(
                    shaderName + " compilation failed:\n" + infoLog
            );
        }
    }

    public ShaderClass(String vertex_file_path, String frag_file_path){

        File vertex_file_obj = new File(vertex_file_path);
        File frag_file_obj = new File(frag_file_path);

        StringBuilder vertex_source = get_shader_source(vertex_file_obj);
        StringBuilder frag_source = get_shader_source(frag_file_obj);

        vertex_shader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex_shader, vertex_source);
        glCompileShader(vertex_shader);

        checkShaderCompilation(vertex_shader, "Vertex shader");

        frag_shader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(frag_shader, frag_source);
        glCompileShader(frag_shader);

        checkShaderCompilation(frag_shader, "Frag shader");

        ID = glCreateProgram();

        glAttachShader(ID, frag_shader);
        glAttachShader(ID, vertex_shader);
        glLinkProgram(ID);

        if (glGetProgrami(ID, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Shader linking failed:");
            System.err.println(glGetProgramInfoLog(ID));
        }
    }

    public void use() {
        glUseProgram(ID);
    }
}
