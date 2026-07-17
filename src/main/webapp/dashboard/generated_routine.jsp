<%@page import="algorithm.CSPSolver"%>
<%@page import="local_db.TeacherData"%>
<%@page import="java.util.Comparator"%>
<%@page import="algorithm.Value"%>
<%@page import="entity.Course"%>
<%@page import="algorithm.RoutinePrinter"%>
<%@page import="algorithm.Variable"%>
<%@page import="java.util.ArrayList"%>
<%@page import="entity.Section"%>
<%@page import="algorithm.Main"%>
<%@page import="algorithm.CSPState"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page errorPage="no_solution.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Generated Routine</title>
<style type="text/css">
body {
	font-family: Arial, sans-serif;
	background-color: #fff;
	color: #333;
	padding: 20px;
}

.routine-header {
	text-align: center;
	background-color: #5c0931;
	color: white;
	padding: 20px;
	border-radius: 8px;
	margin-bottom: 30px;
}

.routine-section {
	margin-bottom: 40px;
}

.routine-section h2 {
	background-color: #5c0931;
	color: white;
	padding: 10px;
	border-radius: 4px 4px 0 0;
}

.routine-table {
	width: 100%;
	border-collapse: collapse;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.routine-table th, .routine-table td {
	border: 1px solid #ddd;
	padding: 12px;
	text-align: left;
}

.routine-table th {
	background-color: #5c0931;
	color: white;
}

.routine-table tr:nth-child(even) {
	background-color: #f9f9f9;
}

.routine-table tr:hover {
	background-color: #f1f1f1;
}

/* Print Button */
.print-btn {
	background-color: #5c0931;
	color: white;
	border: none;
	padding: 12px 20px;
	border-radius: 4px;
	cursor: pointer;
	font-size: 16px;
	display: block;
	margin: 20px auto;
}

.print-btn:hover {
	background-color: #7a1145;
}

/* Print Styles */
@media print {
	* {
		-webkit-print-color-adjust: exact !important; /* Chrome, Safari */
		print-color-adjust: exact !important; /* Firefox */
		color-adjust: exact !important; /* Legacy */
	}
	.print-btn {
		display: none;
	}
}
</style>

</head>
<body>
	<header class="routine-header">
		<h1>Final Routine</h1>
		<%
		if(CSPSolver.isAllLabFitted(Main.state)==false){ %>
		<p style="background-color: #fff9d9;
  color: red;
  display: inline-block;
  padding: 10px;
  border-radius: 10px;">
Some Lab Oriented Courses did not get Lab room due to insufficient room		
		</p><%} %>
		<%
		if (Main.isComplete == false) {
		%>
		<h2>Result might be incomplete due to resource insufficient!</h2>

		<%
		if (Main.isLabOrientedIncomplete) {
		%>
		<div style="display: flex;
  justify-content: center;">
		<div
			style="text-align: left; background: white; width: fit-content; color: red; padding: 10px; border-radius: 15px;">
			<h3>Lab Oriented Courses could not got at least one LAB
				classroom</h3>

			<ul>
				<%
				for (String loid : Main.inCompleteLabOriented.keySet()) {
					if (Main.inCompleteLabOriented.get(loid) == false) {
				%>
				<li><%=loid%></li>

				<%
				}
				}
				%>
			</ul>
		</div></div>
		<%
		}
		}
		%>
	</header>

	<main>
		<%
		CSPState state = Main.state;
		ArrayList<Variable> vars = new ArrayList<>(state.variables.values());
		// Sort by day → startSlot
		vars.sort(
				Comparator.comparingInt((Variable v) -> v.assignedValue.day).thenComparingInt(v -> v.assignedValue.startSlot));

		for (Section sec : state.sections.values()) {
		%>
		<section class="routine-section">
			<h2>
				Section:
				<%=sec.id%></h2>
			<table class="routine-table">
				<thead>
					<tr>
						<th>Day</th>
						<th>Time</th>
						<th>Course</th>
						<th>Teacher</th>
						<th>Room</th>
					</tr>
				</thead>
				<tbody>
					<%
					for (Variable v : vars) {

						if (v.section.id != sec.id) {
							continue;
						}
						Value val = v.assignedValue;
						Course c = v.course;

						String day = RoutinePrinter.DAYS[val.day];
						String time = RoutinePrinter.timeRange(val.startSlot, val.slotCount);
					%>
					<tr>
						<td><%=day%></td>
						<td><%=time%></td>
						<td><%=v.course.id%></td>
						<td><%=TeacherData.teachers.get(val.teacherId).name%></td>
						<td><%=val.roomId%></td>
					</tr>

					<%
					}
					%>
				</tbody>
			</table>
		</section>
		<%
		}
		%>


		<!-- Print Button -->
	<div style="display: flex;
  justify-content: center;
  gap: 18px;">	<a><button class="print-btn" onclick="window.print()">🖨️ Print
			Routine</button></a><a href="<%=request.getContextPath()%>/ClassCSVExportServlet"><button class="print-btn">📥 Download As CSV</button></a></div>
			<a href="teachers_schedule.jsp"><button class="print-btn">Teacher's Schedule</button></a>
		<a href="courses.jsp"><button class="print-btn">🏠 Home</button></a>
	</main>
</body>
</html>
