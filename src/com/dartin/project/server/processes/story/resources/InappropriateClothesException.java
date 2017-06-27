package com.dartin.project.server.processes.story.resources;

/**
 * Created by Daniil Y on 20.01.2017.
 */
public class InappropriateClothesException extends RuntimeException{
	
	public InappropriateClothesException(){
		super();
	}
	
	public InappropriateClothesException(String message){
		super(message);
	}
}
