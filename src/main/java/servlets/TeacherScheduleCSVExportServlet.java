package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import algorithm.CSPState;
import algorithm.Main;
import algorithm.Value;
import algorithm.Variable;
import entity.Course;
import entity.Teacher;

/**
 * Servlet implementation class TeacherScheduleCSVExportServlet
 */
@WebServlet("/TeacherScheduleCSVExportServlet")
public class TeacherScheduleCSVExportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String[] DAYS = { "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday" };

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TeacherScheduleCSVExportServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		CSPState state = Main.state;

		// Download setup
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=teacher_routine.csv");

		PrintWriter writer = response.getWriter();

		List<Variable> vars = new ArrayList<>(state.variables.values());

		// sort like your JSP
		vars.sort(Comparator.comparingInt((Variable v) -> v.assignedValue.day)
				.thenComparingInt(v -> v.assignedValue.startSlot));

		for (Teacher t : state.teachers.values()) {

			// ===== TEACHER HEADER =====
			writer.append("Teacher: ").append(t.name)
					.append(",Max Weekly Hours: " + state.teacherWeeklyLoad.get(t.id) + "/" + t.maxSlotHours)
					.append("\n");

			// ===== COLUMN HEADER =====
			writer.append("Day,Time,Course,Section,Room\n");

			for (Variable v : vars) {

				if (!v.assigned || v.assignedValue == null)
					continue;

				if (v.assignedValue.teacherId != t.id)
					continue;

				Value val = v.assignedValue;
				Course c = v.course;

				String day = DAYS[val.day];
				String time = timeRange(val.startSlot, val.slotCount);

				writer.append(day).append(",").append("\"").append(time).append("\"").append(",").append(c.id)
						.append(",").append(v.section.id).append(",").append(val.roomId).append("\n");
			}

			writer.append("\n=========================\n\n");
		}

		writer.flush();
		writer.close();
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

	public static String timeRange(int startSlot, int slotCount) {

		int startMinutes = startSlot * 30;
		int endMinutes = startMinutes + (slotCount * 30);

		int startHour = 9 + startMinutes / 60;
		int startMin = startMinutes % 60;

		int endHour = 9 + endMinutes / 60;
		int endMin = endMinutes % 60;

		return formatTime(startHour, startMin) + " - " + formatTime(endHour, endMin);
	}

	private static String formatTime(int hour, int min) {
		return String.format("%02d:%02d", hour, min);
	}

}
