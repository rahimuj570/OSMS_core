package algorithm;

import java.util.*;

import entity.Course;
import entity.Section;

public class Variable {
	public String id;
	public Course course;
	public Section section;
	
	public boolean skipped = false;
	
	public List<Value> domain = new ArrayList<>();
	public boolean assigned = false;
	public Value assignedValue = null;
	public Set<String> neighbors = new HashSet<>();

	public Variable(String id, Course course, Section section) {
		this.id = id;
		this.course = course;
		this.section = section;
	}
}
