package me.daddychurchill.CityWorld.Plugins.WorldEdit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import me.daddychurchill.CityWorld.CityWorldGenerator;
import me.daddychurchill.CityWorld.Clipboard.Clipboard;
import me.daddychurchill.CityWorld.Support.RealBlocks;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

public class Clipboard_WorldEdit extends Clipboard {
	private BaseBlock[][][][] blocks;
	private int facingCount;
	private boolean flipableX = false;
	private boolean flipableZ = false;
	private static final String metaExtension = ".yml";
	private static final String tagGroundLevelY = "GroundLevelY";
	private static final String tagFlipableX = "FlipableX";
	private static final String tagFlipableZ = "FlipableZ";
	private static final String tagOddsOfAppearance = "OddsOfAppearance";
	private static final String tagBroadcastLocation = "BroadcastLocation";
	private static final String tagDecayable = "Decayable";
	private com.sk89q.worldedit.extent.clipboard.Clipboard shematic;

	public Clipboard_WorldEdit(CityWorldGenerator generator, File file) throws Exception {
		super(generator, file);
	}

	protected void load(CityWorldGenerator generator, File file) throws Exception {
		YamlConfiguration metaYaml = new YamlConfiguration();
		metaYaml.options().header("CityWorld/WorldEdit schematic configuration");
		metaYaml.options().copyDefaults(true);

		metaYaml.addDefault("GroundLevelY", Integer.valueOf(this.groundLevelY));
		metaYaml.addDefault("FlipableX", Boolean.valueOf(this.flipableX));
		metaYaml.addDefault("FlipableZ", Boolean.valueOf(this.flipableZ));
		metaYaml.addDefault("OddsOfAppearance", Double.valueOf(this.oddsOfAppearance));
		metaYaml.addDefault("BroadcastLocation", Boolean.valueOf(this.broadcastLocation));
		metaYaml.addDefault("Decayable", Boolean.valueOf(this.decayable));

		File metaFile = new File(String.valueOf(file.getAbsolutePath()) + ".yml");
		if (metaFile.exists()) {
			metaYaml.load(metaFile);
			this.groundLevelY = Math.max(0, metaYaml.getInt("GroundLevelY", this.groundLevelY));
			this.flipableX = metaYaml.getBoolean("FlipableX", this.flipableX);
			this.flipableZ = metaYaml.getBoolean("FlipableZ", this.flipableZ);
			this.oddsOfAppearance = Math.max(0.0D,
					Math.min(1.0D, metaYaml.getDouble("OddsOfAppearance", this.oddsOfAppearance)));
			this.broadcastLocation = metaYaml.getBoolean("BroadcastLocation", this.broadcastLocation);
			this.decayable = metaYaml.getBoolean("Decayable", this.decayable);
		}

		com.sk89q.worldedit.extent.clipboard.Clipboard shematic = (new SchematicLoadTask(file)).call();
		
		ClipboardHolder holder = new ClipboardHolder(shematic);
		Region r = holder.getClipboard().getRegion();
		this.sizeX = r.getWidth();
		this.sizeZ = r.getLength();
		this.sizeY = r.getHeight();

		if (this.sizeX == 16 && this.sizeZ == 16) {
			this.shematic = shematic;
		}

		try {
			metaYaml.save(metaFile);
		} catch (IOException e) {

			generator.reportException("[WorldEdit] Could not resave " + metaFile.getAbsolutePath(), e);
		}
	}

	private EditSession getEditSession(CityWorldGenerator generator) {
		return WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(generator.getWorld()),
				this.blockCount);
	}

	private int getFacingIndex(final BlockFace facing) {
        int result = 0;
        switch (facing) {
            case SOUTH: {
                result = 0;
                break;
            }
            case WEST: {
                result = 1;
                break;
            }
            case NORTH: {
                result = 2;
                break;
            }
            default: {
                result = 3;
                break;
            }
        }
        return Math.min(this.facingCount - 1, result);
    }

	public void paste(CityWorldGenerator generator, RealBlocks chunk, BlockFace facing, int blockX, int blockY,
			int blockZ) {
		Vector at = new Vector(blockX, blockY, blockZ);
		try {
			place(generator, getFacingIndex(facing), at, true);
		} catch (Exception e) {
			generator.reportException("[WorldEdit] Place schematic " + this.name + " at " + at + " failed", e);
		}
	}

	private void place(CityWorldGenerator generator, int facing, Vector pos, boolean noAir) throws MaxChangedBlocksException {
		if (Objects.isNull(this.shematic)) return;
		try (EditSession editSession = getEditSession(generator)){
			final Operation operation = new ClipboardHolder(this.shematic)
						.createPaste(editSession)
						.to(BlockVector3.at(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()))
						.ignoreAirBlocks(noAir)
						.build();
			Operations.complete(operation);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class SchematicLoadTask extends Object implements Callable<com.sk89q.worldedit.extent.clipboard.Clipboard> {
		private final File file;

		SchematicLoadTask(File file) {
			this.file = file;
		}

		public com.sk89q.worldedit.extent.clipboard.Clipboard call() throws Exception {
			ClipboardFormat format = ClipboardFormats.findByFile(file);
			try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
			   com.sk89q.worldedit.extent.clipboard.Clipboard clipboard = reader.read();
			   return clipboard;
			}
		}
	}

	public void paste(CityWorldGenerator generator, RealBlocks chunk, BlockFace facing, int blockX, int blockY,
			int blockZ, int x1, int x2, int y1, int y2, int z1, int z2) {
		paste(generator, chunk, facing, blockX, blockY, blockZ);
	}
}
