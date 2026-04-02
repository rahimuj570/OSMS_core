package entity;

import java.util.Set;

public class Course {
	public String id;
	public CourseType type;
	public Set<String> sectionIds;
	public Set<String> preferredTeachers;
	public Set<String> forbiddenTeachers;

	public Course(String id, CourseType type, Set<String> preferred,  Set<String> sections,  Set<String> forbidden) {
		this.id = id;
		this.type = type;
		this.preferredTeachers = preferred;
		this.forbiddenTeachers = forbidden;
		this.sectionIds = sections;
	}
}
