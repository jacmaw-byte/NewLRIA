package org.game.player.server.inventory.items;

import org.game.cell.Cell;
import org.game.cell.Cells;
import org.game.player.server.inventory.Item;
import org.game.renderManager.Texture;
import org.game.renderManager.image.Sprite;
import org.joml.Vector2f;

public class DirtItem implements Item {

    Sprite sprite;

    public DirtItem(){
        Texture texture = Cell.get_cell_texture(Cells.DIRT);

        sprite = new Sprite(texture, new Vector2f(), new Vector2f(40, 40));
    }

    @Override
    public Sprite get_sprite() {
        return sprite;
    }

    @Override
    public boolean is_block() {
        return true;
    }

    @Override
    public boolean is_item() {
        return false;
    }

    @Override
    public String name() {
        return "Dirt";
    }
}
