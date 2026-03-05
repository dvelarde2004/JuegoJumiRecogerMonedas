package engine.view.hud.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import engine.view.hud.core.DataHUD;
import gamerules.CoinGameRules;

public class GameHUD extends DataHUD {

    private Font timerFont = new Font("Monospaced", Font.BOLD, 48);
    private int asteroidsDestroyed = 0;
    private int coinsCollected = 0;
    private int timeRemainingSeconds = 300;
    private String gameState = "PLAYING";
    private final int coinsToWin = CoinGameRules.getCoinsToWin();
    // NUEVO: Obtener el tiempo total para mostrarlo también
    private final int totalTimeSeconds = CoinGameRules.getTimeLimit();

    public GameHUD() {
        super(
                new Color(255, 215, 0, 255),
                Color.RED,
                new Color(255, 255, 255, 200),
                new Color(255, 255, 255, 255),
                50, 50, 40);
    }

    @Override
    public void draw(Graphics2D g, Object... values) {
        double damage = values.length > 0 ? ((Number) values[0]).doubleValue() : 0.0;
        int asteroids = values.length > 1 ? ((Number) values[1]).intValue() : 0;
        int coins = values.length > 2 ? ((Number) values[2]).intValue() : 0;

        double health = 1.0 - damage;
        health = Math.max(0.0, Math.min(1.0, health));

        // Barra de vida (código existente)
        int barX = 50;
        int barY = 50;
        int barWidth = 200;
        int barHeight = 20;

        g.setColor(new Color(50, 50, 50));
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);

        int fillWidth = (int) (barWidth * health);
        if (fillWidth > 0) {
            if (health > 0.66) {
                g.setColor(Color.GREEN);
            } else if (health > 0.33) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.RED);
            }
            g.fillRect(barX + 1, barY + 1, fillWidth - 1, barHeight - 2);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.drawString("HP", barX - 30, barY + 15);
        String healthPercent = String.format("%d%%", (int)(health * 100));
        g.drawString(healthPercent, barX + barWidth + 10, barY + 15);

        // RELOJ - AHORA MUESTRA EL TIEMPO REAL
        g.setFont(timerFont);
        g.setColor(Color.CYAN);

        int minutes = this.timeRemainingSeconds / 60;
        int seconds = this.timeRemainingSeconds % 60;
        String timeText = String.format("%02d:%02d", minutes, seconds);

        int screenWidth = 1920;
        int timerX = (screenWidth / 2) - 100;
        int timerY = 80;
        g.drawString(timeText, timerX, timerY);

        // Contador de monedas
        g.setFont(new Font("Monospaced", Font.BOLD, 28));
        g.setColor(Color.YELLOW);
        String coinsText = String.format("COINS: %d/%d", coins, coinsToWin);
        g.drawString(coinsText, 50, 150);

        // Contador de asteroides
        g.setFont(new Font("Monospaced", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        String asteroidsText = String.format("ASTEROIDS: %d", asteroids);
        g.drawString(asteroidsText, 50, 200);

        // Mensajes de victoria/derrota
        if ("WINNER".equals(this.gameState)) {
            g.setFont(new Font("Arial", Font.BOLD, 72));
            g.setColor(Color.GREEN);
            String winnerText = "WINNER!";
            int winnerX = (screenWidth / 2) - 150;
            int winnerY = 400;
            g.drawString(winnerText, winnerX, winnerY);

            g.setFont(new Font("Arial", Font.PLAIN, 36));
            g.setColor(Color.WHITE);
            g.drawString("Time: " + timeText, winnerX - 50, winnerY + 60);
            g.drawString("Asteroids: " + asteroids, winnerX - 50, winnerY + 110);

        } else if ("GAMEOVER".equals(this.gameState)) {
            g.setFont(new Font("Arial", Font.BOLD, 72));
            g.setColor(Color.RED);
            String gameOverText = "GAME OVER";
            int gameOverX = (screenWidth / 2) - 200;
            int gameOverY = 400;
            g.drawString(gameOverText, gameOverX, gameOverY);
        }
    }

    // *** SETTERS ***
    public void setTimeRemaining(int seconds) {
        this.timeRemainingSeconds = Math.max(0, seconds);
    }

    public void setAsteroidsDestroyed(int count) {
        this.asteroidsDestroyed = count;
    }

    public void setCoinsCollected(int count) {
        this.coinsCollected = count;
    }

    public void setGameState(String state) {
        this.gameState = state;
    }

    public int getTimeRemaining() {
        return this.timeRemainingSeconds;
    }
}