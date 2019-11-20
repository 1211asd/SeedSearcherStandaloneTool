package main;

import java.util.ArrayList;
import java.util.List;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;

public class StructureSearcher {
	
	public boolean structureFound = false;
	
	public enum Type {
		MINESHAFT, OCEAN_RUINS, OCEAN_FEATURES, VILLAGE, STRONGHOLD, TEMPLE, MANSION, OCEAN_MONUMENT, SLIME_CHUNK, BIOME_DATA
	}
	public static List<WorldIcon> findVillage(World world, CoordinatesInWorld coords) {
		return world.getVillageProducer().getAt(coords, null);
	}

	public static List<WorldIcon> findStronghold(World world, CoordinatesInWorld coords) {
		return world.getStrongholdProducer().getAt(coords, null);
	}

	public static List<WorldIcon> findMansion(World world, CoordinatesInWorld coords) {
		return world.getWoodlandMansionProducer().getAt(coords, null);
	}

	public static List<WorldIcon> findMineshafts(World world, CoordinatesInWorld coords) {
		return world.getMineshaftProducer().getAt(coords, null);
	}
	
	public static List<WorldIcon> findOceanRuins(World world, CoordinatesInWorld coords) {
		List<WorldIcon> ocean_features = findOceanFeatures(world, coords);
		List<WorldIcon> ocean_ruins = new ArrayList<WorldIcon>();
		for (WorldIcon feature : ocean_features) {
			if (feature.getName().toUpperCase().equals("OCEAN RUINS")) {
				System.out.println("has ocean ruin");
				ocean_ruins.add(feature);
			}
		}
		return ocean_ruins;
	}
	
	public static List<WorldIcon> findOceanFeatures(World world, CoordinatesInWorld coords) {
		return world.getOceanFeaturesProducer().getAt(coords, null);
	}
	
	public static List<WorldIcon> findOceanMounments(World world, CoordinatesInWorld coords) {
		return world.getOceanMonumentProducer().getAt(coords, null);
	}
	
	private static CoordinatesInWorld coords(long nwCornerX, long nwCornerY) {
		return CoordinatesInWorld.from(nwCornerX, nwCornerY);
	}

	public static boolean hasStructures(Type[] structures, World world, long nwCornerX, long nwCornerY) {
		CoordinatesInWorld coords = coords(nwCornerX, nwCornerY);
		System.out.println(nwCornerX+","+nwCornerY+"---"+coords.getX()+","+coords.getY());
		for (Type type : structures) {
			if (type.equals(Type.MINESHAFT)) {
				List<WorldIcon> ocean_monuments = StructureSearcher.findMineshafts(
						world,
						coords);
				if (ocean_monuments.size() < 1) return false;
			} else if (type.equals(Type.OCEAN_RUINS)) {
				List<WorldIcon> ocean_ruins = StructureSearcher.findOceanRuins(
						world,
						coords);
				if (ocean_ruins.size() < 1) return false;
			} else if (type.equals(Type.OCEAN_FEATURES)) {
				List<WorldIcon> ocean_features = StructureSearcher.findOceanFeatures(
						world,
						coords);
				if (ocean_features.size() < 1) return false;
			} else if (type.equals(Type.OCEAN_MONUMENT)) {
				List<WorldIcon> ocean_monuments = StructureSearcher.findOceanMounments(
						world,
						coords);
				if (ocean_monuments.size() < 1) return false;
				System.out.println("has ocean monument");
			} else if (type.equals(Type.MANSION)) {
				List<WorldIcon> mansion = StructureSearcher.findMansion(
						world,
						coords);
				System.out.println(mansion + ", " + mansion.size());
				if(mansion.size() > 1){
					System.out.println(mansion.get(1));
				}
				if (mansion.size() < 1) return false;

			} else if (type.equals(Type.STRONGHOLD)) {
				List<WorldIcon> stronghold = StructureSearcher.findStronghold(
						world,
						coords);
				if (stronghold.size() < 1) return false;
			} else if (type.equals(Type.VILLAGE)) {
				List<WorldIcon> village = StructureSearcher.findVillage(
						world,
						coords);
				System.out.println(village + ", " + village.size());
				if(village.size() >= 1){
					WorldIcon test = village.get(0);
					//Found a stupid way of making structures work... need to keep looking into this
					System.out.println(test + ", " + test.getCoordinates().getX());
				}
				if (village.size() >= 1 && village.get(0).getCoordinates().getX() < 100 && village.get(0).getCoordinates().getX() > -100 && village.get(0).getCoordinates().getY() < 100 && village.get(0).getCoordinates().getY() > -100){
					System.out.println("Approved Buildings!");
					return true;
				}
			}
		}
		

		return false;
	}
	
}
