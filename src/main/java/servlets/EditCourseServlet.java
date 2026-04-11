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
 * Servlet implementation class EditCourseServlet
 */
@WebServlet("/EditCourseServlet")
public class EditCourseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditCourseServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession sc = req.getSession();
		
		String courseId = req.getParameter("courseId");
		String courseType = req.getParameter("courseType");
		String requiredLab = req.getParameter("requiredLab");

		String[] sectionIds = req.getParameterValues("sections");
		String[] preferredTeachers = req.getParameterValues("preferredTeachers");
		String[] forbiddenTeachers = req.getParameterValues("forbiddenTeachers");
		Connection con = ConnectionProvider.getCon();
		try {
			con.setAutoCommit(false); // transaction start

			// Update course
			String sqlCourse = "UPDATE courses SET course_type=?, required_lab=? WHERE course_id=?";
			try (PreparedStatement psCourse = con.prepareStatement(sqlCourse)) {
				psCourse.setString(1, courseType);
				if (requiredLab == null || requiredLab.isEmpty()) {
					psCourse.setNull(2, java.sql.Types.VARCHAR);
				} else {
					psCourse.setString(2, requiredLab);
				}
				psCourse.setString(3, courseId);
				psCourse.executeUpdate();
			}

			// Reset sections
			try (PreparedStatement psDel = con.prepareStatement("DELETE FROM course_sections WHERE course_id=?")) {
				psDel.setString(1, courseId);
				psDel.executeUpdate();
			}
			if (sectionIds != null) {
				try (PreparedStatement psSec = con
						.prepareStatement("INSERT INTO course_sections (course_id, section_id) VALUES (?,?)")) {
					for (String sid : sectionIds) {
						psSec.setString(1, courseId);
						psSec.setString(2, sid);
						psSec.addBatch();
					}
					psSec.executeBatch();
				}
			}

			// Reset preferred teachers
			try (PreparedStatement psDel = con
					.prepareStatement("DELETE FROM course_preferred_teachers WHERE course_id=?")) {
				psDel.setString(1, courseId);
				psDel.executeUpdate();
			}
			if (preferredTeachers != null) {
				try (PreparedStatement psPref = con.prepareStatement(
						"INSERT INTO course_preferred_teachers (course_id, teacher_id) VALUES (?,?)")) {
					for (String tid : preferredTeachers) {
						psPref.setString(1, courseId);
						psPref.setInt(2, Integer.parseInt(tid));
						psPref.addBatch();
					}
					psPref.executeBatch();
				}
			}

			// Reset forbidden teachers
			try (PreparedStatement psDel = con
					.prepareStatement("DELETE FROM course_forbidden_teachers WHERE course_id=?")) {
				psDel.setString(1, courseId);
				psDel.executeUpdate();
			}
			if (forbiddenTeachers != null) {
				try (PreparedStatement psForb = con.prepareStatement(
						"INSERT INTO course_forbidden_teachers (course_id, teacher_id) VALUES (?,?)")) {
					for (String tid : forbiddenTeachers) {
						psForb.setString(1, courseId);
						psForb.setInt(2, Integer.parseInt(tid));
						psForb.addBatch();
					}
					psForb.executeBatch();
				}
			}

			con.commit();
			sc.setAttribute("course_true","Course updated successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			sc.setAttribute("course_false", "Error occurred at server-side!");
		} finally {
			try {
				con.setAutoCommit(true);
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			resp.sendRedirect(req.getContextPath()+"/dashboard/courses.jsp");
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
