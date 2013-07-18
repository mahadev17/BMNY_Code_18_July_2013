package com.tbldevelopment.bmny.listselection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ListItemContainer<T extends ListItemInterface> {
	 
    ArrayList<T> objects;
 
    public ListItemContainer(){
        objects = new ArrayList<T>();
    }
 
    @SuppressWarnings("unchecked")
	public void addData(T c)
    {
        objects.add(c);
        Collections.sort(objects);
    }
 
    public Map<String, ArrayList<T>> getSortedData()
    {
        Map<String, ArrayList<T>> results = new HashMap<String, ArrayList<T>>();
        ArrayList<T> contacts = null;
        String currletter = null;
 
        for (T c : objects)
        {
            if (!c.getLabel().equals(currletter))
            {
                contacts = new ArrayList<T>();
                currletter = c.getLabel();
                results.put(currletter,contacts);
                System.out.println("making new letter:" + currletter);
            }
            System.out.println("adding" + c.toString());
            contacts.add(c);
        }
        return results;
 
    }
}