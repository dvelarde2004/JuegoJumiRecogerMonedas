package engine.model.bodies.impl;

import engine.model.bodies.core.AbstractBody;
import engine.model.bodies.ports.BodyEventProcessor;
import engine.model.bodies.ports.BodyState;
import engine.model.bodies.ports.BodyType;
import engine.model.physics.ports.PhysicsEngine;
import engine.model.physics.ports.PhysicsValuesDTO;
import engine.utils.spatial.core.SpatialGrid;

/**
 * CoinBody
 * --------
 * Representa una moneda en el juego.
 * Las monedas rotan lentamente y son recogidas por el jugador al colisionar.
 */
public class CoinBody extends AbstractBody {

    // Constante para rotación lenta (grados por segundo)
    private static final double ROTATION_SPEED = 45.0; // 45 grados/segundo

    // region Constructors
    public CoinBody(
            BodyEventProcessor bodyEventProcessor,
            SpatialGrid spatialGrid,
            PhysicsEngine phyEngine,
            double maxLifeInSeconds,
            String emitterId) {

        super(bodyEventProcessor, spatialGrid, phyEngine,
                BodyType.COIN,
                maxLifeInSeconds, emitterId);
    }
    // endregion

    // *** PUBLICS ***

    @Override
    public synchronized void activate() {
        super.activate();
        this.setState(BodyState.ALIVE);

        // Configurar rotación constante y lenta
        this.getPhysicsEngine().setAngularSpeed(ROTATION_SPEED);
    }

    @Override
    public void onTick() {
        // Calcular nueva posición/física (incluye la rotación automática)
        PhysicsValuesDTO newPhyValues = this.getPhysicsEngine().calcNewPhysicsValues();

        // Actualizar grid espacial para detección de colisiones
        if (this.getSpatialGrid() != null) {
            double r = newPhyValues.size * 0.5;
            this.getSpatialGrid().upsert(
                    this.getBodyId(),
                    newPhyValues.posX - r, newPhyValues.posX + r,
                    newPhyValues.posY - r, newPhyValues.posY + r,
                    this.getScratchIdxs());
        }

        // Procesar eventos (principalmente colisiones con el jugador)
        this.processBodyEvents(this, newPhyValues, this.getPhysicsEngine().getPhysicsValues());
    }
}