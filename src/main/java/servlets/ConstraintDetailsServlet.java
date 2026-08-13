package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import algorithm.AvailabilityHelper;
import algorithm.CSPState;
import algorithm.Main;
import algorithm.TimeSlotInfo;
import algorithm.Variable;

/**
 * Servlet implementation class ConstraintDetailsServlet
 */
@WebServlet("/ConstraintDetailsServlet")
public class ConstraintDetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConstraintDetailsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 CSPState state = Main.state;

	        String courseId = request.getParameter("course");
	        String sectionId = request.getParameter("section");

	        Variable target = null;

	        for (Variable v : state.variables.values()) {

	            if (!v.assigned &&
	                    v.course.id.equals(courseId) &&
	                    v.section.id.equals(sectionId)) {

	                target = v;
	                break;
	            }
	        }

	        if (target == null) {

	            response.sendRedirect(request.getContextPath()+"/dashboard/constraint_analysis.jsp");
	            return;
	        }

	        List<TimeSlotInfo> teacherSlots =
	                AvailabilityHelper.getTeacherFreeSlots(state, target);

	        List<TimeSlotInfo> roomSlots =
	                AvailabilityHelper.getRoomFreeSlots(state, target);

	        request.setAttribute("variable", target);
	        request.setAttribute("teacherSlots", teacherSlots);
	        request.setAttribute("roomSlots", roomSlots);

	        request.getRequestDispatcher("/dashboard/constraint_details.jsp")
	               .forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
