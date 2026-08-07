package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import helper.ConnectionProvider;

/**
 * Servlet implementation class AddRoomServlet
 */
@WebServlet("/AddRoomServlet")
public class AddRoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddRoomServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	// Map slot string to bit position
	private static final Map<String, Integer> SLOT_MAP = new HashMap<>();
	static {
		SLOT_MAP.put("9:00-9:30", 13);
		SLOT_MAP.put("9:30-10:00", 12);
		SLOT_MAP.put("10:00-10:30", 11);
		SLOT_MAP.put("10:30-11:00", 10);
		SLOT_MAP.put("11:00-11:30", 9);
		SLOT_MAP.put("11:30-12:00", 8);
		SLOT_MAP.put("12:00-12:30", 7);
		SLOT_MAP.put("12:30-1:00", 6);
		SLOT_MAP.put("1:00-1:30", 5);
		SLOT_MAP.put("1:30-2:00", 4);
		SLOT_MAP.put("2:00-2:30", 3);
		SLOT_MAP.put("2:30-3:00", 2);
		SLOT_MAP.put("3:00-3:30", 1);
		SLOT_MAP.put("3:30-4:00", 0);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession sc = req.getSession();

		{

			String roomId = req.getParameter("roomId");
			String roomType = req.getParameter("roomType");
			String labType = req.getParameter("labType");
			String capacityStr = req.getParameter("roomCapacity");

			if (!"LAB".equalsIgnoreCase(roomType)) {
				labType = null;
			}

			int capacity;
			try {
				capacity = Integer.parseInt(capacityStr);
			} catch (Exception e) {
				sc.setAttribute("room_false", "Invalid capacity!");
				return;
			}

			Connection con = ConnectionProvider.getCon();
			try {
				// Insert into rooms
				String sqlRoom = "INSERT INTO rooms (room_id, room_type, room_capacity, lab_type, dept_type) VALUES (?,?,?,?,?)";
				PreparedStatement psRoom = con.prepareStatement(sqlRoom);
				psRoom.setString(1, roomId);
				psRoom.setString(2, roomType);
				psRoom.setInt(3, capacity);
				if (labType == null || labType.isEmpty()) {
					psRoom.setNull(4, java.sql.Types.VARCHAR);
				} else {
					psRoom.setString(4, labType);
				}
				psRoom.setString(5, (String)sc.getAttribute("dept_type"));
				psRoom.executeUpdate();

				for (int day = 0; day <= 6; day++) {
					String[] slots = req.getParameterValues("slots_" + day);
					int slotValue = 0;
					if (slots != null) {
						for (String s : slots) {
							Integer bit = SLOT_MAP.get(s);
							if (bit != null) {
								slotValue |= (1 << bit);
							}
						}
					}
					// Insert availability row
					String sqlAvail = "INSERT INTO room_availability (room_id, slot_day, slot_value) VALUES (?,?,?)";
					PreparedStatement psAvail = con.prepareStatement(sqlAvail);
					psAvail.setString(1, roomId);
					psAvail.setInt(2, day);
					psAvail.setInt(3, slotValue);
					psAvail.executeUpdate();
				}
				sc.setAttribute("room_true", "Room added successfully!");
			} catch (Exception e) {
				e.printStackTrace();
				sc.setAttribute("room_false", "Error occurred at server-side!");
			} finally {
				try {
					if (con != null)
						con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				resp.sendRedirect(req.getContextPath() + "/dashboard/rooms.jsp");
			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
