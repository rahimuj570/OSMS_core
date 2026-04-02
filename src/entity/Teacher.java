package entity;


public class Teacher {
	public String id;
	public boolean[] availability;

	public Teacher(String id, boolean[] availability) {
		this.id = id;
		this.availability = availability;
	}
}
