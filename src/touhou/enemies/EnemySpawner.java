package touhou.enemies;

import bases.FrameCounter;
import bases.GameObject;
import bases.Vector2D;
import bases.pools.GameObjectPool;
import bases.renderers.ImageRenderer;
import bases.renderers.Renderer;
import touhou.scene.Background;
import touhou.settings.Level1Settings;
import touhou.settings.Settings;

import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

/**
 * Created by huynq on 8/9/17.
 */
public class EnemySpawner extends GameObject {
    private FrameCounter spawnCounterStart;
    private FrameCounter spawnCounterMedium;
    private FrameCounter spawnCounterBoss;
    private FrameCounter spawnCounter;
    private Random random;
    private int mediumEnemyCount = 5;
    private int mediumEnemyCountPink = 1;

    public EnemySpawner() {
        spawnCounter = new FrameCounter(90);
        spawnCounterStart = new FrameCounter(10);
        spawnCounterMedium = new FrameCounter(10);
        spawnCounterBoss = new FrameCounter(140);
        random = new Random();
    }

    @Override
    public void run(Vector2D parrentPosition) {
        super.run(parrentPosition);
        int mapRolling = (int) Background.getMapPosition().y;
        switch (mapRolling) {
            case Level1Settings.startStage:
                break;
            case Level1Settings.mediumStage:
                if (spawnCounterMedium.run()) {
                    if (mediumEnemyCountPink > 0) {
                        mediumStageHandlePink();
                        --mediumEnemyCountPink;
                    }
                    if (mediumEnemyCount > 0) {
                        mediumStageHandleBlue();
                        mediumEnemyCount--;
                    }
                    spawnCounterMedium.reset();
                }
                break;
            case Level1Settings.startStage - 5:
                startStageHandle();
                break;
            case Level1Settings.bossStage:
                bossHandle();
                //TODO: CREATE BOSS SKILL
                if (spawnCounterBoss.run()) {
                    spawnCounterBoss.reset();
                    Enemy enemy = GameObjectPool.recycle(Enemy.class);
                    if (enemy.color == 1) enemy.getPosition().set(Settings.instance.getGamePlayWidth() / 2, 20);
                    else
                        enemy.getPosition().set(random.nextInt(Settings.instance.getGamePlayWidth() - 50) + 50, 20);
                    break;
                }
                break;
            default:
                if (spawnCounter.run()) {
                    spawnCounter.reset();
                    Enemy enemy = GameObjectPool.recycle(Enemy.class);
                    if (enemy.color == 1) enemy.getPosition().set(Settings.instance.getGamePlayWidth() / 2, 20);
                    else
                        enemy.getPosition().set(random.nextInt(Settings.instance.getGamePlayWidth() - 50) + 50, 20);
                    break;
                }
        }
    }

    private void mediumStageHandleBlue() {
        Enemy enemy = GameObjectPool.recycle(Enemy.class);
        enemy.getPosition().set(Settings.instance.getGamePlayWidth() / 2, 40);
        enemy.createBlue();
        enemy.status.setHealth(8);
        enemy.status.setDamage(3);
        enemy.markAsStage2 = true;
    }

    private void mediumStageHandlePink() {
        Enemy enemy = GameObjectPool.recycle(Enemy.class);
        enemy.getPosition().set(Settings.instance.getGamePlayWidth() / 2, Settings.instance.getGamePlayHeight() / 2);
        EnemyExplosion enemyExplosion = GameObjectPool.recycle(EnemyExplosion.class);
        enemyExplosion.getPosition().set(enemy.getPosition());
        enemy.createPink();
        enemy.markAsStage2 = true;
        enemy.status.setHealth(30);
    }

    private void startStageHandle() {
        for (int i = 1; i <= 8; ++i) {
            Enemy enemy = GameObjectPool.recycle(Enemy.class);
            enemy.getPosition().set(i * 40, i * 40);
            enemy.mark = true;
            enemy.markX = -5;
            enemy.markY = -2;
            enemy.createBlue();
            enemy.collDownCounter = new FrameCounter(3);
            enemy.status.setHealth(3);
        }
        for (int i = 1; i <= 8; ++i) {
            Enemy enemy = GameObjectPool.recycle(Enemy.class);
            enemy.getPosition().set(Settings.instance.getGamePlayWidth() - i * 40, i * 40);
            enemy.mark = true;
            enemy.markX = 5;
            enemy.markY = -2;
            enemy.collDownCounter = new FrameCounter(3);
            enemy.createBlue();
            enemy.status.setHealth(3);
        }
    }

    private void bossHandle() {
        if (!Level1Settings.bossAppear) {
            Enemy enemy = GameObjectPool.recycle(Enemy.class);
            enemy.createBlack();
            enemy.collDownCounter.setCountMax(10);
            enemy.collDownCounter.reset();
            enemy.getPosition().set(Settings.instance.getGamePlayWidth() / 2, Settings.instance.getGamePlayHeight() / 2);
            Level1Settings.bossAppear = true;
        }
    }
}