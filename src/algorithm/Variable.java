package algorithm;

import java.util.*;

import entity.Course;

public class Variable {
	public String id;
	public Course course;
	public List<Value> domain = new ArrayList<>();
	public boolean assigned = false;
	public Value assignedValue = null;
	public Set<String> neighbors = new HashSet<>();

	public Variable(String id, Course course) {
		this.id = id;
		this.course = course;
	}
}
