package sassa.util;

import java.util.HashMap;
import java.util.Map;


public class Version {

	//Default Version
	static String minecraftVersion = Version.V1_15_2;

	public static final String V9_99_99 = "9.99.9"; // Used to block out unsupported biomes/structures
	public static final String V1_15_2 = "1.15.2";
	public static final String V1_15_1 = "1.15.1";
	public static final String V1_15 = "1.15";
	public static final String V1_14_4 = "1.14.4";
	public static final String V1_14_3 = "1.14.3";
	public static final String V1_14 = "1.14";
	public static final String V1_13_2 = "1.13.2";
	public static final String V1_13_1 = "1.13.1";
	public static final String V1_13 = "1.13";
	public static final String V1_12_2 = "1.12.2";
	public static final String V1_12 = "1.12";
	public static final String V1_11_2 = "1.11.2";
	public static final String V1_11 = "1.11";
	public static final String V1_10_2 = "1.10.2";
	public static final String V1_9_4 = "1.9.4";
	public static final String V1_9_2 = "1.9.2";
	public static final String V1_8_9 = "1.8.9";
	public static final String V1_8_3 = "1.8.3";
	public static final String V1_8_1 = "1.8.1";
	public static final String V1_8 = "1.8";
	public static final String V1_7_10 = "1.7.10";
	//public static final String V1_6_4 = "1.6.4";
	
	
	private static Map<String, Integer> versions = new HashMap<String, Integer>();
	
	public static void registerSupportedVersions() {
		versions.put(V9_99_99, 99999);
		versions.put(V1_15_2, 1502);
		versions.put(V1_15_1, 1501);
		versions.put(V1_15, 1500);
		versions.put(V1_14_4, 1404);
		versions.put(V1_14_3, 1403);
		versions.put(V1_14, 1400);
		versions.put(V1_13_2, 1302);
		versions.put(V1_13_1, 1301);
		versions.put(V1_13, 1300);
		versions.put(V1_12_2, 1202);
		versions.put(V1_12, 1200);
		versions.put(V1_11_2, 1102);
		versions.put(V1_11, 1100);
		versions.put(V1_10_2, 1002);
		versions.put(V1_9_4, 940);
		versions.put(V1_9_2, 920);
		versions.put(V1_8_9, 809);
		versions.put(V1_8_3, 803);
		versions.put(V1_8_1, 801);
		versions.put(V1_8, 800);
		versions.put(V1_7_10, 710);
		//versions.put(V1_6_4, 604);
	}
	
	public static boolean isOrNewerThanVersion(String version) {
		int selected = versions.get(minecraftVersion);
		int wanted = versions.get(version);
		if (selected >= wanted) return true;
		else return false;
	}
	
	public static boolean isOrOlderThanVersion(String version) {
		int selected = versions.get(minecraftVersion);
		int wanted = versions.get(version);
		if (selected <= wanted) return true;
		else return false;
	}
	
//	public static boolean isOrGreaterThanSelectedVersion(int version) {
//		int selected = versions.get(GUI.minecraftVersion);
//		if (version >= selected) return true;
//		else return false;
//	}

	public static Map<String, Integer> getVersions(){
		return versions;
	}

	public static String getDefaultVersion(){
		return minecraftVersion;
	}
	
}
