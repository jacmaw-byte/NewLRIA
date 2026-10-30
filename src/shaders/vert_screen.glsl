#version 330 core
layout (location = 0) in vec2 aPos;
out vec2 tex_coord;

void main()
{
    gl_Position = vec4(aPos.x, aPos.y, 0.0f, 1.0);
    tex_coord = (aPos + 1.0f) * 0.5f;
}
