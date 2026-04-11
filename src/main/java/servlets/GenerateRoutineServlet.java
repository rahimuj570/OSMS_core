package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import local_db.CourseData;
import local_db.RoomData;
import local_db.SectionData;
import local_db.TeacherData;

import java.io.IOException;

import algorithm.CSPSolver;
import algorithm.Main;

/**
 * Servlet implementation class GenerateRoutineServlet
 */
@WebServlet("/GenerateRoutineServlet")
public class GenerateRoutineServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GenerateRoutineServlet() {
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
		
		SectionData.getSections();
		RoomData.getRooms();
		CourseData.getCourses();
		TeacherData.getTeachers();
		
		String efficiency = request.getParameter("efficiency");

		if (!CSPSolver.isSolverRunning) {
			CSPSolver.isSolverRunning = true;
			new Thread(() -> {
				CSPSolver.efficiency = efficiency;
				Main.main(null);
				CSPSolver.isSolverRunning = false;
			}).start();
		}
		response.sendRedirect(request.getContextPath() + "/dashboard/generate_routine.jsp");
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
