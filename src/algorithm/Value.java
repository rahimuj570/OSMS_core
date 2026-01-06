package algorithm;

public class Value {
	public int day;
	public int startSlot;
	public int slotCount;
	public String roomId;
	public String teacherId;
	public long slotMask;

	public Value(int day, int startSlot, int slotCount, String roomId, String teacherId) {
		this.day = day;
		this.startSlot = startSlot;
		this.slotCount = slotCount;
		this.roomId = roomId;
		this.teacherId = teacherId;

		this.slotMask = ((1L << slotCount) - 1) << startSlot;
	}
}
