package touhou.scene;

import bases.GameObject;
import bases.Vector2D;
import bases.renderers.ImageRenderer;
import tklibs.SpriteUtils;
import touhou.enemies.Enemy;
import touhou.settings.Level1Settings;
import touhou.settings.Settings;

import java.awt.*;

public class Background extends GameObject{
    private static final float SPEED = 1;
    private static Vector2D mapPosition = new Vector2D();
    private ImageRenderer imageRenderer;


    public Background() {
        super();
        this.imageRenderer = new ImageRenderer(SpriteUtils.loadImage("assets/images/background/0.png"));
        this.imageRenderer.getAnchor().set(0,1);
        this.mapPosition.set(0, Settings.instance.getGamePlayHeight());
        this.position.set(0, Settings.instance.getGamePlayHeight());
        this.renderer = imageRenderer;
    }

    @Override
    public void run(Vector2D parentPosition) {
        if (Level1Settings.bossAppear) ++ Level1Settings.bossBehavior;
        super.run(parentPosition);
        switch ((int) mapPosition.y) {
            default:
                move();
                break;
            case Level1Settings.startStage:
                if (getEnemyNum() == 0 && Level1Settings.clearStage1)
                    move();
                break;
            case Level1Settings.mediumStage:
                if (getEnemyNum() == 0 && Level1Settings.clearStage2)
                    move();
                break;
            case Level1Settings.bossStage:
                break;
        }
    }

    private void move() {
        mapPosition.y += SPEED;
        if (mapPosition.y > imageRenderer.image.getHeight())
            mapPosition.y = imageRenderer.image.getHeight();
        position.set(mapPosition);
    }

    @Override
    public void render(Graphics2D g2d) {
        super.render(g2d);
    }

    public static Vector2D getMapPosition() {
        return Background.mapPosition;
    }

    public static void setMapPosition(Vector2D position) {
        Background.mapPosition= position;
    }

}
