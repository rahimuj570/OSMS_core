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
import java.util.List;

import algorithm.CSPState;
import algorithm.Main;
import algorithm.RoutinePrinter;
import algorithm.Value;
import algorithm.Variable;
import entity.Course;
import entity.Section;

@WebServlet("/ClassCSVExportServlet")
public class ClassCSVExportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public ClassCSVExportServlet() {
        super();
    }

    public static final String[] DAYS = {
            "Saturday",
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday"
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CSPState state = Main.state;

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=section_routine.csv");

        PrintWriter writer = response.getWriter();

        List<Variable> vars = new ArrayList<>(state.variables.values());

        // Assigned routines first, then TBA
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

        for (Section section : state.sections.values()) {

            String secId = section.id;

            writer.println("=================================================");
            writer.println("SECTION : " + secId);
            writer.println("=================================================");

            writer.println(
                    "Course,Section,Teacher,Room,Day,Time,Status");

            int assignedCount = 0;
            int skippedCount = 0;

            for (Variable v : vars) {

                if (!v.section.id.equals(secId))
                    continue;

                // ---------- TBA ----------
                if (!v.assigned || v.assignedValue == null) {

                    skippedCount++;

                    writer.append(v.course.id).append(",")
                            .append(v.section.id).append(",")
                            .append("TBA,")
                            .append("TBA,")
                            .append("TBA,")
                            .append("TBA,")
                            .append("Not Assigned")
                            .append("\n");

                    continue;
                }

                assignedCount++;

                Value val = v.assignedValue;
                Course c = v.course;

                String day = DAYS[val.day];
                String time = RoutinePrinter.timeRange(
                        val.startSlot,
                        val.slotCount);

                writer.append(c.id).append(",")
                        .append(v.section.id).append(",")
                        .append(TeacherData.teachers
                                .get(val.teacherId).name)
                        .append(",")
                        .append(val.roomId).append(",")
                        .append(day).append(",")
                        .append("\"")
                        .append(time)
                        .append("\"")
                        .append(",")
                        .append("Assigned")
                        .append("\n");
            }

            writer.println();

            writer.println("Assigned Classes," + assignedCount);
            writer.println("Not Assigned," + skippedCount);

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
        int endMinutes = startMinutes + slotCount * 30;

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