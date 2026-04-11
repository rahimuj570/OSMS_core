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

import helper.ConnectionProvider;

/**
 * Servlet implementation class DeleteRoomServlet
 */
@WebServlet("/DeleteRoomServlet")
public class DeleteRoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteRoomServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession sc = req.getSession();
		String roomId = req.getParameter("roomId");
		Connection con = ConnectionProvider.getCon();
		try {
			con.setAutoCommit(false);
			// First delete availability rows
			String sqlAvail = "DELETE FROM room_availability WHERE room_id=?";
			PreparedStatement psAvail = con.prepareStatement(sqlAvail);
			psAvail.setString(1, roomId);
			psAvail.executeUpdate();

			// Then delete the room itself
			String sqlRoom = "DELETE FROM rooms WHERE room_id=?";
			PreparedStatement psRoom = con.prepareStatement(sqlRoom);
			psRoom.setString(1, roomId);
			int rows = psRoom.executeUpdate();

			if (rows > 0) {
				sc.setAttribute("room_true", "Room deleted successfully!");
			} else {
				sc.setAttribute("room_false", "Room not found!");
			}
			con.commit();
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			sc.setAttribute("room_false", "Error occurred at server-side!");
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			resp.sendRedirect(req.getContextPath() + "/dashboard/rooms.jsp");
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
