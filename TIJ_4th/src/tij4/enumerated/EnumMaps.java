//: enumerated/EnumMaps.java
// Basics of EnumMaps.
package tij4.enumerated;

import static net.mindview.util.Print.print;
import static net.mindview.util.Print.printnb;
import static tij4.enumerated.AlarmPoints.BATHROOM;
import static tij4.enumerated.AlarmPoints.KITCHEN;
import static tij4.enumerated.AlarmPoints.UTILITY;

import java.util.EnumMap;
import java.util.Map;

interface Command {
	void action();
}

public class EnumMaps {
	public static void main(String[] args) {
		EnumMap<AlarmPoints, Command> em = new EnumMap<AlarmPoints, Command>(
				AlarmPoints.class);
		em.put(KITCHEN, new Command() {
			public void action() {
				print("Kitchen fire!");
			}
		});
		em.put(BATHROOM, new Command() {
			public void action() {
				print("Bathroom alert!");
			}
		});
		System.out.println(em.size());
		// 1、无论是如何放入的，通过 em.entrySet() 取出时，其顺序就是 enum常量 声明的顺序
		for (Map.Entry<AlarmPoints, Command> e : em.entrySet()) {
			printnb(e.getKey() + ": ");
			e.getValue().action();
		}
		try { // If there's no value for a particular key:
			em.get(UTILITY).action();
		} catch (Exception e) {
			print(e);
		}
	}
} /*
 * Output: BATHROOM: Bathroom alert! KITCHEN: Kitchen fire!
 * java.lang.NullPointerException
 */// :~
