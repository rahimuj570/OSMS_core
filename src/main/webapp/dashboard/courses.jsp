<%@page import="local_db.SectionData"%>
<%@page import="java.util.Map"%>
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
					<th colspan="2"></th>
				</tr>
			</thead>
			<tbody>

				<%
				Map<Integer, Teacher> teachers = TeacherData.getTeachers(session);
				for (Course c : CourseData.getCourses(session)) {
				%>
				<tr>
					<td><%=c.id%></td>
					<td><%=c.type.name()%></td>
					<td><%=c.requiredLab%></td>
					<td>
						<%
						String names = "";
						for (int i = 0; i < c.preferredTeachers.size(); i++) {
							Integer tid = (Integer) c.preferredTeachers.toArray()[i];
							names += teachers.get(tid).name;
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
							Integer tid = (Integer) c.forbiddenTeachers.toArray()[i];
							names2 += teachers.get(tid).name;
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

					<td><a
						href="<%=request.getContextPath()%>/DeleteCourseServlet?course_id=<%=c.id%>"><button
								style="background: red">Delete</button></a></td>


				</tr>
				<%
				}
				%>
				<!-- More rows dynamically generated -->
			</tbody>
		</table>
			<%@ include file="footer.jsp" %>
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
								for (Teacher t : TeacherData.getTeachers(session).values()) {
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
								for (Teacher t : TeacherData.getTeachers(session).values()) {
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
				<div class="modal-header">
					<h5 class="modal-title" id="addTeacherModalLabel">Edit Course (<small id="editCourseTitleId"></small>)</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
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
									for (entity.Section s : SectionData.getSections(session).values()) {
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
								for (entity.Teacher t : TeacherData.getTeachers(session).values()) {
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
								for (entity.Teacher t : TeacherData.getTeachers(session).values()) {
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
   * 2. SHARED HELPERS: Click-to-Toggle & Sync Logic
   */
  
  // Custom multi-select toggle (click without holding Ctrl/Cmd)
  // FIXED: Added check to prevent clicking disabled options
  function enableToggle(selectElement) {
    if (!selectElement) return;
    Array.from(selectElement.options).forEach(opt => {
      opt.addEventListener('mousedown', e => {
        e.preventDefault();
        
        // --- THE FIX: If the option is disabled by the opposite list, ignore click ---
        if (opt.disabled) return; 

        opt.selected = !opt.selected;
        selectElement.dispatchEvent(new Event('change'));
      });
    });
  }

  // Mutual exclusion logic: if selected in list A, disable in list B
  function syncLists(preferredSelect, forbiddenSelect) {
    if (!preferredSelect || !forbiddenSelect) return;
    
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

  /**
   * 3. ADD COURSE MODAL LOGIC
   */
  const courseTypeSelect = document.getElementById('courseType');
  const requiredLabWrapper = document.getElementById('requiredLabWrapper');
  const requiredLabSelect = document.getElementById('requiredLab');
  const preferredSelect = document.getElementById('preferredTeachers');
  const forbiddenSelect = document.getElementById('forbiddenTeachers');

  // Lab Toggle
  if (courseTypeSelect) {
    courseTypeSelect.addEventListener('change', () => {
      const isLab = (courseTypeSelect.value === 'LAB' || courseTypeSelect.value === 'LAB_ORIENTED_THEORY');
      requiredLabWrapper.style.display = isLab ? 'block' : 'none';
      requiredLabSelect.required = isLab;
      if (!isLab) requiredLabSelect.value = "";
    });
  }

  // Teacher Selection Sync
  if (preferredSelect && forbiddenSelect) {
    enableToggle(preferredSelect);
    enableToggle(forbiddenSelect);

    preferredSelect.addEventListener('change', () => syncLists(preferredSelect, forbiddenSelect));
    forbiddenSelect.addEventListener('change', () => syncLists(preferredSelect, forbiddenSelect));
    
    syncLists(preferredSelect, forbiddenSelect); // Initial run
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
  const editForbiddenTeachers = document.getElementById('editForbiddenTeachers');

  if (editCourseModal) {
    // Initialize Toggles
    enableToggle(editSections);
    enableToggle(editPreferredTeachers);
    enableToggle(editForbiddenTeachers);

    // Sync Listeners
    editPreferredTeachers.addEventListener('change', () => syncLists(editPreferredTeachers, editForbiddenTeachers));
    editForbiddenTeachers.addEventListener('change', () => syncLists(editPreferredTeachers, editForbiddenTeachers));

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
      const forbiddenData = button.getAttribute('data-forbidden') || "";

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
      setSelections(editForbiddenTeachers, forbiddenData);

      // Trigger sync immediately after pre-filling to lock disabled options
      syncLists(editPreferredTeachers, editForbiddenTeachers);
    });
  }

  // Clear All Sections Button
  const clearSectionsBtn = document.getElementById('clearSectionsBtn');
  if (clearSectionsBtn && editSections) {
    clearSectionsBtn.addEventListener('click', () => {
      Array.from(editSections.options).forEach(opt => opt.selected = false);
    });
  }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>


</body>
</html>
