package touhou.settings;

import java.awt.*;

public class Settings {
    private int windowWidth;
    private int windowHeight;

    private int gamePlayWidth;
    private int gamePlayHeight;

    private Insets windowInsets;

    public static final Settings instance = new Settings();

    private Settings(int windowWidth, int windowHeight, int gamePlayWidth, int gamePlayHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.gamePlayWidth = gamePlayWidth;
        this.gamePlayHeight = gamePlayHeight;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getGamePlayWidth() {
        return gamePlayWidth;
    }

    public int getGamePlayHeight() {
        return gamePlayHeight;
    }

    private Settings() {
        this(1024,768,384,768);
    }

    public Insets getWindowInsets() {
        return windowInsets;
    }

    public void setWindowInsets(Insets windowInsets) {
        this.windowInsets = windowInsets;
    }
}
