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
 * Servlet implementation class CreateSectionServlet
 */
@WebServlet("/CreateSectionServlet")
public class CreateSectionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateSectionServlet() {
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
		String sectionId = request.getParameter("sectionName");
		int students = Integer.parseInt(request.getParameter("sectionStudents"));
		Connection con = ConnectionProvider.getCon();
		try {
			String sql = "INSERT INTO sections (section_id, students, dept_type) VALUES (?, ?, ?)";
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, sectionId);
				ps.setInt(2, students);
				ps.setString(3, (String) sc.getAttribute("dept_type"));
				int rows = ps.executeUpdate();

				if (rows > 0) {
					sc.setAttribute("section_true", "New Section Created!");
				} else {
					sc.setAttribute("section_false", "Failed to Create New Section!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			sc.setAttribute("section_false", "Something went wrong at server-side.");
		} finally {
			response.sendRedirect(request.getContextPath() + "/dashboard/sections.jsp");
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
