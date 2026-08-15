<%@page import="java.util.Map"%>
<%@page import="entity.Section"%>
<%@page import="local_db.SectionData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	// Pre-compute summary counts server-side
	java.util.List<Section> sectionList = SectionData.getSections(session).values() != null
			? new java.util.ArrayList<>(SectionData.getSections(session).values())
			: new java.util.ArrayList<>();
	int totalCount = 0, totalStudents = 0;
	for (Section s : sectionList) {
		totalCount++;
		totalStudents +=  s.students ;
	}
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Sections — Admin</title>
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
.section-table {
	background: #fff;
	border-radius: 8px;
	overflow: hidden;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}
.section-table thead th {
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
.section-table tbody td {
	vertical-align: middle;
	padding: 0.65rem 0.65rem;
	font-size: 0.9rem;
}
.section-table tbody tr:hover {
	background-color: #f8f9fa;
}
.section-id {
	font-weight: 600;
	color: #212529;
	font-size: 0.92rem;
}

/* ── Badges ── */
.badge-students {
	background-color: #e9ecef;
	color: #495057;
	font-size: 0.8rem;
	padding: 0.3em 0.6em;
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
				<h1>Sections</h1>
				<p class="subtitle">Manage class sections and student capacity</p>
			</div>
			<button class="add-btn" data-bs-toggle="modal"
				data-bs-target="#addSectionModal">+ Add Section</button>
		</div>

		<!-- Notifications -->
		<%
		String addTrue = session.getAttribute("section_true") == null ? null : session.getAttribute("section_true").toString();
		String addFalse = session.getAttribute("section_false") == null ? null : session.getAttribute("section_false").toString();
		if (addTrue != null) {
		%>
		<div class="alert alert-success alert-dismissible fade show" role="alert">
			<%=addTrue%>
			<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
		</div>
		<%
		session.removeAttribute("section_true");
		} else if (addFalse != null) {
		%>
		<div class="alert alert-danger alert-dismissible fade show" role="alert">
			<%=addFalse%>
			<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
		</div>
		<%
		session.removeAttribute("section_false");
		}
		%>

		<!-- Stat Cards -->
		<div class="stat-cards">
			<div class="stat-card">
				<div class="stat-value"><%=totalCount%></div>
				<div class="stat-label">Total Sections</div>
			</div>
			<div class="stat-card">
				<div class="stat-value"><%=totalStudents%></div>
				<div class="stat-label">Total Students</div>
			</div>
		</div>

		<!-- Section Table -->
		<%
		if (sectionList.isEmpty()) {
		%>
		<div class="empty-state">
			<h3>No sections yet</h3>
			<p>You haven't created any class sections. Add your first section to get started.</p>
			<button class="btn btn-primary" data-bs-toggle="modal"
				data-bs-target="#addSectionModal">+ Add Section</button>
		</div>
		<%
		} else {
		%>
		<!-- Search Toolbar -->
		<div class="filter-toolbar">
			<input type="text" class="form-control search-input" id="searchInput" placeholder="Search sections...">
		</div>
		<div class="result-counter" id="resultCounter"></div>
		<div class="no-results" id="noResults">
			<strong>No sections found</strong>
			<span>Try a different search term.</span>
		</div>
		<div class="table-responsive section-table">
			<table class="table table-hover mb-0">
				<thead>
					<tr>
						<th>Section</th>
						<th>Student Capacity</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<%
					for (Section s : sectionList) {
					%>
					<tr data-section-id="<%=s.id%>">
						<td><span class="section-id"><%=s.id%></span></td>
						<td>
							<span class="badge badge-students"><%=s.students%> students</span>
						</td>
						<td>
							<button class="btn btn-sm btn-outline-warning action-btn" data-bs-toggle="modal"
								data-bs-target="#editSectionModal" data-id="<%=s.id%>"
								data-students="<%=s.students%>">Edit</button>
							<button class="btn btn-sm btn-outline-danger action-btn"
								data-section-id="<%=s.id%>" data-delete-url="<%=request.getContextPath()%>/DeleteSectionServlet?sectionId=<%=s.id%>"
								onclick="if(confirm('Are you sure you want to delete this section?')) window.location.href = this.dataset.deleteUrl">Delete</button>
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

	<!-- Add Section Modal -->
	<div class="modal fade" id="addSectionModal" tabindex="-1"
		aria-labelledby="addSectionLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="addSectionLabel">Add New Section</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<p class="text-muted mb-3" style="font-size: 0.875rem;">Create a class section and define its student capacity.</p>
					<form action="<%=request.getContextPath()%>/CreateSectionServlet"
						method="post">
						<div class="mb-3">
							<label for="sectionName" class="form-label">Section Name <span class="text-danger">*</span></label>
							<input type="text" class="form-control" id="sectionName"
								name="sectionName" placeholder="e.g. A1, B2, CSE-A" required>
						</div>

						<div class="mb-3">
							<label for="sectionStudents" class="form-label">Student Capacity <span class="text-danger">*</span></label>
							<input type="number" class="form-control" id="sectionStudents"
								name="sectionStudents" min="1" required>
						</div>
				<div class="modal-footer">
					<button type="submit" class="btn btn-success">Save Section</button>
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Cancel</button>
				</div>
					</form>
				</div>
			</div>
		</div>
	</div>

	<!-- Edit Section Modal -->
	<div class="modal fade" id="editSectionModal" tabindex="-1"
		aria-labelledby="editSectionLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="editSectionLabel">Edit Section — <small id="editSectionIdLabel"></small></h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form action="<%=request.getContextPath()%>/EditSectionServlet"
						method="post">
						<input type="hidden" id="sectionId" name="sectionId">

						<div class="mb-3">
							<label for="editSectionName" class="form-label">Section Name</label>
							<input readonly="readonly" type="text" class="form-control"
								id="editSectionName" name="editSectionName">
						</div>

						<div class="mb-3">
							<label for="editSectionStudents" class="form-label">Student Capacity <span class="text-danger">*</span></label>
							<input type="number" class="form-control" id="editSectionStudents"
								name="editSectionStudents" min="1" required>
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

	<!-- Bootstrap JS — must load BEFORE inline scripts -->
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

	<!-- Menu Toggle Script -->
	<script>
  /**
   * 1. MENU TOGGLE (Mobile)
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
   * 2. EDIT SECTION MODAL POPULATION
   */
  document.addEventListener('DOMContentLoaded', function() {
    var editModal = document.getElementById('editSectionModal');
    editModal.addEventListener('show.bs.modal', function (event) {
        var button = event.relatedTarget;
        var id = button.getAttribute('data-id');
        var students = button.getAttribute('data-students');

        document.getElementById('editSectionIdLabel').innerText = id;
        document.getElementById('sectionId').value = id;
        document.getElementById('editSectionName').value = id;
        document.getElementById('editSectionStudents').value = students;
    });
  });
  </script>

  <!-- Search & Filter Script -->
  <script>
  /**
   * 3. SECTION SEARCH & FILTER
   */
  const searchInput = document.getElementById('searchInput');
  const resultCounter = document.getElementById('resultCounter');
  const noResultsDiv = document.getElementById('noResults');
  const tableBody = document.querySelector('.section-table tbody');
  const tableWrapper = document.querySelector('.table-responsive.section-table');

  function filterSections() {
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

    resultCounter.textContent = 'Showing ' + visibleCount + ' of ' + totalRows + ' sections';
    noResultsDiv.style.display = (visibleCount === 0 && totalRows > 0) ? 'block' : 'none';
    if (tableWrapper) {
      tableWrapper.style.display = (visibleCount === 0 && totalRows > 0) ? 'none' : 'block';
    }
  }

  if (searchInput) searchInput.addEventListener('input', filterSections);

  // Initialize counter on page load
  if (resultCounter && tableBody) {
    const total = tableBody.querySelectorAll('tr').length;
    resultCounter.textContent = 'Showing ' + total + ' of ' + total + ' sections';
  }
  </script>

</body>
</html>
