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
		PreparedStatement pst = null;

		try {
			con = ConnectionProvider.getCon();
			con.setAutoCommit(false);
			// Delete from child tables first to avoid FK constraint errors
			pst = con.prepareStatement("DELETE FROM course_sections WHERE course_id = ?");
			pst.setString(1, courseId);
			pst.executeUpdate();

			pst = con.prepareStatement("DELETE FROM course_preferred_teachers WHERE course_id = ?");
			pst.setString(1, courseId);
			pst.executeUpdate();

			pst = con.prepareStatement("DELETE FROM course_forbidden_teachers WHERE course_id = ?");
			pst.setString(1, courseId);
			pst.executeUpdate();

			// Finally delete from courses
			pst = con.prepareStatement("DELETE FROM courses WHERE course_id = ?");
			pst.setString(1, courseId);
			int rows = pst.executeUpdate();

			if (rows > 0) {
				sc.setAttribute("course_true", "Course with ID " + courseId + " deleted successfully!");
				con.commit();
			} else {
				sc.setAttribute("course_false", "No course found with ID " + courseId);
				con.rollback();
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
				if (pst != null)
					pst.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				response.sendRedirect(request.getContextPath()+"/dashboard/courses.jsp");
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
