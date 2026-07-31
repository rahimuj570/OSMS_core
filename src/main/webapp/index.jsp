<%@page import="java.time.LocalDateTime"%>
<%@ page language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html lang="en">

<head>

<meta charset="UTF-8">

<meta name="viewport"
content="width=device-width, initial-scale=1.0">
<meta name="description"
      content="Automated University Routine Generator (AURG), an Integrated Design Project-II developed for the University of Scholars to automate university timetable generation using Constraint Satisfaction Problem (CSP).">

<meta name="author"
      content="Md. Rahimujjaman Rahim">

<meta name="keywords"
      content="Automated University Routine Generator,AURG,University of Scholars,Routine Generator,Timetable Generator,CSP,Constraint Satisfaction Problem,Java,JSP,IDP-II">


<title>Automated University Routine Generator</title>

<style>

*{
    margin:0;
    padding:0;
    box-sizing:border-box;
}

body{

    font-family:Segoe UI,Tahoma,Geneva,Verdana,sans-serif;

    background:linear-gradient(135deg,#5c0931,#8b174f);

    min-height:100vh;

    display:flex;

    justify-content:center;

    align-items:center;

    padding:30px;

}

.login-container{

    width:460px;

    background:white;

    border-radius:14px;

    box-shadow:0 15px 40px rgba(0,0,0,.25);

    padding:40px;

    text-align:center;

}

.university{

    font-size:28px;

    color:#5c0931;

    font-weight:bold;

}

.department{

    margin-top:5px;

    color:#666;

    font-size:14px;

}

.system-name{

    margin-top:25px;

    font-size:22px;

    color:#5c0931;

    font-weight:bold;

    line-height:1.4;

}

.system-short{

    color:#888;

    font-size:15px;

    margin-top:5px;

}

.subtitle{

    margin-top:18px;

    margin-bottom:25px;

    color:#555;

    font-size:15px;

}

.form-group{

    text-align:left;

    margin-bottom:18px;

}

.form-group label{

    display:block;

    margin-bottom:7px;

    font-weight:600;

    color:#444;

}

.form-group input{

    width:100%;

    padding:12px;

    border:1px solid #d5d5d5;

    border-radius:7px;

    font-size:15px;

    transition:.3s;

}

.form-group input:focus{

    outline:none;

    border-color:#5c0931;

    box-shadow:0 0 8px rgba(92,9,49,.15);

}

.login-btn{

    width:100%;

    padding:13px;

    background:#5c0931;

    color:white;

    border:none;

    border-radius:7px;

    font-size:16px;

    cursor:pointer;

    transition:.3s;

}

.login-btn:hover{

    background:#7c1044;

}

.error{

    color:#d63031;

    margin-bottom:20px;

    font-size:14px;

}

.divider{

    margin:28px 0 18px;

    border-top:1px solid #ececec;

}

.footer{

    font-size:13px;

    color:#666;

    line-height:1.8;

}

.footer strong{

    color:#5c0931;

}

.version{

    margin-top:10px;

    color:#999;

    font-size:12px;

}



.footer{

    font-size:13px;
    color:#666;
    line-height:1.8;

}

.footer strong{

    color:#5c0931;

}

.footer a{

    color:#5c0931;
    text-decoration:none;
    font-weight:600;

}

.footer a:hover{

    text-decoration:underline;

}

.version{

    margin-top:12px;
    color:#999;
    font-size:12px;

}

</style>

</head>

<body>

<div class="login-container">

    <div class="university">

        University of Scholars

    </div>

    <div class="department">

        Department of Computer Science & Engineering

    </div>

    <div class="system-name">

        Automated University Routine Generator

    </div>

    <div class="system-short">

        (AURG)

    </div>

    <div class="subtitle">

        Sign in to continue

    </div>

    <% if(session.getAttribute("invalid_credential") != null){ %>

        <div class="error">

            <%=session.getAttribute("invalid_credential")%>

        </div>

    <%

        session.removeAttribute("invalid_credential");

       }

    %>

    <form action="LoginServlet" method="post">

        <div class="form-group">

            <label>Username</label>

            <input
            type="text"
            name="username"
            required>

        </div>

        <div class="form-group">

            <label>Password</label>

            <input
            type="password"
            name="password"
            required>

        </div>

        <button
        class="login-btn"
        type="submit">

            Login

        </button>

    </form>
<div class="divider"></div>

<div style="margin-bottom:18px;">

    <a href="about.jsp"
       style="color:#5c0931;
              text-decoration:none;
              font-weight:600;
              font-size:14px;">

        🛈 About the System

    </a>

</div>

<div class="footer">

    &copy; <%=LocalDateTime.now().getYear()%> University of Scholars

    <br>

    Department of Computer Science & Engineering

    <br>

    <strong>Automated University Routine Generator (AURG)</strong>

    <div class="version">

        Version 1.0

    </div>

</div>
</div>

</body>

</html>