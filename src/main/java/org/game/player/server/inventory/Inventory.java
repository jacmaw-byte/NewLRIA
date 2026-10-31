package org.game.player.server.inventory;

import org.game.input.Input;
import org.game.player.server.inventory.items.DirtItem;
import org.game.renderManager.UI.UI;
import org.game.renderManager.UI.UI_Inventory;
import org.joml.Vector2i;

public class Inventory {
    private Vector2i slot_dimensions;
    public int hotbar_length = 4;
    public int selected_slot = 0;

    private Item[] items;

    UI_Inventory ui_inventory;

    public Inventory(){
        slot_dimensions = new Vector2i(10, 3);

        items = new Item[slot_dimensions.x * slot_dimensions.y + hotbar_length];

        add_item_to_hotbar(0, new DirtItem());

        ui_inventory = new UI_Inventory(
                this,
                new Vector2i(100, 100),
                60
        );

        UI.add_element(ui_inventory);
    }

    public void update(){
        // Move the selected slot by the scroll wheel offset but allow it to seamlessly go to both sides
        selected_slot = ((selected_slot - (int)Input.get_scroll_y()) % hotbar_length + hotbar_length) % hotbar_length;
    }

    public Item get_item(int x, int y){
        return items[y * slot_dimensions.x + x];
    }

    // Looks at the last few elements in items, where the hotbar is encoded
    public Item get_item_from_hotbar(int x){
        return items[slot_dimensions.x * slot_dimensions.y + x];
    }

    public void add_item(int x, int y, Item item){
        items[y * slot_dimensions.x + x] = item;
    }

    public void add_item_to_hotbar(int x, Item item){
        items[slot_dimensions.x * slot_dimensions.y + x] = item;
    }
}
