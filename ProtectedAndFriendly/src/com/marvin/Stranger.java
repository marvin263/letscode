package com.marvin;

import daniel.otherpackage.SonOther;

public class Stranger {
	public void access_field_OfOtherInstance() {
		Father f = new Father();
		System.out.println(f.protectedMoney);
		System.out.println(f.friendly);

		Son s = new Son();
		System.out.println(s.protectedMoney);
		System.out.println(s.friendly);

		SonOther so = new SonOther();
		System.out.println(so.protectedMoney);
		System.out.println(so.friendly);
	}
}
