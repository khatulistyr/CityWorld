package me.daddychurchill.CityWorld.Plugins;

import java.util.ArrayList;

import org.bukkit.Material;

import me.daddychurchill.CityWorld.CityWorld;
import me.daddychurchill.CityWorld.CityWorldGenerator;

class OreProvider_Normal extends OreProvider {

	public OreProvider_Normal(CityWorldGenerator generator) {
		super(generator);
		ore_types = new ArrayList<>();
		ore_types.add(generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(0, Material.WATER));
		ore_types.add(generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(1, Material.LAVA));
		ore_types.add(generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(2, Material.GRAVEL));
		ore_types.add(generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(3, Material.COAL_ORE));
		ore_types.add(generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(4, Material.IRON_ORE));

		ore_types.add(generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(5, Material.GOLD_ORE));
		ore_types.add(generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(6, Material.LAPIS_ORE));
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(7, Material.REDSTONE_ORE));
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(8, Material.DIAMOND_ORE));
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(9, Material.EMERALD_ORE));
		
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(10, Material.COPPER_ORE));
		
		
		ore_types.add(generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(11, Material.DEEPSLATE_COAL_ORE));
		ore_types.add(generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(12, Material.DEEPSLATE_IRON_ORE));

		ore_types.add(generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(13, Material.DEEPSLATE_GOLD_ORE));
		ore_types.add(generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(14, Material.DEEPSLATE_LAPIS_ORE));
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(15, Material.DEEPSLATE_REDSTONE_ORE));
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(16, Material.DEEPSLATE_DIAMOND_ORE));
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(17, Material.DEEPSLATE_EMERALD_ORE));
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(18, Material.DEEPSLATE_COPPER_ORE));
		
		
		//special.ores
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(19, Material.CLAY));
		
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(20, Material.ANCIENT_DEBRIS));
		
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(21, Material.AMETHYST_BLOCK));
		
		//bettershape
		
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(22, Material.STONE));
		
		ore_types.add(
				generator.materialProvider.itemsMaterialListFor_NormalOres.getNthMaterial(23, Material.DEEPSLATE));
		
		ore_types.stream().forEach(entry -> {
			CityWorld.log.info(entry.name());
		});
		
	}
}
