package com.dartin.project.server.processes.story.resources;

/**
 * @author Daniil Yurkov on 23.11.2016.
 */
public abstract class Location {

    protected String name;
    public static final Location UNIVERSE = new Location("Universe"){
        @Override
        public String toString() {
            return "root.Location = Universe";
        }
    };
    
    protected Location(String name){
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public abstract String toString();
    
}
