package touhou.items;

import bases.GameObject;
import bases.Vector2D;
import bases.physics.BoxCollider;
import bases.physics.Physics;
import bases.physics.PhysicsBody;
import bases.pools.GameObjectPool;
import bases.renderers.Animation;
import bases.renderers.ImageRenderer;
import tklibs.SpriteUtils;
import touhou.enemies.EnemyExplosion;
import touhou.players.Player;

public class Items extends GameObject implements PhysicsBody{
    private static final float SPEED = 3;
    private BoxCollider boxCollider;
    private int color;

    public Items() {
        this.boxCollider = new BoxCollider(20,20);
        children.add(boxCollider);
    }

    public void createBlueItem(){
        renderer = new ImageRenderer(SpriteUtils.loadImage("assets/images/items/power-up-blue.png"));
        color = 0;
    }

    public void createRedItem(){
        renderer = new ImageRenderer(SpriteUtils.loadImage("assets/images/items/power-up-red.png"));
        color = 1;
    }

    @Override
    public void run(Vector2D parentPosition) {
        super.run(parentPosition);
        this.position.addUp(0,SPEED);
        hitPlayer();
    }

    private void hitPlayer() {
        Player player = Physics.collideWith(this.boxCollider, Player.class);
        if (player != null){
            if (color == 1)
                player.status.setHealth(Math.min(player.status.getHealth() + 2,10));
            if (color == 0) {
                player.status.setPower(Math.min(player.status.getPower() + 1, 5));
                System.out.println(player.status.getPower());
            }
            this.isActive = false;
        }
    }


    @Override
    public BoxCollider getBoxCollider() {
        return this.boxCollider;
    }
}
