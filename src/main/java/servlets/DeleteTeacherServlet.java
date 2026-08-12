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
 * Servlet implementation class DeleteTeacherServlet
 */
@WebServlet("/DeleteTeacherServlet")
public class DeleteTeacherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteTeacherServlet() {
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
		int teacherId = Integer.parseInt(request.getParameter("teacher_id"));
		Connection con = ConnectionProvider.getCon();
		try {
			con.setAutoCommit(false);

			// First delete from mapping tables to avoid foreign key constraint errors
			try (PreparedStatement pst1 = con
					.prepareStatement("DELETE FROM course_preferred_teachers WHERE teacher_id=?")) {
				pst1.setInt(1, teacherId);
				pst1.executeUpdate();
			}

			try (PreparedStatement pst2 = con
					.prepareStatement("DELETE FROM course_forbidden_teachers WHERE teacher_id=?")) {
				pst2.setInt(1, teacherId);
				pst2.executeUpdate();
			}

			// Finally delete from teachers table
			try (PreparedStatement pst3 = con.prepareStatement("DELETE FROM teachers WHERE teacher_id=?")) {
				pst3.setInt(1, teacherId);
				int rows = pst3.executeUpdate();

				if (rows > 0) {
					con.commit();
					sc.setAttribute("teacher_true", "Teacher deleted successfully.");

				} else {
					con.rollback();
					sc.setAttribute("teacher_false", "No teacher found with ID: " + teacherId);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			sc.setAttribute("teacher_false", "Error deleting teacher: " + e.getMessage());
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
