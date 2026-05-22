package entity;

import java.util.ArrayList;

public class Teacher {
	public int id;
	public String name;
	public ArrayList<Boolean> availability;
	public int maxSlotHours;

	public Teacher(int id, String name, ArrayList<Boolean> avail, int maxSlotHours) {
		this.id = id;
		this.name = name;
		this.availability = avail;
		this.maxSlotHours = maxSlotHours;
	}
}
