<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Generate Routine</title>
<style type="text/css">
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: #f4f2f4;
  color: #333;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 40px 16px;
}

.page-wrapper {
  width: 100%;
  max-width: 680px;
}

/* ── Header ── */
.routine-header {
  text-align: center;
  background-color: #5c0931;
  color: white;
  padding: 36px 28px;
  border-radius: 12px 12px 0 0;
}

.routine-header h1 {
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-bottom: 6px;
}

.routine-header .subtitle {
  font-size: 14px;
  opacity: 0.88;
  font-weight: 400;
  line-height: 1.5;
  margin-bottom: 18px;
}

.routine-header .note {
  background: rgba(255,255,255,0.12);
  border-radius: 8px;
  padding: 14px 18px;
  font-size: 13px;
  line-height: 1.6;
  text-align: left;
  opacity: 0.95;
}

/* ── Card body ── */
.routine-card {
  background: #fff;
  padding: 32px 28px 36px;
  border-radius: 0 0 12px 12px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
}

/* ── Option cards ── */
.option-group {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin: 28px 0;
}

.option-card {
  display: flex;
  align-items: flex-start;
  border: 2px solid #e0d8dc;
  border-radius: 10px;
  padding: 16px 18px;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
  position: relative;
}

.option-card:hover {
  border-color: #c4a3b5;
  background: #fdf9fb;
}

.option-card:has(input:checked) {
  border-color: #5c0931;
  background: #fdf5f7;
  box-shadow: 0 0 0 1px #5c0931, 0 2px 8px rgba(92,9,49,0.12);
}

/* Hide default radio, style a custom one */
.option-card input[type="radio"] {
  appearance: none;
  -webkit-appearance: none;
  width: 20px;
  height: 20px;
  min-width: 20px;
  border: 2px solid #c4a3b5;
  border-radius: 50%;
  margin-top: 2px;
  margin-right: 14px;
  position: relative;
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s;
}

.option-card input[type="radio"]:checked {
  border-color: #5c0931;
  background: #5c0931;
  box-shadow: inset 0 0 0 3px #fff;
}

.option-card input[type="radio"]:checked + .option-content .mode-name {
  color: #5c0931;
}

.option-content {
  flex: 1;
  min-width: 0;
}

.mode-name {
  font-size: 16px;
  font-weight: 600;
  color: #555;
  margin-bottom: 3px;
  transition: color 0.2s;
}

.mode-desc {
  font-size: 13px;
  color: #777;
  line-height: 1.5;
}

/* ── Buttons ── */
.btn-row {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.btn-primary {
  flex: 1;
  background-color: #5c0931;
  color: white;
  border: none;
  padding: 14px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.3px;
  transition: background-color 0.2s, box-shadow 0.2s;
}

.btn-primary:hover {
  background-color: #7a1145;
  box-shadow: 0 2px 8px rgba(92,9,49,0.25);
}

.btn-secondary {
  background-color: #f0ecee;
  color: #5c0931;
  border: 1px solid #d5c8ce;
  padding: 14px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  transition: background-color 0.2s, border-color 0.2s;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.btn-secondary:hover {
  background-color: #e8e0e4;
  border-color: #5c0931;
}

/* ── Responsive ── */
@media (max-width: 520px) {
  body {
    padding: 16px 8px;
  }
  .routine-header {
    padding: 24px 16px;
  }
  .routine-card {
    padding: 24px 16px 28px;
  }
  .routine-header h1 {
    font-size: 22px;
  }
  .btn-row {
    flex-direction: column;
  }
}
</style>
</head>
<body>
	<div class="page-wrapper">
		<header class="routine-header">
			<h1>Routine Generator</h1>
			<p class="subtitle">
				Choose how extensively AURG should search for a feasible routine.
			</p>
			<div class="note">
				AURG searches through possible timetable assignments while respecting the configured scheduling constraints. Higher search modes explore more possibilities and may produce better assignment results, but can require more processing time.
			</div>
		</header>

		<main class="routine-card">
			<form action="<%=request.getContextPath()%>/GenerateRoutineServlet"
				method="get" class="option-group">

				<label class="option-card">
					<input type="radio" name="efficiency" value="low" required>
					<div class="option-content">
						<div class="mode-name">Quick</div>
						<div class="mode-desc">Faster generation with a smaller search range.</div>
					</div>
				</label>

				<label class="option-card">
					<input type="radio" name="efficiency" value="medium">
					<div class="option-content">
						<div class="mode-name">Standard</div>
						<div class="mode-desc">Balanced search time and routine quality.</div>
					</div>
				</label>

				<label class="option-card">
					<input type="radio" name="efficiency" value="high">
					<div class="option-content">
						<div class="mode-name">Thorough</div>
						<div class="mode-desc">Explores more possibilities for improved assignment.</div>
					</div>
				</label>

				<label class="option-card">
					<input type="radio" name="efficiency" value="optimal">
					<div class="option-content">
						<div class="mode-name">Maximum Search</div>
						<div class="mode-desc">Performs the most extensive search for the best solution found within the configured limit.</div>
					</div>
				</label>

				<div class="btn-row">
					<button type="submit" class="btn-primary">Generate Routine</button>
					<a href="courses.jsp" class="btn-secondary">&#127968; Home</a>
				</div>
			</form>
		</main>
	<%@ include file="footer.jsp" %>
	</div>

</body>
</html>
