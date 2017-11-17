package marvin.doit.writegeneric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
	public static List<? extends Beverage> write2() {
		List<Beverage> list = new ArrayList<Beverage>();
		list.add(new Beverage());
		list.add(new Cola());
		list.add(new CocaCola());
		list.add(new PepsiCola());
		list.add(new Sprite());
		return list;
	}

	public static void readFrom(List<? extends Beverage> list) {
		for (Iterator<? extends Beverage> it = list.iterator(); it.hasNext();) {
			Beverage oneBeverage = it.next();
			System.out.println(oneBeverage);
		}
	}

	public static void main(String[] args) {
		List<? extends Beverage> list = write2();
		readFrom(list);

		// Always get from "extends"
		List<? extends Cola> list1 = new ArrayList<PepsiCola>();
		List<? extends Cola> list_Error1 = new ArrayList<Beverage>();
		// Marvin:虽然得到的总是子类，但，由于不确定是哪个 specific subtype。所以，取出后，总是向上转型到 Cola（或以上）
		Beverage    a = list1.get(0);
		Cola        b = list1.get(1);
		PepsiCola   c = list1.get(2);

		// Always super add
		List<? super Cola> list2 = new ArrayList<Beverage>();
		List<? super Cola> list_Error2 = new ArrayList<PepsiCola>();
		// Marvin: 子类，多个子类。都是可以加入的（父类是不行滴）
		list2.add(new Beverage());
		list2.add(new Cola());
		list2.add(new PepsiCola());
		System.out.println(list2);
	}
}
