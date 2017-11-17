package com.syniverse.loader;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class SetNameThreadFactory implements ThreadFactory {
	private final String name;
	private ThreadFactory factory;

	public SetNameThreadFactory(final String name) {
		this.name = name;
		factory = Executors.defaultThreadFactory();
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = factory.newThread(r);
		t.setName(name + "-" + t.getName());
		return t;
	}

}
