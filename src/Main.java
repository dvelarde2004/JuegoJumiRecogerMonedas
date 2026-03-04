import engine.controller.impl.Controller;
import engine.controller.ports.ActionsGenerator;
import engine.model.impl.Model;
import engine.utils.helpers.DoubleVector;
import engine.view.core.View;
import engine.world.ports.WorldDefinition;
import engine.world.ports.WorldDefinitionProvider;
import gameworld.ProjectAssets;
import gamerules.CoinGameRules;  // ← REGLAS PERSONALIZADAS
import gamelevel.CoinLevelGenerator; // ← GENERADOR DE NIVEL PERSONALIZADO
import gameai.AIBasicSpawner;

public class Main {

	public static void main(String[] args) {

		// region Graphics configuration
		System.setProperty("sun.java2d.uiScale", "1.0");
		System.setProperty("sun.java2d.opengl", "true");
		System.setProperty("sun.java2d.d3d", "false"); // OpenGL
		// endregion

		// region Dimensions and limits
		// Debido a un problema conocido con BufferStrategy cuando
		// el tamaño del Canvas es mayor que la pantalla, BufferStrategy falla (ventana en blanco)
		// en ese caso el motor lanzará un error y saldrá.
		//
		// => **********************************************************
		// => *** Mantén viewDimension más pequeño que el tamaño real de la pantalla ***
		// => *** o... no establezcas viewDimension                         ***
		// => **********************************************************
		DoubleVector viewDimension = new DoubleVector(1920, 1080);
		DoubleVector worldDimension = new DoubleVector(5000, 5000);
		// endregion

		int maxBodies = 1000;
		int maxAsteroidCreationDelay = 3000; // 3 segundos entre asteroides

		ProjectAssets projectAssets = new ProjectAssets();

		// *** REGLAS DEL JUEGO PERSONALIZADAS ***
		// Usamos CoinGameRules que maneja:
		// - Colisión nave-asteroide: -25% vida
		// - Colisión nave-moneda: +1 moneda
		// - Detección de victoria (30 monedas) y derrota (vida 0)
		ActionsGenerator gameRules = new CoinGameRules();

		// *** WORLD DEFINITION PROVIDER ***
		WorldDefinitionProvider worldProv = new gameworld.RandomWorldDefinitionProvider(
				worldDimension, projectAssets);

		// *** CORE ENGINE ***
		Controller controller = new Controller(
				worldDimension, viewDimension, maxBodies,
				new View(), new Model(worldDimension, maxBodies),
				gameRules);

		controller.activate();

		// *** SCENE ***
		WorldDefinition worldDef = worldProv.provide();

		// *** LEVEL GENERATOR PERSONALIZADO ***
		// CoinLevelGenerator coloca 30 monedas en posiciones aleatorias
		new CoinLevelGenerator(controller, worldDef);

		// *** AI GENERATOR ***
		// Genera asteroides continuamente durante la partida
		new AIBasicSpawner(controller, worldDef, maxAsteroidCreationDelay).activate();

		System.out.println("=== JUEGO INICIADO ===");
		System.out.println("Objetivo: Recoge 30 monedas en 5 minutos");
		System.out.println("Vida: 4 golpes (25% por asteroide)");
		System.out.println("¡Buena suerte, piloto!");
	}
}