
#version 330 core

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 texCoord;

out vec2 uv;

uniform mat3 projection;

void main() {
    uv = texCoord;

    vec3 pos = projection * vec3(position, 1.0);

    gl_Position = vec4(pos.xy, 0.0, 1.0);
}
