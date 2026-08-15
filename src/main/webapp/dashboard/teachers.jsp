<%@page import="java.util.List"%>
<%@page import="entity.Teacher"%>
<%@page import="local_db.TeacherData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	// Pre-compute summary counts server-side
	java.util.List<Teacher> teacherList = new java.util.ArrayList<>(TeacherData.getTeachers(session).values());
	int totalCount = 0, availableCount = 0;
	int totalMaxSlotHours = 0;
	String[] days = {"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"};
	for (Teacher t : teacherList) {
		totalCount++;
		// Available: at least one day true
		boolean hasAnyDay = false;
		for (int i = 0; i < t.availability.size(); i++) {
			if (t.availability.get(i)) { hasAnyDay = true; break; }
		}
		if (hasAnyDay) availableCount++;
		totalMaxSlotHours +=  t.maxSlotHours;
	}
	double avgMaxSlotHours = (totalCount > 0) ? (double) totalMaxSlotHours / totalCount : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Teachers — Admin</title>
<!-- Bootstrap CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet" type="text/css" href="./dashboard.css">

<style>
/* ── Page Header ── */
.page-header {
	margin-bottom: 1.5rem;
}
.page-header h1 {
	font-size: 1.6rem;
	font-weight: 700;
	color: #212529;
	margin-bottom: 0.25rem;
}
.page-header .subtitle {
	font-size: 0.9rem;
	color: #6c757d;
	margin-bottom: 1rem;
}

/* ── Stat Cards ── */
.stat-cards {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
	gap: 0.75rem;
	margin-bottom: 1.5rem;
}
.stat-card {
	background: #fff;
	border: 1px solid #dee2e6;
	border-radius: 8px;
	padding: 0.75rem 1rem;
	text-align: center;
}
.stat-card .stat-value {
	font-size: 1.5rem;
	font-weight: 700;
	color: #5c0931;
	line-height: 1.2;
}
.stat-card .stat-label {
	font-size: 0.75rem;
	color: #6c757d;
	text-transform: uppercase;
	letter-spacing: 0.05em;
	margin-top: 0.15rem;
}

/* ── Table ── */
.teacher-table {
	background: #fff;
	border-radius: 8px;
	overflow: hidden;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}
.teacher-table thead th {
	background: #f8f9fa;
	font-size: 0.78rem;
	font-weight: 600;
	text-transform: uppercase;
	letter-spacing: 0.04em;
	color: #495057;
	border-bottom: 2px solid #dee2e6;
	padding: 0.75rem 0.65rem;
	vertical-align: middle;
	white-space: nowrap;
}
.teacher-table tbody td {
	vertical-align: middle;
	padding: 0.65rem 0.65rem;
	font-size: 0.9rem;
}
.teacher-table tbody tr:hover {
	background-color: #f8f9fa;
}
.teacher-id {
	font-weight: 600;
	color: #6c757d;
	font-size: 0.82rem;
}
.teacher-name {
	font-weight: 600;
	color: #212529;
}

/* ── Availability Badges ── */
.badge-availability {
	background-color: #d1e7dd;
	color: #0f5132;
	font-size: 0.72rem;
	padding: 0.25em 0.5em;
	border-radius: 4px;
	font-weight: 500;
}

/* ── Actions ── */
.action-btn {
	font-size: 0.78rem;
	padding: 0.25rem 0.55rem;
}

/* ── Empty State ── */
.empty-state {
	text-align: center;
	padding: 3rem 1rem;
	background: #fff;
	border-radius: 8px;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}
.empty-state h3 {
	color: #495057;
	font-weight: 600;
	margin-bottom: 0.5rem;
}
.empty-state p {
	color: #6c757d;
	margin-bottom: 1rem;
}



/* ── Day Selection Cards ── */
.day-selection {
	display: flex;
	flex-wrap: wrap;
	gap: 0.5rem;
	margin-top: 0.25rem;
}
.day-card {
	position: relative;
}
.day-card input[type="checkbox"] {
	position: absolute;
	opacity: 0;
	width: 0;
	height: 0;
}
.day-card label {
	display: inline-block;
	padding: 0.35rem 0.75rem;
	border: 1px solid #dee2e6;
	border-radius: 6px;
	font-size: 0.82rem;
	font-weight: 500;
	color: #495057;
	cursor: pointer;
	transition: background-color 0.15s, border-color 0.15s;
	user-select: none;
}
.day-card input[type="checkbox"]:checked + label {
	background-color: #d1e7dd;
	border-color: #0f5132;
	color: #0f5132;
}
.day-card label:hover {
	border-color: #adb5bd;
}

/* ── Toolbar ── */
.filter-toolbar {
	display: flex;
	flex-wrap: wrap;
	gap: 0.75rem;
	align-items: center;
	margin-bottom: 0.75rem;
}
.filter-toolbar .search-input {
	flex: 1 1 260px;
	min-width: 180px;
}
.result-counter {
	font-size: 0.82rem;
	color: #6c757d;
	margin-bottom: 0.75rem;
}
.no-results {
	text-align: center;
	padding: 2rem 1rem;
	background: #fff;
	border: 1px dashed #dee2e6;
	border-radius: 8px;
	color: #6c757d;
	display: none;
}
.no-results strong {
	color: #495057;
	display: block;
	margin-bottom: 0.25rem;
}
</style>

</head>
<body>
	<!-- Navigation Bar -->
	<%@include file="nav_bar.jsp"%>

	<!-- Main Content -->
	<main class="container-fluid py-4">

		<!-- Page Header -->
		<div class="page-header d-flex justify-content-between align-items-start flex-wrap gap-2">
			<div>
				<h1>Teachers</h1>
				<p class="subtitle">Manage teachers and their weekly availability</p>
			</div>
			<button class="add-btn" data-bs-toggle="modal"
				data-bs-target="#addTeacherModal">+ Add New Teacher</button>
		</div>

		<!-- Notifications -->
		<%
		String addTrue = session.getAttribute("teacher_true") == null ? null : session.getAttribute("teacher_true").toString();
		String addFalse = session.getAttribute("teacher_false") == null ? null : session.getAttribute("teacher_false").toString();
		if (addTrue != null) {
		%>
		<div class="alert alert-success alert-dismissible fade show" role="alert">
			<%=addTrue%>
			<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
		</div>
		<%
		session.removeAttribute("teacher_true");
		} else if (addFalse != null) {
		%>
		<div class="alert alert-danger alert-dismissible fade show" role="alert">
			<%=addFalse%>
			<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
		</div>
		<%
		session.removeAttribute("teacher_false");
		}
		%>

		<!-- Stat Cards -->
		<div class="stat-cards">
			<div class="stat-card">
				<div class="stat-value"><%=totalCount%></div>
				<div class="stat-label">Total Teachers</div>
			</div>
			<div class="stat-card">
				<div class="stat-value"><%=availableCount%></div>
				<div class="stat-label">Available</div>
			</div>
			<div class="stat-card">
				<div class="stat-value"><%=String.format("%.1f", avgMaxSlotHours)%></div>
				<div class="stat-label">Avg Max Hours</div>
			</div>
		</div>

		<!-- Teacher Table -->
		<%
		if (teacherList.isEmpty()) {
		%>
		<div class="empty-state">
			<h3>No teachers found</h3>
			<p>Add your first teacher to configure weekly availability.</p>
			<button class="btn btn-primary" data-bs-toggle="modal"
				data-bs-target="#addTeacherModal">+ Add New Teacher</button>
		</div>
		<%
		} else {
		%>
		<!-- Search Filter Toolbar -->
		<div class="filter-toolbar">
			<input type="text" class="form-control search-input" id="searchInput" placeholder="Search teachers by ID or name...">
		</div>
		<div class="result-counter" id="resultCounter"></div>
		<div class="no-results" id="noResults">
			<strong>No teachers found</strong>
			<span>Try changing your search.</span>
		</div>
		<div class="table-responsive teacher-table">
			<table class="table table-hover mb-0">
				<thead>
					<tr>
						<th>ID</th>
						<th>Teacher</th>
						<th>Max Slot Hours</th>
						<th>Weekly Availability</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<%
					for (Teacher t : teacherList) {
					// Build availability day labels for this teacher
					List<String> availableDays = new java.util.ArrayList<>();
					for (int i = 0; i < days.length; i++) {
						if (t.availability.get(i)) {
							availableDays.add(days[i].substring(0, 3)); // "Sat", "Sun", etc.
						}
					}
					%>
					<tr>
						<td><span class="teacher-id"><%=t.id%></span></td>
						<td><span class="teacher-name"><%=t.name%></span></td>
						<td><%=t.maxSlotHours %> hrs</td>
						<td>
							<%
							if (availableDays.isEmpty()) {
							%>
							<span class="text-muted" style="font-size: 0.85rem;">No availability</span>
							<%
							} else {
								for (int i = 0; i < availableDays.size(); i++) {
							%>
							<span class="badge badge-availability"><%=availableDays.get(i)%></span>
							<%
								}
							}
							%>
						</td>
						<td>
							<button class="btn btn-sm btn-outline-warning action-btn" data-bs-toggle="modal"
								data-bs-target="#editTeacherModal" data-id="<%=t.id%>"
								data-name="<%=t.name%>"
								data-max-slot-hours="<%=t.maxSlotHours%>"
								data-availability="<%
									// Build a comma-separated list of available days dynamically
								   java.util.List<String> availDays = new java.util.ArrayList<>();
									for (int i = 0; i < days.length; i++) {
										if (t.availability.get(i)) {
											availDays.add(days[i]);
										}
									}
									out.print(String.join(",", availDays));
								%>">Edit</button>
							<a class="text-decoration-none"
								href="<%=request.getContextPath()%>/DeleteTeacherServlet?teacher_id=<%=t.id%>"
								onclick="return confirm('Are you sure you want to delete this teacher?')">
								<button class="btn btn-sm btn-outline-danger action-btn">Delete</button>
							</a>
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

		<%@ include file="footer.jsp" %>
	</main>

	<!-- Add Teacher Modal -->
	<div class="modal fade" id="addTeacherModal" tabindex="-1"
		aria-labelledby="addTeacherLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="addTeacherLabel">Add New Teacher</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<p class="text-muted mb-3" style="font-size: 0.875rem;">Configure the teacher's weekly teaching availability.</p>
					<form action="<%=request.getContextPath()%>/CreateTeacher"
						method="post">
						<div class="modal-section-title">Teacher Information</div>

						<div class="mb-3">
							<label for="teacherName" class="form-label">Teacher Name <span class="text-danger">*</span></label>
							<input type="text" class="form-control" id="teacherName"
								name="teacherName" placeholder="Enter teacher name" required>
						</div>

						<div class="mb-3">
							<label for="teacherMaxSlotHours" class="form-label">Max Slot Hours <span class="text-danger">*</span></label>
							<input type="number" class="form-control" id="teacherMaxSlotHours"
								name="teacherMaxSlotHours" min="1" required>
						</div>

						<div class="mb-3">
							<label class="form-label">Availability</label>
							<p class="text-muted" style="font-size: 0.82rem;">Select the days when this teacher is available.</p>
							<div class="day-selection">
								<div class="day-card">
									<input type="checkbox" name="availability" value="Saturday" id="addSat">
									<label for="addSat">Saturday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Sunday" id="addSun">
									<label for="addSun">Sunday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Monday" id="addMon">
									<label for="addMon">Monday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Tuesday" id="addTue">
									<label for="addTue">Tuesday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Wednesday" id="addWed">
									<label for="addWed">Wednesday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Thursday" id="addThu">
									<label for="addThu">Thursday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Friday" id="addFri">
									<label for="addFri">Friday</label>
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
	</div>

	<!-- Edit Teacher Modal -->
	<div class="modal fade" id="editTeacherModal" tabindex="-1"
		aria-labelledby="editTeacherLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="editTeacherLabel">Edit Teacher — <small id="editTeacherNameLabel"></small></h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form action="<%=request.getContextPath()%>/EditTeacherServlet"
						method="post">
						<input type="hidden" id="teacherId" name="teacherId">

						<div class="modal-section-title">Teacher Information</div>

						<div class="mb-3">
							<label for="editTeacherName" class="form-label">Teacher Name</label>
							<input type="text" class="form-control" id="editTeacherName"
								name="editTeacherName" required>
						</div>

						<div class="mb-3">
							<label for="editTeacherMaxSlotHours" class="form-label">Max Slot Hours <span class="text-danger">*</span></label>
							<input type="number" class="form-control" id="editTeacherMaxSlotHours"
								name="editTeacherMaxSlotHours" min="1" required>
						</div>

						<div class="mb-3">
							<label class="form-label">Availability</label>
							<p class="text-muted" style="font-size: 0.82rem;">Select the days when this teacher is available.</p>
							<div class="day-selection">
								<div class="day-card">
									<input type="checkbox" name="availability" value="Saturday" id="editSat">
									<label for="editSat">Saturday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Sunday" id="editSun">
									<label for="editSun">Sunday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Monday" id="editMon">
									<label for="editMon">Monday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Tuesday" id="editTue">
									<label for="editTue">Tuesday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Wednesday" id="editWed">
									<label for="editWed">Wednesday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Thursday" id="editThu">
									<label for="editThu">Thursday</label>
								</div>
								<div class="day-card">
									<input type="checkbox" name="availability" value="Friday" id="editFri">
									<label for="editFri">Friday</label>
								</div>
							</div>
						</div>
				<div class="modal-footer">
					<button type="submit" class="btn btn-success">Save Changes</button>
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Cancel</button>
				</div>
					</form>
				</div>
			</div>
		</div>
	</div>

  <!-- Search Filter Script -->
  <script>
  /**
   * 1. SEARCH / FILTER
   */
  const searchInput = document.getElementById('searchInput');
  const resultCounter = document.getElementById('resultCounter');
  const noResultsDiv = document.getElementById('noResults');
  const tableBody = document.querySelector('.teacher-table tbody');
  const tableWrapper = document.querySelector('.table-responsive.teacher-table');

  function filterTeachers() {
    const query = searchInput.value.toLowerCase().trim();
    let visibleCount = 0;
    const totalRows = tableBody ? tableBody.querySelectorAll('tr').length : 0;

    if (tableBody) {
      tableBody.querySelectorAll('tr').forEach(row => {
        const visibleText = (row.textContent || '').toLowerCase();
        const matchesSearch = !query || visibleText.includes(query);
        if (matchesSearch) {
          row.style.display = '';
          visibleCount++;
        } else {
          row.style.display = 'none';
        }
      });
    }

    resultCounter.textContent = 'Showing ' + visibleCount + ' of ' + totalRows + ' teachers';
    noResultsDiv.style.display = (visibleCount === 0 && totalRows > 0) ? 'block' : 'none';
  }

  if (searchInput) {
    searchInput.addEventListener('input', filterTeachers);
    searchInput.addEventListener('change', filterTeachers);
    filterTeachers();
  }
  </script>

	<!-- Menu Toggle Script -->
	<script>
  /**
   * 2. MENU TOGGLE (Mobile)
   */
  const menuToggle = document.querySelector('.menu-toggle');
  const navLinks = document.querySelector('.nav-links');
  if (menuToggle) {
    menuToggle.addEventListener('click', () => {
      navLinks.classList.toggle('active');
    });
  }
  </script>

  <!-- Edit Modal Population Script -->
  <script>
  /**
   * 2. EDIT TEACHER MODAL POPULATION
   */
  document.addEventListener('DOMContentLoaded', function() {
    var editModal = document.getElementById('editTeacherModal');
    editModal.addEventListener('show.bs.modal', function (event) {
        var button = event.relatedTarget;
        var id = button.getAttribute('data-id');
        var name = button.getAttribute('data-name');
        var maxSlotHours = button.getAttribute('data-max-slot-hours');
        var availability = button.getAttribute('data-availability').split(',');

        document.getElementById('editTeacherNameLabel').innerText = name;
        document.getElementById('teacherId').value = id;
        document.getElementById('editTeacherName').value = name;
        document.getElementById('editTeacherMaxSlotHours').value = maxSlotHours;

        // Reset all availability checkboxes
        editModal.querySelectorAll('input[type=checkbox]').forEach(cb => cb.checked = false);

        // Set availability checkboxes
        availability.forEach(function(day) {
            var cb = editModal.querySelector('input[value="' + day.trim() + '"]');
            if (cb) cb.checked = true;
        });
    });
  });
  </script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
