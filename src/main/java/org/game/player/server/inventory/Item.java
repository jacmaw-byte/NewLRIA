package org.game.player.server.inventory;

import org.game.renderManager.image.Sprite;

public interface Item {
    Sprite get_sprite();
    boolean is_block();
    boolean is_item();
    String name();
}
