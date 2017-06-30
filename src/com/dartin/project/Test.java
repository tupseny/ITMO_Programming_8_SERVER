package com.dartin.project;

import com.dartin.project.server.data.DatabaseManager;
import com.dartin.util.Item;

import javax.xml.crypto.Data;

/**
 * Created by Martin on 30.06.2017.
 */
public class Test {
    public static void main(String[] args) {
        Testing test = new Testing();
        test.setName("ello");
        test.setSize("size");
        DatabaseManager.deleteTFromTable(test);
    }

    public static class Testing{
        private String name;
        private String size;

        public String getName(){
            return name;
        }

        public String getSize(){
            return size;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }
}
