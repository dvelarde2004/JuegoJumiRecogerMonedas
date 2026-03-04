package gamelevel;

import java.util.ArrayList;

import engine.controller.ports.WorldManager;
import engine.generators.AbstractLevelGenerator;
import engine.world.ports.DefEmitterDTO;
import engine.world.ports.DefItem;
import engine.world.ports.DefItemDTO;
import engine.world.ports.WorldDefinition;

/**
 * CoinLevelGenerator
 * ------------------
 * Genera el nivel inicial para el juego de las monedas:
 * - Coloca 30 monedas en posiciones aleatorias
 * - Añade decoración de fondo
 * - Posiciona al jugador
 */
public class CoinLevelGenerator extends AbstractLevelGenerator {

    private static final int COINS_TO_GENERATE = 30;

    // *** CONSTRUCTORS ***
    public CoinLevelGenerator(WorldManager worldManager, WorldDefinition worldDef) {
        super(worldManager, worldDef);
    }

    // *** PROTECTED ***

    @Override
    protected void createDecorators() {
        // Añadir decoración de estrellas y galaxias de fondo
        ArrayList<DefItem> decorators = this.getWorldDefinition().spaceDecorators;

        for (DefItem def : decorators) {
            DefItemDTO deco = this.defItemToDTO(def);
            this.addDecoratorIntoTheGame(deco);
        }
    }

    @Override
    protected void createStatics() {
        // Añadir cuerpos estáticos (planetas, etc.) si los hay
        ArrayList<DefItem> bodyDefs = this.getWorldDefinition().gravityBodies;

        for (DefItem def : bodyDefs) {
            DefItemDTO body = this.defItemToDTO(def);
            this.addStaticIntoTheGame(body);
        }
    }

    @Override
    protected void createPlayers() {
        // Crear el jugador
        WorldDefinition worldDef = this.getWorldDefinition();
        ArrayList<DefItem> shipDefs = worldDef.spaceships;
        ArrayList<DefEmitterDTO> weaponDefs = worldDef.weapons;
        ArrayList<DefEmitterDTO> trailDefs = worldDef.trailEmitters;

        for (DefItem def : shipDefs) {
            DefItemDTO body = this.defItemToDTO(def);
            this.addLocalPlayerIntoTheGame(body, weaponDefs, trailDefs);
        }
    }

    @Override
    protected void createDynamics() {
        // Crear las 30 monedas en posiciones aleatorias
        this.createCoins();

        // También podemos añadir algunos asteroides iniciales
        ArrayList<DefItem> asteroidDefs = this.getWorldDefinition().asteroids;
        for (int i = 0; i < 5; i++) {
            if (!asteroidDefs.isEmpty()) {
                DefItem def = asteroidDefs.get(i % asteroidDefs.size());
                DefItemDTO asteroid = this.defItemToDTO(def);
                this.addDynamicIntoTheGame(asteroid);
            }
        }
    }

    // *** PRIVATE ***

    // En gamelevel/CoinLevelGenerator.java, dentro del método createCoins()
    private void createCoins() {
        double worldWidth = this.getWorldDefinition().worldWidth;
        double worldHeight = this.getWorldDefinition().worldHeight;

        System.out.println("=== CREANDO " + COINS_TO_GENERATE + " MONEDAS ===");

        for (int i = 0; i < COINS_TO_GENERATE; i++) {
            // Generar posición aleatoria pero ahora en un mundo de 5000x5000
            double posX = this.randomDoubleBetween(200, worldWidth - 200);
            double posY = this.randomDoubleBetween(200, worldHeight - 200);
            double size = this.randomDoubleBetween(80, 120); // ← MÁS GRANDES (antes 30-50)
            double angle = this.randomDoubleBetween(0, 360);

            System.out.println("Creando moneda " + (i+1) + " en posición: (" +
                    String.format("%.0f", posX) + ", " +
                    String.format("%.0f", posY) + ") tamaño: " +
                    String.format("%.0f", size));

            this.getWorldManager().addCoin(
                    "coin_gold",
                    size,
                    posX, posY,
                    angle,
                    45 // angularSpeed (rotación lenta)
            );
        }

        System.out.println("=== MONEDAS CREADAS ===");
    }
}