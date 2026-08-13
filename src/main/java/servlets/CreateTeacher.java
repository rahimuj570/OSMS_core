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
 * Servlet implementation class CreateTeacher
 */
@WebServlet("/CreateTeacher")
public class CreateTeacher extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateTeacher() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession sc = request.getSession();

		String teacherName = request.getParameter("teacherName");
		int teacherMaxSlotHours =Integer.parseInt(request.getParameter("teacherMaxSlotHours"));

		// Fetch multiple checkboxes (availability)
		String[] availability = request.getParameterValues("availability");

		// Example: convert availability into a boolean[7]
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

		// Now you can insert into DB
		Connection con = ConnectionProvider.getCon();
		try (PreparedStatement pst = con.prepareStatement(
				"INSERT INTO teachers (teacher_name,max_slot_hours,dept_type, saturday, sunday, monday, tuesday, wednesday, thursday, friday) VALUES (?,?,?,?,?,?,?,?,?,?)")) {

			pst.setString(1, teacherName);
			pst.setInt(2, teacherMaxSlotHours);
			pst.setString(3, (String)sc.getAttribute("dept_type"));

			for (int i = 0; i < 7; i++) {
				pst.setInt(i + 4, days[i] ? 1 : 0);
			}

			pst.executeUpdate();

			sc.setAttribute("teacher_true", "New teacher created!");
		} catch (SQLException e) {
			sc.setAttribute("teacher_false", "Could not create new teacher. Try Again!");
			e.printStackTrace();
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
