<%@page import="entity.Section"%>
<%@page import="local_db.SectionData"%>
<%@page import="entity.Teacher"%>
<%@page import="local_db.TeacherData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>All Sections</title>
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

		<h1>All Sections</h1>
		<button class="add-btn btn btn-primary" data-bs-toggle="modal"
			data-bs-target="#addTeacherModal">+ Add New Section</button>

		<%
		String addTrue = session.getAttribute("section_true") == null ? null : session.getAttribute("section_true").toString();
		String addFalse = session.getAttribute("section_false") == null
				? null
				: session.getAttribute("section_false").toString();
		if (addTrue != null) {
		%>
		<p class="notify_true"><%=addTrue%></p>
		<%
		session.removeAttribute("section_true");
		} else if (addFalse != null) {
		%>
		<p class="notify_false"><%=addFalse%></p>
		<%
		session.removeAttribute("section_false");
		}
		%>

		<table class="data-table">
			<thead>
				<tr>
					<th>Section</th>
					<th>Student Capacity</th>
					<th colspan="2"></th>
				</tr>
			</thead>
			<tbody>
				<%
				for (Section t : SectionData.getSections().values()) {
				%>
				<tr>
					<td><%=t.id%></td>
					<td><%=t.students%></td>
					<td><button class="btn btn-warning" data-bs-toggle="modal"
							data-bs-target="#editTeacherModal" data-id="<%=t.id%>"
							data-students="<%=t.students%>">Edit</button></td>
					<td><a href="<%=request.getContextPath()%>/DeleteSectionServlet?sectionId=<%=t.id%>"><button style="background: red">Delete</button></a></td>
				</tr>
				<%
				}
				%>
				<!-- More rows dynamically generated -->
			</tbody>
		</table>
	</main>

	<!-- Modal -->
	<div class="modal fade" id="addTeacherModal" tabindex="-1"
		aria-labelledby="addTeacherModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<form action="<%=request.getContextPath()%>/CreateSectionServlet"
					method="post">
					<div class="modal-header">
						<h5 class="modal-title" id="addTeacherModalLabel">Add New
							Section</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"
							aria-label="Close"></button>
					</div>
					<div class="modal-body">

						<div class="mb-3">
							<label for="sectionName" class="form-label">Section Name</label>
							<input type="text" class="form-control" id="sectionName"
								name="sectionName" required>
						</div>

						<div class="mb-3">
							<label for="sectionStudents" class="form-label">Students
								Quantity</label> <input type="number" class="form-control"
								id="sectionStudents" name="sectionStudents" required>
						</div>

					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-success">Save
							Section</button>
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Cancel</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Edit Teacher Modal -->
	<div class="modal fade" id="editTeacherModal" tabindex="-1"
		aria-labelledby="editTeacherModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<form action="<%=request.getContextPath()%>/EditSectionServlet"
					method="post">
					<div class="modal-header">
						<h5 class="modal-title" id="editTeacherModalLabel">Edit
							Section</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
					</div>
					<div class="modal-body">
						<input type="hidden" id="sectionId" name="sectionId">

						<div class="mb-3">
							<label for="editSectionName" class="form-label">Section
								Name</label> <input type="text" class="form-control"
								id="editSectionName" name="editSectionName" required>
						</div>

						<div class="mb-3">
							<label for="editSectionStudents" class="form-label">Students
								Quantity</label> <input type="number" class="form-control"
								id="editSectionStudents" name="editSectionStudents" required>
						</div>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-success">Save
							Changes</button>
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Cancel</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Bootstrap JS -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

	<script>
    const menuToggle = document.querySelector('.menu-toggle');
    const navLinks = document.querySelector('.nav-links');

    menuToggle.addEventListener('click', () => {
      navLinks.classList.toggle('active');
    });
  </script>

	<script>
// Pre-fill modal fields when Edit button is clicked
document.addEventListener('DOMContentLoaded', function() {
    var editModal = document.getElementById('editTeacherModal');
    editModal.addEventListener('show.bs.modal', function (event) {
        var button = event.relatedTarget;
        var id = button.getAttribute('data-id');
        var students = button.getAttribute('data-students');
        
        // Fill form fields
        document.getElementById('sectionId').value = id;
        document.getElementById('editSectionName').value = id;
        document.getElementById('editSectionStudents').value = students;

        
    });
});
</script>
</body>
</html>

