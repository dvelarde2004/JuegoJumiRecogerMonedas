package engine.view.hud.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import engine.view.hud.core.DataHUD;

/**
 * GameHUD
 * -------
 * HUD personalizado para el juego de recolección de monedas.
 * Muestra:
 * - Vida (barra de 4 segmentos)
 * - Monedas recogidas (COINS: X/30)
 * - Asteroides destruidos
 * - Tiempo restante (grande en la parte superior)
 */
public class GameHUD extends DataHUD {

    private Font timerFont = new Font("Monospaced", Font.BOLD, 48);
    private int asteroidsDestroyed = 0;
    private int coinsCollected = 0;
    private int timeRemainingSeconds = 300; // 5 minutos = 300 segundos
    private String gameState = "PLAYING"; // PLAYING, WINNER, GAMEOVER

    // region Constructors
    public GameHUD() {
        super(
                new Color(255, 215, 0, 255), // Title color (dorado)
                Color.RED, // Highlight color
                new Color(255, 255, 255, 200), // Label color (blanco semi-transparente)
                new Color(255, 255, 255, 255), // Data color
                50, 50, 40);

        // No añadimos items porque dibujamos todo manualmente
    }
    // endregion

    @Override
    public void draw(Graphics2D g, Object... values) {
        // Valores recibidos del Renderer
        double damage = values.length > 0 ? ((Number) values[0]).doubleValue() : 0.0;
        int asteroids = values.length > 1 ? ((Number) values[1]).intValue() : 0;
        int coins = values.length > 2 ? ((Number) values[2]).intValue() : 0;

        // CONVERSIÓN: damage es el daño recibido (0.0 = sin daño, 1.0 = muerto)
        // Pero nosotros queremos vida (1.0 = vida máxima, 0.0 = muerto)
        // Por lo tanto: vida = 1.0 - damage
        double health = 1.0 - damage;

        // Asegurar que health esté entre 0 y 1
        health = Math.max(0.0, Math.min(1.0, health));

        // 1) DIBUJAR LA BARRA DE VIDA
        int barX = 50;
        int barY = 50;
        int barWidth = 200;
        int barHeight = 20;

        // Fondo de la barra (gris oscuro)
        g.setColor(new Color(50, 50, 50));
        g.fillRect(barX, barY, barWidth, barHeight);

        // Borde de la barra (blanco)
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);

        // Relleno de la barra según vida
        int fillWidth = (int) (barWidth * health);
        if (fillWidth > 0) {
            // Elegir color según porcentaje de vida
            if (health > 0.66) {
                g.setColor(Color.GREEN);
            } else if (health > 0.33) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.RED);
            }
            g.fillRect(barX + 1, barY + 1, fillWidth - 1, barHeight - 2);
        }

        // Texto "HP" y porcentaje
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.drawString("HP", barX - 30, barY + 15);

        // Mostrar porcentaje de vida al lado de la barra
        String healthPercent = String.format("%d%%", (int)(health * 100));
        g.drawString(healthPercent, barX + barWidth + 10, barY + 15);

        // 2) RELOJ
        g.setFont(timerFont);
        g.setColor(Color.CYAN);

        int minutes = this.timeRemainingSeconds / 60;
        int seconds = this.timeRemainingSeconds % 60;
        String timeText = String.format("%02d:%02d", minutes, seconds);

        int screenWidth = 1920;
        int timerX = (screenWidth / 2) - 100;
        int timerY = 80;
        g.drawString(timeText, timerX, timerY);

        // 3) CONTADOR DE MONEDAS
        g.setFont(new Font("Monospaced", Font.BOLD, 28));
        g.setColor(Color.YELLOW);
        String coinsText = String.format("COINS: %d/30", coins);
        g.drawString(coinsText, 50, 150);

        // 4) CONTADOR DE ASTEROIDES
        g.setFont(new Font("Monospaced", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        String asteroidsText = String.format("ASTEROIDS: %d", asteroids);
        g.drawString(asteroidsText, 50, 200);

        // 5) MENSAJES DE VICTORIA/DERROTA
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