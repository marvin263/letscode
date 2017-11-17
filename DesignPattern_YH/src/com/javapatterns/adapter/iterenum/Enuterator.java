package com.javapatterns.adapter.iterenum;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Enumeration;

public class Enuterator implements Iterator
{
	Enumeration enumx;

    public Enuterator(Enumeration enumx)
    {
		this.enumx = enumx;
    }

    public boolean hasNext()
    {
		return enumx.hasMoreElements();
    }

    public Object next() throws NoSuchElementException
    {
        return enumx.nextElement();
    }

    public void remove()
    {
		throw new UnsupportedOperationException();
    }

}
