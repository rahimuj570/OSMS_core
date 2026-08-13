<%@page import="entity.CourseType"%>
<%@page import="java.util.ArrayList"%>
<%@page import="algorithm.Main"%>
<%@page import="algorithm.CSPSolver"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isErrorPage="true"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>No Solution Found</title>
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

.status-list {
	list-style: none;
	padding: 0;
}

.status-list li {
	margin: 8px 0;
	font-size: 16px;
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
	.print-btn {
		display: none;
	}
	* {
		-webkit-print-color-adjust: exact !important;
		print-color-adjust: exact !important;
	}
}
</style>

</head>
<body>
	<header class="routine-header">
		<h1>Routine Generation Report</h1>
	</header>

<%


int countAssigned = 0;
int countUnassigned = 0;
int countUnassignedTheory = 0;
int countUnassignedLab = 0;
int countUnasignedLabOrientedTheory = 0;
ArrayList<String>unassigedVarId=new ArrayList<String>();
for (var a : Main.state.variables.values()) {
	if (a.assigned) {
		countAssigned++;
	} else {
		countUnassigned++;
		if (a.course.type == CourseType.LAB) {
			countUnassignedLab++;
		} else if (a.course.type == CourseType.THEORY) {
			countUnassignedTheory++;
		} else {
			countUnasignedLabOrientedTheory++;
		}
		unassigedVarId.add(a.id);
	}
}


%>


	<main>
		<section class="routine-section">
			<h2>Status</h2>

<%
boolean partialSolution =
    CSPSolver.getBestAssignment().size() > 0;
%>

<ul class="status-list">

<% if(Main.timeout && partialSolution){ %>

    <li><strong>Status:</strong> Partial Solution Generated</li>
    <li><strong>Reason:</strong> Search timeout reached before completion</li>
    <li><strong>Assigned Classes:</strong> <%=CSPSolver.getBestAssignment().size()%></li>
    <li><a href="tba_generated_routine.jsp"><button class="print-btn"> View TBA Partial Routinw</button></a></li>

<% } else if(Main.timeout){ %>

    <li><strong>Status:</strong> Timeout</li>
    <li><strong>Reason:</strong> No partial solution found before timeout</li>

<% } else { %>

    <li><strong>Status:</strong> No Feasible Solution</li>
    <li><strong>Reason:</strong> Constraints cannot be satisfied</li>

<% } %>

</ul>
		</section>

		<section class="routine-section">
			<h2>Statistics</h2>
			<table class="routine-table">
				<tr>
					<th>Total Variables</th>
					<td><%=Main.state.variables.size() %></td>
				</tr>
				<tr>
					<th>Assigned Variables</th>
					<td><%=countAssigned %></td>
				</tr>
				<tr>
					<th>Unassigned Variables</th>
					<td><%=countUnassigned %></td>
				</tr>
				<tr>
					<th>Unassigned Lab Variables</th>
					<td><%=countUnassignedLab %></td>
				</tr>
				<tr>
					<th>Unassigned Theory Variables</th>
					<td><%=countUnassignedTheory %></td>
				</tr>
				<tr>
					<th>Unassigned Lab-Oriented Theory Variables</th>
					<td><%=countUnasignedLabOrientedTheory %></td>
				</tr>
			</table>
		</section>

		<section class="routine-section">
			<h2>Unassigned Variable IDs</h2>
			<ul class="status-list">
			<%for(String s : unassigedVarId){ %>
				<li><%=s %></li>
			<%} %>
			</ul>
		</section>

		<button class="print-btn" onclick="window.print()">🖨️ Print
			Report</button>
			<a href="courses.jsp"><button class="print-btn"> 🏠 Home</button></a>
	</main>
</body>
</html>
