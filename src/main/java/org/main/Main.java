package org.main;
import org.gameManager.GameManager;
import org.gameManager.renderManager.RenderManager;

public class Main {
    public static void main(String[] args){
        GameManager.make_instance();

        GameManager.get_instance().start_game();
    }
}
