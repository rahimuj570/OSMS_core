<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Select Efficiency Level</title>
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

.efficiency-form {
	text-align: center;
	margin-top: 30px;
	font-size: 18px;
}

.efficiency-form label {
	display: block;
	margin: 12px 0;
	color: #5c0931;
	font-weight: bold;
}

.efficiency-form input[type="radio"] {
	margin-right: 8px;
}

.print-btn {
	background-color: #5c0931;
	color: white;
	border: none;
	padding: 12px 20px;
	border-radius: 4px;
	cursor: pointer;
	font-size: 16px;
	margin-top: 20px;
}

.print-btn:hover {
	background-color: #7a1145;
}
</style>
</head>
<body>
	<header class="routine-header">
		<h1>Routine Generator</h1>
		<p>Please choose your efficiency level before generating the
			routine</p>
		<p>
			NOTE: At first try in <b>"Low"</b> mode, and check is routine can be
			generated or not!
		</p>
	</header>

	<main>
		<form action="<%=request.getContextPath()%>/GenerateRoutineServlet"
			method="get" class="efficiency-form">
			<label> <input type="radio" name="efficiency" value="low"
				required> Low
			</label><br> <label> <input type="radio" name="efficiency"
				value="medium"> Medium
			</label><br> <label> <input type="radio" name="efficiency"
				value="high"> High
			</label><br> <label> <input type="radio" name="efficiency"
				value="optimal"> Optimal
			</label><br> <br> <label>Want to use teachers outside of
				preferred list for a course?</label>
			<div style="display: flex;
  justify-content: center;">
				<input checked="checked" type="radio" id="useYes" name="outsidePreferred" value="yes">
				<label for="useYes">Yes</label> <input style="margin-left: 30px" type="radio" id="useNo"
					name="outsidePreferred" value="no"> <label for="useNo">No</label>
			</div>


			<button type="submit" class="print-btn">Generate Routine</button>
		</form>
		<center>
			<a href="courses.jsp"><button class="print-btn">🏠 Home</button></a>
		</center>
	</main>
</body>
</html>
