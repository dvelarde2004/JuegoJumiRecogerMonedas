import engine.controller.impl.Controller;
import engine.controller.ports.ActionsGenerator;
import engine.model.impl.Model;
import engine.utils.helpers.DoubleVector;
import engine.view.core.View;
import engine.world.ports.WorldDefinition;
import engine.world.ports.WorldDefinitionProvider;
import gameworld.ProjectAssets;
import gamerules.CoinGameRules;
import gamelevel.CoinLevelGenerator;
import gameai.AIBasicSpawner;

public class Main {

	public static void main(String[] args) {

		System.setProperty("sun.java2d.uiScale", "1.0");
		System.setProperty("sun.java2d.opengl", "true");
		System.setProperty("sun.java2d.d3d", "false");

		DoubleVector viewDimension = new DoubleVector(1920, 1080);
		DoubleVector worldDimension = new DoubleVector(5000, 5000);

		int maxBodies = 1000;
		int maxAsteroidCreationDelay = 3000;

		ProjectAssets projectAssets = new ProjectAssets();

		// NUEVO: Configurar el número de monedas necesarias para ganar
		CoinGameRules.setCoinsToWin(3); // ← CAMBIA ESTE VALOR PARA MODIFICAR LA DIFICULTAD
		System.out.println("=== CONFIGURACIÓN ===");
		System.out.println("Monedas necesarias para ganar: " + CoinGameRules.getCoinsToWin());

		ActionsGenerator gameRules = new CoinGameRules();

		WorldDefinitionProvider worldProv = new gameworld.RandomWorldDefinitionProvider(
				worldDimension, projectAssets);

		Controller controller = new Controller(
				worldDimension, viewDimension, maxBodies,
				new View(), new Model(worldDimension, maxBodies),
				gameRules);

		controller.activate();

		WorldDefinition worldDef = worldProv.provide();

		new CoinLevelGenerator(controller, worldDef);

		new AIBasicSpawner(controller, worldDef, maxAsteroidCreationDelay).activate();

		System.out.println("=== JUEGO INICIADO ===");
		System.out.println("Objetivo: Recoge " + CoinGameRules.getCoinsToWin() + " monedas en 5 minutos");
		System.out.println("Vida: 4 golpes (25% por asteroide)");
		System.out.println("¡Buena suerte, piloto!");
	}
}