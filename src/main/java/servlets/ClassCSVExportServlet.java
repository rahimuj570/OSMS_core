package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import local_db.TeacherData;

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
import entity.Section;

/**
 * Servlet implementation class ClassCSVExportServlet
 */
@WebServlet("/ClassCSVExportServlet")
public class ClassCSVExportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClassCSVExportServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public static final String[] DAYS = {
            "Saturday", "Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday"
    };


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 CSPState state = Main.state;

	        // 🔥 Download setup
	        response.setContentType("text/csv");
	        response.setHeader("Content-Disposition", "attachment; filename=section_routine.csv");

	        PrintWriter writer = response.getWriter();

	        List<Variable> vars = new ArrayList<>(state.variables.values());

	        // Sort by day → startSlot
	        vars.sort(Comparator
	                .comparingInt((Variable v) -> v.assignedValue.day)
	                .thenComparingInt(v -> v.assignedValue.startSlot));

	        // Loop per section (same as your printer)
	        for (Section section : state.sections.values()) {

	            String secId = section.id;

	            // Section header
	            writer.append("Class ").append(secId).append("\n");

	            // Column header
	            writer.append("Course,Section,Teacher,Room,Day,Time\n");

	            for (Variable v : vars) {

	                if (!v.assigned || v.assignedValue == null)
	                    continue;

	                if (!v.section.id.equals(secId))
	                    continue;

	                Value val = v.assignedValue;
	                Course c = v.course;

	                String day = DAYS[val.day];
	                String time = timeRange(val.startSlot, val.slotCount);

	                writer.append(c.id).append(",")
	                        .append(v.section.id).append(",")
	                        .append(String.valueOf(TeacherData.teachers.get(val.teacherId).name)).append(",")
	                        .append(val.roomId).append(",")
	                        .append(day).append(",")
	                        .append("\"").append(time).append("\"") // 🔥 important
	                        .append("\n");
	            }

	            writer.append("\n=========================\n\n");
	        }

	        writer.flush();
	        writer.close();
	    
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
