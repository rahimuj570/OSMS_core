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
 * Servlet implementation class AddCourseServlet
 */
@WebServlet("/AddCourseServlet")
public class AddCourseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddCourseServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String courseId = req.getParameter("courseId");
		String courseType = req.getParameter("courseType");
		String requiredLab = req.getParameter("requiredLab");

		// Preferred and forbidden teachers (multi-select lists)
		String[] preferredTeachers = req.getParameterValues("preferredTeachers");
		String[] forbiddenTeachers = req.getParameterValues("forbiddenTeachers");

		Connection con = ConnectionProvider.getCon();
		HttpSession sc = req.getSession();
		
		try {
			// Insert into courses
			con.setAutoCommit(false);
			String sqlCourse = "INSERT INTO courses (course_id, course_type, required_lab) VALUES (?,?,?)";
			PreparedStatement psCourse = con.prepareStatement(sqlCourse);
			psCourse.setString(1, courseId);
			psCourse.setString(2, courseType);
			if (requiredLab == null || requiredLab.isEmpty()) {
				psCourse.setNull(3, java.sql.Types.VARCHAR);
			} else {
				psCourse.setString(3, requiredLab);
			}
			psCourse.executeUpdate();

			// Insert preferred teachers
			if (preferredTeachers != null) {
				String sqlPref = "INSERT INTO course_preferred_teachers (course_id, teacher_id) VALUES (?,?)";
				PreparedStatement psPref = con.prepareStatement(sqlPref);
				for (String tid : preferredTeachers) {
					psPref.setString(1, courseId);
					psPref.setInt(2, Integer.parseInt(tid));
					psPref.addBatch();
				}
				psPref.executeBatch();
			}

			// Insert forbidden teachers
			if (forbiddenTeachers != null) {
				String sqlForb = "INSERT INTO course_forbidden_teachers (course_id, teacher_id) VALUES (?,?)";
				PreparedStatement psForb = con.prepareStatement(sqlForb);
				for (String tid : forbiddenTeachers) {
					psForb.setString(1, courseId);
					psForb.setInt(2, Integer.parseInt(tid));
					psForb.addBatch();
				}
				psForb.executeBatch();
			}
			con.commit();
			sc.setAttribute("course_true", "Course added successfully!");
		} catch (Exception e) {
			sc.setAttribute("course_false", "Error occurred at server-side!");
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
