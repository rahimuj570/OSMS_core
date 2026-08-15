<%@page import="java.util.Map"%>
<%@page import="entity.Room"%>
<%@page import="local_db.RoomData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%!
	String[] dayNames = {"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"};
	String[] slotLabels = {"9:00-9:30","9:30-10:00","10:00-10:30","10:30-11:00","11:00-11:30","11:30-12:00",
		"12:00-12:30","12:30-1:00","1:00-1:30","1:30-2:00","2:00-2:30","2:30-3:00","3:00-3:30","3:30-4:00"};
%>
<%
	// Pre-compute summary counts server-side
	java.util.List<Room> roomList = new java.util.ArrayList<>(RoomData.getRooms(session).values());
	int totalCount = 0, labCount = 0, theoryCount = 0;
	int totalCapacity = 0;
	for (Room r : roomList) {
		totalCount++;
		if (r.type == entity.RoomType.LAB) labCount++;
		else theoryCount++;
		totalCapacity +=  r.capacity ;
	}
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Rooms — Admin</title>
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
.room-table {
	background: #fff;
	border-radius: 8px;
	overflow: hidden;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}
.room-table thead th {
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
.room-table tbody td {
	vertical-align: middle;
	padding: 0.65rem 0.65rem;
	font-size: 0.9rem;
}
.room-table tbody tr:hover {
	background-color: #f8f9fa;
}
.room-id {
	font-weight: 600;
	color: #212529;
	font-size: 0.92rem;
}

/* ── Badges ── */
.badge-theory { background-color: #e7f3ff; color: #0d6efd; }
.badge-lab { background-color: #fff3cd; color: #856404; }
.badge-labtype { background-color: #e8f5e9; color: #1b5e20; font-size: 0.75rem; padding: 0.25em 0.55em; }
.badge-capacity { background-color: #e9ecef; color: #495057; font-size: 0.8rem; padding: 0.3em 0.6em; }

/* ── Weekly Availability Cell ── */
.avail-cell {
	min-width: 220px;
	max-width: 280px;
}
.avail-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 2px 12px;
	font-size: 0.78rem;
	margin-bottom: 6px;
}
.avail-day {
	display: flex;
	align-items: baseline;
	gap: 5px;
	padding: 1px 0;
}
.avail-day-indicator {
	width: 7px;
	height: 7px;
	border-radius: 50%;
	flex-shrink: 0;
}
.avail-day-indicator.active { background-color: #5c0931; }
.avail-day-indicator.inactive { background-color: #dee2e6; }
.avail-day-name {
	font-weight: 600;
	color: #495057;
	min-width: 26px;
	flex-shrink: 0;
}
.avail-day-ranges {
	color: #6c757d;
	line-height: 1.3;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.avail-day-none {
	color: #adb5bd;
	font-style: italic;
}
.avail-more {
	font-size: 0.75rem;
	color: #868e96;
	font-style: italic;
}
.avail-view-btn {
	font-size: 0.72rem;
	padding: 0.2rem 0.5rem;
	margin-top: 4px;
}

/* ── View Modal Day Cards ── */
.avail-summary-bar {
	display: flex;
	gap: 1.5rem;
	font-size: 0.82rem;
	color: #495057;
	margin-bottom: 1rem;
	padding: 0.5rem 0.75rem;
	background: #f8f9fa;
	border-radius: 6px;
}
.avail-summary-item .avail-summary-value {
	font-weight: 700;
	color: #5c0931;
}
.avail-summary-item .avail-summary-label {
	font-size: 0.72rem;
	text-transform: uppercase;
	letter-spacing: 0.04em;
	color: #868e96;
}
.avail-day-card {
	border: 1px solid #dee2e6;
	border-radius: 8px;
	padding: 0.75rem;
	background: #fff;
	margin-bottom: 0.5rem;
}
.avail-day-card-header {
	font-weight: 600;
	font-size: 0.85rem;
	color: #212529;
	margin-bottom: 0.35rem;
	display: flex;
	align-items: center;
	gap: 6px;
}
.avail-day-card-header .indicator {
	width: 8px;
	height: 8px;
	border-radius: 50%;
}
.avail-day-card-header .indicator.active { background-color: #5c0931; }
.avail-day-card-header .indicator.inactive { background-color: #dee2e6; }
.avail-day-range-summary {
	font-size: 0.78rem;
	color: #6c757d;
	margin-bottom: 0.4rem;
}
.avail-slot-chip {
	display: inline-block;
	font-size: 0.72rem;
	padding: 0.2rem 0.5rem;
	border: 1px solid #5c0931;
	border-radius: 4px;
	color: #5c0931;
	font-weight: 500;
	background: #fff;
	margin: 0 3px 3px 0;
}
.avail-no-availability {
	font-size: 0.78rem;
	color: #adb5bd;
	font-style: italic;
	padding: 0.25rem 0;
}

/* ── Filter Toolbar ── */
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

/* ── Availability Slots (Modal) ── */
.day-slots-grid {
	display: flex;
	flex-wrap: wrap;
	gap: 0.4rem;
	margin-top: 0.25rem;
}
.day-helper {
	font-size: 0.75rem;
	margin-bottom: 0.25rem;
}
.day-helper .btn {
	padding: 0.1rem 0.4rem;
	font-size: 0.72rem;
	line-height: 1;
}

.slot-card input[type="checkbox"] {
	position: absolute;
	opacity: 0;
	width: 0;
	height: 0;
}
.slot-card label {
	display: inline-flex;
	align-items: center;
	gap: 3px;
	padding: 0.3rem 0.6rem;
	border: 1px solid #dee2e6;
	border-radius: 6px;
	font-size: 0.78rem;
	font-weight: 500;
	color: #6c757d;
	cursor: pointer;
	transition: background-color 0.15s, border-color 0.15s, color 0.15s;
	user-select: none;
	background: #fff;
}
.slot-card input[type="checkbox"]:checked + label {
	background-color: #5c0931;
	border-color: #5c0931;
	color: #fff;
}
.slot-card label:hover {
	border-color: #adb5bd;
	background-color: #f8f9fa;
}
.slot-card input[type="checkbox"]:checked + label:hover {
	background-color: #7a1145;
	border-color: #7a1145;
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
				<h1>Rooms &amp; Availability</h1>
				<p class="subtitle">Manage classrooms, laboratories, capacity, and weekly availability.</p>
			</div>
			<button class="add-btn" data-bs-toggle="modal"
				data-bs-target="#addRoomModal">+ Add New Room</button>
		</div>

		<!-- Notifications -->
		<%
		String addTrue = session.getAttribute("room_true") == null ? null : session.getAttribute("room_true").toString();
		String addFalse = session.getAttribute("room_false") == null ? null : session.getAttribute("room_false").toString();
		if (addTrue != null) {
		%>
		<div class="alert alert-success alert-dismissible fade show" role="alert">
			<%=addTrue%>
			<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
		</div>
		<%
		session.removeAttribute("room_true");
		} else if (addFalse != null) {
		%>
		<div class="alert alert-danger alert-dismissible fade show" role="alert">
			<%=addFalse%>
			<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
		</div>
		<%
		session.removeAttribute("room_false");
		}
		%>

		<!-- Stat Cards -->
		<div class="stat-cards">
			<div class="stat-card">
				<div class="stat-value"><%=totalCount%></div>
				<div class="stat-label">Total Rooms</div>
			</div>
			<div class="stat-card">
				<div class="stat-value"><%=theoryCount%></div>
				<div class="stat-label">Classrooms</div>
			</div>
			<div class="stat-card">
				<div class="stat-value"><%=labCount%></div>
				<div class="stat-label">Labs</div>
			</div>
		</div>

		<!-- Room Table -->
		<%
		if (roomList.isEmpty()) {
		%>
		<div class="empty-state">
			<h3>No rooms available</h3>
			<p>Start by adding your first classroom or laboratory.</p>
			<button class="btn btn-primary" data-bs-toggle="modal"
				data-bs-target="#addRoomModal">+ Add New Room</button>
		</div>
		<%
		} else {
		%>
		<!-- Search Filter Toolbar -->
		<div class="filter-toolbar">
			<input type="text" class="form-control search-input" id="searchInput" placeholder="Search rooms by ID, type, or capacity...">
		</div>
		<div class="result-counter" id="resultCounter"></div>
		<div class="no-results" id="noResults">
			<strong>No rooms found</strong>
			<span>Try changing your search.</span>
		</div>
		<div class="table-responsive room-table">
			<table class="table table-hover mb-0">
				<thead>
					<tr>
						<th>Room</th>
						<th>Type</th>
						<th>Lab Type</th>
						<th>Capacity</th>
						<th>Weekly Availability</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<%
					for (Room r : roomList) {
					// Build JSON for view modal
					StringBuilder jsonSb = new StringBuilder("[");
					for (int day = 0; day < r.availability.length; day++) {
						if (day > 0) jsonSb.append(",");
						jsonSb.append("{\"day\":\"").append(dayNames[day]).append("\",\"slots\":[");
						boolean firstSlot = true;
						for (int bit = 0; bit < slotLabels.length; bit++) {
							if ((r.availability[day] & (1 << (13 - bit))) != 0) {
								if (!firstSlot) jsonSb.append(",");
								jsonSb.append("\"").append(slotLabels[bit]).append("\"");
								firstSlot = false;
							}
						}
						jsonSb.append("]}");
					}
					jsonSb.append("]");
					String roomJson = jsonSb.toString();
					%>
					<tr>
						<td><span class="room-id"><%=r.id%></span></td>
						<td>
							<%
							String typeBadge = (r.type == entity.RoomType.THEORY) ? "badge-theory" : "badge-lab";
							%>
							<span class="badge <%=typeBadge%>"><%=r.type.name()%></span>
						</td>
						<td>
							<%
							if (r.labType == null) {
								out.print("<span class=\"text-muted\">N/A</span>");
							} else {
								out.print("<span class=\"badge badge-labtype\">" + r.labType.name() + "</span>");
							}
							%>
						</td>
						<td><span class="badge badge-capacity"><%=r.capacity%> students</span></td>
						<td>
							<%
							// ── Build availability display data for this room ──
							// Compute consecutive ranges with AM/PM labels for each day
							int totalDays = 7, activeDays = 0, totalSlots = 0;
							for (int day = 0; day < r.availability.length; day++) {
								boolean any = false;
								for (int bit = 0; bit < slotLabels.length; bit++) {
									if ((r.availability[day] & (1 << (13 - bit))) != 0) { any = true; break; }
								}
								if (any) activeDays++;
							}
							// Count total selected slots
							for (int day = 0; day < r.availability.length; day++) {
								for (int bit = 0; bit < slotLabels.length; bit++) {
									if ((r.availability[day] & (1 << (13 - bit))) != 0) totalSlots++;
								}
							}
							// AM/PM labels for each slot index
							String[] dayNamesShort = {"Sat","Sun","Mon","Tue","Wed","Thu","Fri"};
							String[] ampmSlotLabels = {"9:00 AM – 9:30 AM","9:30 AM – 10:00 AM","10:00 AM – 10:30 AM",
								"10:30 AM – 11:00 AM","11:00 AM – 11:30 AM","11:30 AM – 12:00 PM",
								"12:00 PM – 12:30 PM","12:30 PM – 1:00 PM","1:00 PM – 1:30 PM",
								"1:30 PM – 2:00 PM","2:00 PM – 2:30 PM","2:30 PM – 3:00 PM",
								"3:00 PM – 3:30 PM","3:30 PM – 4:00 PM"};

							// Build range strings per day
							for (int day = 0; day < r.availability.length; day++) {
								int startBit = -1, prevBit = -1;
								java.util.List<String> ranges = new java.util.ArrayList<>();
								for (int bit = 0; bit < slotLabels.length; bit++) {
									if ((r.availability[day] & (1 << (13 - bit))) != 0) {
										if (startBit == -1) { startBit = bit; prevBit = bit; }
										else if (bit == prevBit + 1) { prevBit = bit; }
										else {
											ranges.add((startBit == prevBit) ? ampmSlotLabels[startBit] : ampmSlotLabels[startBit].substring(0, ampmSlotLabels[startBit].indexOf(" – ")) + " – " + ampmSlotLabels[prevBit].split(" – ")[1]);
											startBit = bit; prevBit = bit;
										}
									}
								}
								if (startBit >= 0) {
									ranges.add((startBit == prevBit) ? ampmSlotLabels[startBit] : ampmSlotLabels[startBit].substring(0, ampmSlotLabels[startBit].indexOf(" – ")) + " – " + ampmSlotLabels[prevBit].split(" – ")[1]);
								}
							}
							// Build JSON bitmask array for modal JS
							String availJson = "[";
							for (int day = 0; day < r.availability.length; day++) {
								if (day > 0) availJson += ",";
								availJson += r.availability[day];
							}
							availJson += "]";
							%>
							<div class="avail-grid">
								<%
								for (int day = 0; day < r.availability.length; day++) {
									boolean any = false;
									for (int bit = 0; bit < slotLabels.length; bit++) {
										if ((r.availability[day] & (1 << (13 - bit))) != 0) { any = true; break; }
									}
									String dayAbbr = dayNamesShort[day];
								%>
								<div class="avail-day">
									<span class="avail-day-indicator <%= any ? "active" : "inactive" %>"></span>
									<span class="avail-day-name"><%=dayAbbr%></span>
									<span class="<%= any ? "avail-day-ranges" : "avail-day-none" %>">
										<% if (!any) out.print("—"); %>
									</span>
								</div>
								<%
								}
								%>
							</div>
							<button class="btn btn-outline-secondary btn-sm avail-view-btn" data-bs-toggle="modal"
								data-bs-target="#viewAvailabilityModal"
								data-room-name="Room <%=r.id%>"
								data-room-id="<%=r.id%>"
								data-room-availbits='<%=availJson%>'
								data-active-days="<%=activeDays%>"
								data-total-slots="<%=totalSlots%>">
								View schedule
							</button>
						</td>
						<td>
							<%
							// Build slots string for edit button data attribute
							StringBuilder sb = new StringBuilder();
							for (int day = 0; day < r.availability.length; day++) {
							    long mask = r.availability[day];
							    for (int bit = 0; bit < slotLabels.length; bit++) {
							        if ((mask & (1 << (13 - bit))) != 0) {
							            if (sb.length() > 0) sb.append(",");
							            sb.append(day).append("_").append(slotLabels[bit]);
							        }
							    }
							}
							String slotsString = sb.toString();
							%>
							<button class="btn btn-sm btn-outline-warning action-btn" data-bs-toggle="modal"
								data-bs-target="#editRoomModal" data-roomid="<%=r.id%>"
								data-capacity="<%=r.capacity%>"
								data-roomtype="<%=r.type.name()%>"
								data-labtype="<%=r.labType == null ? "" : r.labType.name()%>"
								data-slots="<%=slotsString%>">Edit</button>
							<button class="btn btn-sm btn-outline-danger action-btn"
								data-room-id="<%=r.id%>"
								data-delete-url="<%=request.getContextPath()%>/DeleteRoomServlet?roomId=<%=r.id%>"
								onclick="if(confirm('Are you sure you want to delete room <%=r.id%>?')) window.location.href = this.dataset.deleteUrl">Delete</button>
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

	<!-- Add Room Modal -->
	<div class="modal fade" id="addRoomModal" tabindex="-1"
		aria-labelledby="addRoomLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="addRoomLabel">Add New Room</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form action="<%=request.getContextPath()%>/AddRoomServlet"
						method="post">

						<div class="modal-section-title">Room Information</div>
						<div class="row">
							<div class="col-md-6 mb-3">
								<label for="roomId" class="form-label">Room ID <span class="text-danger">*</span></label>
								<input type="text" class="form-control" id="roomId" name="roomId"
									required>
							</div>
							<div class="col-md-6 mb-3">
								<label for="roomCapacity" class="form-label">Student Capacity <span class="text-danger">*</span></label>
								<input type="number" class="form-control" id="roomCapacity"
									name="roomCapacity" min="1" required>
							</div>
						</div>
						<div class="row">
							<div class="col-md-6 mb-3">
								<label for="roomType" class="form-label">Type <span class="text-danger">*</span></label>
								<select class="form-select" id="roomType" name="roomType" required>
									<option value="">— Select type —</option>
									<option value="THEORY">THEORY</option>
									<option value="LAB">LAB</option>
								</select>
							</div>
							<div class="col-md-6 mb-3" id="labTypeWrapper" style="display: none;">
								<label for="labType" class="form-label">Lab Type</label>
								<select class="form-select" id="labType" name="labType">
									<option value="">— Select lab type —</option>
									<option value="COMPUTER">COMPUTER</option>
									<option value="ELECTRONIC">ELECTRONIC</option>
									<option value="GENERAL">GENERAL</option>
								</select>
							</div>
						</div>

						<div class="modal-section-title">Weekly Availability</div>
						<%
						String[] days = {"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"};
						for (int d = 0; d < days.length; d++) {
						%>
						<div class="mb-3">
							<div class="d-flex justify-content-between align-items-center mb-1">
								<label class="fw-bold mb-0" style="font-size: 0.85rem;"><%=days[d]%></label>
								<span class="day-helper">
									<button type="button" class="btn btn-outline-primary btn-sm"
										data-day-target="slots_<%=d%>" data-action="select-all">Select All</button>
									<button type="button" class="btn btn-outline-secondary btn-sm"
										data-day-target="slots_<%=d%>" data-action="clear-all">Clear All</button>
								</span>
							</div>
							<div class="day-slots-grid" id="slots-grid-<%=d%>">
								<%
								for (int s = 0; s < slotLabels.length; s++) {
								%>
								<div class="slot-card">
									<input type="checkbox" name="slots_<%=d%>" value="<%=slotLabels[s]%>" id="add_<%=d%>_<%=s%>">
									<label for="add_<%=d%>_<%=s%>"><%=slotLabels[s]%></label>
								</div>
								<%
								}
								%>
							</div>
						</div>
						<%
						}
						%>
				<div class="modal-footer">
					<button type="submit" class="btn btn-success">Save Room</button>
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Cancel</button>
				</div>
					</form>
				</div>
			</div>
		</div>
	</div>

	<!-- Edit Room Modal -->
	<div class="modal fade" id="editRoomModal" tabindex="-1"
		aria-labelledby="editRoomLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="editRoomLabel">Edit Room — <small id="editRoomTitleId"></small></h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form action="<%=request.getContextPath()%>/EditRoomServlet"
						method="post">
						<input type="hidden" id="editRoomId" name="roomId">

						<div class="modal-section-title">Room Information</div>
						<div class="row">
							<div class="col-md-6 mb-3">
								<label for="editRoomCapacity" class="form-label">Student Capacity <span class="text-danger">*</span></label>
								<input type="number" class="form-control" id="editRoomCapacity"
									name="roomCapacity" min="1" required>
							</div>
							<div class="col-md-6 mb-3">
								<label for="editRoomType" class="form-label">Type <span class="text-danger">*</span></label>
								<select class="form-select" id="editRoomType" name="roomType" required>
									<option value="">— Select type —</option>
									<option value="THEORY">THEORY</option>
									<option value="LAB">LAB</option>
								</select>
							</div>
						</div>
						<div class="row">
							<div class="col-md-6 mb-3" id="editLabTypeWrapper" style="display: none;">
								<label for="editLabType" class="form-label">Lab Type</label>
								<select class="form-select" id="editLabType" name="labType">
									<option value="">— Select lab type —</option>
									<option value="COMPUTER">COMPUTER</option>
									<option value="ELECTRONIC">ELECTRONIC</option>
									<option value="GENERAL">GENERAL</option>
								</select>
							</div>
						</div>

						<div class="modal-section-title">Weekly Availability</div>
						<%
						for (int d = 0; d < days.length; d++) {
						%>
						<div class="mb-3">
							<div class="d-flex justify-content-between align-items-center mb-1">
								<label class="fw-bold mb-0" style="font-size: 0.85rem;"><%=days[d]%></label>
								<span class="day-helper">
									<button type="button" class="btn btn-outline-primary btn-sm"
										data-day-target="slots_<%=d%>" data-action="select-all">Select All</button>
									<button type="button" class="btn btn-outline-secondary btn-sm"
										data-day-target="slots_<%=d%>" data-action="clear-all">Clear All</button>
								</span>
							</div>
							<div class="day-slots-grid" id="edit-slots-grid-<%=d%>">
								<%
								for (int s = 0; s < slotLabels.length; s++) {
								%>
								<div class="slot-card">
									<input type="checkbox" name="slots_<%=d%>" value="<%=slotLabels[s]%>" id="edit_<%=d%>_<%=s%>">
									<label for="edit_<%=d%>_<%=s%>"><%=slotLabels[s]%></label>
								</div>
								<%
								}
								%>
							</div>
						</div>
						<%
						}
						%>
				<div class="modal-footer">
					<button type="submit" class="btn btn-success">Update Room</button>
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Cancel</button>
				</div>
					</form>
				</div>
			</div>
		</div>
	</div>

	<!-- View Availability Modal -->
	<div class="modal fade" id="viewAvailabilityModal" tabindex="-1"
		aria-labelledby="viewAvailabilityLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="viewAvailabilityLabel">Weekly Availability</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body" id="viewModalBody">
					<!-- Weekly Summary Bar -->
					<div class="avail-summary-bar" id="viewSummaryBar"></div>
					<!-- Room Name -->
					<p class="fw-bold mb-3" id="viewRoomName"></p>
					<!-- Day Cards Container -->
					<div class="row" id="viewDayCards"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Bootstrap JS — must load BEFORE inline scripts -->
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

  <!-- Search Filter Script -->
  <script>
  /**
   * 1. SEARCH / FILTER
   */
  const searchInput = document.getElementById('searchInput');
  const resultCounter = document.getElementById('resultCounter');
  const noResultsDiv = document.getElementById('noResults');
  const tableBody = document.querySelector('.room-table tbody');
  const tableWrapper = document.querySelector('.table-responsive.room-table');

  function filterRooms() {
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

    resultCounter.textContent = 'Showing ' + visibleCount + ' of ' + totalRows + ' rooms';
    noResultsDiv.style.display = (visibleCount === 0 && totalRows > 0) ? 'block' : 'none';
  }

  if (searchInput) {
    searchInput.addEventListener('input', filterRooms);
    searchInput.addEventListener('change', filterRooms);
    filterRooms();
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

  <!-- Lab Type Visibility Script -->
  <script>
  /**
   * 2. LAB TYPE VISIBILITY
   */
  const roomTypeSelect = document.getElementById('roomType');
  const editRoomTypeSelect = document.getElementById('editRoomType');
  const labTypeWrapper = document.getElementById('labTypeWrapper');
  const editLabTypeWrapper = document.getElementById('editLabTypeWrapper');
  const labTypeSelect = document.getElementById('labType');
  const editLabTypeSelect = document.getElementById('editLabType');

  function toggleLab(wrapper, select) {
    if (!wrapper || !select) return;
    const isLab = (roomTypeSelect && roomTypeSelect.value === 'LAB') ||
                  (editRoomTypeSelect && editRoomTypeSelect.value === 'LAB');
  }

  if (roomTypeSelect) {
    roomTypeSelect.addEventListener('change', () => {
      const isLab = roomTypeSelect.value === 'LAB';
      labTypeWrapper.style.display = isLab ? 'block' : 'none';
      labTypeSelect.required = isLab;
      if (!isLab) labTypeSelect.value = "";
    });
  }

  if (editRoomTypeSelect) {
    editRoomTypeSelect.addEventListener('change', () => {
      const isLab = editRoomTypeSelect.value === 'LAB';
      editLabTypeWrapper.style.display = isLab ? 'block' : 'none';
      editLabTypeSelect.required = isLab;
      if (!isLab) editLabTypeSelect.value = "";
    });
  }
  </script>

  <!-- Availability Helpers Script -->
  <script>
  /**
   * 3. SELECT ALL / CLEAR ALL for availability slots
   */
  function setupDayHelpers() {
    document.querySelectorAll('[data-action="select-all"]').forEach(btn => {
      btn.addEventListener('click', function() {
        const targetName = this.getAttribute('data-day-target');
        const modal = this.closest('.modal');
        modal.querySelectorAll('input[name="' + targetName + '"]').forEach(cb => {
          cb.checked = true;
        });
      });
    });

    document.querySelectorAll('[data-action="clear-all"]').forEach(btn => {
      btn.addEventListener('click', function() {
        const targetName = this.getAttribute('data-day-target');
        const modal = this.closest('.modal');
        modal.querySelectorAll('input[name="' + targetName + '"]').forEach(cb => {
          cb.checked = false;
        });
      });
    });
  }

  // Set up helpers for both modals when they open
  const addRoomModal = document.getElementById('addRoomModal');
  const editRoomModal = document.getElementById('editRoomModal');

  if (addRoomModal) {
    addRoomModal.addEventListener('show.bs.modal', setupDayHelpers);
    // Also set up on page load in case modal is already visible
    setupDayHelpers();
  }
  if (editRoomModal) {
    editRoomModal.addEventListener('show.bs.modal', setupDayHelpers);
    setupDayHelpers();
  }
  </script>

  <script>
  // ── Edit Modal Population ──
  if (editRoomModal) {
    editRoomModal.addEventListener('show.bs.modal', function(event) {
      const button = event.relatedTarget;

      const roomId = button.getAttribute('data-roomid');
      const capacity = button.getAttribute('data-capacity');
      const roomType = button.getAttribute('data-roomtype');
      const labType = button.getAttribute('data-labtype');

      document.getElementById('editRoomTitleId').innerText = roomId;
      document.getElementById('editRoomId').value = roomId;
      document.getElementById('editRoomCapacity').value = capacity;
      document.getElementById('editRoomType').value = roomType;

      if (roomType === 'LAB') {
        document.getElementById('editLabTypeWrapper').style.display = 'block';
        document.getElementById('editLabType').value = labType;
      } else {
        document.getElementById('editLabTypeWrapper').style.display = 'none';
        document.getElementById('editLabType').value = "";
      }

      // Reset all availability checkboxes
      editRoomModal.querySelectorAll('input[type="checkbox"]').forEach(cb => {
        // Skip the room type checkboxes, only reset slots
        if (cb.name && cb.name.startsWith('slots_')) {
          cb.checked = false;
        }
      });

      const slotsData = button.getAttribute('data-slots');
      if (slotsData) {
        const slotsArr = slotsData.split(',');
        editRoomModal.querySelectorAll('input[type="checkbox"]').forEach(cb => {
          if (cb.name && cb.name.startsWith('slots_')) {
            const dayIndex = cb.name.split('_')[1];
            const uniqueKey = dayIndex + "_" + cb.value;

            if (slotsArr.includes(uniqueKey)) {
              cb.checked = true;
            }
          }
        });
      }
    });
  }

  // ── View Availability Modal ──
  const viewAvailabilityModal = document.getElementById('viewAvailabilityModal');
  if (viewAvailabilityModal) {
    viewAvailabilityModal.addEventListener('show.bs.modal', function(event) {
      const button = event.relatedTarget;
      const roomName = button.getAttribute('data-room-name') || '';
      document.getElementById('viewAvailabilityLabel').innerText = 'Weekly Availability — ' + roomName;
      document.getElementById('viewRoomName').innerText = roomName;

      // Summary
      const activeDays = parseInt(button.getAttribute('data-active-days') || '0');
      const totalSlots = parseInt(button.getAttribute('data-total-slots') || '0');
      document.getElementById('viewSummaryBar').innerHTML =
        '<div class="avail-summary-item"><span class="avail-summary-value">7</span> <span class="avail-summary-label">Days</span></div>' +
        '<div class="avail-summary-item"><span class="avail-summary-value">' + activeDays + '</span> <span class="avail-summary-label">Active Days</span></div>' +
        '<div class="avail-summary-item"><span class="avail-summary-value">' + totalSlots + '</span> <span class="avail-summary-label">Available Slots</span></div>';

      // Bitmask data
      const availBitsStr = button.getAttribute('data-room-availbits') || '[]';
      const availBits = JSON.parse(availBitsStr);
      const days = ['Saturday','Sunday','Monday','Tuesday','Wednesday','Thursday','Friday'];
      const slotLabels = ["9:00-9:30","9:30-10:00","10:00-10:30","10:30-11:00","11:00-11:30","11:30-12:00",
        "12:00-12:30","12:30-1:00","1:00-1:30","1:30-2:00","2:00-2:30","2:30-3:00","3:00-3:30","3:30-4:00"];
      const ampmLabels = ["9:00 AM","9:30 AM","10:00 AM","10:30 AM","11:00 AM","11:30 AM",
        "12:00 PM","12:30 PM","1:00 PM","1:30 PM","2:00 PM","2:30 PM","3:00 PM","3:30 PM"];
      const ampmSlotDisplay = ["9:00 AM – 9:30 AM","9:30 AM – 10:00 AM","10:00 AM – 10:30 AM",
        "10:30 AM – 11:00 AM","11:00 AM – 11:30 AM","11:30 AM – 12:00 PM",
        "12:00 PM – 12:30 PM","12:30 PM – 1:00 PM","1:00 PM – 1:30 PM",
        "1:30 PM – 2:00 PM","2:00 PM – 2:30 PM","2:30 PM – 3:00 PM",
        "3:00 PM – 3:30 PM","3:30 PM – 4:00 PM"];

      let cardsHtml = '';
      days.forEach((day, dayIdx) => {
        let mask = (availBits[dayIdx] !== undefined) ? availBits[dayIdx] : 0;
        let selectedBits = [];
        for (let bit = 0; bit < slotLabels.length; bit++) {
          if ((mask & (1 << (13 - bit)))) selectedBits.push(bit);
        }

        cardsHtml += '<div class="col-md-6 col-lg-4 col-xl-3">';
        cardsHtml += '<div class="avail-day-card">';

        // Header
        cardsHtml += '<div class="avail-day-card-header">';
        cardsHtml += '<span class="indicator ' + (selectedBits.length > 0 ? 'active' : 'inactive') + '"></span>';
        cardsHtml += day;
        cardsHtml += '</div>';

        if (selectedBits.length === 0) {
          cardsHtml += '<div class="avail-no-availability">No availability</div>';
        } else {
          // Continuous range summary with AM/PM
          let ranges = [];
          let startIdx = selectedBits[0], prevIdx = selectedBits[0];
          for (let i = 1; i <= selectedBits.length; i++) {
            if (i < selectedBits.length && selectedBits[i] === prevIdx + 1) {
              prevIdx = selectedBits[i];
            } else {
              ranges.push({ start: startIdx, end: prevIdx });
              startIdx = selectedBits[i];
              prevIdx = selectedBits[i];
            }
          }
          let rangeSummary = ranges.map(function(r) {
            if (r.start === r.end) return ampmLabels[r.start] + ' – ' + ampmLabels[r.end];
            return ampmLabels[r.start] + ' – ' + ampmLabels[r.end];
          });
          cardsHtml += '<div class="avail-day-range-summary"><strong>Available:</strong><br>' + '</div>';

          // Individual slot chips
          selectedBits.forEach(function(bit) {
            cardsHtml += '<span class="avail-slot-chip">' + ampmSlotDisplay[bit] + '</span>';
          });
        }
        cardsHtml += '</div></div>';
      });
      document.getElementById('viewDayCards').innerHTML = cardsHtml;
    });
  }
</script>

</body>
</html>
