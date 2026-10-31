package org.game.renderManager.UI;

import org.game.player.server.inventory.Inventory;
import org.game.player.server.inventory.Item;
import org.game.renderManager.image.Sprite;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector2i;

public class UI_Inventory extends UI_Element{
    private List<Sprite> slot_sprites;
    private Sprite selector;
    private Vector2i position;

    private Inventory inventory;

    int slot_size;
    public UI_Inventory(Inventory inventory, Vector2i position, int slot_size){
        this.slot_size = slot_size;
        this.position = position;
        this.inventory = inventory;

        slot_sprites = new ArrayList<>();

        Vector2f slot_dimensions = new Vector2f(slot_size, slot_size);

        Vector2i slot_atlas_position = new Vector2i(0, 0);
        Vector2i selector_atlas_position = new Vector2i(1, 0);

        for (int i = 0; i < inventory.hotbar_length; i++) {

            Vector2f slot_position = new Vector2f(slot_size * i + position.x, position.y);

            Sprite slot = new Sprite(
                    slot_position,
                    slot_dimensions,
                    UI.ui_atlas,
                    slot_atlas_position
            );

            slot_sprites.add(slot);
        }

        selector = new Sprite(new Vector2f(), slot_dimensions, UI.ui_atlas, selector_atlas_position);
    }

    @Override
    public void draw() {
        int i = 0;
        for (Sprite sprite : slot_sprites){
            sprite.draw();

            Item item = inventory.get_item_from_hotbar(i);
            if (item != null){
                Sprite item_sprite = item.get_sprite();
                // Move the item to be in the slot
                item_sprite.get_transform().set_position(new Vector2f(slot_size * i + position.x + 10, position.y + 10));

                item_sprite.draw();
            }

            i++;
        }

        // Move the selector to be to its slot
        selector.get_transform().set_position(new Vector2f(slot_size * inventory.selected_slot + position.x, position.y));

        selector.draw();
    }

    @Override
    public void update() {

    }
}
