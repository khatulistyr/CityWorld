package me.daddychurchill.CityWorld.Plats;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;

import me.daddychurchill.CityWorld.CityWorldGenerator;
import me.daddychurchill.CityWorld.Clipboard.ClipboardStructure;
import me.daddychurchill.CityWorld.Context.DataContext;
import me.daddychurchill.CityWorld.Plugins.LootProvider.LootLocation;
import me.daddychurchill.CityWorld.Support.*;

public class RoadLot extends ConnectedLot {

	// TODO Lines on the road

	public static final int PlatMapRoadInset = 3;

	protected final static int sidewalkWidth = 3;
	protected final static int lightpostHeight = 3;
	private final static int crossDitchEdge = 7;
	private final static int tunnelHeight = 8;
	private final static int fenceHeight = 2;

	private final static Material lightpostbaseMaterial = Material.STONE;
	private final static Material lightpostMaterial = Material.SPRUCE_FENCE;

	public final static Material sewerMaterial = Material.SMOOTH_STONE;
	private final static Material sewerFloor = Material.STONE_BRICKS;
	private final static Material sewerWall = Material.MOSSY_COBBLESTONE;
	private final static Material sewerCeiling = Material.COBBLESTONE;

	// protected final static Material vineMaterial = Material.VINE;

	private final static Material retainingWallMaterial = Material.SMOOTH_STONE;
	private final static Material retainingFenceMaterial = Material.IRON_BARS;

	protected final static Material tunnelWallMaterial = Material.SMOOTH_STONE;
	private final static Material tunnelTileMaterial = Material.SANDSTONE;
	private final static Material tunnelCeilingMaterial = Material.GLASS;

	//private final static Material bridgesidewalkMaterial = Material.BIRCH_SLAB;
	//private final static Material bridgesidewalkMaterial = Material.BIRCH_SLAB;
	//private final static Material bridgeSidewalk1Material = Material.STONE_SLAB;
	//private final static Material bridgeSidewalk2Material = Material.STONE_SLAB;
	protected final static Material bridgeEdgeMaterial = Material.SMOOTH_STONE;
	private final static Material bridgeRailMaterial = Material.DARK_OAK_FENCE;

	private final Material pavementMat;
	private final Material linesMat;
	private final boolean pavementIsClay;
	//private final static Material pavementClay = Material.CYAN_TERRACOTTA;
	//	protected final static DyeColor crosswalkColor = DyeColor.YELLOW;
	private final Material dirtroadMat;
	private final static Material dirtroadClay = Material.LIME_TERRACOTTA;
	private final boolean dirtroadIsClay;

	protected final boolean roundaboutRoad;
	private final int bottomOfRoad;
	private int topOfRoad;
	private final static int sewerDepth = 32;
	
	/* Dicecraft Default Materials
	List<Material> roadMaterials = Arrays.asList(Material.POLISHED_BLACKSTONE,Material.CRACKED_POLISHED_BLACKSTONE_BRICKS,Material.BLACKSTONE);
	List<Material> sidewalkMaterial = Arrays.asList(Material.STONE_BRICK_SLAB,Material.MOSSY_STONE_BRICK_SLAB);
	
	List<Material> bridgeMaterials = Arrays.asList(Material.POLISHED_BLACKSTONE,Material.CRACKED_POLISHED_BLACKSTONE_BRICKS,Material.BLACKSTONE);
	List<Material> bridgeMaterials2 = Arrays.asList(Material.POLISHED_BLACKSTONE_SLAB,Material.BLACKSTONE_SLAB);
	List<Material> bridgesidewalkMaterial = Arrays.asList(Material.STONE_BRICK_SLAB,Material.MOSSY_STONE_BRICK_SLAB);
	*/
	
	List<Material> roadMaterials = Arrays.asList(Material.DEEPSLATE_TILES,Material.CRACKED_DEEPSLATE_TILES,Material.DEEPSLATE_BRICKS);
	List<Material> sidewalkMaterial = Arrays.asList(Material.MUD_BRICK_SLAB);
	
	List<Material> bridgeMaterials = Arrays.asList(Material.DEEPSLATE_TILES,Material.CRACKED_DEEPSLATE_TILES,Material.DEEPSLATE_BRICKS);
	List<Material> bridgeMaterials2 = Arrays.asList(Material.MUD_BRICK_SLAB);
	List<Material> bridgesidewalkMaterial = Arrays.asList(Material.MUD_BRICK_SLAB);
	
	private static Random random = new Random();
	private static ClipboardStructure lightPost = new ClipboardStructure(new File(new File("plugins/CityWorld/Schematics for diceland/Roadshematic"), "lightpost.schem"));
	
	private Material randomMaterial(List<Material> list) {
		return list.get(random.nextInt(list.size()));
	}
	
	public RoadLot(PlatMap platmap, int chunkX, int chunkZ, long globalconnectionkey, boolean roundaboutPart) {
		super(platmap, chunkX, chunkZ);

		style = LotStyle.ROAD;
		connectedkey = globalconnectionkey;
		roundaboutRoad = roundaboutPart;

		bottomOfRoad = platmap.generator.streetLevel - 1;
//		if (generator.settings.includeSewers && cityRoad)
//			bottomOfRoad -= DataContext.FloorHeight * 2 + 1;
		topOfRoad = platmap.generator.streetLevel + 1;
		if (blockYs.getMaxHeight() > topOfRoad + tunnelHeight)
			topOfRoad += tunnelHeight;

		pavementMat = platmap.generator.materialProvider.itemsMaterialListFor_Roads.getNthMaterial(0,
				Material.WHITE_TERRACOTTA);
		linesMat = platmap.generator.materialProvider.itemsMaterialListFor_Roads.getNthMaterial(1,
				Material.QUARTZ_BLOCK);
		// paved sidewalk is 2, read in PlatLot
		dirtroadMat = platmap.generator.materialProvider.itemsMaterialListFor_Roads.getNthMaterial(3,
				Material.DIRT_PATH);
		// dirt sidewalk is 4, read in PlatLot

		pavementIsClay = pavementMat == Material.WHITE_TERRACOTTA;
		dirtroadIsClay = dirtroadMat == Material.WHITE_TERRACOTTA;
	}

	@Override
	public PlatLot newLike(PlatMap platmap, int chunkX, int chunkZ) {
		return new RoadLot(platmap, chunkX, chunkZ, connectedkey, roundaboutRoad);
	}

	@Override
	public boolean isPlaceableAt(CityWorldGenerator generator, int chunkX, int chunkZ) {
		return generator.getSettings().inRoadRange(chunkX, chunkZ);
	}

	@Override
	public boolean isValidStrataY(CityWorldGenerator generator, int blockX, int blockY, int blockZ) {
		return true;
//		return blockY < bottomOfRoad || blockY > topOfRoad - 5;
	}

	@Override
	protected boolean isShaftableLevel(CityWorldGenerator generator, int blockY) {
		return (blockY < bottomOfRoad - sewerDepth || blockY > topOfRoad + 16)
				&& super.isShaftableLevel(generator, blockY);
	}
	
	private boolean sewerCenterBit;
	private boolean sewerNorthWestBias;
	private boolean sewerNorthEastBias;
	private boolean sewerSouthWestBias;
	private boolean sewerSouthEastBias;

	// where are they?
	protected boolean crosswalkNorth = false;
	protected boolean crosswalkSouth = false;
	protected boolean crosswalkWest = false;
	protected boolean crosswalkEast = false;
	private boolean crosswalksFound = false;

	// where are the crosswalks
	protected void calculateCrosswalks(Surroundings roads) {
		if (!crosswalksFound) {
			if (roundaboutRoad) {
				crosswalkNorth = roads.toNorth() && roads.toWest() && roads.toEast();
				crosswalkSouth = roads.toSouth() && roads.toWest() && roads.toEast();
				crosswalkWest = roads.toWest() && roads.toNorth() && roads.toSouth();
				crosswalkEast = roads.toEast() && roads.toNorth() && roads.toSouth();
			} else {

				// how many connecting roads are there?
				int roadways = (roads.toNorth() ? 1 : 0) + (roads.toSouth() ? 1 : 0) + (roads.toWest() ? 1 : 0)
						+ (roads.toEast() ? 1 : 0);

				// crosswalks for intersections and turns
				boolean crosswalks = roadways == 4 || roadways == 3;
				if (roadways == 2)
					crosswalks = !((roads.toNorth() && roads.toSouth()) || (roads.toWest() && roads.toEast()));

				// finally draw the crosswalks
				crosswalkNorth = crosswalks && roads.toNorth();
				crosswalkSouth = crosswalks && roads.toSouth();
				crosswalkWest = crosswalks && roads.toWest();
				crosswalkEast = crosswalks && roads.toEast();
			}
			crosswalksFound = true;
		}
	}

	@Override
	public int getBottomY(CityWorldGenerator generator) {
		return generator.streetLevel; // TODO what about sewers? - (generator.settings.includeSewers && cityRoad) ?
	}

	@Override
	public int getTopY(CityWorldGenerator generator, AbstractCachedYs blockYs, int x, int z) {
		if (blockYs.isSea())
			return blockYs.getBlockY(x, z);
		else
			return generator.streetLevel + DataContext.FloorHeight * 2 + 1; // TODO is this really right?
	}

	@Override
	protected void generateActualChunk(CityWorldGenerator generator, PlatMap platmap, InitialBlocks chunk,
			BiomeGrid biomes, DataContext context, int platX, int platZ) {
		// moved to other chunk generator
	}

	private void placeEWBridgeCap(AbstractBlocks chunk, int x, int baseY, int topY) {
		chunk.setBlocks(x, x + 2, baseY, topY, 0, 16, retainingWallMaterial);
	}

	private void placeEWBridgePartA(AbstractBlocks chunk, int x, int baseY) {
		if (inACity) {

			// cross beam
			chunk.setBlocks(x, x + 2, baseY - 1, baseY, 0, 16, bridgeMaterials, random);

			// edges
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 0, 1, bridgeMaterials, random);
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 15, 16, bridgeMaterials, random);

			// rails
			chunk.setBlocks(x, x + 2, baseY + 1, baseY + 2, 0, 1, bridgeRailMaterial, BlockFace.EAST, BlockFace.WEST);
			chunk.setBlocks(x, x + 2, baseY + 1, baseY + 2, 15, 16, bridgeRailMaterial, BlockFace.EAST, BlockFace.WEST);

			// sidewalks
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 1, 3, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 13, 15, bridgeMaterials2, random, Type.DOUBLE);
		}

		// pavement
		chunk.setBlocks(x, x + 2, baseY, baseY + 1, 3, 13, bridgesidewalkMaterial, random);
	}

	private void placeEWBridgePartAE(AbstractBlocks chunk, int x, int baseY) {
		if (inACity) {

			// cross beam
			chunk.setBlocks(x, x + 2, baseY - 1, baseY, 0, 16, bridgeMaterials, random);

			// edges
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 0, 1, bridgeMaterials, random);
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 15, 16, bridgeMaterials, random);

			// rails
			chunk.setBlock(x, baseY + 1, 0, bridgeRailMaterial, BlockFace.EAST, BlockFace.WEST);
			chunk.setBlock(x + 1, baseY + 1, 0, bridgeRailMaterial, BlockFace.WEST);
			chunk.setBlock(x, baseY + 1, 15, bridgeRailMaterial, BlockFace.EAST, BlockFace.WEST);
			chunk.setBlock(x + 1, baseY + 1, 15, bridgeRailMaterial, BlockFace.WEST);

			// sidewalks
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 1, 3, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 13, 15, bridgeMaterials2, random, Type.DOUBLE);
		}

		// pavement
		chunk.setBlocks(x, x + 2, baseY, baseY + 1, 3, 13, bridgesidewalkMaterial, random);
	}

	private void placeEWBridgePartAW(AbstractBlocks chunk, int x, int baseY) {
		if (inACity) {

			// cross beam
			chunk.setBlocks(x, x + 2, baseY - 1, baseY, 0, 16, bridgeMaterials, random);

			// edges
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 0, 1, bridgeMaterials, random);
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 15, 16, bridgeMaterials, random);

			// rails
			chunk.setBlock(x, baseY + 1, 0, bridgeRailMaterial, BlockFace.EAST);
			chunk.setBlock(x + 1, baseY + 1, 0, bridgeRailMaterial, BlockFace.EAST, BlockFace.WEST);
			chunk.setBlock(x, baseY + 1, 15, bridgeRailMaterial, BlockFace.EAST);
			chunk.setBlock(x + 1, baseY + 1, 15, bridgeRailMaterial, BlockFace.EAST, BlockFace.WEST);

			// sidewalks
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 1, 3, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 13, 15, bridgeMaterials2, random, Type.DOUBLE);
		}

		// pavement
		chunk.setBlocks(x, x + 2, baseY, baseY + 1, 3, 13, bridgesidewalkMaterial, random);
	}

	private void placeEWBridgePartBE(AbstractBlocks chunk, int x, int baseY) {
		if (inACity) {

			// edges
			chunk.setBlocks(x, x + 2, baseY, baseY + 2, 0, 1, bridgeMaterials, random);
			chunk.setBlocks(x, x + 2, baseY, baseY + 2, 15, 16, bridgeMaterials, random);

			// rails
			chunk.setBlock(x, baseY + 2, 0, bridgeRailMaterial, BlockFace.EAST, BlockFace.WEST);
			chunk.setBlock(x + 1, baseY + 2, 0, bridgeRailMaterial, BlockFace.WEST);
			chunk.setBlock(x, baseY + 2, 15, bridgeRailMaterial, BlockFace.EAST, BlockFace.WEST);
			chunk.setBlock(x + 1, baseY + 2, 15, bridgeRailMaterial, BlockFace.WEST);

			// sidewalks
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 1, 3, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 13, 15, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(x, x + 2, baseY + 1, baseY + 2, 1, 3, bridgeMaterials2, random);
			chunk.setBlocks(x, x + 2, baseY + 1, baseY + 2, 13, 15, bridgeMaterials2, random);
		}

		// pavement
		chunk.setBlocks(x, x + 2, baseY, baseY + 1, 3, 13, bridgesidewalkMaterial, random, Type.DOUBLE);
	}

	private void placeEWBridgePartBW(AbstractBlocks chunk, int x, int baseY) {
		if (inACity) {

			// edges
			chunk.setBlocks(x, x + 2, baseY, baseY + 2, 0, 1, bridgeMaterials, random);
			chunk.setBlocks(x, x + 2, baseY, baseY + 2, 15, 16, bridgeMaterials, random);

			// rails
			chunk.setBlock(x, baseY + 2, 0, bridgeRailMaterial, BlockFace.EAST);
			chunk.setBlock(x + 1, baseY + 2, 0, bridgeRailMaterial, BlockFace.EAST, BlockFace.WEST);
			chunk.setBlock(x, baseY + 2, 15, bridgeRailMaterial, BlockFace.EAST);
			chunk.setBlock(x + 1, baseY + 2, 15, bridgeRailMaterial, BlockFace.EAST, BlockFace.WEST);

			// sidewalks
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 1, 3, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(x, x + 2, baseY, baseY + 1, 13, 15, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(x, x + 2, baseY + 1, baseY + 2, 1, 3, bridgeMaterials2, random);
			chunk.setBlocks(x, x + 2, baseY + 1, baseY + 2, 13, 15, bridgeMaterials2, random);
		}

		// pavement
		chunk.setBlocks(x, x + 2, baseY, baseY + 1, 3, 13, bridgesidewalkMaterial, random, Type.DOUBLE);
	}

	private void placeWBridgeColumns(AbstractBlocks chunk, int baseY) {
		chunk.setBlocks(0, 1, blockYs.getMinHeight(), baseY, 2, 4, bridgeMaterials, random);
		chunk.setBlocks(0, 1, blockYs.getMinHeight(), baseY, 12, 14, bridgeMaterials, random);
	}

	private void placeEBridgeColumns(AbstractBlocks chunk, int baseY) {
		chunk.setBlocks(15, 16, blockYs.getMinHeight(), baseY, 2, 4, bridgeMaterials, random);
		chunk.setBlocks(15, 16, blockYs.getMinHeight(), baseY, 12, 14, bridgeMaterials, random);
	}

	private void placeNSBridgeCap(AbstractBlocks chunk, int z, int baseY, int topY) {
		if (inACity) {
			chunk.setBlocks(0, 16, baseY, topY - 1, z, z + 2, retainingWallMaterial);
		}
	}

	private void placeNSBridgePartA(AbstractBlocks chunk, int z, int baseY) {
		if (inACity) {

			// cross beam
			chunk.setBlocks(0, 16, baseY - 1, baseY, z, z + 2, bridgeMaterials, random);

			// edges
			chunk.setBlocks(0, 1, baseY, baseY + 1, z, z + 2, bridgeMaterials, random);
			chunk.setBlocks(15, 16, baseY, baseY + 1, z, z + 2, bridgeMaterials, random);

			// rails
			chunk.setBlocks(0, 1, baseY + 1, baseY + 2, z, z + 2, bridgeRailMaterial, BlockFace.NORTH, BlockFace.SOUTH);
			chunk.setBlocks(15, 16, baseY + 1, baseY + 2, z, z + 2, bridgeRailMaterial, BlockFace.NORTH,
					BlockFace.SOUTH);

			// sidewalks
			chunk.setBlocks(1, 3, baseY, baseY + 1, z, z + 2, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(13, 15, baseY, baseY + 1, z, z + 2, bridgeMaterials2, random, Type.DOUBLE);
		}

		// pavement
		chunk.setBlocks(3, 13, baseY, baseY + 1, z, z + 2, bridgesidewalkMaterial, random);
	}

	private void placeNSBridgePartAN(AbstractBlocks chunk, int z, int baseY) {
		if (inACity) {

			// cross beam
			chunk.setBlocks(0, 16, baseY - 1, baseY, z, z + 2, bridgeMaterials, random);

			// edges
			chunk.setBlocks(0, 1, baseY, baseY + 1, z, z + 2, bridgeMaterials, random);
			chunk.setBlocks(15, 16, baseY, baseY + 1, z, z + 2, bridgeMaterials, random);

			// rails
			chunk.setBlock(0, baseY + 1, z, bridgeRailMaterial, BlockFace.SOUTH);
			chunk.setBlock(0, baseY + 1, z + 1, bridgeRailMaterial, BlockFace.NORTH, BlockFace.SOUTH);
			chunk.setBlock(15, baseY + 1, z, bridgeRailMaterial, BlockFace.SOUTH);
			chunk.setBlock(15, baseY + 1, z + 1, bridgeRailMaterial, BlockFace.NORTH, BlockFace.SOUTH);

			// sidewalks
			chunk.setBlocks(1, 3, baseY, baseY + 1, z, z + 2, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(13, 15, baseY, baseY + 1, z, z + 2, bridgeMaterials2, random, Type.DOUBLE);
		}

		// pavement
		chunk.setBlocks(3, 13, baseY, baseY + 1, z, z + 2, bridgesidewalkMaterial, random);
	}

	private void placeNSBridgePartAS(AbstractBlocks chunk, int z, int baseY) {
		if (inACity) {

			// cross beam
			chunk.setBlocks(0, 16, baseY - 1, baseY, z, z + 2, bridgeMaterials, random);

			// edges
			chunk.setBlocks(0, 1, baseY, baseY + 1, z, z + 2, bridgeMaterials, random);
			chunk.setBlocks(15, 16, baseY, baseY + 1, z, z + 2, bridgeMaterials, random);

			// rails
			chunk.setBlock(0, baseY + 1, z, bridgeRailMaterial, BlockFace.NORTH, BlockFace.SOUTH);
			chunk.setBlock(0, baseY + 1, z + 1, bridgeRailMaterial, BlockFace.NORTH);
			chunk.setBlock(15, baseY + 1, z, bridgeRailMaterial, BlockFace.NORTH, BlockFace.SOUTH);
			chunk.setBlock(15, baseY + 1, z + 1, bridgeRailMaterial, BlockFace.NORTH);

			// sidewalks
			chunk.setBlocks(1, 3, baseY, baseY + 1, z, z + 2, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(13, 15, baseY, baseY + 1, z, z + 2, bridgeMaterials2, random, Type.DOUBLE);
		}

		// pavement
		chunk.setBlocks(3, 13, baseY, baseY + 1, z, z + 2, bridgesidewalkMaterial, random);
	}

	private void placeNSBridgePartBN(AbstractBlocks chunk, int z, int baseY) {
		if (inACity) {

			// edges
			chunk.setBlocks(0, 1, baseY, baseY + 2, z, z + 2, bridgeMaterials, random);
			chunk.setBlocks(15, 16, baseY, baseY + 2, z, z + 2, bridgeMaterials, random);

			// rails
			chunk.setBlock(0, baseY + 2, z, bridgeRailMaterial, BlockFace.SOUTH);
			chunk.setBlock(0, baseY + 2, z + 1, bridgeRailMaterial, BlockFace.NORTH, BlockFace.SOUTH);
			chunk.setBlock(15, baseY + 2, z, bridgeRailMaterial, BlockFace.SOUTH);
			chunk.setBlock(15, baseY + 2, z + 1, bridgeRailMaterial, BlockFace.NORTH, BlockFace.SOUTH);

			// sidewalks
			chunk.setBlocks(1, 3, baseY, baseY + 1, z, z + 2, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(13, 15, baseY, baseY + 1, z, z + 2, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(1, 3, baseY + 1, baseY + 2, z, z + 2, bridgeMaterials2, random);
			chunk.setBlocks(13, 15, baseY + 1, baseY + 2, z, z + 2, bridgeMaterials2, random);
		}

		// pavement
		chunk.setBlocks(3, 13, baseY, baseY + 1, z, z + 2, bridgesidewalkMaterial, random, Type.DOUBLE);
	}

	private void placeNSBridgePartBS(AbstractBlocks chunk, int z, int baseY) {
		if (inACity) {

			// edges
			chunk.setBlocks(0, 1, baseY, baseY + 2, z, z + 2, bridgeMaterials, random);
			chunk.setBlocks(15, 16, baseY, baseY + 2, z, z + 2, bridgeMaterials, random);

			// rails
			chunk.setBlock(0, baseY + 2, z, bridgeRailMaterial, BlockFace.NORTH, BlockFace.SOUTH);
			chunk.setBlock(0, baseY + 2, z + 1, bridgeRailMaterial, BlockFace.NORTH);
			chunk.setBlock(15, baseY + 2, z, bridgeRailMaterial, BlockFace.NORTH, BlockFace.SOUTH);
			chunk.setBlock(15, baseY + 2, z + 1, bridgeRailMaterial, BlockFace.NORTH);

			// sidewalks
			chunk.setBlocks(1, 3, baseY, baseY + 1, z, z + 2, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(13, 15, baseY, baseY + 1, z, z + 2, bridgeMaterials2, random, Type.DOUBLE);
			chunk.setBlocks(1, 3, baseY + 1, baseY + 2, z, z + 2, bridgeMaterials2, random);
			chunk.setBlocks(13, 15, baseY + 1, baseY + 2, z, z + 2, bridgeMaterials2, random);
		}

		// pavement
		chunk.setBlocks(3, 13, baseY, baseY + 1, z, z + 2, bridgesidewalkMaterial, random, Type.DOUBLE);
	}

	private void placeNBridgeColumns(AbstractBlocks chunk, int baseY) {
		chunk.setBlocks(2, 4, blockYs.getMinHeight(), baseY, 0, 1, bridgeMaterials, random);
		chunk.setBlocks(12, 14, blockYs.getMinHeight(), baseY, 0, 1, bridgeMaterials, random);
	}

	private void placeSBridgeColumns(AbstractBlocks chunk, int baseY) {
		chunk.setBlocks(2, 4, blockYs.getMinHeight(), baseY, 15, 16, bridgeMaterials, random);
		chunk.setBlocks(12, 14, blockYs.getMinHeight(), baseY, 15, 16, bridgeMaterials, random);
	}

	private void placeEWTunnelArch(CityWorldGenerator generator, AbstractBlocks chunk, int x, int baseY,
			Material shellMaterial, Material tileMaterial, Material ceilingMaterial) {
		chunk.setBlocks(x, baseY - 2, baseY + 4, 0, shellMaterial);

		chunk.setBlocks(x, baseY, baseY + 3, 1, tileMaterial);
		chunk.setBlocks(x, baseY + 3, baseY + 6, 1, shellMaterial);

		chunk.setBlocks(x, baseY + 3, baseY + 5, 2, tileMaterial);
		chunk.setBlocks(x, baseY + 5, baseY + 7, 2, shellMaterial);

		chunk.setBlocks(x, baseY + 5, baseY + 6, 3, tileMaterial);
		chunk.setBlocks(x, baseY + 6, baseY + 8, 3, shellMaterial);

		chunk.setBlocks(x, x + 1, baseY + 6, baseY + 7, 4, 12, tileMaterial);
		chunk.setBlocks(x, x + 1, baseY + 7, baseY + 8, 4, 12, shellMaterial);
		chunk.setBlocks(x, x + 1, baseY + 8, baseY + 9, 5, 11, shellMaterial);

		if (shellMaterial != tileMaterial) {
			chunk.setBlock(x, baseY + 6, 5, ceilingMaterial);
			chunk.setBlock(x, baseY + 6, 10, ceilingMaterial);
			chunk.airoutBlocks(generator, x, x + 1, baseY + 7, baseY + 8, 5, 11);
		}

		chunk.setBlocks(x, baseY + 5, baseY + 6, 12, tileMaterial);
		chunk.setBlocks(x, baseY + 6, baseY + 8, 12, shellMaterial);

		chunk.setBlocks(x, baseY + 3, baseY + 5, 13, tileMaterial);
		chunk.setBlocks(x, baseY + 5, baseY + 7, 13, shellMaterial);

		chunk.setBlocks(x, baseY, baseY + 3, 14, tileMaterial);
		chunk.setBlocks(x, baseY + 3, baseY + 6, 14, shellMaterial);

		chunk.setBlocks(x, baseY - 2, baseY + 4, 15, shellMaterial);
	}

	private void placeNSTunnelArch(CityWorldGenerator generator, AbstractBlocks chunk, int z, int baseY,
			Material shellMaterial, Material tileMaterial, Material ceilingMaterial) {
		chunk.setBlocks(0, baseY - 2, baseY + 4, z, shellMaterial);

		chunk.setBlocks(1, baseY, baseY + 3, z, tileMaterial);
		chunk.setBlocks(1, baseY + 3, baseY + 6, z, shellMaterial);

		chunk.setBlocks(2, baseY + 3, baseY + 5, z, tileMaterial);
		chunk.setBlocks(2, baseY + 5, baseY + 7, z, shellMaterial);

		chunk.setBlocks(3, baseY + 5, baseY + 6, z, tileMaterial);
		chunk.setBlocks(3, baseY + 6, baseY + 8, z, shellMaterial);

		chunk.setBlocks(4, 12, baseY + 6, baseY + 7, z, z + 1, tileMaterial);
		chunk.setBlocks(4, 12, baseY + 7, baseY + 8, z, z + 1, shellMaterial);
		chunk.setBlocks(5, 11, baseY + 8, baseY + 9, z, z + 1, shellMaterial);

		if (shellMaterial != tileMaterial) {
			chunk.setBlock(5, baseY + 6, z, ceilingMaterial);
			chunk.setBlock(10, baseY + 6, z, ceilingMaterial);
			chunk.airoutBlocks(generator, 5, 11, baseY + 7, baseY + 8, z, z + 1);
		}

		chunk.setBlocks(12, baseY + 5, baseY + 6, z, tileMaterial);
		chunk.setBlocks(12, baseY + 6, baseY + 8, z, shellMaterial);

		chunk.setBlocks(13, baseY + 3, baseY + 5, z, tileMaterial);
		chunk.setBlocks(13, baseY + 5, baseY + 7, z, shellMaterial);

		chunk.setBlocks(14, baseY, baseY + 3, z, tileMaterial);
		chunk.setBlocks(14, baseY + 3, baseY + 6, z, shellMaterial);

		chunk.setBlocks(15, baseY - 2, baseY + 4, z, shellMaterial);
	}

	private void placeRetainingWall(AbstractBlocks chunk, int x, int z, int baseY, int topY, BlockFace... facing) {
		chunk.setBlocks(x, baseY, topY + 1, z, retainingWallMaterial, facing);
		if (topY > baseY + fenceHeight)
			chunk.setBlocks(x, topY + 1, topY + fenceHeight + 1, z, retainingFenceMaterial, facing);
	}

	@Override
	protected void generateActualBlocks(CityWorldGenerator generator, PlatMap platmap, RealBlocks chunk,
			DataContext context, int platX, int platZ) {

		// random bits
		sewerCenterBit = chunkOdds.flipCoin();
		sewerNorthWestBias = chunkOdds.flipCoin();
		sewerNorthEastBias = chunkOdds.flipCoin();
		sewerSouthWestBias = chunkOdds.flipCoin();
		sewerSouthEastBias = chunkOdds.flipCoin();

		// compute offset to start of chunk
		int originX = chunk.getOriginX();
		int originZ = chunk.getOriginZ();

		// where do we start
		int base1Y = generator.streetLevel - DataContext.FloorHeight * 2 + 1;
		int sewerY = base1Y + 1;
		int base2Y = base1Y + DataContext.FloorHeight + 1;
		int pavementLevel = generator.streetLevel;
		boolean doSewer = generator.getSettings().includeSewers && inACity;

		// look around
		SurroundingRoads roads = new SurroundingRoads(platmap, platX, platZ);

		// what are we making?
		if (HeightInfo.getHeightsFast(generator, originX, originZ).isSea()) {
			doSewer = false;

			// clear a little space
//			chunk.setLayer(pavementLevel, 4, Material.IRON_BLOCK);
			chunk.airoutLayer(generator, pavementLevel, 4); // @@@
			generateSurface(generator, chunk, false);

			// bridge to the east/west
			if (roads.toWest() && roads.toEast()) {

				// more bridge beside this one?
				boolean toWest = HeightInfo.getHeightsFast(generator, originX - chunk.width, originZ).isSea();
				boolean toEast = HeightInfo.getHeightsFast(generator, originX + chunk.width, originZ).isSea();

				if (toWest) {

					// tall span
					if (toEast) {
						placeWBridgeColumns(chunk, pavementLevel + 5);
						placeEWBridgePartA(chunk, 0, pavementLevel + 5);
						placeEWBridgePartA(chunk, 2, pavementLevel + 5);
						placeEWBridgePartA(chunk, 4, pavementLevel + 5);
						placeEWBridgePartA(chunk, 6, pavementLevel + 5);
						placeEWBridgePartA(chunk, 8, pavementLevel + 5);
						placeEWBridgePartA(chunk, 10, pavementLevel + 5);
						placeEWBridgePartA(chunk, 12, pavementLevel + 5);
						placeEWBridgePartA(chunk, 14, pavementLevel + 5);
						placeEBridgeColumns(chunk, pavementLevel + 5);

						// ramp down
					} else {
						placeEWBridgeCap(chunk, 14, base1Y, pavementLevel + 1);
						placeEWBridgePartAE(chunk, 14, pavementLevel + 1);
						placeEWBridgePartBE(chunk, 12, pavementLevel + 1);
						placeEWBridgePartA(chunk, 10, pavementLevel + 2);
						placeEWBridgePartBE(chunk, 8, pavementLevel + 2);
						placeEWBridgePartA(chunk, 6, pavementLevel + 3);
						placeEWBridgePartBE(chunk, 4, pavementLevel + 3);
						placeEWBridgePartA(chunk, 2, pavementLevel + 4);
						placeEWBridgePartBE(chunk, 0, pavementLevel + 4);
						placeWBridgeColumns(chunk, pavementLevel + 4);
					}

				} else {

					// ramp up
					if (toEast) {
						placeEWBridgeCap(chunk, 0, base1Y, pavementLevel + 1);
						placeEWBridgePartAW(chunk, 0, pavementLevel + 1);
						placeEWBridgePartBW(chunk, 2, pavementLevel + 1);
						placeEWBridgePartA(chunk, 4, pavementLevel + 2);
						placeEWBridgePartBW(chunk, 6, pavementLevel + 2);
						placeEWBridgePartA(chunk, 8, pavementLevel + 3);
						placeEWBridgePartBW(chunk, 10, pavementLevel + 3);
						placeEWBridgePartA(chunk, 12, pavementLevel + 4);
						placeEWBridgePartBW(chunk, 14, pavementLevel + 4);
						placeEBridgeColumns(chunk, pavementLevel + 4);

						// short span
					} else {
						placeEWBridgeCap(chunk, 0, base1Y, pavementLevel + 1);
						placeEWBridgePartAW(chunk, 0, pavementLevel + 1);
						placeEWBridgePartBW(chunk, 2, pavementLevel + 1);
						placeEWBridgePartA(chunk, 4, pavementLevel + 2);
						placeEWBridgePartA(chunk, 6, pavementLevel + 2);
						placeEWBridgePartA(chunk, 8, pavementLevel + 2);
						placeEWBridgePartA(chunk, 10, pavementLevel + 2);
						placeEWBridgePartBE(chunk, 12, pavementLevel + 1);
						placeEWBridgePartAE(chunk, 14, pavementLevel + 1);
						placeEWBridgeCap(chunk, 14, base1Y, pavementLevel + 1);
					}
				}

			} else if (roads.toNorth() && roads.toSouth()) {

				// more bridge beside this one?
				boolean toNorth = HeightInfo.getHeightsFast(generator, originX, originZ - chunk.width).isSea();
				boolean toSouth = HeightInfo.getHeightsFast(generator, originX, originZ + chunk.width).isSea();

				if (toNorth) {

					// tall span
					if (toSouth) {
						placeNBridgeColumns(chunk, pavementLevel + 5);
						placeNSBridgePartA(chunk, 0, pavementLevel + 5);
						placeNSBridgePartA(chunk, 2, pavementLevel + 5);
						placeNSBridgePartA(chunk, 4, pavementLevel + 5);
						placeNSBridgePartA(chunk, 6, pavementLevel + 5);
						placeNSBridgePartA(chunk, 8, pavementLevel + 5);
						placeNSBridgePartA(chunk, 10, pavementLevel + 5);
						placeNSBridgePartA(chunk, 12, pavementLevel + 5);
						placeNSBridgePartA(chunk, 14, pavementLevel + 5);
						placeSBridgeColumns(chunk, pavementLevel + 5);

						// ramp down
					} else {
						placeNSBridgeCap(chunk, 14, base1Y, pavementLevel + 1);
						placeNSBridgePartAS(chunk, 14, pavementLevel + 1);
						placeNSBridgePartBS(chunk, 12, pavementLevel + 1);
						placeNSBridgePartA(chunk, 10, pavementLevel + 2);
						placeNSBridgePartBS(chunk, 8, pavementLevel + 2);
						placeNSBridgePartA(chunk, 6, pavementLevel + 3);
						placeNSBridgePartBS(chunk, 4, pavementLevel + 3);
						placeNSBridgePartA(chunk, 2, pavementLevel + 4);
						placeNSBridgePartBS(chunk, 0, pavementLevel + 4);
						placeNBridgeColumns(chunk, pavementLevel + 4);
					}

				} else {

					// ramp up
					if (toSouth) {
						placeNSBridgeCap(chunk, 0, base1Y, pavementLevel + 1);
						placeNSBridgePartAN(chunk, 0, pavementLevel + 1);
						placeNSBridgePartBN(chunk, 2, pavementLevel + 1);
						placeNSBridgePartA(chunk, 4, pavementLevel + 2);
						placeNSBridgePartBN(chunk, 6, pavementLevel + 2);
						placeNSBridgePartA(chunk, 8, pavementLevel + 3);
						placeNSBridgePartBN(chunk, 10, pavementLevel + 3);
						placeNSBridgePartA(chunk, 12, pavementLevel + 4);
						placeNSBridgePartBN(chunk, 14, pavementLevel + 4);
						placeSBridgeColumns(chunk, pavementLevel + 4);

						// short span
					} else {
						placeNSBridgeCap(chunk, 0, base1Y, pavementLevel + 1);
						placeNSBridgePartAN(chunk, 0, pavementLevel + 1);
						placeNSBridgePartBN(chunk, 2, pavementLevel + 1);
						placeNSBridgePartA(chunk, 4, pavementLevel + 2);
						placeNSBridgePartA(chunk, 6, pavementLevel + 2);
						placeNSBridgePartA(chunk, 8, pavementLevel + 2);
						placeNSBridgePartA(chunk, 10, pavementLevel + 2);
						placeNSBridgePartBS(chunk, 12, pavementLevel + 1);
						placeNSBridgePartAS(chunk, 14, pavementLevel + 1);
						placeNSBridgeCap(chunk, 14, base1Y, pavementLevel + 1);
					}
				}
			}

			// draw a bridge bits
			// bridge to the east/west
			if (inACity) {
				if (roads.toWest() && roads.toEast()) {
					if (HeightInfo.getHeightsFast(generator, originX - chunk.width, originZ).isSea()
							&& HeightInfo.getHeightsFast(generator, originX + chunk.width, originZ).isSea()) {

						// Bridgelights please here
						generateLightPost(generator, chunk, context, pavementLevel + 6, 0, 7);//DAS WURDE ANGEPASST
						generateLightPost(generator, chunk, context, pavementLevel + 6, 15, 8);//DAS WURDE ANGEPASST
					}

				} else if (roads.toNorth() && roads.toSouth()) {
					if (HeightInfo.getHeightsFast(generator, originX, originZ - chunk.width).isSea()
							&& HeightInfo.getHeightsFast(generator, originX, originZ + chunk.width).isSea()) {
						// Bridgelights please
						generateLightPost(generator, chunk, context, pavementLevel + 6, 0, 7);
						generateLightPost(generator, chunk, context, pavementLevel + 6, 15, 8);
					}
				}
			}

			// not a happy place?
			if (generator.getSettings().includeDecayedRoads)
				destroyLot(generator, pavementLevel + 5, pavementLevel + 7);

			// half as likely in the middle of the road
			generateEntities(generator, chunk, pavementLevel + 6);

		} else {

			int sidewalkLevel = getSidewalkLevel(generator);
			boolean doingTunnel = blockYs.getMaxHeight() > pavementLevel + tunnelHeight + 1;

			// clear out a bit and draw pavement
			if (!doingTunnel) {
				if (inACity & pavementLevel != sidewalkLevel) {
					flattenLot(generator, chunk, 4);
					chunk.airoutLayer(generator, sidewalkLevel);
				}
			} else {
				if (roads.toWest() && roads.toEast()) {
					if (inACity) {

						// carve out the tunnel
						chunk.airoutBlocks(generator, 0, 16, pavementLevel + 1, pavementLevel + 7, 2, 14);
					} else {
						chunk.airoutBlocks(generator, 0, 16, pavementLevel + 1, pavementLevel + 4, 3, 13);
						chunk.airoutBlocks(generator, 0, 16, pavementLevel + 4, 5, 11);
						chunk.pepperBlocks(0, 16, pavementLevel + 5, 5, 11, chunkOdds,
								generator.shapeProvider.findAtmosphereMaterialAt(generator, pavementLevel + 5));
					}
				} else if (roads.toNorth() && roads.toSouth()) {
					if (inACity) {

						// carve out the tunnel
						chunk.airoutBlocks(generator, 2, 14, pavementLevel + 1, pavementLevel + 7, 0, 16);
					} else {
						chunk.airoutBlocks(generator, 3, 13, pavementLevel + 1, pavementLevel + 4, 0, 16);
						chunk.airoutBlocks(generator, 5, 11, pavementLevel + 4, 0, 16);
						chunk.pepperBlocks(5, 11, pavementLevel + 5, 0, 16, chunkOdds,
								generator.shapeProvider.findAtmosphereMaterialAt(generator, pavementLevel + 5));
					}
				}
			}

			// now the bottom bit
			paveRoadLot(generator, chunk, pavementLevel, doingTunnel);

			// sidewalk corners
			paveSidewalk(generator, chunk, 0, sidewalkWidth, sidewalkLevel, 0, sidewalkWidth, doingTunnel);
			paveSidewalk(generator, chunk, 0, sidewalkWidth, sidewalkLevel, chunk.width - sidewalkWidth, chunk.width,
					doingTunnel);
			paveSidewalk(generator, chunk, chunk.width - sidewalkWidth, chunk.width, sidewalkLevel, 0, sidewalkWidth,
					doingTunnel);
			paveSidewalk(generator, chunk, chunk.width - sidewalkWidth, chunk.width, sidewalkLevel,
					chunk.width - sidewalkWidth, chunk.width, doingTunnel);

			// sidewalk edges
			if (!roads.toWest())
				paveSidewalk(generator, chunk, 0, sidewalkWidth, sidewalkLevel, sidewalkWidth,
						chunk.width - sidewalkWidth, doingTunnel);
			if (!roads.toEast())
				paveSidewalk(generator, chunk, chunk.width - sidewalkWidth, chunk.width, sidewalkLevel, sidewalkWidth,
						chunk.width - sidewalkWidth, doingTunnel);
			if (!roads.toNorth())
				paveSidewalk(generator, chunk, sidewalkWidth, chunk.width - sidewalkWidth, sidewalkLevel, 0,
						sidewalkWidth, doingTunnel);
			if (!roads.toSouth())
				paveSidewalk(generator, chunk, sidewalkWidth, chunk.width - sidewalkWidth, sidewalkLevel,
						chunk.width - sidewalkWidth, chunk.width, doingTunnel);

			// crosswalks?
			if (inACity) {
				calculateCrosswalks(roads);

				// draw the crosswalk bits
				generateNSCrosswalk(generator, chunk, sidewalkWidth, chunk.width - sidewalkWidth, pavementLevel, 0,
						sidewalkWidth, crosswalkNorth, doingTunnel);
				generateNSCrosswalk(generator, chunk, sidewalkWidth, chunk.width - sidewalkWidth, pavementLevel,
						chunk.width - sidewalkWidth, chunk.width, crosswalkSouth, doingTunnel);
				generateWECrosswalk(generator, chunk, 0, sidewalkWidth, pavementLevel, sidewalkWidth,
						chunk.width - sidewalkWidth, crosswalkWest, doingTunnel);
				generateWECrosswalk(generator, chunk, chunk.width - sidewalkWidth, chunk.width, pavementLevel,
						sidewalkWidth, chunk.width - sidewalkWidth, crosswalkEast, doingTunnel);
			}

			// tunnel walls please
			if (doingTunnel) {
				doSewer = false;

				// tunnel to the east/west
				if (roads.toWest() && roads.toEast()) {
					if (inACity) {

						// place the arches
						placeEWTunnelArch(generator, chunk, 0, pavementLevel + 1, tunnelWallMaterial,
								tunnelWallMaterial, tunnelWallMaterial);
						for (int x = 1; x < chunk.width - 1; x++) {
							placeEWTunnelArch(generator, chunk, x, pavementLevel + 1, tunnelWallMaterial,
									tunnelTileMaterial, tunnelCeilingMaterial);
						}
						placeEWTunnelArch(generator, chunk, 15, pavementLevel + 1, tunnelWallMaterial,
								tunnelWallMaterial, tunnelWallMaterial);
					}

				} else if (roads.toNorth() && roads.toSouth()) {
					if (inACity) {

						// place the arches
						placeNSTunnelArch(generator, chunk, 0, pavementLevel + 1, tunnelWallMaterial,
								tunnelWallMaterial, tunnelWallMaterial);
						for (int z = 1; z < chunk.width - 1; z++) {
							placeNSTunnelArch(generator, chunk, z, pavementLevel + 1, tunnelWallMaterial,
									tunnelTileMaterial, tunnelCeilingMaterial);
						}
						placeNSTunnelArch(generator, chunk, 15, pavementLevel + 1, tunnelWallMaterial,
								tunnelWallMaterial, tunnelWallMaterial);
					}
				}

				// retaining walls please
			} else if (blockYs.getMaxHeight() > pavementLevel + 1) {
				if (inACity) {

					// wall to the east/west
					if (roads.toWest() && roads.toEast()) {

						// carve out the tunnel
						chunk.airoutBlocks(generator, 0, 16, pavementLevel + 2, pavementLevel + tunnelHeight + 2, 0,
								16);

						// walls please, this will find the Y the hard way since we are looking at the
						// next chunk over
						for (int x = 0; x < chunk.width; x++) {
							placeRetainingWall(chunk, x, 0, pavementLevel + 1,
									generator.getFarBlockY(originX + x, originZ - 1), BlockFace.EAST, BlockFace.WEST);
							placeRetainingWall(chunk, x, 15, pavementLevel + 1,
									generator.getFarBlockY(originX + x, originZ + 16), BlockFace.EAST, BlockFace.WEST);
						}
					} else if (roads.toNorth() && roads.toSouth()) {

						// carve out the tunnel
						chunk.airoutBlocks(generator, 0, 16, pavementLevel + 2, pavementLevel + tunnelHeight + 2, 0,
								16);

						// walls please, this will find the Y the hard way since we are looking at the
						// next chunk over
						for (int z = 0; z < chunk.width; z++) {
							placeRetainingWall(chunk, 0, z, pavementLevel + 1,
									generator.getFarBlockY(originX - 1, originZ + z), BlockFace.NORTH, BlockFace.SOUTH);
							placeRetainingWall(chunk, 15, z, pavementLevel + 1,
									generator.getFarBlockY(originX + 16, originZ + z), BlockFace.NORTH,
									BlockFace.SOUTH);
						}
					}
				}

//				int offset = inACity ? 2 : 1;
//				
//				// wall to the east/west
//				if (roads.toWest() && roads.toEast()) {
//					
//					// carve out the tunnel
//					chunk.airoutBlocks(generator, 0, 16, pavementLevel + offset, pavementLevel + tunnelHeight + 2, 0, 16);
//					
//					// walls please, this will find the Y the hard way since we are looking at the next chunk over
//					for (int x = 0; x < chunk.width; x++) {
//						placeRetainingWall(chunk, x, 0, pavementLevel + 1, generator.getFarBlockY(originX + x, originZ - 1));
//						placeRetainingWall(chunk, x, 15, pavementLevel + 1, generator.getFarBlockY(originX + x, originZ + 16));
//					}
//				} else if (roads.toNorth() && roads.toSouth()) {
//
//					// carve out the tunnel
//					chunk.airoutBlocks(generator, 0, 16, pavementLevel + offset, pavementLevel + tunnelHeight + 2, 0, 16);
//
//					// walls please, this will find the Y the hard way since we are looking at the next chunk over
//					for (int z = 0; z < chunk.width; z++) {
//						placeRetainingWall(chunk, 0, z, pavementLevel + 1, generator.getFarBlockY(originX - 1, originZ + z));
//						placeRetainingWall(chunk, 15, z, pavementLevel + 1, generator.getFarBlockY(originX + 16, originZ + z));
//					}
//				}

				// stuff that only can happen outside of tunnels and bridges
			} else {

				// round things out
				if (!roads.toWest() && roads.toEast() && !roads.toNorth() && roads.toSouth())
					generateRoundedOut(generator, context, chunk, sidewalkWidth, sidewalkWidth, false, false);
				if (!roads.toWest() && roads.toEast() && roads.toNorth() && !roads.toSouth())
					generateRoundedOut(generator, context, chunk, sidewalkWidth, chunk.width - sidewalkWidth - 4, false,
							true);
				if (roads.toWest() && !roads.toEast() && !roads.toNorth() && roads.toSouth())
					generateRoundedOut(generator, context, chunk, chunk.width - sidewalkWidth - 4, sidewalkWidth, true,
							false);
				if (roads.toWest() && !roads.toEast() && roads.toNorth() && !roads.toSouth())
					generateRoundedOut(generator, context, chunk, chunk.width - sidewalkWidth - 4,
							chunk.width - sidewalkWidth - 4, true, true);
			}

			// crosswalks?
			calculateCrosswalks(roads);

//			// center bit
//			chunk.setClay(sidewalkWidth, chunk.width - sidewalkWidth, pavementLevel, sidewalkWidth, chunk.width - sidewalkWidth, pavementColor);

			// draw the crosswalk bits
			generateNSCrosswalk(generator, chunk, sidewalkWidth, chunk.width - sidewalkWidth, pavementLevel, 0,
					sidewalkWidth, crosswalkNorth, doingTunnel);
			generateNSCrosswalk(generator, chunk, sidewalkWidth, chunk.width - sidewalkWidth, pavementLevel,
					chunk.width - sidewalkWidth, chunk.width, crosswalkSouth, doingTunnel);
			generateWECrosswalk(generator, chunk, 0, sidewalkWidth, pavementLevel, sidewalkWidth,
					chunk.width - sidewalkWidth, crosswalkWest, doingTunnel);
			generateWECrosswalk(generator, chunk, chunk.width - sidewalkWidth, chunk.width, pavementLevel,
					sidewalkWidth, chunk.width - sidewalkWidth, crosswalkEast, doingTunnel);

			// decay please
			if (generator.getSettings().includeDecayedRoads) {

				// center bit
				decayRoad(chunk, sidewalkWidth, chunk.width - sidewalkWidth, pavementLevel, sidewalkWidth,
						chunk.width - sidewalkWidth);

				// road to the whatever
				if (roads.toNorth())
					decayRoad(chunk, sidewalkWidth, chunk.width - sidewalkWidth, pavementLevel, 0, sidewalkWidth);
				if (roads.toSouth())
					decayRoad(chunk, sidewalkWidth, chunk.width - sidewalkWidth, pavementLevel,
							chunk.width - sidewalkWidth, chunk.width);
				if (roads.toWest())
					decayRoad(chunk, 0, sidewalkWidth, pavementLevel, sidewalkWidth, chunk.width - sidewalkWidth);
				if (roads.toEast())
					decayRoad(chunk, chunk.width - sidewalkWidth, chunk.width, pavementLevel, sidewalkWidth,
							chunk.width - sidewalkWidth);

				// sidewalk corners
				decaySidewalk(generator, chunk, 0, sidewalkWidth, sidewalkLevel, 0, sidewalkWidth);
				decaySidewalk(generator, chunk, 0, sidewalkWidth, sidewalkLevel, chunk.width - sidewalkWidth,
						chunk.width);
				decaySidewalk(generator, chunk, chunk.width - sidewalkWidth, chunk.width, sidewalkLevel, 0,
						sidewalkWidth);
				decaySidewalk(generator, chunk, chunk.width - sidewalkWidth, chunk.width, sidewalkLevel,
						chunk.width - sidewalkWidth, chunk.width);

				// sidewalk edges
				if (!roads.toWest())
					decaySidewalk(generator, chunk, 0, sidewalkWidth, sidewalkLevel, sidewalkWidth,
							chunk.width - sidewalkWidth);
				if (!roads.toEast())
					decaySidewalk(generator, chunk, chunk.width - sidewalkWidth, chunk.width, sidewalkLevel,
							sidewalkWidth, chunk.width - sidewalkWidth);
				if (!roads.toNorth())
					decaySidewalk(generator, chunk, sidewalkWidth, chunk.width - sidewalkWidth, sidewalkLevel, 0,
							sidewalkWidth);
				if (!roads.toSouth())
					decaySidewalk(generator, chunk, sidewalkWidth, chunk.width - sidewalkWidth, sidewalkLevel,
							chunk.width - sidewalkWidth, chunk.width);

			}

			// tunnel please
			if (blockYs.getMaxHeight() > pavementLevel + tunnelHeight + 1) {
				doSewer = false;

				// tunnel to the east/west
				if (inACity) {
					if (roads.toWest() && roads.toEast()) {
						chunk.setBlock(3, pavementLevel + 8, 8, context.lightMat);
						chunk.setBlock(12, pavementLevel + 8, 7, context.lightMat);
					} else if (roads.toNorth() && roads.toSouth()) {
						chunk.setBlock(8, pavementLevel + 8, 3, context.lightMat);
						chunk.setBlock(7, pavementLevel + 8, 12, context.lightMat);
					}
				}

				// add nature on top
				generateSurface(generator, chunk, true);

				// TODO decay tunnels please!

				// stuff that only can happen outside of tunnels and bridges
			} else {

				// light posts
				if (inACity) {
					boolean lightPostNW = generateLightPost(generator, chunk, context, sidewalkLevel, sidewalkWidth - 1,
							sidewalkWidth - 1);
					boolean lightPostSE = generateLightPost(generator, chunk, context, sidewalkLevel,
							chunk.width - sidewalkWidth, chunk.width - sidewalkWidth);

					// put signs up?
					if (generator.getSettings().includeNamedRoads) {

						// if we haven't calculated crosswalks yet do so
						calculateCrosswalks(roads);

						// add the signs
						if (lightPostNW && (crosswalkNorth || crosswalkWest))
							generateStreetSign(generator, chunk, sidewalkLevel, sidewalkWidth - 1, sidewalkWidth - 1);
						if (lightPostSE && (crosswalkSouth || crosswalkEast))
							generateStreetSign(generator, chunk, sidewalkLevel, chunk.width - sidewalkWidth,
									chunk.width - sidewalkWidth);
					}
				}

				// half as likely in the middle of the road
				generateEntities(generator, chunk, sidewalkLevel);
			}
		}

		// sewer?
		if (doSewer) {

			// defaults
			boolean superConnected = chunkOdds.playOdds(Odds.oddsPrettyLikely);
			boolean vaultNorthWest = false;
			boolean vaultSouthWest = false;
			boolean vaultNorthEast = false;
			boolean vaultSouthEast = false;
			boolean centerNorth = !roads.toNorth();
			boolean centerSouth = !roads.toSouth();
			boolean centerWest = !roads.toWest();
			boolean centerEast = !roads.toEast();

			// empty out the sewer
			chunk.airoutLayer(generator, base1Y, base2Y - base1Y);
//			Material emptyMaterial = getAirMaterial(generator, sewerY - 1);

			// draw the floor of the sewer
			chunk.setBlocks(0, 16, sewerY - 1, sewerY, 0, 16, chunkOdds, sewerFloor, sewerCeiling);
			chunk.airoutBlocks(generator, crossDitchEdge, chunk.width - crossDitchEdge, sewerY - 1, sewerY,
					crossDitchEdge, chunk.width - crossDitchEdge);

			// corner bits
			chunk.setBlocks(0, 6, sewerY, base2Y, 0, 1, chunkOdds, sewerWall, sewerFloor);
			chunk.setBlocks(0, 1, sewerY, base2Y, 1, 6, chunkOdds, sewerWall, sewerFloor);
			chunk.setBlocks(10, 16, sewerY, base2Y, 0, 1, chunkOdds, sewerWall, sewerFloor);
			chunk.setBlocks(15, 16, sewerY, base2Y, 1, 6, chunkOdds, sewerWall, sewerFloor);
			chunk.setBlocks(0, 6, sewerY, base2Y, 15, 16, chunkOdds, sewerWall, sewerFloor);
			chunk.setBlocks(0, 1, sewerY, base2Y, 10, 15, chunkOdds, sewerWall, sewerFloor);
			chunk.setBlocks(10, 16, sewerY, base2Y, 15, 16, chunkOdds, sewerWall, sewerFloor);
			chunk.setBlocks(15, 16, sewerY, base2Y, 10, 15, chunkOdds, sewerWall, sewerFloor);

			// cross beams
			chunk.setBlocks(6, 10, base2Y - 1, base2Y, 0, 1, chunkOdds, sewerCeiling, sewerWall);
			chunk.setBlocks(6, 10, base2Y - 1, base2Y, 15, 16, chunkOdds, sewerCeiling, sewerWall);
			chunk.setBlocks(0, 1, base2Y - 1, base2Y, 6, 10, chunkOdds, sewerCeiling, sewerWall);
			chunk.setBlocks(15, 16, base2Y - 1, base2Y, 6, 10, chunkOdds, sewerCeiling, sewerWall);

			// cardinal directions known walls and ditches
			if (!superConnected && !roads.toNorth()) {
				chunk.setBlocks(5, 11, sewerY, base2Y, 0, 1, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(5, 11, base2Y - 1, base2Y, 1, 2, chunkOdds, sewerCeiling, sewerWall);
			} else {
				chunk.airoutBlocks(generator, 7, 9, sewerY - 1, sewerY, 0, 7);
			}
			if (!superConnected && !roads.toSouth()) {
				chunk.setBlocks(5, 11, sewerY, base2Y, 15, 16, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(5, 11, base2Y - 1, base2Y, 14, 15, chunkOdds, sewerCeiling, sewerWall);
			} else {
				chunk.airoutBlocks(generator, 7, 9, sewerY - 1, sewerY, 9, 16);
			}
			if (!superConnected && !roads.toWest()) {
				chunk.setBlocks(0, 1, sewerY, base2Y, 5, 11, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(1, 2, base2Y - 1, base2Y, 5, 11, chunkOdds, sewerCeiling, sewerWall);
			} else {
				chunk.airoutBlocks(generator, 0, 7, sewerY - 1, sewerY, 7, 9);
			}
			if (!superConnected && !roads.toEast()) {
				chunk.setBlocks(15, 16, sewerY, base2Y, 5, 11, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(14, 15, base2Y - 1, base2Y, 5, 11, chunkOdds, sewerCeiling, sewerWall);
			} else {
				chunk.airoutBlocks(generator, 9, 16, sewerY - 1, sewerY, 7, 9);
			}

			// show our bias
			if (roads.toNorth()) {
				vaultNorthWest = sewerNorthWestBias;
				vaultNorthEast = sewerNorthEastBias;
			}
			if (roads.toSouth()) {
				vaultSouthWest = sewerSouthWestBias;
				vaultSouthEast = sewerSouthEastBias;
			}
			if (roads.toWest()) {
				vaultNorthWest = sewerNorthWestBias;
				vaultSouthWest = sewerSouthWestBias;
			}
			if (roads.toEast()) {
				vaultNorthEast = sewerNorthEastBias;
				vaultSouthEast = sewerSouthEastBias;
			}

			// make sure there is a way down
			if (roads.toNorth() && roads.toWest()) {
				vaultNorthWest = true;
			}

			// figure out the center
			if (!(vaultNorthWest && vaultNorthEast && vaultSouthWest && vaultSouthEast)) {
				centerNorth = sewerCenterBit || (vaultNorthWest && vaultNorthEast);
				centerSouth = sewerCenterBit || (vaultSouthWest && vaultSouthEast);
				centerWest = sewerCenterBit || (vaultNorthWest && vaultSouthWest);
				centerEast = sewerCenterBit || (vaultNorthEast && vaultSouthEast);
			}

			// show the vaults
			if (vaultNorthWest) {
				chunk.setBlocks(4, 5, sewerY, base2Y - 1, 1, 4, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(1, 4, sewerY, base2Y - 1, 4, 5, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(1, 6, base2Y - 1, base2Y, 1, 6, chunkOdds, sewerCeiling, sewerWall);
			} else {
				chunk.setBlocks(1, 6, base2Y - 1, base2Y, 1, 2, chunkOdds, sewerCeiling, sewerWall);
				chunk.setBlocks(1, 2, base2Y - 1, base2Y, 2, 6, chunkOdds, sewerCeiling, sewerWall);
			}
			if (vaultSouthWest) {
				chunk.setBlocks(4, 5, sewerY, base2Y - 1, 12, 15, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(1, 4, sewerY, base2Y - 1, 11, 12, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(1, 6, base2Y - 1, base2Y, 10, 15, sewerCeiling);
			} else {
				chunk.setBlocks(1, 6, base2Y - 1, base2Y, 14, 15, chunkOdds, sewerCeiling, sewerWall);
				chunk.setBlocks(1, 2, base2Y - 1, base2Y, 10, 14, chunkOdds, sewerCeiling, sewerWall);
			}
			if (vaultNorthEast) {
				chunk.setBlocks(11, 12, sewerY, base2Y - 1, 1, 4, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(12, 15, sewerY, base2Y - 1, 4, 5, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(10, 15, base2Y - 1, base2Y, 1, 6, chunkOdds, sewerCeiling, sewerWall);
			} else {
				chunk.setBlocks(10, 15, base2Y - 1, base2Y, 1, 2, chunkOdds, sewerCeiling, sewerWall);
				chunk.setBlocks(14, 15, base2Y - 1, base2Y, 2, 6, chunkOdds, sewerCeiling, sewerWall);
			}
			if (vaultSouthEast) {
				chunk.setBlocks(11, 12, sewerY, base2Y - 1, 12, 15, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(12, 15, sewerY, base2Y - 1, 11, 12, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(10, 15, base2Y - 1, base2Y, 10, 15, chunkOdds, sewerCeiling, sewerWall);
			} else {
				chunk.setBlocks(10, 15, base2Y - 1, base2Y, 14, 15, chunkOdds, sewerCeiling, sewerWall);
				chunk.setBlocks(14, 15, base2Y - 1, base2Y, 10, 14, chunkOdds, sewerCeiling, sewerWall);
			}

			// show the center center
			if (centerNorth) {
				chunk.setBlocks(4, 12, sewerY, base2Y - 1, 4, 5, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(3, 13, base2Y - 1, base2Y, 3, 6, chunkOdds, sewerCeiling, sewerWall);
			}
			if (centerSouth) {
				chunk.setBlocks(4, 12, sewerY, base2Y - 1, 11, 12, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(3, 13, base2Y - 1, base2Y, 10, 13, chunkOdds, sewerCeiling, sewerWall);
			}
			if (centerWest) {
				chunk.setBlocks(4, 5, sewerY, base2Y - 1, 4, 12, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(3, 6, base2Y - 1, base2Y, 3, 13, chunkOdds, sewerCeiling, sewerWall);
			}
			if (centerEast) {
				chunk.setBlocks(11, 12, sewerY, base2Y - 1, 4, 12, chunkOdds, sewerWall, sewerFloor);
				chunk.setBlocks(10, 13, base2Y - 1, base2Y, 3, 13, chunkOdds, sewerCeiling, sewerWall);
			}

			// ceiling please
			chunk.setBlocks(0, 16, base2Y, base2Y + 1, 0, 16, chunkOdds, sewerCeiling, sewerWall);

			// show our bias
			if (roads.toNorth()) {
				vaultNorthWest = sewerNorthWestBias;
				vaultNorthEast = sewerNorthEastBias;
			}
			if (roads.toSouth()) {
				vaultSouthWest = sewerSouthWestBias;
				vaultSouthEast = sewerSouthEastBias;
			}
			if (roads.toWest()) {
				vaultNorthWest = sewerNorthWestBias;
				vaultSouthWest = sewerSouthWestBias;
			}
			if (roads.toEast()) {
				vaultNorthEast = sewerNorthEastBias;
				vaultSouthEast = sewerSouthEastBias;
			}

			// make sure there is a way down
			if (roads.toNorth() && roads.toWest()) {
				vaultNorthWest = true;

				// place the manhole
				chunk.setBlock(3, pavementLevel, 2, Material.ACACIA_TRAPDOOR, BlockFace.EAST, Half.TOP);

				// ladder
				chunk.setLadder(3, sewerY, pavementLevel, 2, BlockFace.WEST); // fixed
			}

			// figure out the center
			if (!(vaultNorthWest && vaultNorthEast && vaultSouthWest && vaultSouthEast)) {
				centerNorth = sewerCenterBit || (vaultNorthWest && vaultNorthEast);
				centerSouth = sewerCenterBit || (vaultSouthWest && vaultSouthEast);
				centerWest = sewerCenterBit || (vaultNorthWest && vaultSouthWest);
				centerEast = sewerCenterBit || (vaultNorthEast && vaultSouthEast);
			}

			Material fluidMaterial = generator.oreProvider.fluidFluidMaterial;

			// cardinal directions known walls and ditches
			// static water (prevent cross-chunk domino effect)
			if (superConnected || roads.toNorth()) {
				chunk.setBlocks(7, 9, sewerY - 1, 0, 2, fluidMaterial);
			}
			if (superConnected || roads.toSouth()) {
				chunk.setBlocks(7, 9, sewerY - 1, 14, 16, fluidMaterial);
			}
			if (superConnected || roads.toWest()) {
				chunk.setBlocks(0, 2, sewerY - 1, 7, 9, fluidMaterial);
			}
			if (superConnected || roads.toEast()) {
				chunk.setBlocks(14, 16, sewerY - 1, 7, 9, fluidMaterial);
			}

			// flowing water
			boolean wasDoPhysics = chunk.setDoPhysics(true);
			try {
				if (superConnected || roads.toNorth()) {
					chunk.setBlock(8, sewerY - 1, 2, fluidMaterial);
					generateEntryVines(chunk, base2Y - 1, BlockFace.NORTH, 6, 1, 7, 1, 8, 1, 9, 1);
				}
				if (superConnected || roads.toSouth()) {
					chunk.setBlock(7, sewerY - 1, 13, fluidMaterial);
					generateEntryVines(chunk, base2Y - 1, BlockFace.SOUTH, 6, 14, 7, 14, 8, 14, 9, 14);
				}
				if (superConnected || roads.toWest()) {
					chunk.setBlock(2, sewerY - 1, 7, fluidMaterial);
					generateEntryVines(chunk, base2Y - 1, BlockFace.WEST, 1, 6, 1, 7, 1, 8, 1, 9);
				}
				if (superConnected || roads.toEast()) {
					chunk.setBlock(13, sewerY - 1, 8, fluidMaterial);
					generateEntryVines(chunk, base2Y - 1, BlockFace.EAST, 14, 6, 14, 7, 14, 8, 14, 9);
				}
			} finally {
				chunk.setDoPhysics(wasDoPhysics);
			}

			// add the various doors
			if (vaultNorthWest) {
				generateDoor(chunk, 4, sewerY, 1, BlockFace.EAST_NORTH_EAST);
				generateDoor(chunk, 1, sewerY, 4, BlockFace.SOUTH_SOUTH_WEST);
			}
			if (vaultNorthEast) {
				generateDoor(chunk, 11, sewerY, 1, BlockFace.WEST_NORTH_WEST);
				generateDoor(chunk, 14, sewerY, 4, BlockFace.SOUTH_SOUTH_EAST);
			}
			if (vaultSouthWest) {
				generateDoor(chunk, 1, sewerY, 11, BlockFace.NORTH_NORTH_WEST);
				generateDoor(chunk, 4, sewerY, 14, BlockFace.EAST_SOUTH_EAST);
			}
			if (vaultSouthEast) {
				generateDoor(chunk, 14, sewerY, 11, BlockFace.NORTH_NORTH_EAST);
				generateDoor(chunk, 11, sewerY, 14, BlockFace.WEST_SOUTH_WEST);
			}

			// we might put down a plank... or maybe not...
			boolean placedPlank = false;
			Trees trees = new Trees(chunkOdds);
			Material woodSlab = trees.getRandomWoodSlab();

			// fancy up the center walls?
			if (centerNorth) {
				generateDoor(chunk, 10, sewerY, 4, BlockFace.NORTH_NORTH_EAST);
				chunk.setBlock(7, sewerY, 4, Material.COBBLESTONE, Type.TOP);
				chunk.setBlock(8, sewerY, 4, Material.COBBLESTONE, Type.TOP);
			} else if (!placedPlank && roads.toNorth() && chunkOdds.flipCoin()) {
				placedPlank = true;
				chunk.setBlocks(6, 10, sewerY, 5, 6, woodSlab);
			}
			if (centerSouth) {
				generateDoor(chunk, 5, sewerY, 11, BlockFace.SOUTH_SOUTH_WEST);
				chunk.setBlock(7, sewerY, 11, Material.COBBLESTONE, Type.TOP);
				chunk.setBlock(8, sewerY, 11, Material.COBBLESTONE, Type.TOP);
			} else if (!placedPlank && roads.toSouth() && chunkOdds.flipCoin()) {
				placedPlank = true;
				chunk.setBlocks(6, 10, sewerY, 10, 11, woodSlab);
			}
			if (centerWest) {
				generateDoor(chunk, 4, sewerY, 5, BlockFace.WEST_NORTH_WEST);
				chunk.setBlock(4, sewerY, 7, Material.COBBLESTONE, Type.TOP);
				chunk.setBlock(4, sewerY, 8, Material.COBBLESTONE, Type.TOP);
			} else if (!placedPlank && roads.toWest() && chunkOdds.flipCoin()) {
				placedPlank = true;
				chunk.setBlocks(5, 6, sewerY, 6, 10, woodSlab);
			}
			if (centerEast) {
				generateDoor(chunk, 11, sewerY, 10, BlockFace.EAST_SOUTH_EAST);
				chunk.setBlock(11, sewerY, 7, Material.COBBLESTONE, Type.TOP);
				chunk.setBlock(11, sewerY, 8, Material.COBBLESTONE, Type.TOP);
			} else if (!placedPlank && roads.toEast() && chunkOdds.flipCoin()) {
				placedPlank = true;
				chunk.setBlocks(10, 11, sewerY, 6, 10, woodSlab);
			}

			// populate the vaults
			if (vaultNorthWest) {
				if (!(roads.toNorth() && roads.toWest())) // special case for manholes
					generateTreat(generator, chunk, 2, sewerY, 2);
			}
			if (vaultNorthEast) {
				generateTreat(generator, chunk, 13, sewerY, 2);
			}
			if (vaultSouthWest) {
				generateTreat(generator, chunk, 2, sewerY, 13);
			}
			if (vaultSouthEast) {
				generateTreat(generator, chunk, 13, sewerY, 13);
			}
			if (centerNorth && centerSouth && centerWest && centerEast) {

				// look carefully, these are actually different
				switch (chunkOdds.getRandomInt(4)) {
				case 1:
					generateTreat(generator, chunk, 6, sewerY, 6);
					generateTrick(generator, chunk, 9, sewerY, 9);
					break;
				case 2:
					generateTreat(generator, chunk, 9, sewerY, 6);
					generateTrick(generator, chunk, 6, sewerY, 9);
					break;
				case 3:
					generateTreat(generator, chunk, 6, sewerY, 9);
					generateTrick(generator, chunk, 9, sewerY, 6);
					break;
				default:
					generateTreat(generator, chunk, 9, sewerY, 9);
					generateTrick(generator, chunk, 6, sewerY, 6);
					break;
				}
			} else {
				if (centerNorth) {
					if (vaultNorthWest && !vaultNorthEast)
						generateTrick(generator, chunk, 6, sewerY, 2);
					else if (vaultNorthEast && !vaultNorthWest)
						generateTrick(generator, chunk, 9, sewerY, 2);
				}
				if (centerSouth) {
					if (vaultSouthWest && !vaultSouthEast)
						generateTrick(generator, chunk, 6, sewerY, 13);
					else if (vaultSouthEast && !vaultSouthWest)
						generateTrick(generator, chunk, 9, sewerY, 13);
				}
				if (centerWest) {
					if (vaultNorthWest && !vaultSouthWest)
						generateTrick(generator, chunk, 2, sewerY, 6);
					else if (vaultSouthWest && !vaultNorthWest)
						generateTrick(generator, chunk, 2, sewerY, 9);
				}
				if (centerEast) {
					if (vaultNorthEast && !vaultSouthEast)
						generateTrick(generator, chunk, 13, sewerY, 6);
					else if (vaultSouthEast && !vaultNorthEast)
						generateTrick(generator, chunk, 13, sewerY, 9);
				}
			}

			// now the vines
			for (int i = 2; i < 14; i++) {
				generateHangingVine(chunk, base2Y - 1, BlockFace.NORTH, i, 2, i, 1);
//				generateHangingVine(chunk, base2Y - 1, Direction.Vine.NORTH, i, 5, i, 4);
//				generateHangingVine(chunk, base2Y - 1, Direction.Vine.SOUTH, i, 10, i, 11);
				generateHangingVine(chunk, base2Y - 1, BlockFace.SOUTH, i, 13, i, 14);

				generateHangingVine(chunk, base2Y - 1, BlockFace.WEST, 2, i, 1, i);
//				generateHangingVine(chunk, base2Y - 1, Direction.Vine.WEST, 5, i, 4, i);
//				generateHangingVine(chunk, base2Y - 1, Direction.Vine.EAST, 10, i, 11, i);
				generateHangingVine(chunk, base2Y - 1, BlockFace.EAST, 13, i, 14, i);
			}
		}
	}

	private void pepperPlants(CityWorldGenerator generator, SupportBlocks chunk, int x1, int x2, int y, int z1, int z2,
			double chances, Material manMade, boolean doingFolage, boolean doingTunnel) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				if (doingFolage & chunkOdds.playOdds(chances)) {
					if (!chunk.isEmpty(x, y, z) & chunk.isEmpty(x, y + 1, z)) {
						generator.coverProvider.makePlantable(generator, chunk, x, y, z);
						if (!doingTunnel)
							generator.surfaceProvider.generateSurface(generator, this, chunk, x, y, z, true);
//							chunk.setSlab(x, y + 1, z, Material.STONE_SLAB, true);
//						else
//							chunk.setBlock(x, y + 1, z, Material.PURPUR_BLOCK);
//					} else {
//						chunk.setBlock(x, y, z, Material.COAL_BLOCK);
					}
				} else {
					chunk.setBlock(x, y, z, manMade);
				}
			}
		}
	}

	private void paveRoadArea(CityWorldGenerator generator, SupportBlocks chunk, int x1, int x2, int y, int z1,
			int z2, boolean doingFolage, boolean doingTunnel) {
		if (inACity)
			if (pavementIsClay)
				//chunk.setBlocks(x1, x2, y, z1, z2, pavementClay);
				chunk.setBlocks(x1, x2, y, z1, z2, roadMaterials, random);
			else
				//chunk.setBlocks(x1, x2, y, z1, z2, pavementMat);
				chunk.setBlocks(x1, x2, y, z1, z2, this.roadMaterials, random); //Straßen generation
		else if (dirtroadIsClay)
			chunk.setBlocks(x1, x2, y, z1, z2, dirtroadClay);
		else {
//				pepperPlants(generator, chunk, x1, x2, y, z1, z2, Odds.oddsLikely, 
//						doingFolage ? Material.BONE_BLOCK : Material.DIAMOND_BLOCK, doingTunnel);
			pepperPlants(generator, chunk, x1, x2, y, z1, z2, Odds.oddsVeryLikely, dirtroadMat, doingFolage,
					doingTunnel);
		}
//		
//		
//			else if (doingFolage) 
////				pepperPlants(generator, chunk, x1, x2, y, z1, z2, Odds.oddsSomewhatLikely, dirtroadMat, doingTunnel);
//				pepperPlants(generator, chunk, x1, x2, y, z1, z2, Odds.oddsSomewhatLikely, Material.BONE_BLOCK, doingTunnel);
//			else
////				chunk.setBlocks(x1, x2, y, z1, z2, dirtroadMat);
//				chunk.setBlocks(x1, x2, y, z1, z2, Material.DIAMOND_BLOCK);
	}

	protected void paveSidewalk(CityWorldGenerator generator, SupportBlocks chunk, int x1, int x2, int y, int z1,
			int z2, boolean doingTunnel) {
		if (inACity)
			chunk.setBlocks(x1, x2, y, z1, z2, this.sidewalkMaterial, random);
		else {
//			if (dirtroadSidewalk == Material.DIRT)
//				BlackMagic.setBlocks(chunk, x1, x2, y, z1, z2, dirtroadSidewalk, 1); // Coarse dirt
//			else
//				chunk.setBlocks(x1, x2, y, z1, z2, dirtroadSidewalk);
//			if (!doingTunnel)
//				pepperPlants(generator, chunk, x1, x2, y, z1, z2, Odds.oddsSomewhatLikely, Material.EMERALD_BLOCK, doingTunnel);
			pepperPlants(generator, chunk, x1, x2, y, z1, z2, Odds.oddsSomewhatLikely, dirtroadSidewalk, !inACity,
					doingTunnel);
		}
	}

	private void paveSidewalk(CityWorldGenerator generator, SupportBlocks chunk, int x, int y, int z,
			boolean doingTunnel) {
		if (inACity)
			chunk.setBlock(x, y, z, randomMaterial(sidewalkMaterial));
		else {
//			if (dirtroadSidewalk == Material.DIRT)
//				chunk.setBlock(x, y, z, dirtroadSidewalk, 1); // Coarse dirt
//			else
//				chunk.setBlock(x, y, z, dirtroadSidewalk);
//			if (!doingTunnel)
//				pepperPlants(generator, chunk, x, x + 1, y, z, z + 1, Odds.oddsSomewhatLikely, Material.GOLD_BLOCK, doingTunnel);
			pepperPlants(generator, chunk, x, x + 1, y, z, z + 1, Odds.oddsSomewhatLikely, dirtroadSidewalk, !inACity,
					doingTunnel);
		}
	}

	private void paveRoadLot(CityWorldGenerator generator, SupportBlocks chunk, int y, boolean doingTunnel) {
		paveRoadArea(generator, chunk, sidewalkWidth, chunk.width - sidewalkWidth, y - 1, sidewalkWidth,
				chunk.width - sidewalkWidth, false, doingTunnel);
		paveRoadArea(generator, chunk, sidewalkWidth, chunk.width - sidewalkWidth, y, sidewalkWidth,
				chunk.width - sidewalkWidth, !inACity, doingTunnel);
	}

	protected void generateNSCrosswalk(CityWorldGenerator generator, RealBlocks chunk, int x1, int x2, int y, int z1,
			int z2, boolean crosswalk, boolean doingTunnel) {
		paveRoadArea(generator, chunk, x1, x2, y, z1, z2, !inACity, doingTunnel);
		if (inACity) {
			if (crosswalk) {
				chunk.setBlocks(x1 + 1, x1 + 2, y, z1, z2, linesMat);
				chunk.setBlocks(x1 + 3, x1 + 4, y, z1, z2, linesMat);
				chunk.setBlocks(x2 - 2, x2 - 1, y, z1, z2, linesMat);
				chunk.setBlocks(x2 - 4, x2 - 3, y, z1, z2, linesMat);
//				chunk.setClay(x1 + 1, x1 + 2, y, z1, z2, crosswalkColor);
//				chunk.setClay(x1 + 3, x1 + 4, y, z1, z2, crosswalkColor);
//				chunk.setClay(x2 - 2, x2 - 1, y, z1, z2, crosswalkColor);
//				chunk.setClay(x2 - 4, x2 - 3, y, z1, z2, crosswalkColor);
			}
		}
	}

	protected void generateWECrosswalk(CityWorldGenerator generator, RealBlocks chunk, int x1, int x2, int y, int z1,
			int z2, boolean crosswalk, boolean doingTunnel) {
		paveRoadArea(generator, chunk, x1, x2, y, z1, z2, !inACity, doingTunnel);
		if (inACity) {
			if (crosswalk) {
				chunk.setBlocks(x1, x2, y, z1 + 1, z1 + 2, linesMat);
				chunk.setBlocks(x1, x2, y, z1 + 3, z1 + 4, linesMat);
				chunk.setBlocks(x1, x2, y, z2 - 2, z2 - 1, linesMat);
				chunk.setBlocks(x1, x2, y, z2 - 4, z2 - 3, linesMat);
//				chunk.setClay(x1, x2, y, z1 + 1, z1 + 2, crosswalkColor);
//				chunk.setClay(x1, x2, y, z1 + 3, z1 + 4, crosswalkColor);
//				chunk.setClay(x1, x2, y, z2 - 2, z2 - 1, crosswalkColor);
//				chunk.setClay(x1, x2, y, z2 - 4, z2 - 3, crosswalkColor);
			}
		}
	}

	protected void generateEntities(CityWorldGenerator generator, RealBlocks chunk, int y) {
		int x = chunkOdds.calcRandomRange(sidewalkWidth, 15 - sidewalkWidth);
		int z = chunkOdds.calcRandomRange(sidewalkWidth, 15 - sidewalkWidth);
//		chunk.setBlock(x, y + 30, z, Material.GLOWSTONE);
		if (inACity)
			generator.spawnProvider.spawnBeings(generator, chunk, chunkOdds, x, y, z);
		else
			generator.spawnProvider.spawnVagrants(generator, chunk, chunkOdds, x, y, z);
	}

	protected void decayRoad(RealBlocks chunk, int x1, int x2, int y, int z1, int z2) {
		int amount = (x2 - x1) * (z2 - z1) / 10;
		while (amount > 0) {
			int x = x1 + chunkOdds.getRandomInt(x2 - x1);
			int z = z1 + chunkOdds.getRandomInt(z2 - z1);
			if (chunk.isEmpty(x, y + 1, z)) {
				if (chunkOdds.flipCoin())
					chunk.setBlock(x, y, z, Material.COBBLESTONE);
				else
					chunk.setBlock(x, y, z, Material.COBBLESTONE_SLAB, Type.BOTTOM);
				amount--;
			}
		}
	}

	protected void decaySidewalk(CityWorldGenerator generator, RealBlocks chunk, int x1, int x2, int y, int z1,
			int z2) {
		int amount = (x2 - x1) * (z2 - z1) / 10;
		while (amount > 0) {
			int x = x1 + chunkOdds.getRandomInt(x2 - x1);
			int z = z1 + chunkOdds.getRandomInt(z2 - z1);
			if (chunk.isEmpty(x, y + 1, z)) {
				if (chunkOdds.flipCoin())
					chunk.airoutBlock(generator, x, y, z);
				else
					chunk.setBlock(x, y, z, Material.STONE_SLAB, 3);
				amount--;
			}
		}
	}

	private void generateEntryVines(RealBlocks chunk, int y, BlockFace direction, int x1, int z1, int x2, int z2,
			int x3, int z3, int x4, int z4) {
		if (chunkOdds.flipCoin())
			chunk.setVine(x1, y, z1, direction);
		if (chunkOdds.flipCoin())
			chunk.setVine(x2, y, z2, direction);
		if (chunkOdds.flipCoin())
			chunk.setVine(x3, y, z3, direction);
		if (chunkOdds.flipCoin())
			chunk.setVine(x4, y, z4, direction);
	}

	private void generateHangingVine(RealBlocks chunk, int y, BlockFace direction, int x1, int z1, int x2, int z2) {
		if (chunkOdds.flipCoin() && chunk.isEmpty(x1, y, z1) && !chunk.isEmpty(x2, y, z2))
			chunk.setVine(x1, y, z1, direction);
	}
	
	protected boolean generateLightPost(CityWorldGenerator generator, RealBlocks chunk, DataContext context, int sidewalkLevel, int x, int z) {
		if(lightPost.canPlace()) {
			if(chunk.getChunkX() % 2 == 0 && chunk.getChunkZ() % 2 == 0) {
				int blockX = chunk.getBlockX(x);
				int blockZ = chunk.getBlockZ(x);
				lightPost.paste(blockX, sidewalkLevel, blockZ, generator.getWorld(), false);
			}else if(chunk.getChunkX() % 2 != 0 && chunk.getChunkZ() % 2 != 0) {
				int blockX = chunk.getBlockX(x);
				int blockZ = chunk.getBlockZ(x);
				lightPost.paste(blockX, sidewalkLevel, blockZ, generator.getWorld(), false);
			}
		}else {
			chunk.setBlock(x, sidewalkLevel, z, lightpostbaseMaterial);
			if (generator.getSettings().includeDecayedRoads) {
				int y = sidewalkLevel + 1;
				while (y < sidewalkLevel + lightpostHeight + 1) {
					if (chunkOdds.playOdds(0.25))
						break;
					chunk.setBlock(x, y, z, lightpostMaterial);
					y++;
				}
				if (y > sidewalkLevel + lightpostHeight) {
					if (chunkOdds.playOdds(0.75))
						chunk.setBlock(x, y, z, context.lightMat);
					return true;
				}
				return false;
			} else {
				chunk.setBlocks(x, sidewalkLevel + 1, sidewalkLevel + lightpostHeight + 1, z, lightpostMaterial);
				chunk.setBlock(x, sidewalkLevel + lightpostHeight + 1, z, context.lightMat);
				return true;
			}
		}
		return true;
	}

//	protected boolean generateLightPost(CityWorldGenerator generator, RealBlocks chunk, DataContext context, int sidewalkLevel, int x, int z) {
//		chunk.setBlock(x, sidewalkLevel, z, Material.DIAMOND_BLOCK);
//		if (generator.getSettings().includeDecayedRoads) {
//			int y = sidewalkLevel + 1;
//			while (y < sidewalkLevel + lightpostHeight + 1) {
//				if (chunkOdds.playOdds(0.25))
//					break;
//				chunk.setBlock(x, y, z, lightpostMaterial);
//				y++;
//			}
//			if (y > sidewalkLevel + lightpostHeight) {
//				if (chunkOdds.playOdds(0.75))
//					chunk.setBlock(x, y, z, context.lightMat);
//				return true;
//			}
//			return false;
//		} else {
//			chunk.setBlocks(x, sidewalkLevel + 1, sidewalkLevel + lightpostHeight + 1, z, lightpostMaterial);
//			chunk.setBlock(x, sidewalkLevel + lightpostHeight + 1, z, context.lightMat);
//			return true;
//		}
//	}

	private final static double oddsOfDecayedSign = Odds.oddsExtremelyLikely;

	protected void generateStreetSign(CityWorldGenerator generator, RealBlocks chunk, int sidewalkLevel, int x, int z) {
		int cx = chunk.sectionX;
		int cz = chunk.sectionZ;
		int y = sidewalkLevel + lightpostHeight;

		// decay or not?
		if (generator.getSettings().includeDecayedRoads) {

			// put the signs up
			if (chunkOdds.playOdds(oddsOfDecayedSign)) {
				String[] odonym = generator.odonymProvider.generateNorthSouthStreetOdonym(generator, cx, cz);
				generator.odonymProvider.decaySign(chunkOdds, odonym);
				chunk.setWallSign(x, y, z - 1, BlockFace.NORTH, odonym);
			}
			if (chunkOdds.playOdds(oddsOfDecayedSign)) {
				String[] odonym = generator.odonymProvider.generateNorthSouthStreetOdonym(generator, cx, cz);
				generator.odonymProvider.decaySign(chunkOdds, odonym);
				chunk.setWallSign(x, y, z + 1, BlockFace.SOUTH, odonym);
			}
			if (chunkOdds.playOdds(oddsOfDecayedSign)) {
				String[] odonym = generator.odonymProvider.generateWestEastStreetOdonym(generator, cx, cz);
				generator.odonymProvider.decaySign(chunkOdds, odonym);
				chunk.setWallSign(x - 1, y, z, BlockFace.WEST, odonym);
			}
			if (chunkOdds.playOdds(oddsOfDecayedSign)) {
				String[] odonym = generator.odonymProvider.generateWestEastStreetOdonym(generator, cx, cz);
				generator.odonymProvider.decaySign(chunkOdds, odonym);
				chunk.setWallSign(x + 1, y, z, BlockFace.EAST, odonym);
			}
		} else {

			// compute the name for the roads
			String[] odonymNorthSouth = generator.odonymProvider.generateNorthSouthStreetOdonym(generator, cx, cz);
			String[] odonymWestEast = generator.odonymProvider.generateWestEastStreetOdonym(generator, cx, cz);

			// put the signs up
			chunk.setWallSign(x, y, z - 1, BlockFace.NORTH, odonymNorthSouth);
			chunk.setWallSign(x, y, z + 1, BlockFace.SOUTH, odonymNorthSouth);
			chunk.setWallSign(x - 1, y, z, BlockFace.WEST, odonymWestEast);
			chunk.setWallSign(x + 1, y, z, BlockFace.EAST, odonymWestEast);
		}
	}

	private void generateDoor(RealBlocks chunk, int x, int y, int z, BlockFace direction) {
		switch (chunkOdds.getRandomInt(5)) {
		case 1:
			chunk.setBlocks(x, y, y + 2, z, Material.BRICKS);
			break;
		case 2:
			chunk.setFenceDoor(x, y, y + 2, z, Material.IRON_BARS, direction);
			break;
		case 3:
			chunk.setBlocks(x, y, y + 2, z, Material.AIR);
			break;
//		case 4:
//			chunk.setIronDoor(x, y, z, direction);
//			break;
		default:
			chunk.setDoor(x, y, z, Material.BIRCH_DOOR, direction);
			break;
		}
	}

	protected void generateRoundedOut(CityWorldGenerator generator, DataContext context, SupportBlocks chunk, int x,
			int z, boolean toNorth, boolean toEast) {
		int sidewalkLevel = getSidewalkLevel(generator);

		// long bits
		for (int i = 0; i < 4; i++) {
			paveSidewalk(generator, chunk, toNorth ? x + 3 : x, sidewalkLevel, z + i, false);
			paveSidewalk(generator, chunk, x + i, sidewalkLevel, toEast ? z + 3 : z, false);
		}
		// little notch
		paveSidewalk(generator, chunk, toNorth ? x + 2 : x + 1, sidewalkLevel, toEast ? z + 2 : z + 1, false);
	}

	private void generateTreat(CityWorldGenerator generator, SupportBlocks chunk, int x, int y, int z) {

		// cool stuff?
		if (generator.getSettings().treasuresInSewers && chunkOdds.playOdds(generator.getSettings().oddsOfTreasureInSewers)) {
			chunk.setChest(generator, x, y, z, chunkOdds, generator.lootProvider, LootLocation.SEWER);
		}
	}

	private void generateTrick(CityWorldGenerator generator, SupportBlocks chunk, int x, int y, int z) {

		// not so cool stuff?
		generator.spawnProvider.setSpawnOrSpawner(generator, chunk, chunkOdds, x, y, z,
				generator.getSettings().spawnersInSewers, generator.spawnProvider.itemsEntities_Sewers);
	}
}
