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
 * Servlet implementation class DeleteCourseServlet
 */
@WebServlet("/DeleteCourseServlet")
public class DeleteCourseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteCourseServlet() {
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

		String courseId = request.getParameter("course_id");

		Connection con = null;
		try {
			con = ConnectionProvider.getCon();
			con.setAutoCommit(false);
			// Delete from child tables first to avoid FK constraint errors
			try (PreparedStatement pst1 = con.prepareStatement("DELETE FROM course_sections WHERE course_id = ?")) {
				pst1.setString(1, courseId);
				pst1.executeUpdate();
			}

			try (PreparedStatement pst2 = con.prepareStatement("DELETE FROM course_preferred_teachers WHERE course_id = ?")) {
				pst2.setString(1, courseId);
				pst2.executeUpdate();
			}

			try (PreparedStatement pst3 = con.prepareStatement("DELETE FROM course_forbidden_teachers WHERE course_id = ?")) {
				pst3.setString(1, courseId);
				pst3.executeUpdate();
			}

			// Finally delete from courses
			try (PreparedStatement pst4 = con.prepareStatement("DELETE FROM courses WHERE course_id = ?")) {
				pst4.setString(1, courseId);
				int rows = pst4.executeUpdate();

				if (rows > 0) {
					sc.setAttribute("course_true", "Course with ID " + courseId + " deleted successfully!");
					con.commit();
				} else {
					sc.setAttribute("course_false", "No course found with ID " + courseId);
					con.rollback();
				}
			}

		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			sc.setAttribute("course_false", "Error deleting course: " + e.getMessage());
		} finally {
			try {
				response.sendRedirect(request.getContextPath()+"/dashboard/courses.jsp");
			} catch (IOException e) {
				e.printStackTrace();
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
