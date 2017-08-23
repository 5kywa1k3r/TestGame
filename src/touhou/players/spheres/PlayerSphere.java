package touhou.players.spheres;

import bases.FrameCounter;
import bases.GameObject;
import bases.Vector2D;
import bases.physics.BoxCollider;
import bases.physics.PhysicsBody;
import bases.pools.GameObjectPool;
import bases.renderers.Animation;
import tklibs.SpriteUtils;
import touhou.players.Player;
import touhou.players.PlayerSpell;

/**
 * Created by huynq on 8/16/17.
 */
public class PlayerSphere extends GameObject implements PhysicsBody{
    Animation animation;
    private BoxCollider boxCollider;
    private double startPoint;
    private FrameCounter coolDownCounter;

    public PlayerSphere() {
        super();
        boxCollider = new BoxCollider(20,20);
        coolDownCounter = new FrameCounter(30);
        children.add(boxCollider);
        this.animation = new Animation(
                10,
                false,
                SpriteUtils.loadImage("assets/images/sphere/0.png"),
                SpriteUtils.loadImage("assets/images/sphere/1.png"),
                SpriteUtils.loadImage("assets/images/sphere/2.png"),
                SpriteUtils.loadImage("assets/images/sphere/3.png")
        );
        this.renderer = animation;
    }

    @Override
    public void run(Vector2D parrentPosition){
        super.run(parrentPosition);
//        this.position.set((float) Math.cos(Math.toDegrees(startPoint)) * 50, (float)  Math.sin(Math.toDegrees(startPoint)) * 50);
//        startPoint += 0.004;
//        counterBullet();
        shoot();
    }

    private void shoot() {
        if (coolDownCounter.run()){
            PlayerSpell playerSpell = GameObjectPool.recycle(PlayerSpell.class);
            playerSpell.getPosition().set(this.screenPosition.add(0,-30));
            playerSpell.setDrX(0);
            playerSpell.getStatus().setDamage((float) 0.1);
            playerSpell.isChasing = true;
            playerSpell.setRenderer(new Animation(3,false,
                    SpriteUtils.loadImage("assets/images/sphere-bullets/0.png"),
                    SpriteUtils.loadImage("assets/images/sphere-bullets/1.png"),
                    SpriteUtils.loadImage("assets/images/sphere-bullets/2.png"),
                    SpriteUtils.loadImage("assets/images/sphere-bullets/3.png")
            ));
            coolDownCounter.reset();
        }
    }

//    private void counterBullet() {
//
//    }

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
