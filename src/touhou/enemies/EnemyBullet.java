package touhou.enemies;

import bases.GameObject;
import bases.Vector2D;
import bases.physics.BoxCollider;
import bases.physics.Physics;
import bases.physics.PhysicsBody;
import bases.pools.GameObjectPool;
import bases.renderers.Animation;
import bases.renderers.ImageRenderer;
import bases.status.Status;
import tklibs.SpriteUtils;
import touhou.players.Player;
import touhou.players.PlayerSpell;
import touhou.settings.Level1Settings;
import touhou.settings.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Set;

public class EnemyBullet extends GameObject implements PhysicsBody{
    BoxCollider boxCollider;
    Animation animation;
    private float drX, drY;
    Status status;
    public double startPoint = 0;


    public EnemyBullet() {
        super();
        status = new Status(1,1);
        createAnimation();
        startPoint = 0;
        boxCollider = new BoxCollider(20,20);
        this.children.add(boxCollider);
    }

    @Override
    public void reset() {
        super.reset();
        startPoint = 0;
        status.setHealth(1);
        status.setDamage(1);
    }

    private void createAnimation() {
        ArrayList <BufferedImage> images = new ArrayList<>();
        animation = new Animation(3,false,
                SpriteUtils.loadImage("assets/images/enemies/bullets/blue.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/yellow.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/cyan.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/pink.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/green.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/red.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/white.png")
        );
        renderer = animation;
    }

    @Override
    public void run(Vector2D parentPosition) {
        super.run(parentPosition);
        position.addUp(drX, drY * 5 / 4);
        hitPlayer();
        deactiveIfNeeded();
    }

    private void deactiveIfNeeded() {
        if (status.getHealth() <= 0 ||
                this.screenPosition.y < 0 || this.screenPosition.y > Settings.instance.getGamePlayHeight() ||
                this.screenPosition.x < 0 ||this.screenPosition.x > Settings.instance.getGamePlayWidth()){
            this.isActive = false;
        }
    }

    private void hitPlayer() {
        Player player = Physics.collideWith(this.boxCollider, Player.class);
        if (player != null && !player.isInvisible()){
            player.status.setHealth(player.status.getHealth() - this.status.getDamage());
            player.setInvisibleTrue();
            this.status.setHealth(this.status.getHealth() - player.status.getDamage());
            EnemyExplosion enemyExplosion = GameObjectPool.recycle(EnemyExplosion.class);
            enemyExplosion.getPosition().set(player.getPosition());
            GameObject.add(enemyExplosion);
        }
    }

    public float getDrX() {
        return drX;
    }

    public void setDrX(float drX) {
        this.drX = drX;
    }

    public float getDrY() {
        return drY;
    }

    public void setDrY(float drY) {
        this.drY = drY;
    }

    @Override
    public BoxCollider getBoxCollider() {
        return this.boxCollider;
    }
}
