<%@page import="local_db.SectionData"%>
<%@page import="entity.Section"%>
<%@page import="entity.Teacher"%>
<%@page import="local_db.TeacherData"%>
<%@page import="helper.ConnectionProvider"%>
<%@page import="local_db.CourseData"%>
<%@page import="entity.Course"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Courses</title>
<!-- Bootstrap CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet" type="text/css" href="./dashboard.css">


</head>
<body>
	<!-- Navigation Bar -->
	<%@include file="nav_bar.jsp"%>

	<!-- Main Content -->
	<main>
		<h1>All Courses</h1>
		<button class="add-btn btn btn-primary" data-bs-toggle="modal"
			data-bs-target="#addCourseModal">+ Add New Course</button>

		<%
		String addTrue = session.getAttribute("course_true") == null ? null : session.getAttribute("course_true").toString();
		String addFalse = session.getAttribute("course_false") == null ? null : session.getAttribute("course_false").toString();
		if (addTrue != null) {
		%>
		<p class="notify_true"><%=addTrue%></p>
		<%
		session.removeAttribute("course_true");
		} else if (addFalse != null) {
		%>
		<p class="notify_false"><%=addFalse%></p>
		<%
		session.removeAttribute("course_false");
		}
		%>
		<table class="data-table">
			<thead>
				<tr>
					<th>Course</th>
					<th>Type</th>
					<th>RequiredLab</th>
					<th>Preferred Teacher</th>
					<th>Blacklisted Teacher</th>
					<th>Sections</th>
					<th></th>
				</tr>
			</thead>
			<tbody>

				<%
				TeacherData.getTeachers();
				for (Course c : CourseData.getCourses()) {
				%>
				<tr>
					<td><%=c.id%></td>
					<td><%=c.type.name()%></td>
					<td><%=c.requiredLab%></td>
					<td>
						<%
						String names = "";
						for (int i = 0; i < c.preferredTeachers.size(); i++) {
							names += TeacherData.teachers.get(c.preferredTeachers.toArray()[i]).name;
							if (i + 1 != c.preferredTeachers.size()) {
								names += ", ";
							}
						}
						out.print(names);
						%>
					</td>
					<td>
						<%
						String names2 = "";
						for (int i = 0; i < c.forbiddenTeachers.size(); i++) {
							names2 += TeacherData.teachers.get(c.forbiddenTeachers.toArray()[i]).name;
							if (i + 1 != c.forbiddenTeachers.size()) {
								names2 += ", ";
							}
						}
						out.print(names2);
						%>

					</td>
					<td>
						<%
						String sections = "";
						for (int i = 0; i < c.sectionIds.size(); i++) {
							sections += c.sectionIds.toArray()[i];
							if (i + 1 != c.sectionIds.size()) {
								sections += ", ";
							}
						}
						out.print(sections);
						%>

					</td>
					<td>
						<button class="btn btn-sm btn-warning" data-bs-toggle="modal"
							data-bs-target="#editCourseModal" data-courseid="<%=c.id%>"
							data-coursetype="<%=c.getTypeString()%>"
							data-requiredlab="<%=c.getRequiredLabString()%>"
							data-sections="<%=c.getSectionsCsv()%>"
							data-preferred="<%=c.getPreferredTeachersCsv()%>"
							data-forbidden="<%=c.getForbiddenTeachersCsv()%>">Edit</button>
					</td>

				</tr>
				<%
				}
				%>
				<!-- More rows dynamically generated -->
			</tbody>
		</table>
	</main>

	<!-- Add Course Modal -->
	<!-- Add Course Modal -->
	<div class="modal fade" id="addCourseModal" tabindex="-1"
		aria-labelledby="addCourseLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="addTeacherModalLabel">Add New
						Course</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form action="<%=request.getContextPath()%>/AddCourseServlet"
						method="post">
						<!-- Course ID -->
						<div class="mb-3">
							<label for="courseId" class="form-label">Course ID</label> <input
								type="text" class="form-control" id="courseId" name="courseId"
								required>
						</div>

						<!-- Course Type -->
						<div class="mb-3">
							<label for="courseType" class="form-label">Course Type</label> <select
								class="form-select" id="courseType" name="courseType" required>
								<option value="THEORY">THEORY</option>
								<option value="LAB">LAB</option>
								<option value="LAB_ORIENTED_THEORY">LAB ORIENTED THEORY</option>
							</select>
						</div>

						<!-- Required Lab -->
						<div class="mb-3" id="requiredLabWrapper" style="display: none;">
							<label for="requiredLab" class="form-label">Required Lab</label>
							<select class="form-select" id="requiredLab" name="requiredLab">
								<option value="GENERAL">GENERAL</option>
								<option value="COMPUTER">COMPUTER</option>
								<option value="ELECTRONIC">ELECTRONIC</option>
							</select>
						</div>

						<!-- Preferred Teachers -->
						<div class="mb-3">
							<label class="form-label">Preferred Teachers</label> <select
								class="form-select" name="preferredTeachers"
								id="preferredTeachers" multiple>
								<%
								for (Teacher t : TeacherData.getTeachers().values()) {
								%>
								<option value="<%=t.id%>"><%=t.name%></option>
								<%
								}
								%>
							</select> <small class="text-muted">Hold Ctrl (Windows) or Command
								(Mac) to select multiple</small>
						</div>

						<!-- Forbidden Teachers -->
						<div class="mb-3">
							<label class="form-label">Forbidden Teachers</label> <select
								class="form-select" name="forbiddenTeachers"
								id="forbiddenTeachers" multiple>
								<%
								for (Teacher t : TeacherData.getTeachers().values()) {
								%>
								<option value="<%=t.id%>"><%=t.name%></option>
								<%
								}
								%>
							</select> <small class="text-muted">Hold Ctrl (Windows) or Command
								(Mac) to select multiple</small>
						</div>



						<div class="modal-footer">
							<button type="submit" class="btn btn-success">Save
								Course</button>
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
				<div class="modal-header bg-maroon text-white">
					<h5 class="modal-title" id="editCourseLabel">Edit Course</h5>
					<button type="button" class="btn-close btn-close-white"
						data-bs-dismiss="modal"></button>
				</div>
				<div class="modal-body">
					<form action="<%=request.getContextPath()%>/EditCourseServlet"
						method="post">
						<input type="hidden" id="editCourseId" name="courseId">

						<!-- Course Type -->
						<div class="mb-3">
							<label for="editCourseType" class="form-label">Course
								Type</label> <select class="form-select" id="editCourseType"
								name="courseType" required>
								<option value="THEORY">THEORY</option>
								<option value="LAB">LAB</option>
								<option value="LAB_ORIENTED_THEORY">LAB ORIENTED THEORY</option>
							</select>
						</div>

						<!-- Required Lab -->
						<div class="mb-3" id="editRequiredLabWrapper"
							style="display: none;">
							<label for="editRequiredLab" class="form-label">Required
								Lab</label> <select class="form-select" id="editRequiredLab"
								name="requiredLab">
								<option value="GENERAL">GENERAL</option>
								<option value="COMPUTER">COMPUTER</option>
								<option value="ELECTRONIC">ELECTRONIC</option>
							</select>
						</div>

						<!-- Sections -->
						<div class="mb-3">
							<label class="form-label">Linked Sections</label>
							<div class="d-flex align-items-center">
								<select class="form-select me-2" name="sections"
									id="editSections" multiple>
									<%
									for (entity.Section s : SectionData.getSections().values()) {
									%>
									<option value="<%=s.id%>"><%=s.id%> (Students:
										<%=s.students%>)
									</option>
									<%
									}
									%>
								</select>
								<button type="button" class="btn btn-outline-danger btn-sm"
									id="clearSectionsBtn">Clear All</button>
							</div>
							<small class="text-muted">Click once to select, click
								again to unselect. Use Clear All to reset.</small>
						</div>

						<!-- Preferred Teachers -->
						<div class="mb-3">
							<label class="form-label">Preferred Teachers</label> <select
								class="form-select" name="preferredTeachers"
								id="editPreferredTeachers" multiple>
								<%
								for (entity.Teacher t : TeacherData.getTeachers().values()) {
								%>
								<option value="<%=t.id%>"><%=t.name%></option>
								<%
								}
								%>
							</select> <small class="text-muted">Hold Ctrl (Windows) or Command
								(Mac) to select multiple</small>
						</div>

						<!-- Forbidden Teachers -->
						<div class="mb-3">
							<label class="form-label">Forbidden Teachers</label> <select
								class="form-select" name="forbiddenTeachers"
								id="editForbiddenTeachers" multiple>
								<%
								for (entity.Teacher t : TeacherData.getTeachers().values()) {
								%>
								<option value="<%=t.id%>"><%=t.name%></option>
								<%
								}
								%>
							</select> <small class="text-muted">Hold Ctrl (Windows) or Command
								(Mac) to select multiple</small>
						</div>

						<div class="modal-footer">
							<button type="submit" class="btn btn-success">Update
								Course</button>
							<button type="button" class="btn btn-secondary"
								data-bs-dismiss="modal">Cancel</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>




	<script>
    const menuToggle = document.querySelector('.menu-toggle');
    const navLinks = document.querySelector('.nav-links');

    menuToggle.addEventListener('click', () => {
      navLinks.classList.toggle('active');
    });
  </script>
	<!-- Bootstrap JS -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>



	<script>
	  const courseTypeSelect = document.getElementById('courseType');
	  const requiredLabWrapper = document.getElementById('requiredLabWrapper');
	  const requiredLabSelect = document.getElementById('requiredLab');

	  courseTypeSelect.addEventListener('change', () => {
	    if (courseTypeSelect.value === 'LAB' || courseTypeSelect.value === 'LAB_ORIENTED_THEORY') {
	      requiredLabWrapper.style.display = 'block';
	      requiredLabSelect.required = true;
	    } else {
	      requiredLabWrapper.style.display = 'none';
	      requiredLabSelect.value = "";
	      requiredLabSelect.required = false;
	    }
	  });
	</script>

	<script type="text/javascript">
	const editCourseTypeSelect = document.getElementById('editCourseType');
	  const editRequiredLabWrapper = document.getElementById('editRequiredLabWrapper');
	  const editRequiredLabSelect = document.getElementById('editRequiredLab');

	  editCourseTypeSelect.addEventListener('change', () => {
	    if (editCourseTypeSelect.value === 'LAB' || editCourseTypeSelect.value === 'LAB_ORIENTED_THEORY') {
	      editRequiredLabWrapper.style.display = 'block';
	      editRequiredLabSelect.required = true;
	    } else {
	      editRequiredLabWrapper.style.display = 'none';
	      editRequiredLabSelect.value = "";
	      editRequiredLabSelect.required = false;
	    }
	  });
	</script>

	<script>
  const preferredSelect = document.getElementById('preferredTeachers');
  const forbiddenSelect = document.getElementById('forbiddenTeachers');

  // Helper: toggle selection on click
  function enableToggle(selectElement) {
    Array.from(selectElement.options).forEach(opt => {
      opt.addEventListener('mousedown', e => {
        e.preventDefault(); // prevent default selection behavior
        opt.selected = !opt.selected; // toggle manually
        selectElement.dispatchEvent(new Event('change')); // trigger sync
      });
    });
  }

  function syncLists() {
    const preferredValues = Array.from(preferredSelect.selectedOptions).map(opt => opt.value);
    const forbiddenValues = Array.from(forbiddenSelect.selectedOptions).map(opt => opt.value);

    // Disable in forbidden if selected in preferred
    Array.from(forbiddenSelect.options).forEach(opt => {
      opt.disabled = preferredValues.includes(opt.value);
    });

    // Disable in preferred if selected in forbidden
    Array.from(preferredSelect.options).forEach(opt => {
      opt.disabled = forbiddenValues.includes(opt.value);
    });
  }

  preferredSelect.addEventListener('change', syncLists);
  forbiddenSelect.addEventListener('change', syncLists);

  // Enable toggle behavior
  enableToggle(preferredSelect);
  enableToggle(forbiddenSelect);

  // Initial sync
  syncLists();
</script>

	<script>
  const editCourseModal = document.getElementById('editCourseModal');
  editCourseModal.addEventListener('show.bs.modal', event => {
    const button = event.relatedTarget;
    const courseId = button.getAttribute('data-courseid');
    const courseType = button.getAttribute('data-coursetype');
    const requiredLab = button.getAttribute('data-requiredlab');
    const sectionsData = button.getAttribute('data-sections'); // comma-separated
    const preferredData = button.getAttribute('data-preferred'); // comma-separated
    const forbiddenData = button.getAttribute('data-forbidden'); // comma-separated

    document.getElementById('editCourseId').value = courseId;
    document.getElementById('editCourseType').value = courseType;

    if(courseType === 'LAB' || courseType === 'LAB_ORIENTED_THEORY'){
      document.getElementById('editRequiredLabWrapper').style.display = 'block';
      document.getElementById('editRequiredLab').value = requiredLab;
    } else {
      document.getElementById('editRequiredLabWrapper').style.display = 'none';
      document.getElementById('editRequiredLab').value = "";
    }

    // Prefill sections
    if(sectionsData){
      const sectionsArr = sectionsData.split(',');
      document.querySelectorAll('#editSections option').forEach(opt=>{
        opt.selected = sectionsArr.includes(opt.value);
      });
    }else{
    	const sectionsArr = sectionsData.split(',');
        document.querySelectorAll('#editSections option').forEach(opt=>{
          opt.selected = false;
        });
    }

    // Prefill preferred teachers
    if(preferredData){
      const prefArr = preferredData.split(',');
      document.querySelectorAll('#editPreferredTeachers option').forEach(opt=>{
        opt.selected = prefArr.includes(opt.value);
      });
    }else{
    	const prefArr = preferredData.split(',');
        document.querySelectorAll('#editPreferredTeachers option').forEach(opt=>{
          opt.selected = false;
        });
    }

    // Prefill forbidden teachers
    if(forbiddenData){
      const forbArr = forbiddenData.split(',');
      document.querySelectorAll('#editForbiddenTeachers option').forEach(opt=>{
        opt.selected = forbArr.includes(opt.value);
      });
    }else{
    	const forbArr = forbiddenData.split(',');
        document.querySelectorAll('#editForbiddenTeachers option').forEach(opt=>{
          opt.selected = forbArr.includes(opt.value);
        });
    }
  });
</script>

	<script>
  // Toggle selection on click
  function enableToggle(selectElement) {
    Array.from(selectElement.options).forEach(opt => {
      opt.addEventListener('mousedown', e => {
        e.preventDefault();
        opt.selected = !opt.selected;
        selectElement.dispatchEvent(new Event('change'));
      });
    });
  }

  const editSections = document.getElementById('editSections');
  const editPreferredTeachers = document.getElementById('editPreferredTeachers');
  const editForbiddenTeachers = document.getElementById('editForbiddenTeachers');

  enableToggle(editSections);
  enableToggle(editPreferredTeachers);
  enableToggle(editForbiddenTeachers);

  // Mutual exclusion for preferred vs forbidden teachers
  function syncLists() {
    const preferredValues = Array.from(editPreferredTeachers.selectedOptions).map(opt => opt.value);
    const forbiddenValues = Array.from(editForbiddenTeachers.selectedOptions).map(opt => opt.value);

    Array.from(editForbiddenTeachers.options).forEach(opt => {
      opt.disabled = preferredValues.includes(opt.value);
    });
    Array.from(editPreferredTeachers.options).forEach(opt => {
      opt.disabled = forbiddenValues.includes(opt.value);
    });
  }

  editPreferredTeachers.addEventListener('change', syncLists);
  editForbiddenTeachers.addEventListener('change', syncLists);
  syncLists();

  // Clear All Sections button
  document.getElementById('clearSectionsBtn').addEventListener('click', () => {
    Array.from(editSections.options).forEach(opt => opt.selected = false);
  });
</script>


</body>
</html>
