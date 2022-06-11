package me.daddychurchill.CityWorld.Plugins;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Biome;

import me.daddychurchill.CityWorld.CityWorldGenerator;
import me.daddychurchill.CityWorld.Plats.PlatLot;
import me.daddychurchill.CityWorld.Support.AbstractCachedYs;
import me.daddychurchill.CityWorld.Support.MinWorldHeight;
import me.daddychurchill.CityWorld.Support.Odds;
import me.daddychurchill.CityWorld.Support.SupportBlocks;

public abstract class OreProvider extends Provider {

	//	public final static int lavaFluidLevel = 24;
	public final static int lavaFieldLevel = 12;
	private final static double oreSprinkleOdds = Odds.oddsLessLikely;
	private final static double snowSprinkleOdds = Odds.oddsMoreLikely;

	List<Material> ore_types = new ArrayList<Material>();

	public Material surfaceMaterial;
	public Material subsurfaceMaterial;
	public Material stratumMaterial;
	public Material substratumMaterial;

	public Material fluidMaterial;
	public Material fluidFluidMaterial;
	public Material fluidSurfaceMaterial;
	public Material fluidSubsurfaceMaterial;
	public Material fluidFrozenMaterial;

	OreProvider(CityWorldGenerator generator) {
		super();

		surfaceMaterial = Material.GRASS_BLOCK;
		subsurfaceMaterial = Material.DIRT;
		stratumMaterial = Material.STONE;
		substratumMaterial = Material.BEDROCK;

		fluidMaterial = Material.WATER;
		fluidFluidMaterial = Material.WATER;
		fluidSurfaceMaterial = Material.SAND;
		fluidSubsurfaceMaterial = Material.GRAVEL;
		fluidFrozenMaterial = Material.PACKED_ICE;
	}

	// Based on work contributed by drew-bahrue
	// (https://github.com/echurchill/CityWorld/pull/2)
	public static OreProvider loadProvider(CityWorldGenerator generator) {

		OreProvider provider = null;

		// default to stock OreProvider
		if (provider == null) {

			switch (generator.worldStyle) {
			case ASTRAL:
				provider = new OreProvider_Astral(generator);
				break;
			case SNOWDUNES:
				provider = new OreProvider_SnowDunes(generator);
				break;
			case SANDDUNES:
				provider = new OreProvider_SandDunes(generator);
				break;
			case FLOODED:
			case FLOATING:
			case DESTROYED:
			case MAZE:
			case NATURE:
			case METRO:
			case SPARSE:
			case NORMAL:
				switch (generator.worldEnvironment) {
				case NETHER:
					provider = new OreProvider_Nether(generator);
					break;
				case THE_END:
					provider = new OreProvider_TheEnd(generator);
					break;
				case NORMAL:
					if (generator.getSettings().includeDecayedNature)
						provider = new OreProvider_Decayed(generator);
					else
						provider = new OreProvider_Normal(generator);
					break;
				}
			}
		}

		return provider;
	}

	public Biome remapBiome(Biome biome) {
		return biome;
	}

	public void sprinkleSnow(CityWorldGenerator generator, SupportBlocks chunk, Odds odds, int x1, int x2, int y,
			int z1, int z2) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				if (odds.playOdds(snowSprinkleOdds))
					chunk.setBlock(x, y, z, Material.SNOW_BLOCK);
			}
		}
	}

	public void dropSnow(CityWorldGenerator generator, SupportBlocks chunk, int x, int y, int z) {
		dropSnow(generator, chunk, x, y, z, 0);
	}

	public void dropSnow(CityWorldGenerator generator, SupportBlocks chunk, int x, int y, int z, double level) {
		y = chunk.findLastEmptyBelow(x, y + 1, z, y - 6);
		if (!chunk.isOfTypes(x, y - 1, z, Material.AIR, Material.SNOW))
			chunk.setBlock(x, y, z, Material.SNOW, level);
	}

	/**
	 * Populates the world with ores.
	 *
	 * original authors Nightgunner5, Markus Persson modified by simplex wildly
	 * modified by daddychurchill
	 */

	// WATER LAVA GRAV COAL IRON GOLD LAPIS REDST DIAM EMER
	private static final int[] ore_iterations = new int[] { 
			8, // WATER
			6, // LAVA
			40, // GRAV
			30, // COAL
			12, // IRON
			4, // GOLD
			2, // LAPIS
			4, // REDST
			2, // DIAM
			2, // COPPER
			15, // EMER
			30, // DEEPSLATE_COAL_ORE
			12, // DEEPSLATE_IRON_ORE
			4, // DEEPSLATE_GOLD_ORE
			2, // DEEPSLATE_LAPIS_ORE
			4, // DEEPSLATE_REDSTONE_ORE
			2, // DEEPSLATE_DIAMOND_ORE
			10, // DEEPSLATE_EMERALD_ORE
			15 , // DEEPSLATE_COPPER_ORE
			20, // CLAY
			1, // ANCIENT_DEBRIS
			3, // AMETHYST_BLOCK
			20, // STONE
			15 // DEEPSLATE
 			};
	
	private static final int[] ore_amountToDo = new int[] { 
			1, // WATER
			1, // LAVA
			12, // GRAV
			8, // COAL
			8, // IRON
			3, // GOLD
			3, // LAPIS
			10, // REDST
			3, // DIAM
			1, // EMER
			8, // COPPER
			8, 
			8,
			3,
			3, 
			10,
			3,
			1, 
			8 ,
			5,
			2, 
			3, 
			15, 
			20};
	
	private static final int[] ore_maxY = new int[] { 
			128, // WATER
			32, // LAVA
			111, // GRAV
			128, // COAL
			61, // IRON
			29, // GOLD
			25, // LAPIS
			16, // REDST
			15, // DIAM
			15, // EMER
			94, // COPPER
			-1, 
			-25, 
			-30, 
			-35, 
			-40, 
			-55, 
			-50, 
			-5, 
			5, 
			-10, 
			-10,
			0, 
			7};
	
	private static final int[] ore_minY = new int[] { 
			32, // WATER
			2, // LAVA
			40, // GRAV
			16, // COAL
			10, // IRON
			8, // GOLD
			8, // LAPIS
			6, // REDST
			2, // DIAM
			2, // EMER
			0, // COPPER
			-63, 
			-63, 
			-63, 
			-63, 
			-63, 
			-63, 
			-63, 
			-63, 
			-45, 
			-45, 
			-30, 
			-12, 
			0};
	
	private static final boolean[] ore_upper = new boolean[] { true, false, false, true, true, true, true, true, false, false, false, true, true, true, true, true, false, false, false , false, false, false, false, false};
	private static final boolean[] ore_liquid = new boolean[] { true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false , false, false, false, false, false};

	public void sprinkleOres(CityWorldGenerator generator, PlatLot lot, SupportBlocks chunk, AbstractCachedYs blockYs,
			Odds odds) {

		// do it... maybe!
		int oreCount = Math.min(ore_types.size(), ore_iterations.length);
		for (int typeNdx = 0; typeNdx < oreCount; typeNdx++) {
			sprinkleOre(generator, lot, chunk, blockYs, odds, ore_types.get(typeNdx), ore_maxY[typeNdx],
					ore_minY[typeNdx], ore_iterations[typeNdx], ore_amountToDo[typeNdx], ore_upper[typeNdx],
					ore_liquid[typeNdx]);
		}
	}

	private void sprinkleOre(CityWorldGenerator generator, PlatLot lot, SupportBlocks chunk, AbstractCachedYs blockYs,
			Odds odds, Material material, int maxY, int minY, int iterations, int amount, boolean mirror,
			boolean liquid) {
		
		if(Material.ANCIENT_DEBRIS == material || Material.AMETHYST_BLOCK == material) {
			if(odds.calcRandomRange(1, 5) != 1) {
				return;
			}
		}

		// do we do this one?
		if ((liquid && generator.getSettings().includeUndergroundFluids) || (!liquid && generator.getSettings().includeOres)) {
			//if (material != stratumMaterial || material != Material.DEEPSLATE) {

				// sprinkle it around!
				int range = maxY - minY;
				for (int iter = 0; iter < iterations; iter++) {
					int x = odds.getRandomInt(16);
					int y = odds.getRandomInt(range) + minY;
					int z = odds.getRandomInt(16);
					if (y < blockYs.getBlockY(x, z))
						growVein(generator, lot, chunk, blockYs, odds, x, y, z, amount, material);
					if (mirror) {
						y = (generator.seaLevel + generator.landRange) - minY - odds.getRandomInt(range);
						if (y < blockYs.getBlockY(x, z)) {
							growVein(generator, lot, chunk, blockYs, odds, x, y, z, amount, material);
						}
							
					}
				}
			//}
		}
	}

	private void growVein(CityWorldGenerator generator, PlatLot lot, SupportBlocks chunk, AbstractCachedYs blockYs,
			Odds odds, int originX, int originY, int originZ, int amountToDo, Material material) {
		int trysLeft = amountToDo * 2;
		int oresDone = 0;
		if (lot.isValidStrataY(generator, originX, originY, originZ)
				&& blockYs.getBlockY(originX, originZ) > originY + amountToDo / 4) {
			while (oresDone < amountToDo && trysLeft > 0) {

				// shimmy
				int x = chunk.clampXZ(originX + odds.getRandomInt(Math.max(1, amountToDo / 2)) - amountToDo / 4);
				int y = chunk.clampY(originY + odds.getRandomInt(Math.max(1, amountToDo / 4)) - amountToDo / 8);
				int z = chunk.clampXZ(originZ + odds.getRandomInt(Math.max(1, amountToDo / 2)) - amountToDo / 4);

				// ore it is
				oresDone += placeOre(generator, chunk, odds, x, y, z, amountToDo - oresDone, material);

				// one less try to try
				trysLeft--;
			}
		}
	}

	private int placeOre(CityWorldGenerator generator, SupportBlocks chunk, Odds odds, int centerX, int centerY,
			int centerZ, int oresToDo, Material material) {
		int count = 0;
		if (centerY > MinWorldHeight.getMinHeight() && centerY < chunk.height) {
			if (placeBlock(chunk, odds, centerX, centerY, centerZ, material)) {
				count++;
				if (count < oresToDo && centerX < 15
						&& placeBlock(chunk, odds, centerX + 1, centerY, centerZ, material))
					count++;
				if (count < oresToDo && centerX > 0 && placeBlock(chunk, odds, centerX - 1, centerY, centerZ, material))
					count++;
				if (count < oresToDo && centerZ < 15
						&& placeBlock(chunk, odds, centerX, centerY, centerZ + 1, material))
					count++;
				if (count < oresToDo && centerZ > 0 && placeBlock(chunk, odds, centerX, centerY, centerZ - 1, material))
					count++;
			}
		}
		return count;
	}

	private boolean placeBlock(SupportBlocks chunk, Odds odds, int x, int y, int z, Material material) {
		if (odds.playOdds(oreSprinkleOdds))
			if (chunk.isOfTypes(x, y, z, stratumMaterial, Material.DEEPSLATE)) {
				chunk.setBlock(x, y, z, material);
				return true;
			}
		return false;
	}

}
