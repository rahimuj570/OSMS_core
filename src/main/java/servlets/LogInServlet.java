package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import helper.ConnectionProvider;

/**
 * Servlet implementation class LogInServlet
 */
@WebServlet("/LoginServlet")
public class LogInServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogInServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession sc = request.getSession();
		try {
			PreparedStatement pst = ConnectionProvider.getCon().prepareStatement("select * from admins where admin_username=? and admin_password=?");
			pst.setString(1, request.getParameter("username"));
			pst.setString(2, request.getParameter("password"));
			try (ResultSet res = pst.executeQuery()) {
			
				if(res.next()) {
					sc.setAttribute("dept_type", res.getString(3));
					response.sendRedirect(request.getContextPath()+"/dashboard/courses.jsp");
				}else {
					sc.setAttribute("invalid_credential", "Invalid Credential");
					response.sendRedirect(request.getHeader("referer"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
