package com.marvin;

import daniel.otherpackage.SonOther;

public class Son extends Father {
	public void access_Owned_field() {
		System.out.println(protectedMoney);
		System.out.println(friendly);
	}

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
