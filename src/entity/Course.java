package entity;

public class Course {
	public String id;
	public CourseType type;
	public String teacherId;
	public String sectionId;
//	public int students;

	public Course(String id, CourseType type, String teacherId, String sectionId) {
		this.id = id;
		this.type = type;
		this.teacherId = teacherId;
		this.sectionId = sectionId;
//		this.students = students;
	}
}
