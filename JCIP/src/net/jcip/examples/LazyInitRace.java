package net.jcip.examples;

import net.jcip.annotations.NotThreadSafe;

/**
 * LazyInitRace
 * 
 * Race condition in lazy initialization
 * 
 * @author Brian Goetz and Tim Peierls
 */

@NotThreadSafe
public class LazyInitRace {
	private ExpensiveObject instance = null;
	//Marvin: 目的有两个
	//1.必须时才 init
	//2.保证仅仅 init 一次
	//
	public ExpensiveObject getInstance() {
		if (instance == null)
			instance = new ExpensiveObject();
		return instance;
	}
}

class ExpensiveObject {
}
