package algorithm;

public class TimeSlotInfo {

    public String name;

    public int day;

    public int startSlot;

    public int slotCount;

    public TimeSlotInfo(String name,
                        int day,
                        int startSlot,
                        int slotCount) {

        this.name = name;
        this.day = day;
        this.startSlot = startSlot;
        this.slotCount = slotCount;
    }

    public String getDayName() {

        return RoutinePrinter.DAYS[day];
    }

    public String getTimeRange() {

        return RoutinePrinter.timeRange(startSlot, slotCount);
    }

}