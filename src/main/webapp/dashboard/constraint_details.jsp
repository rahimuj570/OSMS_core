<%@page import="java.util.List"%>
<%@page import="algorithm.TimeSlotInfo"%>
<%@page import="algorithm.Variable"%>
<%@page language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<%
Variable variable=(Variable)request.getAttribute("variable");

List<TimeSlotInfo> teacherSlots=
(List<TimeSlotInfo>)request.getAttribute("teacherSlots");

List<TimeSlotInfo> roomSlots=
(List<TimeSlotInfo>)request.getAttribute("roomSlots");
%>

<!DOCTYPE html>

<html>

<head>

<meta charset="UTF-8">

<title>Constraint Details</title>

<style>

body{

    font-family:Arial;

    background:#f4f6fb;

    margin:0;

    padding:30px;

}

.header{

    background:#5c0931;

    color:white;

    padding:25px;

    border-radius:12px;

    text-align:center;

    margin-bottom:30px;

}

.info{

    background:white;

    padding:20px;

    border-radius:10px;

    box-shadow:0 3px 10px rgba(0,0,0,.15);

    margin-bottom:30px;

}

.section{

    background:white;

    border-radius:10px;

    box-shadow:0 3px 10px rgba(0,0,0,.15);

    margin-bottom:30px;

}

.section h2{

    margin:0;

    background:#5c0931;

    color:white;

    padding:15px;

    border-radius:10px 10px 0 0;

}

table{

    width:100%;

    border-collapse:collapse;

}

th{

    background:#eeeeee;

}

th,td{

    padding:10px;

    border:1px solid #ddd;

    text-align:left;

}

tr:nth-child(even){

    background:#fafafa;

}

.empty{

    padding:25px;

    text-align:center;

    color:red;

    font-weight:bold;

}

.footer{

    text-align:center;

    margin-top:40px;

}

button{

    background:#5c0931;

    color:white;

    border:none;

    padding:12px 20px;

    border-radius:6px;

    cursor:pointer;

    margin:8px;

}

button:hover{

    background:#7a1145;

}

</style>

</head>

<body>

<div class="header">

<h1>Constraint Details</h1>

<p>Available Teachers & Rooms</p>

</div>

<div class="info">

<h2><%=variable.course.id%></h2>

<p>

<b>Section :</b>

<%=variable.section.id%>

</p>

<p>

<b>Course Type :</b>

<%=variable.course.type%>

</p>

</div>

<div class="section">

<h2>

Preferred Teacher Free Slots

</h2>

<%

if(teacherSlots.isEmpty()){

%>

<div class="empty">

No preferred teacher has any free slot.

</div>

<%

}else{

%>

<table>

<tr>

<th>Teacher</th>

<th>Day</th>

<th>Time</th>

</tr>

<%

for(TimeSlotInfo slot:teacherSlots){

%>

<tr>

<td><%=slot.name%></td>

<td><%=slot.getDayName()%></td>

<td><%=slot.getTimeRange()%></td>

</tr>

<%

}

%>

</table>

<%

}

%>

</div>

<div class="section">

<h2>

Suitable Room Free Slots

</h2>

<%

if(roomSlots.isEmpty()){

%>

<div class="empty">

No suitable room is currently free.

</div>

<%

}else{

%>

<table>

<tr>

<th>Room</th>

<th>Day</th>

<th>Time</th>

</tr>

<%

for(TimeSlotInfo slot:roomSlots){

%>

<tr>

<td><%=slot.name%></td>

<td><%=slot.getDayName()%></td>

<td><%=slot.getTimeRange()%></td>

</tr>

<%

}

%>

</table>

<%

}

%>

</div>

<div class="footer">


<a href="<%=request.getContextPath() %>/dashboard/generated_routine.jsp">

<button>

Section Routine

</button>

</a>

<a href="<%=request.getContextPath() %>/dashboard/courses.jsp">

<button>

🏠 Home

</button>

</a>

</div>

</body>

</html>