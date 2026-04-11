<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Routine Generation</title>
<style type="text/css">
body {
  font-family: Arial, sans-serif;
  background-color: #5c0931;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.loading-container {
  text-align: center;
}

h1 {
  margin-bottom: 20px;
}

.progress-bar {
  width: 400px;
  height: 25px;
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  margin: 0 auto;
}

.progress {
  height: 100%;
  width: 0;
  background-color: #5c0931;
  transition: width 0.3s ease;
}

#progress-text {
  margin-top: 15px;
  font-size: 18px;
}

</style>
</head>
<body>
  <div class="loading-container">
    <h1>Generating Weekly Routine...</h1>
    <div class="progress-bar">
      <div class="progress" id="progress"></div>
    </div>
    <p id="progress-text">0%</p>
  </div>

  <script>
async function fetchProgress() {
  const response = await fetch('<%=request.getContextPath()%>/GetRoutineProgressServlet');
  const data = await response.json();
  const percent = Math.max(0, Math.min(100, data.percentage));

  document.getElementById('progress').style.width = percent + '%';
  document.getElementById('progress-text').textContent = percent + '%';

  if (percent === 100) {
    window.location.href = '<%=request.getContextPath()%>/dashboard/generated_routine.jsp';
  }
}

setInterval(fetchProgress, 1000);
</script>

</body>
</html>
