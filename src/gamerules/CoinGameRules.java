package gamerules;

import java.util.List;

import engine.actions.ActionType;
import engine.actions.ActionDTO;
import engine.controller.ports.ActionsGenerator;
import engine.events.domain.ports.DomainEventType;
import engine.events.domain.ports.eventtype.CollisionEvent;
import engine.events.domain.ports.eventtype.DomainEvent;
import engine.events.domain.ports.eventtype.EmitEvent;
import engine.events.domain.ports.eventtype.LifeOver;
import engine.events.domain.ports.eventtype.LimitEvent;
import engine.model.bodies.ports.BodyType;

/**
 * CoinGameRules
 * -------------
 * Reglas específicas para el juego de recolección de monedas:
 * - Nave vs Asteroide → -25% vida (si no hay inmunidad)
 * - Nave vs Moneda → +1 moneda, la moneda desaparece
 * - Al llegar a X monedas → VICTORIA (X definido por setCoinsToWin)
 * - Al llegar a 0 vida o tiempo límite → DERROTA
 */
public class CoinGameRules implements ActionsGenerator {

    // Variables de estado del juego
    private int coinsCollected = 0;
    private int asteroidsDestroyed = 0;
    private double playerHealth = 1.0; // 1.0 = 100%, 0.75, 0.5, 0.25, 0.0
    private boolean gameOver = false;
    private boolean gameWon = false;

    // Constantes
    private static final double DAMAGE_PER_HIT = 0.25; // 25% de daño

    // Configuración de monedas
    private static int coinsToWin = 30; // Valor por defecto

    // NUEVO: Configuración de tiempo (en segundos)
    private static int timeLimitSeconds = 300; // 5 minutos por defecto

    // *** MÉTODOS DE CONFIGURACIÓN DE MONEDAS ***

    public static void setCoinsToWin(int coins) {
        if (coins <= 0) {
            throw new IllegalArgumentException("El número de monedas debe ser positivo");
        }
        coinsToWin = coins;
        System.out.println("CoinGameRules: Monedas para ganar cambiado a " + coins);
    }

    public static int getCoinsToWin() {
        return coinsToWin;
    }

    // *** NUEVOS MÉTODOS DE CONFIGURACIÓN DE TIEMPO ***

    /**
     * Establece el tiempo límite en segundos
     * @param seconds tiempo en segundos (ej: 300 = 5 minutos, 60 = 1 minuto)
     */
    public static void setTimeLimit(int seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("El tiempo límite debe ser positivo");
        }
        timeLimitSeconds = seconds;
        System.out.println("CoinGameRules: Tiempo límite cambiado a " + seconds + " segundos (" +
                (seconds / 60) + " minutos " + (seconds % 60) + " segundos)");
    }

    /**
     * Establece el tiempo límite en minutos
     * @param minutes tiempo en minutos
     */
    public static void setTimeLimitMinutes(int minutes) {
        setTimeLimit(minutes * 60);
    }

    public static int getTimeLimit() {
        return timeLimitSeconds;
    }

    public static String getTimeLimitFormatted() {
        int minutes = timeLimitSeconds / 60;
        int seconds = timeLimitSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // *** INTERFACE IMPLEMENTATIONS ***

    @Override
    public void provideActions(List<DomainEvent> domainEvents, List<ActionDTO> actions) {
        if (gameOver || gameWon) {
            // Si el juego terminó, no procesar más eventos
            return;
        }

        if (domainEvents != null) {
            for (DomainEvent event : domainEvents) {
                this.applyGameRules(event, actions);
            }
        }
    }

    // *** PRIVATE ***

    private void applyGameRules(DomainEvent event, List<ActionDTO> actions) {
        switch (event) {
            case CollisionEvent collisionEvent -> {
                this.resolveCollision(collisionEvent, actions);
            }

            case EmitEvent emitEvent -> {
                // Proyectiles (disparos) - cuando un proyectil impacta un asteroide
                if (emitEvent.type == DomainEventType.FIRE_REQUESTED) {
                    // El arma ya maneja el disparo
                }
            }

            case LifeOver lifeOver -> {
                // Si una entidad llega al final de su vida, muere
                actions.add(new ActionDTO(
                        lifeOver.primaryBodyRef.id(),
                        lifeOver.primaryBodyRef.type(),
                        ActionType.DIE,
                        event));
            }

            case LimitEvent limitEvent -> {
                // Cuando los cuerpos llegan al límite, rebotan
                ActionType action;
                switch (limitEvent.type) {
                    case REACHED_EAST_LIMIT:
                        action = ActionType.MOVE_REBOUND_IN_EAST;
                        break;
                    case REACHED_WEST_LIMIT:
                        action = ActionType.MOVE_REBOUND_IN_WEST;
                        break;
                    case REACHED_NORTH_LIMIT:
                        action = ActionType.MOVE_REBOUND_IN_NORTH;
                        break;
                    case REACHED_SOUTH_LIMIT:
                        action = ActionType.MOVE_REBOUND_IN_SOUTH;
                        break;
                    default:
                        action = ActionType.MOVE;
                }

                actions.add(new ActionDTO(
                        limitEvent.primaryBodyRef.id(),
                        limitEvent.primaryBodyRef.type(),
                        action,
                        event));
            }

            default -> {
                // Otros eventos se ignoran
            }
        }
    }

    private void resolveCollision(CollisionEvent event, List<ActionDTO> actions) {
        BodyType typeA = event.primaryBodyRef.type();
        BodyType typeB = event.secondaryBodyRef.type();

        // CASO 1: NAVE vs MONEDA
        if ((typeA == BodyType.PLAYER && typeB == BodyType.COIN) ||
                (typeA == BodyType.COIN && typeB == BodyType.PLAYER)) {

            String coinId = (typeA == BodyType.COIN) ?
                    event.primaryBodyRef.id() : event.secondaryBodyRef.id();

            String playerId = (typeA == BodyType.PLAYER) ?
                    event.primaryBodyRef.id() : event.secondaryBodyRef.id();

            actions.add(new ActionDTO(coinId, BodyType.COIN, ActionType.DIE, event));
            actions.add(new ActionDTO(playerId, BodyType.PLAYER, ActionType.COLLECT_COIN, event));

            this.coinsCollected++;
            System.out.println("DEBUG - Moneda recogida! Total reglas: " + this.coinsCollected + "/" + coinsToWin);

            if (this.coinsCollected >= coinsToWin) {
                this.gameWon = true;
                System.out.println("¡VICTORIA! Has recogido " + coinsToWin + " monedas");
            }

            return;
        }

        // CASO 2: NAVE vs ASTEROIDE
        if ((typeA == BodyType.PLAYER && typeB == BodyType.ASTEROID) ||
                (typeA == BodyType.ASTEROID && typeB == BodyType.PLAYER)) {

            if (event.payload.haveImmunity) {
                return;
            }

            this.playerHealth -= DAMAGE_PER_HIT;
            System.out.println("DEBUG - ¡Impacto! Vida restante: " + (this.playerHealth * 100) + "%");

            String asteroidId = (typeA == BodyType.ASTEROID) ?
                    event.primaryBodyRef.id() : event.secondaryBodyRef.id();

            actions.add(new ActionDTO(asteroidId, BodyType.ASTEROID, ActionType.DIE, event));
            this.asteroidsDestroyed++;

            if (this.playerHealth <= 0) {
                this.gameOver = true;
                System.out.println("GAME OVER - Nave destruida");
                actions.add(new ActionDTO(event.primaryBodyRef.id(), BodyType.PLAYER, ActionType.DIE, event));
            }

            return;
        }

        // CASO 3: PROYECTIL vs ASTEROIDE
        if ((typeA == BodyType.PROJECTILE && typeB == BodyType.ASTEROID) ||
                (typeA == BodyType.ASTEROID && typeB == BodyType.PROJECTILE)) {

            String asteroidId = (typeA == BodyType.ASTEROID) ?
                    event.primaryBodyRef.id() : event.secondaryBodyRef.id();
            String projectileId = (typeA == BodyType.PROJECTILE) ?
                    event.primaryBodyRef.id() : event.secondaryBodyRef.id();

            actions.add(new ActionDTO(asteroidId, BodyType.ASTEROID, ActionType.DIE, event));
            actions.add(new ActionDTO(projectileId, BodyType.PROJECTILE, ActionType.DIE, event));
            this.asteroidsDestroyed++;

            return;
        }
    }

    // *** GETTERS PARA EL HUD ***

    public int getCoinsCollected() {
        return this.coinsCollected;
    }

    public int getAsteroidsDestroyed() {
        return this.asteroidsDestroyed;
    }

    public double getPlayerHealth() {
        return Math.max(0, this.playerHealth);
    }

    public boolean isGameOver() {
        return this.gameOver;
    }

    public boolean isGameWon() {
        return this.gameWon;
    }
}