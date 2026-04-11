<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Login - Class Routine</title><base>
<link rel="stylesheet" type="text/css" href="./dashboard/dashboard.css">
<style type="text/css">
/* Reset */
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	font-family: Arial, sans-serif;
	background-color: #5c0931;
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100vh;
}

.login-container {
	background-color: white;
	padding: 40px;
	border-radius: 8px;
	width: 350px;
	box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
	text-align: center;
}

.login-container h1 {
	color: #5c0931;
	margin-bottom: 20px;
}

.form-group {
	margin-bottom: 15px;
	text-align: left;
}

label {
	display: block;
	margin-bottom: 5px;
	color: #333;
	font-weight: bold;
}

input {
	width: 100%;
	padding: 10px;
	border: 1px solid #ccc;
	border-radius: 4px;
}

.login-btn {
	background-color: #5c0931;
	color: white;
	border: none;
	padding: 12px;
	width: 100%;
	border-radius: 4px;
	font-size: 16px;
	cursor: pointer;
	transition: background 0.3s ease;
}

.login-btn:hover {
	background-color: #7a1145;
}

.footer-text {
	margin-top: 20px;
	font-size: 12px;
	color: #777;
}
</style>


</head>
<body>
	<div class="login-container">
		<h1>Class Routine Login</h1>
		<%if(session.getAttribute("invalid_credential")!=null){ %>
		<p class='error_p'><%=session.getAttribute("invalid_credential") %></p>
		<%session.removeAttribute("invalid_credential"); }%>
		<form action="LoginServlet" method="post">
			<div class="form-group">
				<label for="username">Username</label> <input type="text"
					id="username" name="username" required>
			</div>

			<div class="form-group">
				<label for="password">Password</label> <input type="password"
					id="password" name="password" required>
			</div>

			<button type="submit" class="login-btn">Login</button>
		</form>
		<p class="footer-text">&copy; 2026 Varsity Routine Generator</p>
	</div>
</body>
</html>
