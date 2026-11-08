
#version 330 core

in vec2 uv;

out vec4 fragColor;

uniform sampler2D fontTexture;
uniform vec4 textColor;

void main() {
    float alpha = texture(fontTexture, uv).r;

    fragColor = vec4(
            textColor.rgb,
            textColor.a * alpha
    );
}
