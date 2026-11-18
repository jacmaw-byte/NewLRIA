package org.game.player.client;

public interface TerrainView {
    boolean is_solid(int wx, int wy);
    /** False if the chunk containing this cell isn't generated/loaded yet. */
    boolean is_loaded(int wx, int wy);
}