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
<meta name="description"
      content="Automated University Routine Generator (AURG), an Integrated Design Project-II developed for the University of Scholars to automate university timetable generation using Constraint Satisfaction Problem (CSP).">

<meta name="author"
      content="Md. Rahimujjaman Rahim">

<meta name="keywords"
      content="Automated University Routine Generator,AURG,University of Scholars,Routine Generator,Timetable Generator,CSP,Constraint Satisfaction Problem,Java,JSP,IDP-II">

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
	box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.routine-table th,
.routine-table td {
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

.tba-row{
	background:#fff3cd !important;
	color:#856404;
	font-weight:bold;
}

.print-btn {
	background-color: #5c0931;
	color: white;
	border: none;
	padding: 12px 20px;
	border-radius: 4px;
	cursor: pointer;
	font-size: 16px;
	display: block;
}

.print-btn:hover {
	background-color: #7a1145;
}

@media print {

	*{
		-webkit-print-color-adjust: exact !important;
		print-color-adjust: exact !important;
		color-adjust: exact !important;
	}

	.print-btn{
		display:none;
	}
}
</style>

<style type="text/css">
.summary-box{
    display:flex;
    gap:20px;
    margin-bottom:30px;
    justify-content:center;
    flex-wrap:wrap;
}

.summary-item{
    min-width:180px;
    background:white;
    border-radius:10px;
    box-shadow:0 2px 8px rgba(0,0,0,.15);
    text-align:center;
    padding:20px;
}

.summary-item h2{
    margin:0;
    font-size:36px;
}

.summary-item.success{
    border-top:6px solid #28a745;
}

.summary-item.warning{
    border-top:6px solid #dc3545;
}

</style>

</head>

<body>

<header class="routine-header">
	<h1>Final Routine</h1>
</header>

<main>


<div class="summary-box">

    <div class="summary-item success">
        <h2><%=CSPSolver.getBestAssignment().size()%></h2>
        <p>Assigned Classes</p>
    </div>

    <div class="summary-item warning">
        <h2><%=CSPSolver.getBestSkipped().size()%></h2>
        <p>Unscheduled (TBA)</p>
    </div>

    <div class="summary-item">
        <h2><%=Main.state.variables.size()%></h2>
        <p>Total Classes</p>
    </div>
    
   

</div>

<div class="summary-box">

    <div class="summary-item success">
        <h2><%= CSPSolver.getVisitedNodes()%></h2>
        <p>Search States Explored</p>
    </div>

    <div class="summary-item warning">
        <h2><%= String.format("%.2f", CSPSolver.getSolveTime()/1000.0) %> s</h2>
        <p>Execution Time</p>
    </div>

    <div class="summary-item">
        <h2><%= String.format("%,.0f", CSPSolver.getStatesPerSecond()) %></h2>
        <p>Search States/sec</p>
   
</div>
</div>




<%
	CSPState state = Main.state;
	ArrayList<Variable> vars = new ArrayList<>(state.variables.values());

	// Assigned classes first, TBA last
	vars.sort(
		Comparator
			.comparing((Variable v) -> !v.assigned)
			.thenComparingInt(v -> v.assigned ? v.assignedValue.day : Integer.MAX_VALUE)
			.thenComparingInt(v -> v.assigned ? v.assignedValue.startSlot : Integer.MAX_VALUE)
	);

	for (Section sec : state.sections.values()) {
%>

<section class="routine-section">

<h2>Section: <%=sec.id%></h2>

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

		if (!v.section.id.equals(sec.id))
			continue;

		if (v.assigned) {

			Value val = v.assignedValue;

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
		else if(v.skipped){
%>

<tr class="tba-row">
	<td>TBA</td>
	<td>TBA</td>
	<td><%=v.course.id%></td>
	<td>TBA</td>
	<td>TBA</td>
</tr>

<%
		}
	}
%>

</tbody>

</table>

</section>

<%
	}
%>


<h2 style="background:#dc3545;color:white;padding:12px;">
Courses That Could Not Be Scheduled
</h2>

<table class="routine-table">

<tr>
    <th>Section</th>
    <th>Course</th>
    <th>Course Type</th>
</tr>

<%
for(Variable v : vars){

    if(v.skipped){
%>

<tr class="tba-row">
    <td><%=v.section.id%></td>
    <td><%=v.course.id%></td>
    <td><%=v.course.type%></td>
</tr>

<%
    }
}
%>

</table>

<div style="display:flex;

    justify-content:center;

    gap:18px;

    flex-wrap:wrap;

    margin-top:40px;">

	<button class="print-btn" onclick="window.print()">
		🖨️ Print Routine
	</button>

	<a href="<%=request.getContextPath()%>/ClassCSVExportServlet">
		<button class="print-btn">
			📥 Download As CSV
		</button>
	</a>

<a href="teachers_schedule.jsp">
	<button class="print-btn">
		Teacher's Schedule
	</button>
</a>

<a href="diagnostic.jsp">
    <button class="print-btn">
        🔍 Routine Diagnostics
    </button>
</a>

<a href="courses.jsp">
	<button class="print-btn">
		🏠 Home
	</button>
</a>
</div>


</main>




<br>
<br>

<div style="
text-align:center;
color:#666;
font-size:14px;
padding:20px 0;
border-top:1px solid #ddd;
margin-top:40px;
">

Generated by
<b>University Routine Generator</b>

<br><br>

Printed on

<%=new java.text.SimpleDateFormat("dd MMM yyyy  hh:mm a").format(new java.util.Date())%></div>

</body>
</html>