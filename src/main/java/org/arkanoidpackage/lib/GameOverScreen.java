package org.arkanoidpackage.lib;

public abstract class GameOverScreen implements GameEntity {
    protected boolean gameWon;
    protected static final String keysText = "[R]\n[M]\n[E]";
    protected static final String optionsText = "Reset\nMain Menu\nExit";

    public boolean isGameWon() {
        return this.gameWon;
    }

    @Override
    abstract public void update();
    @Override
    abstract public void render();
}
