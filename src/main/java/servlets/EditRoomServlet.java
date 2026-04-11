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
 * Servlet implementation class EditRoomServlet
 */
@WebServlet("/EditRoomServlet")
public class EditRoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditRoomServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	// Map time strings to their bit positions (matching your JSP's 1 << 13 down to
	// 1 << 0)
	private static final Map<String, Integer> SLOT_MAP = new HashMap<>();
	static {
		String[] labels = { "9:00-9:30", "9:30-10:00", "10:00-10:30", "10:30-11:00", "11:00-11:30", "11:30-12:00",
				"12:00-12:30", "12:30-1:00", "1:00-1:30", "1:30-2:00", "2:00-2:30", "2:30-3:00", "3:00-3:30",
				"3:30-4:00" };
		for (int i = 0; i < labels.length; i++) {
			SLOT_MAP.put(labels[i], 13 - i);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		String roomId = request.getParameter("roomId");
		String capacity = request.getParameter("roomCapacity");
		String roomType = request.getParameter("roomType");
		String labType = request.getParameter("labType");

		// Simple SQL queries
		String updateRoomSQL = "UPDATE rooms SET room_type = ?, room_capacity = ?, lab_type = ? WHERE room_id = ?";
		String deleteAvailabilitySQL = "DELETE FROM room_availability WHERE room_id = ?";
		String insertAvailabilitySQL = "INSERT INTO room_availability (room_id, slot_day, slot_value) VALUES (?, ?, ?)";

		Connection conn = ConnectionProvider.getCon();

		try {
			conn.setAutoCommit(false); // Transactions are important to explain!

			// 1. Update basic Room Info
			try (PreparedStatement psRoom = conn.prepareStatement(updateRoomSQL)) {
				psRoom.setString(1, roomType);
				psRoom.setInt(2, Integer.parseInt(capacity));
				psRoom.setString(3, "LAB".equals(roomType) ? labType : null);
				psRoom.setString(4, roomId);
				psRoom.executeUpdate();
			}

			// 2. Clear old availability (Delete)
			try (PreparedStatement psDel = conn.prepareStatement(deleteAvailabilitySQL)) {
				psDel.setString(1, roomId);
				psDel.executeUpdate();
			}

			// 3. Add new availability (Insert)
			try (PreparedStatement psIns = conn.prepareStatement(insertAvailabilitySQL)) {
				for (int day = 0; day < 7; day++) {
					String[] selectedSlots = request.getParameterValues("slots_" + day);
					long dayMask = 0;

					if (selectedSlots != null) {
						for (String slot : selectedSlots) {
							if (SLOT_MAP.containsKey(slot)) {
								dayMask |= (1L << SLOT_MAP.get(slot));
							}
						}
					}

					psIns.setString(1, roomId);
					psIns.setInt(2, day);
					psIns.setLong(3, dayMask);
					psIns.addBatch(); // Batching makes it faster
				}
				psIns.executeBatch();
			}

			conn.commit(); // Save all changes
			session.setAttribute("room_true", "Room updated successfully!");

		} catch (Exception e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			e.printStackTrace();
			session.setAttribute("room_false", "Error: " + e.getMessage());
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Important: Close connection if not handled by ConnectionProvider
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		response.sendRedirect(request.getContextPath() + "/dashboard/rooms.jsp");
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
