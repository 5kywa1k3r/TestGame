package touhou.enemies;

import bases.FrameCounter;
import bases.GameObject;
import bases.Vector2D;
import bases.physics.BoxCollider;
import bases.physics.Physics;
import bases.physics.PhysicsBody;
import bases.pools.GameObjectPool;
import bases.renderers.Animation;
import bases.status.Status;
import tklibs.SpriteUtils;
import touhou.enemies.EnemyExplosion;
import touhou.players.Player;
import touhou.players.PlayerSpell;
import touhou.settings.Level1Settings;
import touhou.settings.Settings;

/**
 * Created by huynq on 8/16/17.
 */
public class BossBullet extends GameObject implements PhysicsBody{
    Animation animation;
    private BoxCollider boxCollider;
    private double startPoint;
    private FrameCounter coolDownCounter;
    private int R = 50;
    private Status status;

    public BossBullet() {
        super();
        status = new Status(1,1);
        boxCollider = new BoxCollider(20,20);
        coolDownCounter = new FrameCounter(2);
        children.add(boxCollider);
        this.animation = new Animation(
                10,
                false,
                SpriteUtils.loadImage("assets/images/enemies/bullets/blue.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/yellow.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/cyan.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/pink.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/green.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/red.png"),
                SpriteUtils.loadImage("assets/images/enemies/bullets/white.png")
        );
        this.renderer = animation;
    }

    @Override
    public void reset() {
        super.reset();
        status = new Status(1,1);
        R = 50;
    }

    @Override
    public void run(Vector2D parrentPosition){
        if (coolDownCounter.run()) {
            super.run(parrentPosition);
            this.position.set((float) Math.cos(Math.toDegrees(startPoint)) * R, (float) Math.sin(Math.toDegrees(startPoint)) * R);
            startPoint += 0.1;
            R += 1;
            coolDownCounter.reset();
        }
        deactiveIfNeeded();
        hitPlayer();
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

    private void deactiveIfNeeded() {
        if (status.getHealth() <= 0 ||
                this.screenPosition.y < 0 || this.screenPosition.y > Settings.instance.getGamePlayHeight() ||
                this.screenPosition.x < 0 ||this.screenPosition.x > Settings.instance.getGamePlayWidth()){
            this.isActive = false;
        }
    }

    public void setReverse(boolean reverse) {
        this.animation.setReverse(reverse);
    }

    @Override
    public BoxCollider getBoxCollider() {
        return this.boxCollider;
    }

    public double getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(double startPoint) {
        this.startPoint = startPoint;
    }
}
