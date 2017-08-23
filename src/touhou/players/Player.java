package touhou.players;

import bases.GameObject;
import bases.Vector2D;
import bases.physics.BoxCollider;
import bases.physics.Physics;
import bases.physics.PhysicsBody;
import bases.pools.GameObjectPool;
import bases.status.Status;
import javafx.scene.shape.Sphere;
import tklibs.SpriteUtils;
import bases.Constraints;
import bases.FrameCounter;
import bases.renderers.ImageRenderer;
import touhou.enemies.Enemy;
import touhou.enemies.EnemyExplosion;
import touhou.inputs.InputManager;
import touhou.players.spheres.PlayerSphere;
import touhou.scene.Background;
import touhou.settings.Level1Settings;
import touhou.settings.Settings;

import java.awt.*;
import java.util.Vector;

/**
 * Created by huynq on 8/2/17.
 */
public class Player extends GameObject implements PhysicsBody{
    private static final int SPEED = 5;
    private static final int INVISIBLETIME = 90;
    private InputManager inputManager;
    private Constraints constraints;
    private Vector2D velocity;
    private FrameCounter coolDownCounter;
    private boolean spellLock;
    private PlayerAnimator animator;
    private BoxCollider boxCollider;
    public Status status;
    private boolean invisible;
    private int invisibleTime;
    private boolean hasSphere = false;

    public Player() {
        super();
        hasSphere = false;
        status = new Status(10,3);
        status.setPlayerStatus(5,10,3,0);
        this.spellLock = false;
        this.invisibleTime = 0;
        this.animator = new PlayerAnimator();
        this.renderer = animator;
        this.coolDownCounter = new FrameCounter(10);
        this.velocity = new Vector2D();
        this.boxCollider = new BoxCollider(12,12);
        children.add(boxCollider);
    }

    @Override
    public void reset() {
        super.reset();
        hasSphere = false;
        status.setPlayerStatus(5,10,3,0);
        this.invisibleTime = 0;
    }

    private void addSpheres() {
        PlayerSphere leftSphere = new PlayerSphere();
        leftSphere.getPosition().set(-25, 0);
        leftSphere.setStartPoint(180);
        PlayerSphere rightSphere = new PlayerSphere();
        rightSphere.getPosition().set(25, 0);
        rightSphere.setReverse(true);
        rightSphere.setStartPoint(0);
        this.children.add(leftSphere);
        this.children.add(rightSphere);
    }

    public void setContraints(Constraints contraints) {
        this.constraints = contraints;
    }

    public void run(Vector2D parentPostion) {
        if (status.getPower() == 5){
            if (!hasSphere)
                addSpheres();
            hasSphere = true;
        }

        if (Background.getMapPosition().y == Level1Settings.startStage + 1) status.setPower(status.getPower() + 1);
        if (Background.getMapPosition().y == Level1Settings.mediumStage + 1) status.setPower(status.getPower() + 1);
        super.run(parentPostion);

        velocity.set(0,0);

        if (inputManager.upPressed)
            velocity.addUp(0,-SPEED);

        if (inputManager.downPressed)
            velocity.addUp(0,SPEED);

        if (inputManager.leftPressed)
            velocity.addUp(-SPEED,0);

        if (inputManager.rightPressed)
            velocity.addUp(SPEED,0);
        this.position.addUp(velocity);

        if (constraints != null) {
            constraints.make(position);
        }

        if (invisibleTime > 0){ --invisibleTime; }
        else{
            invisibleTime = 0;
            setInvisibleFalse();
            hitEnemy();
        }

        animator.update(this);

        castSpell();
        deactiveIfNeeded();
    }

    private void deactiveIfNeeded() {
        if ((this.screenPosition.y < 0) || (this.screenPosition.y > Settings.instance.getGamePlayHeight())
                || this.screenPosition.x < 0 || (this.screenPosition.x > Settings.instance.getGamePlayWidth()) || status.getHealth() < 0) {
            this.isActive = false;
            PlayerExplosion playerExplosion = GameObjectPool.recycle(PlayerExplosion.class);
            playerExplosion.getPosition().set(this.position);
        }
    }

    private void hitEnemy() {
        Enemy enemy = Physics.collideWith(boxCollider, Enemy.class);
        if (enemy != null){
            enemy.getStatus().setHealth(enemy.getStatus().getHealth() - status.getDamage());
            this.status.setHealth( this.status.getHealth() - enemy.getStatus().getDamage() );
            this.status.setPower(Math.max (0,this.status.getPower() - 1));
            EnemyExplosion enemyExplosion = GameObjectPool.recycle(EnemyExplosion.class);
            enemyExplosion.getPosition().set(enemy.getPosition());
            GameObject.add(enemyExplosion);
            setInvisibleTrue();
        }
    }

    private void castSpell() {
        if (inputManager.xPressed && !spellLock) {
            switch ((int) status.getPower()){
                case 0:
                    PlayerSpell newSpell = GameObjectPool.recycle(PlayerSpell.class);
                    newSpell.getPosition().set(this.position.add(0, -30));
                    newSpell.setRenderer(new ImageRenderer(SpriteUtils.loadImage("assets/images/player-spells/a/1.png")));
                    break;
                case 1:
                    create1PowerSpell();
                    break;
                case 2:
                    create2PowerSpell();
                    break;
                case 3:
                    create2PowerSpell();
                    newSpell = GameObjectPool.recycle(PlayerSpell.class);
                    newSpell.getPosition().set(this.position.add(0, -30));
                    newSpell.setDrX(0);
                    newSpell.setRenderer(new ImageRenderer(SpriteUtils.loadImage("assets/images/player-spells/a/purple.png")));
                    break;
                default:
                    create2PowerSpell();
                    createMagicPowerSpell();
                    break;
            }
            spellLock = true;
            coolDownCounter.reset();
        }

        unlockSpell();
    }

    private void create2PowerSpell() {
        PlayerSpell newSpell = GameObjectPool.recycle(PlayerSpell.class);
        newSpell.getPosition().set(this.position.add(0, -30));
        newSpell.setDrX(0);
        newSpell.setRenderer(new ImageRenderer(SpriteUtils.loadImage("assets/images/player-spells/a/1.png")));
        newSpell = GameObjectPool.recycle(PlayerSpell.class);
        newSpell.getPosition().set(this.position.add(-20, -30));
        newSpell.setDrX(-2);
        newSpell.setRenderer(new ImageRenderer(SpriteUtils.loadImage("assets/images/player-spells/a/0.png")));
        newSpell = GameObjectPool.recycle(PlayerSpell.class);
        newSpell.getPosition().set(this.position.add(20, -30));
        newSpell.setDrX(2);
        newSpell.setRenderer(new ImageRenderer(SpriteUtils.loadImage("assets/images/player-spells/a/2.png")));
    }
    private void createMagicPowerSpell() {
        PlayerSpell newSpell = GameObjectPool.recycle(PlayerSpell.class);
        newSpell.getPosition().set(this.position.add(0, -30));
        newSpell.setDrX(0);
        newSpell.setRenderer(new ImageRenderer(SpriteUtils.loadImage("assets/images/player-spells/a/purple.png")));
        newSpell = GameObjectPool.recycle(PlayerSpell.class);
        newSpell.getPosition().set(this.position.add(-20, -30));
        newSpell.setDrX(-2);
        newSpell.setRenderer(new ImageRenderer(SpriteUtils.loadImage("assets/images/player-spells/a/purple.png")));
        newSpell = GameObjectPool.recycle(PlayerSpell.class);
        newSpell.getPosition().set(this.position.add(20, -30));
        newSpell.setDrX(2);
        newSpell.setRenderer(new ImageRenderer(SpriteUtils.loadImage("assets/images/player-spells/a/purple.png")));
    }
    private void create1PowerSpell() {
        PlayerSpell newSpell = GameObjectPool.recycle(PlayerSpell.class);
        newSpell.getPosition().set(this.position.add(20, -30));
        newSpell.setRenderer(new ImageRenderer(SpriteUtils.loadImage("assets/images/player-spells/a/1.png")));
        newSpell = GameObjectPool.recycle(PlayerSpell.class);
        newSpell.getPosition().set(this.position.add(-20, -30));
        newSpell.setRenderer(new ImageRenderer(SpriteUtils.loadImage("assets/images/player-spells/a/1.png")));
    }

    private void unlockSpell() {
        if (spellLock) {
            if (coolDownCounter.run()) {
                spellLock = false;
            }
        }
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    @Override
    public void render(Graphics2D g2d) {
        statusDraw(g2d);
        if (invisibleTime % 10 != 0) return;
        super.render(g2d);

    }

    private void statusDraw(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("TimesRoman", Font.PLAIN,20));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Player health: ",400,100);
//        if (status.getMana() > 0) {
//            g2d.drawString("Player mana: ", 400, 120);
//        }
//        else
//            g2d.drawString("Charging: ",400,120);
        //g2d.drawRect(400,100,  (health * 10) , 30);
        g2d.setColor(Color.RED);
        g2d.fillRect(550,85, (int) (status.getHealth() * 30),15);
//        g2d.setColor(Color.BLUE);
//        g2d.fillRect(550,105, (int) (status.getMana()* 30),15);
        for (int i=0; i< status.getPower(); ++i){
            g2d.drawImage(SpriteUtils.loadImage("assets/images/items/power-up-blue.png"),400 + i * 20,200,null);
        }
    }


    @Override
    public BoxCollider getBoxCollider() {
        return this.boxCollider;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisibleTrue() {
        this.invisible = true;
        invisibleTime = INVISIBLETIME;
    }

    public void setInvisibleFalse() {
        this.invisible = false;
    }
}
