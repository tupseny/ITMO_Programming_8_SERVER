package com.dartin.project.server.processes.story.resources;

import com.dartin.util.Item;

import java.time.LocalDate;

/**
 * @author Daniil Yurkov on 16.11.2016.
 */
public class Appearance {
	
	private Clothes head;
	private Clothes top;
	private Clothes bottom;
	public static final String nothing = "undefined";
	
	public Appearance(Clothes head, Clothes top, Clothes bottom) {
		if (head.bodyPart != Clothes.BodyPart.HEAD) throw new InappropriateClothesException("Inappropriate head");
		if (top.bodyPart != Clothes.BodyPart.TOP) throw new InappropriateClothesException("Inappropriate top");
		if (bottom.bodyPart != Clothes.BodyPart.BOTTOM) throw new InappropriateClothesException("Inappropriate bottom");
		this.head = head;
		this.top = top;
		this.bottom = bottom;
	}
	
	public Appearance() {
		head = new Clothes(
				"nothing",
				"unpaintable",
				Clothes.BodyPart.HEAD
		);
		top = new Clothes(
				"nothing",
				"unpaintable",
				Clothes.BodyPart.TOP
		);
		bottom = new Clothes(
				"nothing",
				"unpaintable",
				Clothes.BodyPart.BOTTOM
		);
	}
	
	public Clothes getHead() {
		return head;
	}
	
	public Clothes getTop() {
		return top;
	}
	
	public Clothes getBottom() {
		return bottom;
	}
	
	public void setHead(Clothes head) {
		this.head = head;
	}
	
	public void setTop(Clothes top) {
		this.top = top;
	}
	
	public void setBottom(Clothes bottom) {
		this.bottom = bottom;
	}

	@Override
	public String toString() {
		StringBuilder description = new StringBuilder();
		if (!head.equals(nothing)) description.append("head: " + head);
		if (!top.equals(nothing)) {
			if (!head.equals(nothing)) description.append(", ");
			description.append("top: " + top);
		}
		if (!bottom.equals(nothing)) {
			if (!top.equals(nothing) || !bottom.equals(nothing)) description.append(", ");
			description.append("bottom: " + bottom);
		}
		return description.toString();
	}
	
	public static class Clothes extends Item {
		
		private String color;
		private BodyPart bodyPart;
		
		public enum BodyPart{
			HEAD, TOP, BOTTOM
		}
		
		public Clothes(String name, String color, BodyPart bodyPart){
			super(name, "wears on " + bodyPart, Size.NORMAL, LocalDate.now());
			this.color = color;
			this.bodyPart = bodyPart;
		}
		
		public BodyPart getBodyPart(){
			return bodyPart;
		}
		
		@Override
		public String toString() {
			return "Clothes=\"" + color + " " + name + " for " + bodyPart.toString().toLowerCase() + "\"";
		}
	}
}
