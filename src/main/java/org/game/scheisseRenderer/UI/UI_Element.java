package org.game.scheisseRenderer.UI;

import org.game.input.Input;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.*;

public abstract class UI_Element{
    // Position is private so it is only accessed by a function that will get the world position
    private Vector2f position;
    protected String name;
    protected Vector2f dimensions;

    private List<UI_Element> children = new ArrayList<>();
    protected UI_Element parent;

    public void draw() {

    }

    public void update() {

    }

    public void set_position(Vector2f position) { this.position = position; }

    public Vector2f get_screen_position() {
        // Don't try to access the parent of the root
        if (parent == null) return position;

        Vector2f parent_position = parent.get_screen_position();

        return new Vector2f(
                parent_position.x + position.x,
                parent_position.y + position.y
        );
    }

    protected boolean get_mouse_hover() {

        if (UI.hovered_over_element) return false;

        Vector2i mouse_position = Input.get_mouse_position();

        // AABB collisions
        if (mouse_position.x >= position.x && mouse_position.x <= position.x + dimensions.x &&
                mouse_position.y >= position.y && mouse_position.y <= position.y + dimensions.y){
            UI.hovered_over_element = true;
            return true;
        }

        return false;
    }

    public <T extends UI_Element> T get_child(String name, Class<T> type) {
        for (UI_Element child : children) {
            if (child.getClass() == type && Objects.equals(child.get_name(), name)){
                return type.cast(child);
            }
        }

        return null;
    }

    public void add_child(UI_Element element) {
        children.add(element);
        element.set_parent(this);
    }

    public Vector2f get_relative_position() { return position; }
    public Vector2f get_dimensions() { return dimensions; }
    public List<UI_Element> get_children() {
        return children;
    }

    public void set_parent(UI_Element parent) { this.parent = parent; }
    public String get_name() { return name; }
}
