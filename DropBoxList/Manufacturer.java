package com.isaacson.josie.jisaacsonlab9;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Manufacturer implements Serializable {
    private String name;
    private ArrayList<String> vehicles;


    /*
    In addition to a constructor, you’ll likely want methods for getting the manufacturer name, g
    etting a particular model name (by position in the collection), deleting a particular model,
    getting the number of models, and adding a model (or maybe adding a collection of models).
     */

    public Manufacturer(String name){
        this.name = name;
        vehicles = new ArrayList<>();
    }

    public String getName(){
        return this.name;
    }

    public String getModelName(int position){
        return vehicles.get(position);
    }

    public void removeModel(int position){  //is this by position or name passed in??
        vehicles.remove(position);
    }

    public int getTotalModels(){
        return vehicles.size();
    }

    public void addModel(String name){
        vehicles.add(name);
    }

    public void addModel(Collection<String> models){
        vehicles.addAll(models);
    }
}
