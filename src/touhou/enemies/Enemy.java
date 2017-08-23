package touhou.enemies;

import bases.FrameCounter;
import bases.GameObject;
import bases.physics.BoxCollider;
import bases.physics.Physics;
import bases.physics.PhysicsBody;
import bases.pools.GameObjectPool;
import bases.renderers.Animation;
import bases.status.Status;
import tklibs.SpriteUtils;
import bases.Vector2D;
import bases.renderers.ImageRenderer;
import touhou.items.Items;
import touhou.players.Player;
import touhou.players.PlayerSpell;
import touhou.enemies.BossBullet;
import touhou.settings.Level1Settings;
import touhou.settings.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by huynq on 8/9/17.
 */
public class Enemy extends GameObject implements PhysicsBody {
    private static final float SPEED = 3;
    private BoxCollider boxCollider;
    Status status;
    private boolean bulletLock = false;
    protected int color;
    FrameCounter collDownCounter;
    private int allowFly;
    private FrameCounter collDownRun;
    public boolean mark = false;
    public float markX;
    public float markY;
    public boolean markAsStage2;
    private float drX = 0, drY = 0;
    private FrameCounter pinkCountStage2;
    private int bossBulletNumber = 200;
    private boolean lockBoss = false;

    public Enemy() {
        super();
        markAsStage2 = false;
        drX = 0;
        drY = 0;
        bossBulletNumber = 200;
        pinkCountStage2 = new FrameCounter(140);
        status = new Status(3,3);
        collDownRun = new FrameCounter(60);
        collDownCounter = new FrameCounter(12);
        this.boxCollider = new BoxCollider(20, 20);
        this.children.add(boxCollider);
        initEnemy();
    }

    private void initEnemy() {
        int typeEnemy;
        bossBulletNumber = 200;
        Random random = new Random();
        mark = false;
        collDownCounter = new FrameCounter(12);
        markAsStage2 = false;
        pinkCountStage2 = new FrameCounter(140);
        drX = 0;
        drY = 0;
        int res = random.nextInt(15);
        allowFly = 150;
        if (res == 0) {
            typeEnemy = 1;
        }
        else typeEnemy = 0;
        switch (typeEnemy){
            case 0:
                createBlue();
                break;
            case 1:
                createPink();
                break;
            case 2:
                createBlack();
                break;
        }
    }

    public void createBlue(){
        renderer = new Animation(10, false,
                SpriteUtils.loadImage("assets/images/enemies/level0/blue/0.png"),
                SpriteUtils.loadImage("assets/images/enemies/level0/blue/1.png"),
                SpriteUtils.loadImage("assets/images/enemies/level0/blue/2.png"),
                SpriteUtils.loadImage("assets/images/enemies/level0/blue/3.png")
        );
        color = 0;
        status.setHealth(5); status.setDamage(1);
    }

    public void createPink(){
        renderer = new Animation(10, false,
                SpriteUtils.loadImage("assets/images/enemies/level0/pink/0.png"),
                SpriteUtils.loadImage("assets/images/enemies/level0/pink/1.png"),
                SpriteUtils.loadImage("assets/images/enemies/level0/pink/2.png"),
                SpriteUtils.loadImage("assets/images/enemies/level0/pink/3.png")
        );
        color = 1;
        status.setHealth(10);
        status.setDamage(3);
    }

    public void createBlack(){
        renderer = new Animation(30, false,
                SpriteUtils.loadImage("assets/images/enemies/level0/black/0.png"),
                SpriteUtils.loadImage("assets/images/enemies/level0/black/1.png"),
                SpriteUtils.loadImage("assets/images/enemies/level0/black/2.png"),
                SpriteUtils.loadImage("assets/images/enemies/level0/black/8.png")
        );
        color = 2;
        status.setHealth(100);
        status.setDamage(5);
    }

    @Override
    public void reset() {
        super.reset();
        status.setHealth(3);
        status.setDamage(3);
        initEnemy();
    }

    public void run(Vector2D parentPosition) {
        super.run(parentPosition);
        if (position.y > 0) fly();
        shoot();
        deactiveIfNeeded();
    }

    private void deactiveIfNeeded() {
        if (this.screenPosition.y > Settings.instance.getGamePlayHeight()
                || this.screenPosition.x < 0 || (this.screenPosition.x > Settings.instance.getGamePlayWidth()) || status.getHealth() < 0) {
            this.isActive = false;

            if (color == 0){
                Random random = new Random();
                if (random.nextInt(20) == 0) {
                    Items item = GameObjectPool.recycle(Items.class);
                    item.createBlueItem();
                    item.getPosition().set(this.position);
                }
            }
            if (color == 1){
                if (markAsStage2) Level1Settings.clearStage2 = true;
                Items item = GameObjectPool.recycle(Items.class);
                item.createRedItem();
                item.getPosition().set(this.position);
            }
        }
    }

    private void shoot() {
        if (Level1Settings.bossAppear && color == 2) {
            if (bossBulletNumber > 0) {
                addBossBullet();
                --bossBulletNumber;
                System.out.println(bossBulletNumber);
                if (bossBulletNumber <= 0) {
                    bossBulletNumber = -200;
                    System.out.println("set -500");
                }
            }
            else {
                ++bossBulletNumber;
            }
        }
        if (!bulletLock) {
                if (markAsStage2) {
                    if (color == 1) {
                        switch (pinkCountStage2.getCount() / 7) {
                            case 0:
                                flowBulletDown();
                                flowBulletUp();
                                break;
                            case 1:
                                flowBulletLeft();
                                flowBulletRight();
                                break;
                            case 2:
                                flowBulletDown();
                                flowBulletUp();
                                flowBulletLeft();
                                flowBulletRight();
                                break;
                            default:
                                break;
                        }
                        if (pinkCountStage2.run()) pinkCountStage2.reset();
                    }
                } else {
                    if (mark) {
                        if (position.y < 25)
                            flowBulletMarkDown();
                        if (position.y > 763)
                            flowBulletMarkUp();
                    } else {
                        if (color == 0) {
                            if (position.y < 300 && position.y > 50)
                                flowBulletBlue();
                        }

                        if (color == 1) {
                            if (position.y >= 300 && allowFly > 0) {
                                flowBulletDown();
                                flowBulletRight();
                                flowBulletLeft();
                                flowBulletUp();
                            }
                        }
                    }
                    bulletLock = true;
                }
            }
        unlockBullet();
    }

    private void addBossBullet() {
        BossBullet bossBullet = new BossBullet();
        bossBullet.getPosition().set(-50, 0);
        bossBullet.setStartPoint(180);
        this.children.add(bossBullet);
        bossBullet = new BossBullet();
        bossBullet.getPosition().set(50, 0);
        bossBullet.setStartPoint(0);
        this.children.add(bossBullet);
    }

    private void flowBulletLeft() {
        createNewbullet(-20, -30, -5, 2);
        createNewbullet(-20, -15, -5, 1);
        createNewbullet(-20, 0, -5, 0);
        createNewbullet(-20, 15, -5, -1);
        createNewbullet(-20, 30, -5, -2);
    }

    private void flowBulletUp() {
        createNewbullet(-30, -20, 2, -5);
        createNewbullet(-15, -20, 1, -5);
        createNewbullet(0, -20, 0, -5);
        createNewbullet(15, -20, -1, -5);
        createNewbullet(30, -20, -2, -5);
    }
    private void flowBulletRight() {
        createNewbullet(20, -30, 5, 2);
        createNewbullet(20, -15, 5, 1);
        createNewbullet(20, 0, 5, 0);
        createNewbullet(20, 15, 5, -1);
        createNewbullet(20, 30, 5, -2);
    }

    private void flowBulletDown() {
        createNewbullet(-30, 20, 2, 5);
        createNewbullet(-15, 20, 1, 5);
        createNewbullet(0, 20, 0, 5);
        createNewbullet(15, 20, -1, 5);
        createNewbullet(30, 20, -2, 5);
    }

    private void flowBulletBlue() {
        createNewbullet(30, 20, 2, 5);
//        createNewbullet(15, 20, 1, 5);
        createNewbullet(0, 20, 0, 5);
//        createNewbullet(-15, 20, -1, 5);
        createNewbullet(-30, 20, -2, 5);
    }

    private void flowBulletMarkUp() {
//        createNewbullet(-30, -20, 2, -5);
        createNewbullet(-15, -20, 1, -5);
        createNewbullet(0, -20, 0, -5);
        createNewbullet(15, -20, -1, -5);
//        createNewbullet(30, -20, -2, -5);
    }
    private void flowBulletMarkDown() {
//        createNewbullet(-30, 20, 2, 5);
        createNewbullet(-15, 20, 1, 5);
        createNewbullet(0, 20, 0, 5);
        createNewbullet(15, 20, -1, 5);
//        createNewbullet(30, 20, -2, 5);
    }

    private void unlockBullet() {
        if (bulletLock && collDownCounter.run()){
            bulletLock = false;
            collDownCounter.reset();
        }
    }

    private void createNewbullet(float dx, float dy, float drX, float drY) {
        EnemyBullet newBullet = GameObjectPool.recycle(EnemyBullet.class);
        newBullet.setPosition(this.position.add(dx, dy));
        newBullet.setDrX(drX);
        newBullet.setDrY(drY);
    }

    private void fly() {
        if (markAsStage2){
            if (color == 0){
                Player target = Physics.findObject(Player.class);
                if (target != null) {
                    drX = target.getPosition().x - this.getPosition().x;
                    drY = target.getPosition().y - this.getPosition().y;
                    float tmp = 3 / Math.max(Math.abs(drY), Math.abs(drX));
                    drX *= tmp;
                    drY *= tmp;
                    position.addUp(drX, drY);
                }
            }
        }
        else {
            if (mark) {
                position.addUp(markX, markY);
                if (position.x <= 0) {
                    position.x = 0;
                    markX = -markX;
                }
                if (position.x >= Settings.instance.getGamePlayWidth()) {
                    position.x = Settings.instance.getGamePlayWidth();
                    markX = -markX;
                }
                if (position.y <= 20) {
                    position.y = 20;
                    markY = -markY;
                }
                if (position.y >= Settings.instance.getGamePlayHeight()) {
                    position.y = Settings.instance.getGamePlayHeight();
                    markY = -markY;
                }
            }
            else {
                if (color != 2) {
                    if (color == 1 && position.y >= 300) {
                        --allowFly;
                        if (allowFly <= 0) {
                            position.addUp(0, SPEED);
                        }
                    } else position.addUp(0, SPEED);
                }
            }
        }
    }

    public BoxCollider getBoxCollider() {
        return this.boxCollider;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getBossBulletNumber() {
        return bossBulletNumber;
    }

    public void setBossBulletNumber(int bossBulletNumber) {
        this.bossBulletNumber = bossBulletNumber;
    }
}
