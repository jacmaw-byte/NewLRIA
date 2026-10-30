#version 330 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTex;

out vec2 tex_coord;

uniform mat3 model;

void main()
{
    vec3 pos = model * vec3(aPos, 1.0);
    gl_Position = vec4(pos.xy, 0.0, 1.0);

    tex_coord = aTex;
}