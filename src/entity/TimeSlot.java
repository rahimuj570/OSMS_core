package entity;

public class TimeSlot {
	public String day;
	public double duration; // 1.5 or 3.0

	public TimeSlot(String day, double duration) {
		this.day = day;
		this.duration = duration;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TimeSlot))
			return false;
		TimeSlot t = (TimeSlot) o;
		return day.equals(t.day) && duration == t.duration;
	}
}
