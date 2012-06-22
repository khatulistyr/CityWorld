package me.daddychurchill.CityWorld.Plats;

import me.daddychurchill.CityWorld.PlatMap;

public abstract class PlatIsolated extends PlatLot {

	public PlatIsolated(PlatMap platmap, int chunkX, int chunkZ) {
		super(platmap, chunkX, chunkZ);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public long getConnectedKey() {
		return -1;
	}

	@Override
	public boolean makeConnected(PlatLot relative) {
		return false;
	}

	@Override
	public boolean isConnectable(PlatLot relative) {
		return false;
	}

	@Override
	public boolean isConnected(PlatLot relative) {
		return false;
	}

	@Override
	public PlatLot[][] getNeighborPlatLots(PlatMap platmap, int platX, int platZ, boolean onlyConnectedNeighbors) {
		return null;
	}

}
