package entity;

import java.util.Set;

public class Course {
	public String id;
	public CourseType type;
	public Set<String> teacherIds;
	public Set<String> sectionIds;

	public Course(String id, CourseType type, Set<String> teacherIds, Set<String> sectionIds) {
		this.id = id;
		this.type = type;
		this.teacherIds = teacherIds;
		this.sectionIds = sectionIds;
	}
}
