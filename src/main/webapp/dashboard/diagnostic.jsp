<%@page import="java.util.List"%>
<%@page import="algorithm.RoutineGenerationResult"%>
<%@page import="algorithm.CSPState"%>
<%@page import="algorithm.Variable"%>
<%@page import="java.util.ArrayList"%>

<%@ page language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>

<head>

<meta charset="UTF-8">

<title>Constraint Analysis Dashboard</title>

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

.summary{

    display:grid;

    grid-template-columns:repeat(4,1fr);

    gap:20px;

    margin-bottom:35px;

}

.card{

    background:white;

    border-radius:10px;

    padding:20px;

    text-align:center;

    box-shadow:0 3px 10px rgba(0,0,0,.15);

}

.card h1{

    margin:0;

    color:#5c0931;

}

.card h3{

    margin-top:10px;

    color:#666;

}

.sectionTitle{

    font-size:24px;

    color:#5c0931;

    margin-top:40px;

    margin-bottom:20px;

}

.issue{

    background:white;

    border-left:6px solid #e74c3c;

    padding:20px;

    border-radius:8px;

    margin-bottom:20px;

    box-shadow:0 3px 10px rgba(0,0,0,.15);

}

.issue h2{

    margin:0;

    color:#c0392b;

}

.badge{

    display:inline-block;

    padding:6px 12px;

    border-radius:20px;

    background:#e74c3c;

    color:white;

    font-size:13px;

    margin-top:8px;

}

button{

    margin-top:15px;

    background:#5c0931;

    color:white;

    border:none;

    padding:10px 18px;

    border-radius:6px;

    cursor:pointer;

}

button:hover{

    background:#7a1145;

}

.footer{

    margin-top:40px;

    text-align:center;

}

.footer a{

    text-decoration:none;

}

.footer button{

    margin:10px;

}

.ai-box{

    background:white;

    border-radius:10px;

    padding:20px;

    margin-bottom:30px;

    box-shadow:0 3px 10px rgba(0,0,0,.15);

    border-left:6px solid #5c0931;

}

.ai-box h2{

    margin-top:0;

    color:#5c0931;

}

.ai-box p{

    color:#555;

    line-height:1.7;

}

.copy-btn{

    background:#5c0931;

    color:white;

    border:none;

    padding:12px 20px;

    border-radius:6px;

    cursor:pointer;

    margin-top:15px;

}

.copy-btn:hover{

    background:#7a1145;

}

textarea{

    display:none;

}

</style>

</head>

<body>

<%

RoutineGenerationResult result = (RoutineGenerationResult) session.getAttribute("routineGenerationResult");
CSPState state = (result != null) ? result.getState() : null;

ArrayList<Variable> vars=new ArrayList<>(state.variables.values());

int assigned=0;

int unassigned=0;

for(Variable v:vars){

    if(v.assigned)

        assigned++;

    else

        unassigned++;

}

double completion=((double)assigned*100.0)/vars.size();

%>

<div class="header">

<h1>Constraint Analysis Dashboard</h1>

<p>Routine Generation Report</p>

</div>

<div class="summary">

<div class="card">

<h1><%=vars.size()%></h1>

<h3>Total Courses</h3>

</div>

<div class="card">

<h1 style="color:green;"><%=assigned%></h1>

<h3>Assigned</h3>

</div>

<div class="card">

<h1 style="color:red;"><%=unassigned%></h1>

<h3>Unassigned</h3>

</div>

<div class="card">

<h1><%=String.format("%.2f",completion)%>%</h1>

<h3>Completion</h3>

</div>

</div>

<div class="ai-box">

    <h2>🤖 AI Scheduling Assistant</h2>

    <p>

        The CSP solver has generated the best possible timetable.

        Any remaining courses could not be scheduled without violating the implemented constraints.

        Download an AI-ready prompt containing only the unassigned courses and their conflict-free teacher and room availability.

        You can analyze it using
        <strong><a style="text-decoration: none" href="https://grok.com/">Grok (Recommended)</a></strong>,
        <strong>Gemini</strong>,
        <strong>Claude</strong>,
        or any other AI assistant.

        The AI will only suggest schedules for the remaining unassigned courses.
        It will never modify the existing timetable.

    </p>

    <a href="${pageContext.request.contextPath}/download-ai-prompt">

        <button class="copy-btn">

            🤖 Download AI Prompt

        </button>

    </a>

</div>

<h2 class="sectionTitle">

Critical Issues

</h2>

<%

boolean found=false;

for(Variable v:vars){

    if(v.assigned)

        continue;

    found=true;

%>

<div class="issue">

<h2>

❌

<%=v.course.id%>

</h2>

<br>

<b>Section :</b>

<%=v.section.id%>

<br><br>

<b>Course Type :</b>

<%=v.course.type%>

<br><br>

<span class="badge">

UNASSIGNED

</span>

<br>

<a href="<%=request.getContextPath()%>/ConstraintDetailsServlet?course=<%=v.course.id%>&section=<%=v.section.id%>">
    <button>
        Analyze Constraint
    </button>
</a>

</div>

<%

}

if(!found){

%>

<div class="issue" style="border-left-color:#27ae60;">

<h2 style="color:#27ae60;">

✅ No Constraint Found

</h2>

<p>

Every course has been successfully assigned.

</p>

</div>

<%

}

%>

<div class="footer">

<a href="generated_routine.jsp">

<button>

Section Routine

</button>

</a>

<a href="teachers_schedule.jsp">

<button>

Teacher Schedule

</button>

</a>

<a href="courses.jsp">

<button>

Home

</button>

</a>

</div>


</body>

</html>