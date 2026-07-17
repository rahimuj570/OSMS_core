<%@page import="local_db.TeacherData"%>
<%@page import="java.util.Comparator"%>
<%@page import="algorithm.Value"%>
<%@page import="entity.Course"%>
<%@page import="algorithm.Variable"%>
<%@page import="java.util.ArrayList"%>
<%@page import="entity.Section"%>
<%@page import="algorithm.Main"%>
<%@page import="algorithm.CSPState"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>
<head>
<meta charset="UTF-8">
<title>Partial Routine (TBA)</title>

<style>

body {
	font-family: Arial, sans-serif;
	background: #fff;
	padding: 20px;
	color: #333;
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

.tba-row {
	background-color: #ffe5e5 !important;
	color: #c00000;
	font-weight: bold;
}

.summary {
	background: #fff3cd;
	border: 1px solid #ffeeba;
	padding: 15px;
	border-radius: 8px;
	margin-bottom: 25px;
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
	margin: 20px auto;
}

.print-btn:hover {
	background-color: #7a1145;
}

@media print {

	.print-btn{
		display:none;
	}

	*{
		-webkit-print-color-adjust: exact !important;
		print-color-adjust: exact !important;
	}
}

</style>

</head>

<body>

<%

CSPState state = Main.state;

ArrayList<Variable> vars =
new ArrayList<>(state.variables.values());

int assignedCount = 0;

for(Variable v : vars){
if(v.assignedValue != null){
assignedCount++;
}
}

int totalCount = vars.size();
int tbaCount = totalCount - assignedCount;

/* Safe sort */
vars.sort((a,b)->{

if(a.assignedValue == null &&
   b.assignedValue == null)
	return 0;

if(a.assignedValue == null)
	return 1;

if(b.assignedValue == null)
	return -1;

int cmp = Integer.compare(
		a.assignedValue.day,
		b.assignedValue.day);

if(cmp != 0)
	return cmp;

return Integer.compare(
		a.assignedValue.startSlot,
		b.assignedValue.startSlot);


});

%>

<header class="routine-header">

```
<h1>Partial Routine</h1>

<h3>
	Generated using best available assignment.
</h3>
```

</header>

<div class="summary">

```
<h2>Routine Summary</h2>

<p>
	<b>Total Classes:</b>
	<%=totalCount%>
</p>

<p>
	<b>Assigned Classes:</b>
	<%=assignedCount%>
</p>

<p>
	<b>TBA Classes:</b>
	<%=tbaCount%>
</p>

<p style="color:red;">
	Some classes could not be assigned due to
	resource limitations (teachers, rooms, or search timeout).
</p>
```

</div>

<%

for(Section sec : state.sections.values()){

%>

<section class="routine-section">

```
<h2>
	Section: <%=sec.id%>
</h2>

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

	for(Variable v : vars){

		if(!v.section.id.equals(sec.id))
			continue;

		Value val = v.assignedValue;

		String day = "TBA";
		String time = "TBA";
		String teacher = "TBA";
		String room = "TBA";

		if(val != null){

			try{

				day =
				algorithm.RoutinePrinter.DAYS[val.day];

				time =
				algorithm.RoutinePrinter.timeRange(
						val.startSlot,
						val.slotCount);

				if(val.teacherId != 0 &&
				   TeacherData.teachers.containsKey(val.teacherId)){

					teacher =
					TeacherData.teachers
					.get(val.teacherId)
					.name;
				}

				if(val.roomId != null){
					room = val.roomId;
				}

			}catch(Exception ex){
				// keep TBA
			}
		}

	%>

		<tr class="<%= val == null ? "tba-row" : "" %>">

			<td><%=day%></td>

			<td><%=time%></td>

			<td><%=v.course.id%></td>

			<td><%=teacher%></td>

			<td><%=room%></td>

		</tr>

	<%
	}
	%>

	</tbody>

</table>
```

</section>

<%
}
%>

<div style="display:flex;justify-content:center;gap:15px;">

```
<button class="print-btn"
	onclick="window.print()">
	🖨️ Print Routine
</button>
```

</div>

<a href="courses.jsp">
	<button class="print-btn">
		🏠 Home
	</button>
</a>

</body>
</html>
