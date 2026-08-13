<%@page import="entity.Teacher"%>
<%@page import="local_db.TeacherData"%>
<%@page import="java.util.Comparator"%>
<%@page import="algorithm.Value"%>
<%@page import="entity.Course"%>
<%@page import="algorithm.RoutinePrinter"%>
<%@page import="algorithm.Variable"%>
<%@page import="java.util.ArrayList"%>
<%@page import="algorithm.RoutineGenerationResult"%>
<%@page import="algorithm.CSPState"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page errorPage="no_solution.jsp"%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<title>Teacher Schedule</title>

<style>

*{
    box-sizing:border-box;
}

body{
    margin:0;
    padding:25px;
    background:#f5f7fb;
    font-family:Arial,Helvetica,sans-serif;
    color:#333;
}

.header{

    background:#5c0931;
    color:white;

    border-radius:12px;

    padding:22px;

    margin-bottom:25px;

    text-align:center;

    box-shadow:0 4px 12px rgba(0,0,0,.15);

}

.header h1{

    margin:0;

    font-size:34px;

}

.header p{

    margin-top:10px;

    opacity:.9;

    font-size:16px;

}

.summary{

    display:flex;

    gap:20px;

    flex-wrap:wrap;

    margin-bottom:30px;

}

.summary-card{

    flex:1;

    min-width:220px;

    background:white;

    border-radius:10px;

    padding:18px;

    box-shadow:0 2px 10px rgba(0,0,0,.08);

}

.summary-title{

    color:#777;

    font-size:14px;

}

.summary-value{

    margin-top:8px;

    font-size:32px;

    font-weight:bold;

    color:#5c0931;

}

.teacher-section{

    margin-bottom:45px;

    background:white;

    border-radius:12px;

    overflow:hidden;

    box-shadow:0 4px 12px rgba(0,0,0,.08);

}

.teacher-header{

    background:#5c0931;

    color:white;

    display:flex;

    justify-content:space-between;

    align-items:center;

    padding:18px 25px;

    flex-wrap:wrap;

}

.teacher-name{

    font-size:24px;

    font-weight:bold;

}

.teacher-load{

    font-size:17px;

}

.progress{

    width:260px;

    height:12px;

    background:#d8d8d8;

    border-radius:50px;

    overflow:hidden;

    margin-top:8px;

}

.progress-bar{

    height:100%;

    background:#33b249;

}

table{

    width:100%;

    border-collapse:collapse;

}

th{

    background:#5c0931;

    color:white;

    padding:13px;

    text-align:left;

}

td{

    padding:12px;

    border-bottom:1px solid #ececec;

}

tbody tr:nth-child(even){

    background:#fafafa;

}

tbody tr:hover{

    background:#f3f3f3;

}

.day{

    font-weight:bold;

}

.course{

    font-weight:bold;

    color:#5c0931;

}

.room{

    font-weight:bold;

}

.time{

    color:#444;

}

.empty{

    padding:30px;

    text-align:center;

    color:#999;

    font-style:italic;

}

.btn-area{

    display:flex;

    justify-content:center;

    gap:18px;

    flex-wrap:wrap;

    margin-top:40px;

}

.btn{

    background:#5c0931;

    color:white;

    border:none;

    padding:14px 24px;

    border-radius:8px;

    cursor:pointer;

    font-size:15px;

    text-decoration:none;

}

.btn:hover{

    background:#7a1145;

}

@media print{

    .btn-area{

        display:none;

    }

    body{

        background:white;

        padding:0;

    }

}

</style>

</head>

<body>

<%

RoutineGenerationResult result = (RoutineGenerationResult) session.getAttribute("routineGenerationResult");
CSPState state = (result != null) ? result.getState() : null;

if (state != null) {
ArrayList<Variable> vars = new ArrayList<>(state.variables.values());

vars.removeIf(v -> !v.assigned || v.assignedValue == null);

vars.sort(

Comparator
.comparingInt((Variable v)->v.assignedValue.day)
.thenComparingInt(v->v.assignedValue.startSlot)

);

%>

<div class="header">

<h1>Teacher Schedule</h1>

<p>
Automatically generated teaching schedule for all instructors
</p>

</div>

<%

int totalTeachers = state.teachers.size();
int totalAssigned = vars.size();

float totalHours = 0f;

for (Teacher t : state.teachers.values()) {
    totalHours += state.teacherWeeklyLoad.get(t.id);
}

%>

<div class="summary">

    <div class="summary-card">
        <div class="summary-title">
            Total Teachers
        </div>

        <div class="summary-value">
            <%=totalTeachers%>
        </div>
    </div>

    <div class="summary-card">
        <div class="summary-title">
            Assigned Classes
        </div>

        <div class="summary-value">
            <%=totalAssigned%>
        </div>
    </div>

    <div class="summary-card">
        <div class="summary-title">
            Total Teaching Hours
        </div>

        <div class="summary-value">
            <%=String.format("%.1f", totalHours)%> hrs
        </div>
    </div>

</div>


<%

for(Teacher t : state.teachers.values()){

    float load = state.teacherWeeklyLoad.get(t.id);

    float percent = 0;

    if(t.maxSlotHours>0){
        percent = (load/t.maxSlotHours)*100f;
    }

    if(percent>100)
        percent=100;

    String color="#33b249";
    String status="Normal";

    if(percent>=80){
        color="#f4b400";
        status="Near Limit";
    }

    if(percent>=100){
        color="#d93025";
        status="Overloaded";
    }

%>

<div class="teacher-section">

<div class="teacher-header">

<div>

<div class="teacher-name">

👨‍🏫 <%=t.name%>

</div>

<div style="margin-top:8px;">

Weekly Load :
<b>

<%=String.format("%.1f",load)%> / <%=t.maxSlotHours%> hrs

</b>

&nbsp;&nbsp;

(<%=status%>)

</div>

<div class="progress">

<div class="progress-bar"

style="width:<%=percent%>%;
background:<%=color%>;">

</div>

</div>

</div>

<div style="font-size:18px;">

Teacher ID :
<b><%=t.id%></b>

</div>

</div>

<table>

<thead>

<tr>

<th width="18%">Day</th>

<th width="25%">Time</th>

<th width="22%">Course</th>

<th width="20%">Section</th>

<th width="15%">Room</th>

</tr>

</thead>

<tbody>

<%

boolean found=false;

for(Variable v : vars){

    if(v.assignedValue.teacherId!=t.id)
        continue;

    found=true;

    Value val=v.assignedValue;

    String day=RoutinePrinter.DAYS[val.day];

    String time=RoutinePrinter.timeRange(
            val.startSlot,
            val.slotCount
    );

%>

<tr>

<td class="day">
<%=day%>
</td>

<td class="time">
<%=time%>
</td>

<td class="course">
<%=v.course.id%>
</td>

<td>
<%=v.section.id%>
</td>

<td class="room">
<%=val.roomId%>
</td>

</tr>

<%

}

if(!found){

%>

<tr>

<td colspan="5" class="empty">

No classes assigned for this teacher.

</td>

</tr>

<%

}

%>

</tbody>

</table>

</div>

<%

}

%>

<div class="btn-area">

    <button class="btn" onclick="window.print()">
        🖨️ Print Schedule
    </button>

    <a href="<%=request.getContextPath()%>/TeacherScheduleCSVExportServlet"
       class="btn">
        📥 Download CSV
    </a>

    <a href="generated_routine.jsp"
       class="btn">
        📚 Section Routine
    </a>

    <a href="courses.jsp"
       class="btn">
        🏠 Home
    </a>

</div>

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
<b>Automated University Routine Generator</b>

<br><br>

Printed on

<%=new java.text.SimpleDateFormat("dd MMM yyyy  hh:mm a").format(new java.util.Date())%>

</div>

</body>
<%
} else {
%>
<div class="header">
<h1>No Routine Generated</h1>
<p>Please generate a routine first.</p>
</div>
<div class="btn-area">
<a href="courses.jsp" class="btn">🏠 Home</a>
</div>
<% } %>
</body>
</html>