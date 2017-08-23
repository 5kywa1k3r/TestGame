package touhou.settings;

import bases.FrameCounter;
import tklibs.SpriteUtils;

import java.awt.image.BufferedImage;
import java.util.List;

public class Level1Settings {
    public static final int startStage = 909;
    public static final int mediumStage = 2100;
    public static final int bossStage = 3109;
    public static boolean bossAppear = false;
    public static boolean bossUsingSkill = true;
    public static boolean bossCloneAlive = false;
    public static int bossHealth = 300;
    public static int realBoss = 0;
    public static boolean clearStage1 = true;
    public static boolean clearStage2 = false;
    public static int bossBehavior = 0;
    public static FrameCounter bossR = new FrameCounter(170);
}

