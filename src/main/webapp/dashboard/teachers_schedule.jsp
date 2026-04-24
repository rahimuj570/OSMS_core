<%@page import="entity.Teacher"%>
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
<title>Generated Teachers Schedule</title>
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
		<h1>Teacher's Schedule</h1>
		
	</header>

	<main>
		<%
		CSPState state = Main.state;
		ArrayList<Variable> vars = new ArrayList<>(state.variables.values());
		// Sort by day → startSlot
		vars.sort(
				Comparator.comparingInt((Variable v) -> v.assignedValue.day).thenComparingInt(v -> v.assignedValue.startSlot));

		for (Teacher t : state.teachers.values()) {
		%>
		<section class="routine-section">
			<h2>
				Teacher:
				<%=t.name%></h2>
			<table class="routine-table">
				<thead>
					<tr>
						<th>Day</th>
						<th>Time</th>
						<th>Course</th>
						<th>Section</th>
						<th>Room</th>
					</tr>
				</thead>
				<tbody>
					<%
					for (Variable v : vars) {

						if (v.assignedValue.teacherId != t.id) {
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
						<td><%=v.section.id%></td>
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
		<button class="print-btn" onclick="window.print()">🖨️ Print
			Schedule</button>
			<a href="generated_routine.jsp"><button class="print-btn">Section Routine</button></a>
		<a href="courses.jsp"><button class="print-btn">🏠 Home</button></a>
	</main>
</body>
</html>
