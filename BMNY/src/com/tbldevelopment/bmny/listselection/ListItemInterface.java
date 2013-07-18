package com.tbldevelopment.bmny.listselection;

public interface ListItemInterface extends Comparable {
	 
    public String getLabel();
    public int compareTo(Object arg0);
}