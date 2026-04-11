package entity;

public class Room {
	public String id;
	public RoomType type;
	public int capacity;
	public long[] availability;
	public LabType labType=null;

	public Room(String id, RoomType type, int capacity, long[] availability, LabType labType) {
		this.id = id;
		this.type = type;
		this.capacity = capacity;
		this.availability = availability;
		this.labType = labType;
	}
}
