package entity;

import java.util.Set;
import java.util.stream.Collectors;

public class Course {
	public String id;
	public CourseType type;
	public Set<String> sectionIds;
	public Set<Integer> preferredTeachers;
	public Set<Integer> forbiddenTeachers;
	public LabType requiredLab;

	public Course(String id, CourseType type, Set<Integer> preferred, Set<String> sections, Set<Integer> forbidden,
			LabType reqLabType) {
		this.id = id;
		this.type = type;
		this.preferredTeachers = preferred;
		this.forbiddenTeachers = forbidden;
		this.sectionIds = sections;
		this.requiredLab = reqLabType;
	}
	
	
	// --- Helper methods for JSP ---
    public String getSectionsCsv() {
        return (sectionIds == null || sectionIds.isEmpty())
                ? ""
                : sectionIds.stream().collect(Collectors.joining(","));
    }

    public String getPreferredTeachersCsv() {
        return (preferredTeachers == null || preferredTeachers.isEmpty())
                ? ""
                : preferredTeachers.stream()
                                   .map(String::valueOf)
                                   .collect(Collectors.joining(","));
    }

    public String getForbiddenTeachersCsv() {
        return (forbiddenTeachers == null || forbiddenTeachers.isEmpty())
                ? ""
                : forbiddenTeachers.stream()
                                   .map(String::valueOf)
                                   .collect(Collectors.joining(","));
    }

    public String getRequiredLabString() {
        return requiredLab == null ? "" : requiredLab.name();
    }

    public String getTypeString() {
        return type == null ? "" : type.name();
    }
}
