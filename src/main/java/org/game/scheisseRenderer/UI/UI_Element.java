package org.game.scheisseRenderer.UI;

import org.game.input.Input;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.*;

public abstract class UI_Element{
    // Position is private so it is only accessed by a function that will get the world position
    private Vector2f position;
    private boolean enabled = true;
    protected String name;
    protected Vector2f dimensions;

    private List<UI_Element> children = new ArrayList<>();
    protected UI_Element parent;

    public void draw() {

    }

    public void update() {

    }

    public boolean clickable() { return false; }

    public Vector2f get_screen_position() {
        // Don't try to access the parent of the root
        if (parent == null) return position;

        Vector2f parent_position = parent.get_screen_position();

        return new Vector2f(
                parent_position.x + position.x,
                parent_position.y + position.y
        );
    }

    protected boolean is_mouse_inside() {
        if (dimensions == null) return false;

        Vector2i mouse_position = Input.get_mouse_position();
        Vector2f screen_position = get_screen_position();

        return mouse_position.x >= screen_position.x &&
                mouse_position.x <= screen_position.x + dimensions.x &&
                mouse_position.y >= screen_position.y &&
                mouse_position.y <= screen_position.y + dimensions.y;
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
    public void set_position(Vector2f position) { this.position = position; }
    public void set_position(float x, float y) { position.x = x; position.y = y; }
    public void set_active(boolean active) { enabled = active; }
    public boolean get_active() { return enabled; }

    public boolean is_hovering() { return this == UI.hovered_element; }

    public String get_name() { return name; }
}
