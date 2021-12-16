package me.daddychurchill.CityWorld.Clipboard;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.gson.BlockVectorAdapter;

import me.daddychurchill.CityWorld.CityWorld;
import me.daddychurchill.CityWorld.CityWorldAPI;
import me.daddychurchill.CityWorld.CityWorldGenerator;

public class ClipboardStructure {

	private com.sk89q.worldedit.extent.clipboard.Clipboard clipboard = null;
	
	public ClipboardStructure(File shematic) {
		CityWorld.log.info(shematic.getAbsolutePath().toString() + " " + shematic.exists());
		ClipboardFormat format = ClipboardFormats.findByFile(shematic);
        try (ClipboardReader reader = format.getReader(new FileInputStream(shematic))) {
            clipboard = reader.read();
            CityWorld.log.info("before " + clipboard.getOrigin());
            BlockVector3 center = BlockVector3.at(0, 2, 0);
            clipboard.setOrigin(clipboard.getOrigin().add(center));
            CityWorld.log.info("after " + clipboard.getOrigin());
        }catch(Exception ex) {
        	clipboard = null;
        }
	}
	
	public void paste(double x, double y, double z, World world, boolean useAir) {
		this.paste(x, y, z, world, useAir, 0);
	}
	
	public void paste(double x, double y, double z, World world, boolean useAir, BlockFace face) {
		this.paste(x, y, z, world, useAir, FaceToYaw(face));
	}
	
	public int FaceToYaw(final BlockFace face) {
        switch (face) {
            case NORTH:
                return 0;
            case EAST:
                return 90;
            case SOUTH:
                return 180;
            case WEST:
                return 270;
            default:
                return 0;
        }
    }
	
	public void paste(double x, double y, double z, World world, boolean useAir, int rotation) {
		if(Objects.nonNull(clipboard)) {
			 ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
			 
			 if(rotation > 0) {
				 AffineTransform transform = new AffineTransform();
				 transform.rotateY(rotation);
				 clipboardHolder.setTransform(transform);
				 
			 }
			 try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1)) {
		            Operation operation = clipboardHolder.createPaste(editSession).to(BlockVector3.at(x, y, z)).copyBiomes(false).ignoreAirBlocks(!useAir).build();
		            Operations.complete(operation);
		     }catch(Exception ex) {
		    	 ex.printStackTrace();
		     }
		}
	}
	
	private String toCamelCase(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
	}
	
	public boolean canPlace() {
		return Objects.nonNull(this.clipboard);
	}
	
}
