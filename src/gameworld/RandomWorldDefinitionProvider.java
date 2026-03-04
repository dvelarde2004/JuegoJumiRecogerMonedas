package gameworld;

import engine.assets.ports.AssetType;
import engine.model.bodies.ports.BodyType;
import engine.utils.helpers.DoubleVector;
import engine.world.core.AbstractWorldDefinitionProvider;

public final class RandomWorldDefinitionProvider extends AbstractWorldDefinitionProvider {

	// *** CONSTRUCTORS ***

	public RandomWorldDefinitionProvider(DoubleVector worldDimension, ProjectAssets assets) {
		super(worldDimension, assets);
	}

	// *** PROTECTED (alphabetical order) ***

	@Override
	protected void define() {
		double density = 100d;

		// region Background
		this.setBackgroundStatic("back_12");
		// endregion

		// region Decoration - Reducidas para el mundo pequeño
		this.addDecoratorAnywhereRandomAsset(5, AssetType.STARS, density, 100, 200);
		this.addDecorator("cosmic_portal_01", 300, 1100, 200);

		this.addDecorator("stardust_01", 300, 1300, 400, -20, 1);
		this.addDecorator("stars_07", 2500, 2500, 800, 0, 1); // Reducido
		this.addDecoratorAnywhereRandomAsset(3, AssetType.GALAXY, density, 80, 150);
		this.addDecoratorAnywhereRandomAsset(5, AssetType.HALO, density, 50, 100);
		// endregion

		// region Gravity bodies => Static bodies - Posiciones ajustadas
		this.addGravityBody("planet_04", 1500, 1500, 400); // Reducido
		this.addGravityBody("sun_02", 4000, 800, 600); // Reducido
		this.addGravityBody("moon_05", 3000, 3000, 400); // Reducido
		this.addGravityBody("lab_01", 2000, 3500, 200);
		this.addGravityBody("black_hole_02", 2500, 1500, 150);
		this.addGravityBody("black_hole_01", 4000, 4000, 250);

		this.addGravityBodyAnywhereRandomAsset(3, AssetType.PLANET, density, 80, 150);
		this.addGravityBodyAnywhereRandomAsset(3, AssetType.MOON, density, 60, 120);
		// endregion

		// region Dynamic bodies - Asteroides más pequeños y menos
		this.addAsteroidPrototypeAnywhereRandomAsset(
				8, AssetType.ASTEROID, // Reducido de 6 a 8 pero con menos asteroides en el mundo pequeño
				15, 40, // Tamaños reducidos
				20, 150,
				0, 30);
		// endregion

		// region Players - Posición más céntrica
		this.addSpaceshipRandomAsset(1, AssetType.SPACESHIP, density, 50, 55, 2500, 2500); // Centro
		this.addTrailEmitterCosmetic("stars_06", 100, BodyType.DECORATOR, 25);
		// endregion

		// region Weapons (addWeapon***)
		this.addWeaponPresetBulletRandomAsset(AssetType.BULLET);
		this.addWeaponPresetBurstRandomAsset(AssetType.BULLET);
		this.addWeaponPresetMineLauncherRandomAsset(AssetType.MINE);
		this.addWeaponPresetMissileLauncherRandomAsset(AssetType.MISSILE);
		// endregion
	}
}
