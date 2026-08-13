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
 * Servlet implementation class EditSectionServlet
 */
@WebServlet("/EditSectionServlet")
public class EditSectionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditSectionServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		{
			HttpSession sc = request.getSession();
			String sectionId = request.getParameter("sectionId");
			String sectionName = request.getParameter("editSectionName");
			int students = Integer.parseInt(request.getParameter("editSectionStudents"));
			Connection con = ConnectionProvider.getCon();
			try {
				String sql = "UPDATE sections SET section_id = ?, students=? WHERE section_id = ?";
				try (PreparedStatement ps = con.prepareStatement(sql)) {
					ps.setString(1, sectionName);
					ps.setInt(2, students);
					ps.setString(3, sectionId);

					int rows = ps.executeUpdate();

					if (rows > 0) {
						sc.setAttribute("section_true", "Successfully updated!");
					} else {
						sc.setAttribute("section_false", "Failed to update the section. Try again!");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				sc.setAttribute("section_false", "Something went wrong on server-side!");
			} finally {
				response.sendRedirect(request.getContextPath() + "/dashboard/sections.jsp");
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
