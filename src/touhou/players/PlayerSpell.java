package touhou.players;

import bases.GameObject;
import bases.physics.BoxCollider;
import bases.physics.Physics;
import bases.physics.PhysicsBody;
import bases.pools.GameObjectPool;
import bases.status.Status;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ExplicitGroup;
import tklibs.SpriteUtils;
import bases.Vector2D;
import bases.renderers.ImageRenderer;
import touhou.enemies.Enemy;
import touhou.enemies.EnemyExplosion;
import touhou.settings.Settings;

import java.util.Set;

/**
 * Created by huynq on 8/2/17.
 */
public class PlayerSpell extends GameObject implements PhysicsBody {

    private BoxCollider boxCollider;
    private Status status;
    private float drX, drY;
    public boolean isChasing;
    private Enemy target = null;

    public PlayerSpell() {
        super();
        status = new Status(1,1);
        drX = 0; drY = -10;
        this.renderer = new ImageRenderer(SpriteUtils.loadImage(
                "assets/images/player-spells/a/1.png"
        ));
        isChasing = false;
        this.boxCollider = new BoxCollider(20, 20);
        this.children.add(boxCollider);
    }

    @Override
    public void reset() {
        super.reset();
        status.setHealth(1);
        status.setDamage(1);
        isChasing = false;
        drX = 0; drY = -10;
        target = null;
    }

    public void run(Vector2D parentPosition) {
        super.run(parentPosition);
        if (isChasing()){
            if (target == null) {
                Enemy enemy = Physics.findObject(Enemy.class);
                if (enemy != null) {
                    target = enemy;
                }
            } else
                if (!target.isActive()) {
                Enemy enemy = Physics.findObject(Enemy.class);
                if (enemy != null) {
                    target = enemy;
                }
            }
            if (target != null) {
                float dx = target.getPosition().x - this.getPosition().x;
                float dy = target.getPosition().y - this.getPosition().y;
                float tmp = 8/Math.abs(dy);
                dy *= tmp;
                dx *= tmp;
                drX = dx;
                drY = dy;
            }
        }
        position.addUp(drX, drY);
        hitEnemy();
        deactiveIfNeeded();
    }

    private void deactiveIfNeeded() {
        if ((this.screenPosition.y < 0) || (this.screenPosition.y > Settings.instance.getGamePlayHeight())
                || this.screenPosition.x < 0 || (this.screenPosition.x > Settings.instance.getGamePlayWidth()) || status.getHealth() < 0) {
            this.isActive = false;
        }
    }

    private void hitEnemy() {

        Enemy enemy = Physics.collideWith(this.boxCollider, Enemy.class);

        if (enemy != null) {
            enemy.getStatus().setHealth(enemy.getStatus().getHealth() - status.getDamage());
            this.status.setHealth(this.status.getHealth() - 1);
            EnemyExplosion enemyExplosion = GameObjectPool.recycle(EnemyExplosion.class);
            enemyExplosion.getPosition().set(enemy.getPosition());
            GameObject.add(enemyExplosion);

        }
    }

    @Override
    public BoxCollider getBoxCollider() {
        return boxCollider;
    }

    public float getDrX() {
        return drX;
    }

    public void setDrX(int drX) {
        this.drX = drX;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isChasing() {
        return isChasing;
    }

    public void setChasing(boolean chasing) {
        isChasing = chasing;
    }
}
