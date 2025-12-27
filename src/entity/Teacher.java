package entity;

import java.util.Set;

public class Teacher {
	public String id;
	public Set<String> qualifiedCourseIds;
	public long[] availability; // per day bitmask

	public Teacher(String id, Set<String> qualifiedCourseIds, long[] availability) {
		this.id = id;
		this.qualifiedCourseIds = qualifiedCourseIds;
		this.availability = availability;
	}
}
