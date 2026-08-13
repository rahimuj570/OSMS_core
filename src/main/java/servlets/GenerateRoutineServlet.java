package servlets;

import local_db.CourseData;
import local_db.RoomData;
import local_db.SectionData;
import local_db.TeacherData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import algorithm.CSPSolver;
import algorithm.Main;
import algorithm.RoutineGenerationResult;
import entity.Course;
import entity.Room;
import entity.Section;
import entity.Teacher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
		// Load data as independent snapshots per request
		Map<String, Section> sections = SectionData.getSections(request.getSession());
		Map<String, Room> rooms = RoomData.getRooms(request.getSession());
		List<Course> courses = CourseData.getCourses(request.getSession());
		Map<Integer, Teacher> teachers = TeacherData.getTeachers(request.getSession());
		
		String efficiency = request.getParameter("efficiency");
		String outsidePreferred = request.getParameter("outsidePreferred");

		if (!CSPSolver.isSolverRunning) {
			CSPSolver.isSolverRunning = true;
			final jakarta.servlet.http.HttpSession session = request.getSession();
			new Thread(() -> {
				CSPSolver.efficiency = efficiency;
				CSPSolver.outsidePreferred=outsidePreferred;
				RoutineGenerationResult result = Main.run(courses, sections, teachers, rooms);
				session.setAttribute("routineGenerationResult", result);
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
