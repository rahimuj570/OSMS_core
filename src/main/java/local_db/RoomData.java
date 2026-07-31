package local_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithm.Main;
import entity.LabType;
import entity.Room;
import entity.RoomType;
import helper.ConnectionProvider;
import jakarta.servlet.http.HttpSession;

public class RoomData {
//	static long FULL_DAY = (1L << Main.SLOTS_PER_DAY) - 1;
//	static long LUNCH_MASK = ~((1L << 6) | (1L << 7));
//	static long DAY_WITHOUT_LUNCH = FULL_DAY & LUNCH_MASK;
//
//	static Room r1 = new Room("R801", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
//	static Room r2 = new Room("R803", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
//	static Room r3 = new Room("R804", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
//	static Room r4 = new Room("R1502", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
//	static Room r5 = new Room("R1702", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
//	static Room r6 = new Room("R1704", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
//	static Room r7 = new Room("R8011", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
//	static Room r8 = new Room("R8012", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
//	static Room r9 = new Room("R80123", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
//
//	static Room l1 = new Room("L802", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH },
//			LabType.ELECTRONIC);
//	static Room l2 = new Room("L902", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH },
//			LabType.GENERAL);
//	static Room l3 = new Room("L903", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH },
//			LabType.COMPUTER);
//	static Room l4 = new Room("L1503", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH },
//			LabType.COMPUTER);
//	static Room l5 = new Room("R1703", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
//			DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH },
//			LabType.COMPUTER);
//
//	public static Map<String, Room> rooms = Map.ofEntries(Map.entry(r1.id, r1), Map.entry(r2.id, r2),
//			Map.entry(r3.id, r3), Map.entry(r4.id, r4), Map.entry(r5.id, r5), Map.entry(l1.id, l1),
//			Map.entry(l2.id, l2), Map.entry(l3.id, l3), Map.entry(l4.id, l4), Map.entry(l5.id, l5)
//
//			, Map.entry(r6.id, r6), Map.entry(r7.id, r7), Map.entry(r8.id, r8)
//			, Map.entry(r9.id, r9)
//
//	);

	public static Map<String, Room> rooms = new HashMap<String, Room>();

	public static Map<String, Room> getRooms(HttpSession sc) {
		rooms.clear();

		Connection con = ConnectionProvider.getCon();
		try {
			PreparedStatement pst = con
					.prepareStatement("select * from rooms where dept_type='" + sc.getAttribute("dept_type")+"'");
			ResultSet res = pst.executeQuery();

			while (res.next()) {
				String roomId = res.getString("room_id");
				String roomType = res.getString("room_type");
				int capacity = res.getInt("room_capacity");
				String labType = res.getString("lab_type");

				PreparedStatement pst2 = con.prepareStatement("select * from room_availability  where room_id=?");
				pst2.setString(1, roomId);
				ResultSet res2 = pst2.executeQuery();
//				List<Integer> sv = new ArrayList<>(Collections.nCopies(7, 0));
				long[] sv = new long[7];
				while (res2.next()) {
					int day = res2.getInt("slot_day");
					int slotValue = res2.getInt("slot_value");
					sv[day] = slotValue;
				}

				Room r = new Room(roomId, RoomType.valueOf(roomType), capacity, sv,
						labType == null ? null : LabType.valueOf(labType));
				rooms.put(roomId, r);
			}
			pst.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rooms;
	}
}
