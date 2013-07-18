package com.tbldevelopment.bmny.listselection;

public class ListItemObject implements ListItemInterface {
    public String name;
 
    public ListItemObject(String name) {
        this.name = name;
    }
 
    public int compareTo(Object arg0) {
        ListItemObject c = (ListItemObject) arg0;
        return this.getName().compareTo(c.getName());
    }
 
    public String getName(){
        return name;
    }
 
    @Override
    public String toString() {
        return name;
    }
 
    @Override
    public String getLabel() {
        // TODO Auto-generated method stub
        return Character.toString(name.charAt(0));
    }
 
}
