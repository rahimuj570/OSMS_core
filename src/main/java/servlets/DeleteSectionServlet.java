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
 * Servlet implementation class DeleteSectionServlet
 */
@WebServlet("/DeleteSectionServlet")
public class DeleteSectionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteSectionServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String sectionId = request.getParameter("sectionId");
		HttpSession sc = request.getSession();
		Connection con = ConnectionProvider.getCon();
		try {
			con.setAutoCommit(false);
			PreparedStatement pst2 = con.prepareStatement("delete from course_sections where section_id=?");
			pst2.setNString(1, sectionId);
			pst2.execute();
			pst2.close();

			String sql = "DELETE FROM sections WHERE section_id = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, sectionId);

			int rows = ps.executeUpdate();

			if (rows > 0) {
				sc.setAttribute("section_true", "Section deleted successfully!");
			} else {
				// failure: redirect with error
				sc.setAttribute("section_false", "No section found to delete!");
			}
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			sc.setAttribute("section_false", "Error occurred at server-side!");
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
