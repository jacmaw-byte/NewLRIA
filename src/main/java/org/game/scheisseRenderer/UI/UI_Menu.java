package org.game.scheisseRenderer.UI;

import org.game.scheisseRenderer.image.SpriteAtlas;
import org.joml.Vector2f;

import java.util.List;

public class UI_Menu {
    private boolean active;
    private String name;
    private UI_Node root_node;

    public UI_Menu(String name, Vector2f position, boolean active) {
        this.name = name;
        this.active = active;
        root_node = new UI_Node("root", position);
    }

    private static void draw_element(UI_Element element) {
        element.draw();

        // Process children after so they are drawn over
        for (UI_Element child : element.get_children())
            draw_element(child);
    }

    private static void update_element(UI_Element element) {
        // Process Before so that children will get the opportunity to be pressed on before the parent
        for (UI_Element child : element.get_children())
            update_element(child);

        element.update();
    }

    public void draw(){
        UI.ui_atlas.bind();

        draw_element(root_node);
    }

    public void update(){
        update_element(root_node);

        UI.hovered_over_element = false;
    }

    public <T extends UI_Element> T get_element(String name, Class<T> type) {
        return root_node.get_child(name, type);
    }

    public void set_active(boolean active) { this.active = active; }
    public boolean is_active() { return active; }
    public UI_Node get_root_node() { return root_node; }
    public void add_element(UI_Element element){
        root_node.add_child(element);
    }
    public String get_name() { return name; }
}
