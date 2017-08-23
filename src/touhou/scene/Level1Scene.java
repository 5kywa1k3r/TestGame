package touhou.scene;

import bases.Constraints;
import bases.GameObject;
import tklibs.SpriteUtils;
import touhou.enemies.EnemySpawner;
import touhou.inputs.InputManager;
import touhou.players.Player;
import touhou.settings.Level1Settings;
import touhou.settings.Settings;

import java.awt.image.BufferedImage;

public class Level1Scene {
    private BufferedImage background;
    Player player = new Player();
    EnemySpawner enemySpawner = new EnemySpawner();

    public void init(){
        GameObject.add(new Background());
        addBackground();
        addPlayer();
        addEnemySpawn();
    }

    private void addBackground() {
        GameObject.add(new Background());
    }

    private void addPlayer() {
        player.setInputManager(InputManager.instance);
        player.setContraints(new Constraints(
                Settings.instance.getWindowInsets().top,
                Settings.instance.getWindowHeight(),
                Settings.instance.getWindowInsets().left,
                Settings.instance.getGamePlayWidth()));
        player.getPosition().set(Settings.instance.getGamePlayWidth() / 2, Settings.instance.getGamePlayHeight() * 2/3);
        GameObject.add(player);
    }

    private void addEnemySpawn(){GameObject.add(new EnemySpawner());}
}
