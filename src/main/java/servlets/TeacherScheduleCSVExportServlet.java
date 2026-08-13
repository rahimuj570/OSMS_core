package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import algorithm.CSPState;
import algorithm.Main;
import algorithm.RoutineGenerationResult;
import algorithm.RoutinePrinter;
import algorithm.Value;
import algorithm.Variable;
import entity.Course;
import entity.Teacher;

@WebServlet("/TeacherScheduleCSVExportServlet")
public class TeacherScheduleCSVExportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String[] DAYS = {
            "Saturday",
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday"
    };

    public TeacherScheduleCSVExportServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Object obj = request.getSession().getAttribute("routineGenerationResult");
        CSPState state = (obj != null) ? ((RoutineGenerationResult) obj).getState() : null;

        if (state == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard/generate_routine.jsp");
            return;
        }

        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=teacher_schedule.csv");

        PrintWriter writer = response.getWriter();

        List<Variable> vars = new ArrayList<>(state.variables.values());

        // Safe sorting
        vars.sort((a, b) -> {

            if (!a.assigned || a.assignedValue == null) {

                if (!b.assigned || b.assignedValue == null)
                    return a.id.compareTo(b.id);

                return 1;
            }

            if (!b.assigned || b.assignedValue == null)
                return -1;

            int cmp = Integer.compare(
                    a.assignedValue.day,
                    b.assignedValue.day);

            if (cmp != 0)
                return cmp;

            return Integer.compare(
                    a.assignedValue.startSlot,
                    b.assignedValue.startSlot);
        });

        for (Teacher teacher : state.teachers.values()) {

            float load = state.teacherWeeklyLoad.get(teacher.id);

            float percent = 0;

            if (teacher.maxSlotHours > 0) {
                percent = (load / teacher.maxSlotHours) * 100f;
            }

            String status = "Normal";

            if (percent >= 100) {
                status = "Overloaded";
            } else if (percent >= 80) {
                status = "Near Limit";
            }

            writer.println("==============================================================");
            writer.println("Teacher Name," + teacher.name);
            writer.println("Teacher ID," + teacher.id);
            writer.println("Weekly Load,"
                    + String.format("%.1f", load)
                    + " / "
                    + teacher.maxSlotHours
                    + " Hours");
            writer.println("Status," + status);
            writer.println("==============================================================");

            writer.println("Day,Time,Course,Section,Room");

            int assignedCount = 0;

            for (Variable v : vars) {

                if (!v.assigned || v.assignedValue == null)
                    continue;

                if (v.assignedValue.teacherId != teacher.id)
                    continue;

                assignedCount++;

                Value val = v.assignedValue;
                Course c = v.course;

                writer.append(DAYS[val.day]).append(",")
                        .append("\"")
                        .append(RoutinePrinter.timeRange(val.startSlot, val.slotCount))
                        .append("\"")
                        .append(",")
                        .append(c.id)
                        .append(",")
                        .append(v.section.id)
                        .append(",")
                        .append(val.roomId)
                        .append("\n");
            }

            if (assignedCount == 0) {
                writer.println("No Classes Assigned");
            }

            writer.println();
            writer.println("Total Assigned Classes," + assignedCount);
            writer.println();
            writer.println();
        }

        writer.flush();
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }

    public static String timeRange(int startSlot, int slotCount) {

        int startMinutes = startSlot * 30;
        int endMinutes = startMinutes + (slotCount * 30);

        int startHour = 9 + startMinutes / 60;
        int startMin = startMinutes % 60;

        int endHour = 9 + endMinutes / 60;
        int endMin = endMinutes % 60;

        return formatTime(startHour, startMin)
                + " - "
                + formatTime(endHour, endMin);
    }

    private static String formatTime(int hour, int min) {

        return String.format("%02d:%02d", hour, min);
    }
}