package com.syniverse.info;

import java.util.concurrent.Future;

public class FutureVL {
	public final Future<VResultInfo> futureV;
	public final Future<LResultInfo> futureL;

	public FutureVL(Future<VResultInfo> futureV,
			Future<LResultInfo> futureL) {
		this.futureV = futureV;
		this.futureL = futureL;
	}
}
