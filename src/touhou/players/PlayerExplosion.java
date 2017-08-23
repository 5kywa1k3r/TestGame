package touhou.players;

import bases.GameObject;
import bases.renderers.Animation;
import tklibs.SpriteUtils;

import java.awt.*;

public class PlayerExplosion extends GameObject {
    private Animation animation;
    public PlayerExplosion() {
        super();
        animation = new Animation(5,true,
                SpriteUtils.loadImage("assets/images/players/explosions/0.png"),
                SpriteUtils.loadImage("assets/images/players/explosions/1.png"),
                SpriteUtils.loadImage("assets/images/players/explosions/2.png"),
                SpriteUtils.loadImage("assets/images/players/explosions/3.png"),
                SpriteUtils.loadImage("assets/images/players/explosions/4.png"),
                SpriteUtils.loadImage("assets/images/players/explosions/5.png"),
                SpriteUtils.loadImage("assets/images/players/explosions/6.png"),
                SpriteUtils.loadImage("assets/images/players/explosions/7.png")
        );
        renderer = animation;
    }

    @Override
    public void reset() {
        super.reset();
        animation.reset();
    }

    @Override
    public void render(Graphics2D g2d) {
        super.render(g2d);
        if (animation.isStopped()){
            this.isActive = false;
        }
    }
}
