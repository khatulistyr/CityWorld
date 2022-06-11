package me.daddychurchill.CityWorld.Plugins;

import org.bukkit.Material;

public class MaterialObject {

	private final Material material;
	private final int iterations, amount;
	private final int minY, maxY;
	private final boolean ore_upper, ore_liquid;
	private int amountgenerate = 0;
	
	public MaterialObject(Material material, int iterations,int amount,int minY,int maxY) {
		this(material, iterations, amount, minY, maxY, false, false);
	}
	
	public MaterialObject(Material material, int iterations,int amount,int minY,int maxY, boolean ore_upper, boolean ore_liquid ) {
		this.material = material;
		this.iterations = iterations;
		this.amount = amount;
		this.minY = minY;
		this.maxY = maxY;
		this.ore_liquid = ore_liquid;
		this.ore_upper = ore_upper;
	}

	public Material getMaterial() {
		return material;
	}

	public int getIterations() {
		return iterations;
	}

	public int getAmount() {
		return amount;
	}

	public int getMinY() {
		return minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public boolean isOre_upper() {
		return ore_upper;
	}

	public boolean isOre_liquid() {
		return ore_liquid;
	}
	
	public boolean matchMaterial(Material material) {
		return material == this.material;
	}
	
	public MaterialObject clone(int nth) {
		MaterialObject materialObject = new MaterialObject(material, nth, nth, nth, nth, ore_upper, ore_liquid);
		materialObject.setAmountNth(nth);
		return materialObject;
	}

	public int getAmountNth() {
		return amountgenerate;
	}

	public void setAmountNth(int amountgenerate) {
		this.amountgenerate = amountgenerate;
	}
	
}
