package org.main;
import org.game.GameManager;

public class Main {
    public static void main(String[] args){
        GameManager.make_instance();

        GameManager.get_instance().start_game();
    }
}
