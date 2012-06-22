package me.daddychurchill.CityWorld.Plats;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;

import me.daddychurchill.CityWorld.PlatMap;
import me.daddychurchill.CityWorld.WorldGenerator;
import me.daddychurchill.CityWorld.Context.ContextData;
import me.daddychurchill.CityWorld.Support.ByteChunk;
import me.daddychurchill.CityWorld.Support.RealChunk;

public class PlatObservatory extends PlatIsolated {

	public PlatObservatory(PlatMap platmap, int chunkX, int chunkZ) {
		super(platmap, chunkX, chunkZ);

	}
	
	private final static byte platformId = (byte) Material.SMOOTH_BRICK.getId();
	private final static byte supportId = (byte) Material.COBBLESTONE.getId();
	
	@Override
	protected void generateRandomness() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateChunk(WorldGenerator generator, PlatMap platmap, ByteChunk chunk, BiomeGrid biomes, ContextData context, int platX, int platZ) {
		
//		CityWorld.log.info("Observatory @ " + chunk.worldX + ", " + chunk.worldZ);
		
		// legs
		chunk.setBlocks(2, 4, minHeight, maxHeight - 1, 2, 4, supportId);
		chunk.setBlocks(2, 4, minHeight, maxHeight - 1, 7, 9, supportId);
		chunk.setBlocks(2, 4, minHeight, maxHeight - 1, 12, 14, supportId);
		chunk.setBlocks(7, 9, minHeight, maxHeight - 1, 2, 4, supportId);
		//chunk.setBlocks(7, 9, minHeight, maxHeight - 1, 7, 9, supportId);
		chunk.setBlocks(7, 9, minHeight, maxHeight - 1, 12, 14, supportId);
		chunk.setBlocks(12, 14, minHeight, maxHeight - 1, 2, 4, supportId);
		chunk.setBlocks(12, 14, minHeight, maxHeight - 1, 7, 9, supportId);
		chunk.setBlocks(12, 14, minHeight, maxHeight - 1, 12, 14, supportId);

		// platform
		chunk.setLayer(maxHeight - 1, supportId);
		chunk.setLayer(maxHeight, platformId);
	}
	
	@Override
	public void generateBlocks(WorldGenerator generator, PlatMap platmap, RealChunk chunk, ContextData context, int platX, int platZ) {

		//TODO something!
	}
}
