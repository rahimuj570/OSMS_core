<%@page import="local_db.SectionData"%>
<%@page import="java.util.Map"%>
<%@page import="entity.Section"%>
<%@page import="entity.Teacher"%>
<%@page import="local_db.TeacherData"%>
<%@page import="helper.ConnectionProvider"%>
<%@page import="local_db.CourseData"%>
<%@page import="entity.Course"%>
<%@page import="entity.CourseType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	// Pre-compute summary counts server-side
	Map<Integer, Teacher> teachers = TeacherData.getTeachers(session);
	java.util.List<Course> courseList = CourseData.getCourses(session);
	int totalCount = 0, theoryCount = 0, labCount = 0, labOrientedCount = 0;
	for (Course c : courseList) {
		totalCount++;
		if (c.type == CourseType.THEORY) theoryCount++;
		else if (c.type == CourseType.LAB) labCount++;
		else if (c.type == CourseType.LAB_ORIENTED_THEORY) labOrientedCount++;
	}
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Courses — Admin</title>
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
.course-table {
	background: #fff;
	border-radius: 8px;
	overflow: hidden;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}
.course-table thead th {
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
.course-table tbody td {
	vertical-align: middle;
	padding: 0.65rem 0.65rem;
	font-size: 0.9rem;
}
.course-table tbody tr:hover {
	background-color: #f8f9fa;
}
.course-id {
	font-weight: 600;
	color: #212529;
	font-size: 0.92rem;
}

/* ── Badges ── */
.badge-theory { background-color: #e7f3ff; color: #0d6efd; }
.badge-lab { background-color: #fff3cd; color: #856404; }
.badge-lab-oriented { background-color: #f8d7da; color: #842029; }
.badge-teacher { background-color: #e8f5e9; color: #1b5e20; font-size: 0.75rem; padding: 0.25em 0.55em; }
.badge-section { background-color: #e9ecef; color: #495057; font-size: 0.75rem; padding: 0.25em 0.55em; }

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

/* ── Modal Sections ── */
.modal-section-title {
	font-size: 0.82rem;
	font-weight: 600;
	text-transform: uppercase;
	letter-spacing: 0.04em;
	color: #5c0931;
	border-bottom: 1px solid #e9ecef;
	padding-bottom: 0.35rem;
	margin-top: 1.25rem;
	margin-bottom: 0.75rem;
}
.modal-section-title:first-of-type {
	margin-top: 0;
}

/* ── Teacher Select Helper ── */
.teacher-select-helper {
	font-size: 0.78rem;
	color: #6c757d;
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
.filter-toolbar .type-filter {
	flex: 0 0 180px;
}
.filter-toolbar .type-filter .form-select {
	font-size: 0.875rem;
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
				<h1>Courses</h1>
				<p class="subtitle">Manage courses, sections, and teacher preferences</p>
			</div>
			<button class=" add-btn" data-bs-toggle="modal"
				data-bs-target="#addCourseModal">+ Add Course</button>
		</div>

		<!-- Notifications -->
		<%
		String addTrue = session.getAttribute("course_true") == null ? null : session.getAttribute("course_true").toString();
		String addFalse = session.getAttribute("course_false") == null ? null : session.getAttribute("course_false").toString();
		if (addTrue != null) {
		%>
		<div class="alert alert-success alert-dismissible fade show" role="alert">
			<%=addTrue%>
			<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
		</div>
		<%
		session.removeAttribute("course_true");
		} else if (addFalse != null) {
		%>
		<div class="alert alert-danger alert-dismissible fade show" role="alert">
			<%=addFalse%>
			<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
		</div>
		<%
		session.removeAttribute("course_false");
		}
		%>

		<!-- Stat Cards -->
		<div class="stat-cards">
			<div class="stat-card">
				<div class="stat-value"><%=totalCount%></div>
				<div class="stat-label">Total Courses</div>
			</div>
			<div class="stat-card">
				<div class="stat-value"><%=theoryCount%></div>
				<div class="stat-label">Theory</div>
			</div>
			<div class="stat-card">
				<div class="stat-value"><%=labCount%></div>
				<div class="stat-label">Labs</div>
			</div>
			<div class="stat-card">
				<div class="stat-value"><%=labOrientedCount%></div>
				<div class="stat-label">Lab-Oriented</div>
			</div>
		</div>

		<!-- Course Table -->
		<%
		if (courseList.isEmpty()) {
		%>
		<div class="empty-state">
			<h3>No courses found</h3>
			<p>You haven't added any courses yet. Get started by adding your first course.</p>
			<button class="btn btn-primary" data-bs-toggle="modal"
				data-bs-target="#addCourseModal">+ Add Course</button>
		</div>
		<%
		} else {
		%>
		<!-- Search & Filter Toolbar -->
		<div class="filter-toolbar">
			<input type="text" class="form-control search-input" id="searchInput" placeholder="Search courses by ID, teacher, or section...">
			<select class="form-select type-filter" id="typeFilter">
				<option value="all">All Courses</option>
				<option value="THEORY">Theory</option>
				<option value="LAB">Lab</option>
				<option value="LAB_ORIENTED_THEORY">Lab-Oriented</option>
			</select>
		</div>
		<div class="result-counter" id="resultCounter"></div>
		<div class="no-results" id="noResults">
			<strong>No courses found</strong>
			<span>Try changing your search or filter.</span>
		</div>
		<div class="table-responsive course-table">
			<table class="table table-hover mb-0">
				<thead>
					<tr>
						<th>Course</th>
						<th>Type</th>
						<th>Required Lab</th>
						<th>Preferred Teachers</th>
						<th>Sections</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<%
					for (Course c : courseList) {
					%>
					<tr data-course-id="<%=c.id%>" data-type="<%=c.type.name()%>">
						<td><span class="course-id"><%=c.id%></span></td>
						<td>
							<%
							String badgeClass = "";
							if (c.type == CourseType.THEORY) badgeClass = "badge-theory";
							else if (c.type == CourseType.LAB) badgeClass = "badge-lab";
							else if (c.type == CourseType.LAB_ORIENTED_THEORY) badgeClass = "badge-lab-oriented";
							%>
							<span class="badge <%=badgeClass%>"><%=c.type.name()%></span>
						</td>
						<td>
							<%
							if (c.requiredLab == null ) {
								out.print("<span class=\"text-muted\">—</span>");
							} else {
								out.print("<span class=\"fw-medium\">" + c.requiredLab + "</span>");
							}
							%>
						</td>
						<td>
							<%
							if (c.preferredTeachers.isEmpty()) {
								out.print("<span class=\"text-muted\">No preference</span>");
							} else {
								for (int i = 0; i < c.preferredTeachers.size(); i++) {
									Integer tid = (Integer) c.preferredTeachers.toArray()[i];
									Teacher t = teachers.get(tid);
									String tName = (t != null) ? t.name : "Unknown";
									out.print("<span class=\"badge badge-teacher\">" + tName + "</span> ");
								}
							}
							%>
						</td>
						<td>
							<%
							if (c.sectionIds.isEmpty()) {
								out.print("<span class=\"text-muted\">No sections</span>");
							} else {
								for (int i = 0; i < c.sectionIds.size(); i++) {
									out.print("<span class=\"badge badge-section\">" + c.sectionIds.toArray()[i] + "</span> ");
								}
							}
							%>
						</td>
						<td>
							<button class="btn btn-sm btn-outline-warning action-btn" data-bs-toggle="modal"
								data-bs-target="#editCourseModal" data-courseid="<%=c.id%>"
								data-coursetype="<%=c.getTypeString()%>"
								data-requiredlab="<%=c.getRequiredLabString()%>"
								data-sections="<%=c.getSectionsCsv()%>"
								data-preferred="<%=c.getPreferredTeachersCsv()%>">Edit</button>
							<a class="text-decoration-none"
								href="<%=request.getContextPath()%>/DeleteCourseServlet?course_id=<%=c.id%>"
								onclick="return confirm('Are you sure you want to delete course <%=c.id%>?')">
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

	<!-- Add Course Modal -->
	<div class="modal fade" id="addCourseModal" tabindex="-1"
		aria-labelledby="addCourseLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="addTeacherModalLabel">Add New Course</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form action="<%=request.getContextPath()%>/AddCourseServlet"
						method="post">

						<!-- Basic Information -->
						<div class="modal-section-title">Basic Information</div>
						<div class="row">
							<div class="col-md-6 mb-3">
								<label for="courseId" class="form-label">Course ID <span class="text-danger">*</span></label>
								<input type="text" class="form-control" id="courseId" name="courseId"
									required>
							</div>
							<div class="col-md-6 mb-3">
								<label for="courseType" class="form-label">Course Type <span class="text-danger">*</span></label>
								<select class="form-select" id="courseType" name="courseType" required>
									<option value="">— Select type —</option>
									<option value="THEORY">THEORY</option>
									<option value="LAB">LAB</option>
									<option value="LAB_ORIENTED_THEORY">LAB ORIENTED THEORY</option>
								</select>
							</div>
						</div>
						<div class="mb-3" id="requiredLabWrapper" style="display: none;">
							<label for="requiredLab" class="form-label">Required Lab</label>
							<select class="form-select" id="requiredLab" name="requiredLab">
								<option value="">— Select lab —</option>
								<option value="GENERAL">GENERAL</option>
								<option value="COMPUTER">COMPUTER</option>
								<option value="ELECTRONIC">ELECTRONIC</option>
							</select>
						</div>

						<!-- Teacher Preference -->
						<div class="modal-section-title">Teacher Preference</div>
						<div class="mb-3">
							<label class="form-label">Preferred Teachers</label>
							<select class="form-select" name="preferredTeachers"
							multiple="multiple"
								id="preferredTeachers" size="4" style="min-height: 120px;">
								<%
								for (Teacher t : TeacherData.getTeachers(session).values()) {
								%>
								<option value="<%=t.id%>"><%=t.name%></option>
								<%
								}
								%>
							</select>
							<small class="teacher-select-helper">Hold Ctrl (Windows) or Command (Mac) to select multiple</small>
						</div>

						<div class="modal-footer">
							<button type="submit" class="btn btn-success">Save Course</button>
							<button type="button" class="btn btn-secondary"
								data-bs-dismiss="modal">Cancel</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>

	<!-- Edit Course Modal -->
	<div class="modal fade" id="editCourseModal" tabindex="-1"
		aria-labelledby="editCourseLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="addTeacherModalLabel">Edit Course (<small id="editCourseTitleId"></small>)</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form action="<%=request.getContextPath()%>/EditCourseServlet"
						method="post">
						<input type="hidden" id="editCourseId" name="courseId">

						<!-- Course Information -->
						<div class="modal-section-title">Course Information</div>
						<div class="row">
							<div class="col-md-6 mb-3">
								<label for="editCourseType" class="form-label">Course Type <span class="text-danger">*</span></label>
								<select class="form-select" id="editCourseType"
									name="courseType" required>
									<option value="">— Select type —</option>
									<option value="THEORY">THEORY</option>
									<option value="LAB">LAB</option>
									<option value="LAB_ORIENTED_THEORY">LAB ORIENTED THEORY</option>
								</select>
							</div>
							<div class="col-md-6 mb-3" id="editRequiredLabWrapper"
								style="display: none;">
								<label for="editRequiredLab" class="form-label">Required Lab</label>
								<select class="form-select" id="editRequiredLab"
									name="requiredLab">
									<option value="">— Select lab —</option>
									<option value="GENERAL">GENERAL</option>
									<option value="COMPUTER">COMPUTER</option>
									<option value="ELECTRONIC">ELECTRONIC</option>
								</select>
							</div>
						</div>

						<!-- Sections -->
						<div class="modal-section-title">Linked Sections</div>
						<div class="mb-3">
							<div class="d-flex align-items-center">
								<select class="form-select me-2" name="sections" multiple="multiple"
									id="editSections" size="4" style="min-height: 120px;">
									<%
									for (entity.Section s : SectionData.getSections(session).values()) {
									%>
									<option value="<%=s.id%>"><%=s.id%> (Students: <%=s.students%>)</option>
									<%
									}
									%>
								</select>
								<button type="button" class="btn btn-outline-danger btn-sm"
									id="clearSectionsBtn">Clear</button>
							</div>
							<small class="teacher-select-helper">Click once to select, click again to unselect</small>
						</div>

						<!-- Teacher Preference -->
						<div class="modal-section-title">Teacher Preference</div>
						<div class="mb-3">
							<label class="form-label">Preferred Teachers</label>
							<select class="form-select" name="preferredTeachers" multiple="multiple"
								id="editPreferredTeachers" size="4" style="min-height: 120px;">
								<%
								for (entity.Teacher t : TeacherData.getTeachers(session).values()) {
								%>
								<option value="<%=t.id%>"><%=t.name%></option>
								<%
								}
								%>
							</select>
							<small class="teacher-select-helper">Hold Ctrl (Windows) or Command (Mac) to select multiple</small>
						</div>

						<div class="modal-footer">
							<button type="submit" class="btn btn-success">Update Course</button>
							<button type="button" class="btn btn-secondary"
								data-bs-dismiss="modal">Cancel</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>

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

  /**
   * 2. SHARED HELPERS: Click-to-Toggle
   */
  function enableToggle(selectElement) {
    if (!selectElement) return;
    Array.from(selectElement.options).forEach(opt => {
      opt.addEventListener('mousedown', e => {
        e.preventDefault();
        opt.selected = !opt.selected;
        selectElement.dispatchEvent(new Event('change'));
      });
    });
  }

  /**
   * 3. ADD COURSE MODAL LOGIC
   */
  const courseTypeSelect = document.getElementById('courseType');
  const requiredLabWrapper = document.getElementById('requiredLabWrapper');
  const requiredLabSelect = document.getElementById('requiredLab');
  const preferredSelect = document.getElementById('preferredTeachers');

  // Lab Toggle
  if (courseTypeSelect) {
    courseTypeSelect.addEventListener('change', () => {
      const isLab = (courseTypeSelect.value === 'LAB' || courseTypeSelect.value === 'LAB_ORIENTED_THEORY');
      requiredLabWrapper.style.display = isLab ? 'block' : 'none';
      requiredLabSelect.required = isLab;
      if (!isLab) requiredLabSelect.value = "";
    });
  }

  // Enable click-to-toggle for preferred teachers
  if (preferredSelect) {
    enableToggle(preferredSelect);
  }

  /**
   * 4. EDIT COURSE MODAL LOGIC
   */
  const editCourseModal = document.getElementById('editCourseModal');
  const editCourseTypeSelect = document.getElementById('editCourseType');
  const editRequiredLabWrapper = document.getElementById('editRequiredLabWrapper');
  const editRequiredLabSelect = document.getElementById('editRequiredLab');
  const editSections = document.getElementById('editSections');
  const editPreferredTeachers = document.getElementById('editPreferredTeachers');

  if (editCourseModal) {
    // Initialize Toggles
    enableToggle(editSections);
    enableToggle(editPreferredTeachers);

    // Lab Toggle for Edit
    editCourseTypeSelect.addEventListener('change', () => {
      const isLab = (editCourseTypeSelect.value === 'LAB' || editCourseTypeSelect.value === 'LAB_ORIENTED_THEORY');
      editRequiredLabWrapper.style.display = isLab ? 'block' : 'none';
      editRequiredLabSelect.required = isLab;
      if (!isLab) editRequiredLabSelect.value = "";
    });

    // Populate Modal Data
    editCourseModal.addEventListener('show.bs.modal', event => {
      const button = event.relatedTarget;
      const courseId = button.getAttribute('data-courseid');
      const courseType = button.getAttribute('data-coursetype');
      const requiredLab = button.getAttribute('data-requiredlab');
      const sectionsData = button.getAttribute('data-sections') || "";
      const preferredData = button.getAttribute('data-preferred') || "";

      document.getElementById('editCourseTitleId').innerText = courseId;
      document.getElementById('editCourseId').value = courseId;
      editCourseTypeSelect.value = courseType;

      // Handle Lab Visibility
      const isLab = (courseType === 'LAB' || courseType === 'LAB_ORIENTED_THEORY');
      editRequiredLabWrapper.style.display = isLab ? 'block' : 'none';
      editRequiredLabSelect.value = isLab ? requiredLab : "";

      // Prefill Multi-selects
      const setSelections = (selectEl, dataString) => {
        const values = dataString.split(',');
        Array.from(selectEl.options).forEach(opt => {
          opt.selected = values.includes(opt.value);
        });
      };

      setSelections(editSections, sectionsData);
      setSelections(editPreferredTeachers, preferredData);
    });
  }

  // Clear All Sections Button
  const clearSectionsBtn = document.getElementById('clearSectionsBtn');
  if (clearSectionsBtn && editSections) {
    clearSectionsBtn.addEventListener('click', () => {
      Array.from(editSections.options).forEach(opt => opt.selected = false);
    });
  }

  /**
   * 5. COURSE SEARCH & FILTER
   */
  const searchInput = document.getElementById('searchInput');
  const typeFilter = document.getElementById('typeFilter');
  const resultCounter = document.getElementById('resultCounter');
  const noResultsDiv = document.getElementById('noResults');
  const tableBody = document.querySelector('.course-table tbody');
  const tableWrapper = document.querySelector('.table-responsive.course-table');

  function filterCourses() {
    const query = searchInput.value.toLowerCase().trim();
    const typeVal = typeFilter.value;
    let visibleCount = 0;
    const totalRows = tableBody ? tableBody.querySelectorAll('tr').length : 0;

    if (tableBody) {
      tableBody.querySelectorAll('tr').forEach(row => {
        const courseType = (row.getAttribute('data-type') || '').toLowerCase();
        const visibleText = (row.textContent || '').toLowerCase();

        const matchesSearch = !query || visibleText.includes(query);
        const matchesType = typeVal === 'all' || courseType === typeVal.toLowerCase();

        if (matchesSearch && matchesType) {
          row.style.display = '';
          visibleCount++;
        } else {
          row.style.display = 'none';
        }
      });
    }

    resultCounter.textContent = 'Showing ' + visibleCount + ' of ' + totalRows + ' courses';
    noResultsDiv.style.display = (visibleCount === 0 && totalRows > 0) ? 'block' : 'none';
    if (tableWrapper) {
      tableWrapper.style.display = (visibleCount === 0 && totalRows > 0) ? 'none' : 'block';
    }
  }

  if (searchInput) searchInput.addEventListener('input', filterCourses);
  if (typeFilter) typeFilter.addEventListener('change', filterCourses);

  // Initialize counter on page load
  if (resultCounter && tableBody) {
    const total = tableBody.querySelectorAll('tr').length;
    resultCounter.textContent = 'Showing ' + total + ' of ' + total + ' courses';
  }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
