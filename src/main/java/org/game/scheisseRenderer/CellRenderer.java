package org.game.scheisseRenderer;

import org.game.cell.Cell;
import org.game.chunkManager.Chunk;
import org.game.player.client.Camera;
import org.game.scheisseRenderer.mainRenderer.MainRenderer;
import org.game.server.client.Client;
import org.joml.Vector2i;

import static org.Constants.*;
public class CellRenderer {

    private Camera camera;
    private Client client;
    private Texture screen;

    public CellRenderer(Client client){
        this.client = client;
        this.screen = MainRenderer.get_instance().get_screen().get_texture();

        camera = client.get_player().get_camera();
    }

    public void render_cells(){
        int camera_top = (int)camera.get_position().y - GRID_HEIGHT / 2;
        int camera_bottom = (int)camera.get_position().y + GRID_HEIGHT / 2;
        int camera_left = (int)camera.get_position().x - GRID_WIDTH / 2;
        int camera_right = (int)camera.get_position().x + GRID_WIDTH / 2;

        for (Vector2i chunk_pos : camera.chunks_in_view){

            Chunk chunk = client.get_chunk_manager().request_chunk(chunk_pos);

            if (chunk == null)
                continue;

            // Find which parts of the chunk reside in the camera view
            int start_x = Math.max(camera_left - chunk.get_world_position().x, 0);
            int end_x = Math.min(camera_right - chunk.get_world_position().x, CHUNK_SIZE);

            int start_y = Math.max(camera_top - chunk.get_world_position().y, 0);
            int end_y = Math.min(camera_bottom - chunk.get_world_position().y, CHUNK_SIZE);


            int draw_x = chunk.get_world_position().x - (int)camera.get_position().x + GRID_WIDTH / 2;
            int draw_y = chunk.get_world_position().y - (int)camera.get_position().y + GRID_HEIGHT / 2;

            for (int y = start_y; y < end_y; y++) {
                for (int x = start_x; x < end_x; x++) {
                    int drawing_cell_x = draw_x + x;
                    int drawing_cell_y = draw_y + y;

                    Cell cell = chunk.get_cell(x, y);

                    screen.set_pixel(drawing_cell_x, drawing_cell_y, cell.get_colour());
                }
            }
        }
    }
}
