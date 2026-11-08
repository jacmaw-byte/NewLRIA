package org.game.scheisseRenderer.UI;

import org.joml.Vector2f;

public class UI_Node extends UI_Element {
    public UI_Node(String name, Vector2f position) {
        this.name = name;
        set_position(position);
    }
}
