package effective2.Chapter6.Item33;

// Using an EnumMap to associate data with an enum - Pages 161-162

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Simplistic class representing a culinary herb - Page 161
public class Herb {
	public enum HerbTypes {
		ANNUAL, PERENNIAL, BIENNIAL
	}

	private final String name;
	private final HerbTypes type;

	Herb(String name, HerbTypes type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() {
		return name;
	}

	// Using ordinal() to index an array - DON'T DO THIS!
	public static void wrongMethod() {
		Herb[] garden = { new Herb("Basil", HerbTypes.ANNUAL),
				new Herb("Carroway", HerbTypes.BIENNIAL),
				new Herb("Dill", HerbTypes.ANNUAL),
				new Herb("Lavendar", HerbTypes.PERENNIAL),
				new Herb("Parsley", HerbTypes.BIENNIAL),
				new Herb("Rosemary", HerbTypes.PERENNIAL) };
		//数组 和 泛型 不兼容。所以，必须 强转数组 到泛型类型 
		@SuppressWarnings({ "unchecked" })
		Set<Herb>[] herbsByType = (Set<Herb>[]) new Set[HerbTypes.values().length];

		for (int i = 0; i < herbsByType.length; i++) {
			herbsByType[i] = new HashSet<Herb>();
		}
		for (Herb h : garden) {
			herbsByType[h.type.ordinal()].add(h);
		}
		for (int i = 0; i < herbsByType.length; i++) {
			System.out
					.printf("%s: %s%n", HerbTypes.values()[i], herbsByType[i]);
		}

	}

	public static void main(String[] args) {
		Herb[] garden = { new Herb("Basil", HerbTypes.ANNUAL),
				new Herb("Carroway", HerbTypes.BIENNIAL),
				new Herb("Dill", HerbTypes.ANNUAL),
				new Herb("Lavendar", HerbTypes.PERENNIAL),
				new Herb("Parsley", HerbTypes.BIENNIAL),
				new Herb("Rosemary", HerbTypes.PERENNIAL) };

		// Using an EnumMap to associate data with an enum - Page 162
		Map<Herb.HerbTypes, Set<Herb>> herbsByType = new EnumMap<Herb.HerbTypes, Set<Herb>>(
				Herb.HerbTypes.class);
		for (Herb.HerbTypes t : Herb.HerbTypes.values())
			herbsByType.put(t, new HashSet<Herb>());
		for (Herb h : garden)
			herbsByType.get(h.type).add(h);
		System.out.println(herbsByType);
	}
}
