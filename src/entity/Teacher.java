package entity;

import java.util.Set;

public class Teacher {
	public String id;
	public Set<String> qualifiedCourseIds;
	public boolean[] availability; // per day bitmask

	public Teacher(String id, Set<String> qualifiedCourseIds, boolean[] availability) {
		this.id = id;
		this.qualifiedCourseIds = qualifiedCourseIds;
		this.availability = availability;
	}
}
