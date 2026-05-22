<%@page import="java.util.List"%>
<%@page import="entity.Teacher"%>
<%@page import="local_db.TeacherData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Teachers Availability</title>
<!-- Bootstrap CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">

<link rel="stylesheet" href="./dashboard.css">
</head>
<body>
	<!-- Navigation Bar -->
	<%@include file="nav_bar.jsp"%>

	<!-- Main Content -->
	<main>

		<h1>Teachers Weekly Availability</h1>
		<button class="add-btn btn btn-primary" data-bs-toggle="modal"
			data-bs-target="#addTeacherModal">+ Add New Teacher</button>

		<%
		String addTrue = session.getAttribute("teacher_true") == null
				? null
				: session.getAttribute("teacher_true").toString();
		String addFalse = session.getAttribute("teacher_false") == null
				? null
				: session.getAttribute("teacher_false").toString();
		if (addTrue != null) {
		%>
		<p class="notify_true"><%=addTrue%></p>
		<%
		session.removeAttribute("teacher_true");
		} else if (addFalse != null) {
		%>
		<p class="notify_false"><%=addFalse%></p>
		<%
		session.removeAttribute("teacher_false");
		}
		
		%>
		<table class="data-table">
			<thead>
				<tr>
					<th>ID</th>
					<th>Teacher Name</th>
					<th>Max Slot Hours</th>
					<th>Saturday</th>
					<th>Sunday</th>
					<th>Monday</th>
					<th>Tuesday</th>
					<th>Wednesday</th>
					<th>Thursday</th>
					<th>Friday</th>
					<th colspan="2"></th>
				</tr>
			</thead>
			<tbody>
<%
    String[] days = {"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"};
    for (Teacher t : TeacherData.getTeachers().values()) {
%>
    <tr>
        <td><%=t.id%></td>
        <td><%=t.name%></td>
        <td><%=t.maxSlotHours %></td>
        <% for (int i = 0; i < days.length; i++) { %>
            <td><%= t.availability.get(i) ? "Available" : "Not Available" %></td>
        <% } %>
        <td>
            <button class="btn btn-warning"
                    data-bs-toggle="modal"
                    data-bs-target="#editTeacherModal"
                    data-id="<%=t.id %>"
                    data-name="<%=t.name %>"
                    data-max-slot-hours="<%=t.maxSlotHours %>"
                    data-availability="<%
                        // Build a comma-separated list of available days dynamically
                       List<String> availableDays = new java.util.ArrayList<>();
                        for (int i = 0; i < days.length; i++) {
                            if (t.availability.get(i)) {
                                availableDays.add(days[i]);
                            }
                        }
                        out.print(String.join(",", availableDays));
                    %>">
                Edit
            </button>
        </td>
        <td>
            <a href="<%=request.getContextPath()%>/DeleteTeacherServlet?teacher_id=<%=t.id%>">
                <button style="background: red">Delete</button>
            </a>
        </td>
    </tr>
<%
    }
%>
</tbody>
			
		</table>
	</main>
	<!-- Modal -->
	<div class="modal fade" id="addTeacherModal" tabindex="-1"
		aria-labelledby="addTeacherModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<form action="<%=request.getContextPath()%>/CreateTeacher"
					method="post">
					<div class="modal-header">
						<h5 class="modal-title" id="addTeacherModalLabel">Add New
							Teacher</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"
							aria-label="Close"></button>
					</div>
					<div class="modal-body">

						<div class="mb-3">
							<label for="teacherName" class="form-label">Teacher Name</label>
							<input type="text" class="form-control" id="teacherName"
								name="teacherName" required>
						</div>
						<div class="mb-3">
							<label for="teacherMaxSlotHours" class="form-label">Teacher Max Slot Hours</label>
							<input type="number" class="form-control" id="teacherMaxSlotHours"
								name="teacherMaxSlotHours" required>
						</div>
						<div class="mb-3">
							<label class="form-label">Availability</label><br>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox"
									name="availability" value="Saturday"> <label
									class="form-check-label">Saturday</label>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox"
									name="availability" value="Sunday"> <label
									class="form-check-label">Sunday</label>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox"
									name="availability" value="Monday"> <label
									class="form-check-label">Monday</label>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox"
									name="availability" value="Tuesday"> <label
									class="form-check-label">Tuesday</label>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox"
									name="availability" value="Wednesday"> <label
									class="form-check-label">Wednesday</label>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox"
									name="availability" value="Thursday"> <label
									class="form-check-label">Thursday</label>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox"
									name="availability" value="Friday"> <label
									class="form-check-label">Friday</label>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-success">Save Teacher</button>
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Cancel</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	
	<!-- Edit Teacher Modal -->
<div class="modal fade" id="editTeacherModal" tabindex="-1" aria-labelledby="editTeacherModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <form action="<%=request.getContextPath() %>/EditTeacherServlet" method="post">
        <div class="modal-header">
          <h5 class="modal-title" id="editTeacherModalLabel">Edit Teacher</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
            <input type="hidden" id="teacherId" name="teacherId">

            <div class="mb-3">
                <label for="teacherName" class="form-label">Teacher Name</label>
                <input type="text" class="form-control" id="editTeacherName" name="editTeacherName" required>
            </div>
            
            <div class="mb-3">
                <label for="teacherMaxSlotHours" class="form-label">Teacher Max Slot Hours</label>
                <input type="number" class="form-control" id="editTeacherMaxSlotHours" name="editTeacherMaxSlotHours" required>
            </div>

            <div class="mb-3">
                <label class="form-label">Availability</label><br>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="checkbox" name="availability" value="Saturday" id="satCheck">
                    <label class="form-check-label" for="satCheck">Saturday</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="checkbox" name="availability" value="Sunday" id="sunCheck">
                    <label class="form-check-label" for="sunCheck">Sunday</label>
                </div>
              
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="checkbox" name="availability" value="Monday" id="monCheck">
                    <label class="form-check-label" for="monCheck">Monday</label>
                </div>
                  <div class="form-check form-check-inline">
                    <input class="form-check-input" type="checkbox" name="availability" value="Tuesday" id="tueCheck">
                    <label class="form-check-label" for="tueCheck">Tuesday</label>
                </div>
                  <div class="form-check form-check-inline">
                    <input class="form-check-input" type="checkbox" name="availability" value="Wednesday" id="wedCheck">
                    <label class="form-check-label" for="wedCheck">Wednesday</label>
                </div>
                  <div class="form-check form-check-inline">
                    <input class="form-check-input" type="checkbox" name="availability" value="Thursday" id="thuCheck">
                    <label class="form-check-label" for="thuCheck">Thursday</label>
                </div>
                  <div class="form-check form-check-inline">
                    <input class="form-check-input" type="checkbox" name="availability" value="Friday" id="friCheck">
                    <label class="form-check-label" for="friCheck">Friday</label>
                </div>
              
              </div>
        </div>
        <div class="modal-footer">
          <button type="submit" class="btn btn-success">Save Changes</button>
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        </div>
      </form>
    </div>
  </div>
</div>

	<!-- Bootstrap JS -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
// Pre-fill modal fields when Edit button is clicked
document.addEventListener('DOMContentLoaded', function() {
    var editModal = document.getElementById('editTeacherModal');
    editModal.addEventListener('show.bs.modal', function (event) {
        var button = event.relatedTarget;
        var id = button.getAttribute('data-id');
        var name = button.getAttribute('data-name');
        var max_slot_hours = button.getAttribute('data-max-slot-hours');
        var availability = button.getAttribute('data-availability').split(',');

        // Fill form fields
        document.getElementById('teacherId').value = id;
        document.getElementById('editTeacherName').value = name;
        document.getElementById('editTeacherMaxSlotHours').value = max_slot_hours;

        // Reset checkboxes
        document.querySelectorAll('#editTeacherModal input[type=checkbox]').forEach(cb => cb.checked = false);

        // Set availability checkboxes
        availability.forEach(day => {
            let cb = document.querySelector('#editTeacherModal input[value="' + day.trim() + '"]');
            if (cb) cb.checked = true;
        });
    });
});
</script>

	<script>
    const menuToggle = document.querySelector('.menu-toggle');
    const navLinks = document.querySelector('.nav-links');

    menuToggle.addEventListener('click', () => {
      navLinks.classList.toggle('active');
    });
  </script>
</body>
</html>

