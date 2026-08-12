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
 * Servlet implementation class EditTeacherServlet
 */
@WebServlet("/EditTeacherServlet")
public class EditTeacherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditTeacherServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession sc = request.getSession();
		String teacherId = request.getParameter("teacherId");
		String teacherName = request.getParameter("editTeacherName");
		int teacherSlotMaxHours =Integer.parseInt(request.getParameter("editTeacherMaxSlotHours"));
		String[] availability = request.getParameterValues("availability");
		

		// Convert availability into boolean[7]
		boolean[] days = new boolean[7];
		String[] dayNames = { "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };

		if (availability != null) {
			for (String day : availability) {
				for (int i = 0; i < dayNames.length; i++) {
					if (dayNames[i].equals(day)) {
						days[i] = true;
					}
				}
			}
		}
		Connection con = ConnectionProvider.getCon();
		try (PreparedStatement pst = con.prepareStatement(
				"UPDATE teachers SET teacher_name=?,max_slot_hours=?, saturday=?, sunday=?, monday=?, tuesday=?, wednesday=?, thursday=?, friday=? WHERE teacher_id=?")) {

			pst.setString(1, teacherName);
			pst.setInt(2, teacherSlotMaxHours);
			for (int i = 0; i < 7; i++) {
				pst.setInt(i + 3, days[i] ? 1 : 0);
			}
			pst.setString(10, teacherId);

			int rows = pst.executeUpdate();
			if (rows > 0) {
				sc.setAttribute("teacher_true", "Teacher edited successfully.");
			} else {
				sc.setAttribute("teacher_true", "No teacher found with ID: " + teacherId);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			sc.setAttribute("teacher_true", "Error updating teacher: " + e.getMessage());
		} finally {
			response.sendRedirect(request.getContextPath() + "/dashboard/teachers.jsp");
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
