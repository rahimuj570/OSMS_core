<%@page import="local_db.RoomData"%>
<%@page import="entity.Room"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Rooms Availability</title>
<!-- Bootstrap CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">

<link rel="stylesheet" href="dashboard.css">

<style type="text/css">
/* Reset */
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	font-family: Arial, sans-serif;
	background-color: #fff;
	color: #333;
}

/* Navbar */
.navbar {
	background-color: #5c0931;
	color: white;
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 15px 20px;
}

.logo {
	font-size: 1.5rem;
	font-weight: bold;
}

.nav-links {
	list-style: none;
	display: flex;
}

.nav-links li {
	margin-left: 20px;
}

.nav-links a {
	color: white;
	text-decoration: none;
	font-weight: bold;
}

.nav-links a:hover {
	text-decoration: underline;
}

.menu-toggle {
	display: none;
	font-size: 1.8rem;
	cursor: pointer;
}

/* Main Content */
main {
	padding: 20px;
}

h1 {
	color: #5c0931;
	margin-bottom: 20px;
}

.add-btn {
	background-color: #5c0931;
	color: white;
	border: none;
	padding: 10px 15px;
	border-radius: 4px;
	cursor: pointer;
	margin-bottom: 20px;
}

.add-btn:hover {
	background-color: #7a1145;
}

/* Table */
.data-table {
	width: 100%;
	border-collapse: collapse;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.data-table th, .data-table td {
	border: 1px solid #ddd;
	padding: 12px;
	text-align: center;
	vertical-align: top;
}

.data-table th {
	background-color: #5c0931;
	color: white;
}

.data-table tr:nth-child(even) {
	background-color: #f9f9f9;
}

.data-table tr:hover {
	background-color: #f1f1f1;
}

/* Slots */
.slot-column {
	min-width: 150px;
}

.slots {
	display: flex;
	flex-direction: column;
	align-items: flex-start;
}

.slots label {
	font-size: 12px;
	margin: 2px 0;
	display: flex;
	align-items: center;
}

.slots input[type="checkbox"] {
	pointer-events: none; /* read-only */
	margin-right: 5px;
	transform: scale(1.1);
}

/* Responsive Navbar */
@media ( max-width : 768px) {
	.nav-links {
		display: none;
		flex-direction: column;
		background-color: #5c0931;
		position: absolute;
		top: 60px;
		right: 0;
		width: 200px;
	}
	.nav-links li {
		margin: 15px 0;
		text-align: center;
	}
	.menu-toggle {
		display: block;
	}
	.nav-links.active {
		display: flex;
	}
}
</style>
</head>
<body>
	<!-- Navigation Bar -->

	<%@include file="nav_bar.jsp"%>

	<!-- Main Content -->
	<main>
		<h1>Rooms Weekly Availability</h1>
		<button class="add-btn" data-bs-toggle="modal"
			data-bs-target="#addRoomModal">+ Add New Room</button>

		<%
		String addTrue = session.getAttribute("room_true") == null ? null : session.getAttribute("room_true").toString();
		String addFalse = session.getAttribute("room_false") == null ? null : session.getAttribute("room_false").toString();
		if (addTrue != null) {
		%>
		<p class="notify_true"><%=addTrue%></p>
		<%
		session.removeAttribute("room_true");
		} else if (addFalse != null) {
		%>
		<p class="notify_false"><%=addFalse%></p>
		<%
		session.removeAttribute("room_false");
		}
		%>


		<table class="data-table">
			<thead>
				<tr>
					<th>Room</th>
					<th>Type</th>
					<th>Lab Type</th>
					<th>Capacity</th>
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
				<!-- Example Room -->
				<%
				for (Room r : RoomData.getRooms().values()) {
				%>
				<tr>
					<td><%=r.id%></td>
					<td><%=r.type.name()%></td>
					<td><%=r.labType == null ? "N/A" : r.labType.name()%></td>
					<td><%=r.capacity%></td>
					<%
					for (int i = 0; i < r.availability.length; i++) {
					%>
					<td class="slot-column">
						<!-- 14 slots -->
						<div class="slots">
							<label><input type="checkbox"
								<%=(r.availability[i] & 1 << 13) == 1 << 13 ? "checked" : ""%>>
								9:00 - 9:30</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 12) == 1 << 12 ? "checked" : ""%>>9:30
								- 10:00</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 11) == 1 << 11 ? "checked" : ""%>>10:00
								- 10:30</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 10) == 1 << 10 ? "checked" : ""%>>10:30
								- 11:00</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 9) == 1 << 9 ? "checked" : ""%>>11:00
								- 11:00</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 8) == 1 << 8 ? "checked" : ""%>>11:30
								- 12:00</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 7) == 1 << 7 ? "checked" : ""%>>12:00
								- 12:30</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 6) == 1 << 6 ? "checked" : ""%>>12:30
								- 1:00</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 5) == 1 << 5 ? "checked" : ""%>>1:00
								- 1:30</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 4) == 1 << 4 ? "checked" : ""%>>1:30
								- 2:00</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 3) == 1 << 3 ? "checked" : ""%>>2:00
								- 2:30</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 2) == 1 << 2 ? "checked" : ""%>>2:30
								- 3:00</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 1) == 1 << 1 ? "checked" : ""%>>3:00
								- 3:30</label> <label><input type="checkbox"
								onclick='return false'
								<%=(r.availability[i] & 1 << 0) == 1 << 0 ? "checked" : ""%>>3:30
								- 4:00</label>
						</div>
					</td>
					<%
					}
					%>

					<%
// Build a unique string: "dayIndex_timeLabel"
StringBuilder sb = new StringBuilder();
String[] slotLabels = {"9:00-9:30", "9:30-10:00", "10:00-10:30", "10:30-11:00", "11:00-11:30", "11:30-12:00",
        "12:00-12:30", "12:30-1:00", "1:00-1:30", "1:30-2:00", "2:00-2:30", "2:30-3:00", "3:00-3:30", "3:30-4:00"};

for (int day = 0; day < r.availability.length; day++) {
    long mask = r.availability[day];
    for (int bit = 0; bit < slotLabels.length; bit++) {
        // Use (13 - bit) to match the 1 << 13, 1 << 12... logic used in your table cells
        if ((mask & (1 << (13 - bit))) != 0) {
            if (sb.length() > 0) sb.append(",");
            sb.append(day).append("_").append(slotLabels[bit]);
        }
    }
}
String slotsString = sb.toString();
%>
					<td>
						<button class="btn btn-sm btn-warning" data-bs-toggle="modal"
							data-bs-target="#editRoomModal" data-roomid="<%=r.id%>"
							data-capacity="<%=r.capacity%>"
							data-roomtype="<%=r.type.name()%>"
							data-labtype="<%=r.labType == null ? "" : r.labType.name()%>"
							data-slots="<%=slotsString%>">Edit</button>
					</td>
					<td><a href="<%=request.getContextPath()%>/DeleteRoomServlet?roomId=<%=r.id%>"><button type="submit" class="btn btn-danger btn-sm">Delete</button></a></td>

				</tr>
				<%
				}
				%>

			</tbody>
		</table>
	</main>


	<!-- Add Room Modal -->
	<div class="modal fade" id="addRoomModal" tabindex="-1"
		aria-labelledby="addRoomLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="addTeacherModalLabel">Add New Room</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form action="<%=request.getContextPath()%>/AddRoomServlet"
						method="post">
						<div class="mb-3">
							<label for="roomId" class="form-label">Room ID</label> <input
								type="text" class="form-control" id="roomId" name="roomId"
								required>
						</div>
						<div class="mb-3">
							<label for="roomCapacity" class="form-label">Student
								Capacity</label> <input type="number" class="form-control"
								id="roomCapacity" name="roomCapacity" required>
						</div>
						<div class="mb-3">
							<label for="roomType" class="form-label">Type</label> <select
								class="form-select" id="roomType" name="roomType" required>
								<option value="THEORY">THEORY</option>
								<option value="LAB">LAB</option>
							</select>
						</div>

						<div class="mb-3" id="labTypeWrapper" style="display: none;">
							<label for="labType" class="form-label">Lab Type</label> <select
								class="form-select" id="labType" name="labType">
								<option value="COMPUTER">COMPUTER</option>
								<option value="ELECTRONIC">ELECTRONIC</option>
								<option value="GENERAL">GENERAL</option>
							</select>
						</div>

						<h5 class="mt-4">Weekly Availability</h5>

						<!-- Saturday -->
						<div class="mb-3 slot">
							<label class="fw-bold">Saturday</label><br> <label><input
								type="checkbox" name="slots_0" value="9:00-9:30"> 9:00 -
								9:30</label> <label><input type="checkbox" name="slots_0"
								value="9:30-10:00"> 9:30 - 10:00</label> <label><input
								type="checkbox" name="slots_0" value="10:00-10:30">
								10:00 - 10:30</label> <label><input type="checkbox"
								name="slots_0" value="10:30-11:00"> 10:30 - 11:00</label> <label><input
								type="checkbox" name="slots_0" value="11:00-11:30">
								11:00 - 11:30</label> <label><input type="checkbox"
								name="slots_0" value="11:30-12:00"> 11:30 - 12:00</label> <label><input
								type="checkbox" name="slots_0" value="12:00-12:30">
								12:00 - 12:30</label> <label><input type="checkbox"
								name="slots_0" value="12:30-1:00"> 12:30 - 1:00</label> <label><input
								type="checkbox" name="slots_0" value="1:00-1:30"> 1:00 -
								1:30</label> <label><input type="checkbox" name="slots_0"
								value="1:30-2:00"> 1:30 - 2:00</label> <label><input
								type="checkbox" name="slots_0" value="2:00-2:30"> 2:00 -
								2:30</label> <label><input type="checkbox" name="slots_0"
								value="2:30-3:00"> 2:30 - 3:00</label> <label><input
								type="checkbox" name="slots_0" value="3:00-3:30"> 3:00 -
								3:30</label> <label><input type="checkbox" name="slots_0"
								value="3:30-4:00"> 3:30 - 4:00</label>

						</div>

						<!-- Sunday -->
						<div class="mb-3 slot">
							<label class="fw-bold">Sunday</label><br> <label><input
								type="checkbox" name="slots_1" value="9:00-9:30"> 9:00 -
								9:30</label> <label><input type="checkbox" name="slots_1"
								value="9:30-10:00"> 9:30 - 10:00</label> <label><input
								type="checkbox" name="slots_1" value="10:00-10:30">
								10:00 - 10:30</label> <label><input type="checkbox"
								name="slots_1" value="10:30-11:00"> 10:30 - 11:00</label> <label><input
								type="checkbox" name="slots_1" value="11:00-11:30">
								11:00 - 11:30</label> <label><input type="checkbox"
								name="slots_1" value="11:30-12:00"> 11:30 - 12:00</label> <label><input
								type="checkbox" name="slots_1" value="12:00-12:30">
								12:00 - 12:30</label> <label><input type="checkbox"
								name="slots_1" value="12:30-1:00"> 12:30 - 1:00</label> <label><input
								type="checkbox" name="slots_1" value="1:00-1:30"> 1:00 -
								1:30</label> <label><input type="checkbox" name="slots_1"
								value="1:30-2:00"> 1:30 - 2:00</label> <label><input
								type="checkbox" name="slots_1" value="2:00-2:30"> 2:00 -
								2:30</label> <label><input type="checkbox" name="slots_1"
								value="2:30-3:00"> 2:30 - 3:00</label> <label><input
								type="checkbox" name="slots_1" value="3:00-3:30"> 3:00 -
								3:30</label> <label><input type="checkbox" name="slots_1"
								value="3:30-4:00"> 3:30 - 4:00</label>

						</div>

						<!-- Monday -->
						<div class="mb-3 slot">
							<label class="fw-bold">Monday</label><br> <label><input
								type="checkbox" name="slots_2" value="9:00-9:30"> 9:00 -
								9:30</label> <label><input type="checkbox" name="slots_2"
								value="9:30-10:00"> 9:30 - 10:00</label> <label><input
								type="checkbox" name="slots_2" value="10:00-10:30">
								10:00 - 10:30</label> <label><input type="checkbox"
								name="slots_2" value="10:30-11:00"> 10:30 - 11:00</label> <label><input
								type="checkbox" name="slots_2" value="11:00-11:30">
								11:00 - 11:30</label> <label><input type="checkbox"
								name="slots_2" value="11:30-12:00"> 11:30 - 12:00</label> <label><input
								type="checkbox" name="slots_2" value="12:00-12:30">
								12:00 - 12:30</label> <label><input type="checkbox"
								name="slots_2" value="12:30-1:00"> 12:30 - 1:00</label> <label><input
								type="checkbox" name="slots_2" value="1:00-1:30"> 1:00 -
								1:30</label> <label><input type="checkbox" name="slots_2"
								value="1:30-2:00"> 1:30 - 2:00</label> <label><input
								type="checkbox" name="slots_2" value="2:00-2:30"> 2:00 -
								2:30</label> <label><input type="checkbox" name="slots_2"
								value="2:30-3:00"> 2:30 - 3:00</label> <label><input
								type="checkbox" name="slots_2" value="3:00-3:30"> 3:00 -
								3:30</label> <label><input type="checkbox" name="slots_2"
								value="3:30-4:00"> 3:30 - 4:00</label>
						</div>

						<!-- Tuesday -->
						<div class="mb-3 slot">
							<label class="fw-bold">Tuesday</label><br> <label><input
								type="checkbox" name="slots_3" value="9:00-9:30"> 9:00 -
								9:30</label> <label><input type="checkbox" name="slots_3"
								value="9:30-10:00"> 9:30 - 10:00</label> <label><input
								type="checkbox" name="slots_3" value="10:00-10:30">
								10:00 - 10:30</label> <label><input type="checkbox"
								name="slots_3" value="10:30-11:00"> 10:30 - 11:00</label> <label><input
								type="checkbox" name="slots_3" value="11:00-11:30">
								11:00 - 11:30</label> <label><input type="checkbox"
								name="slots_3" value="11:30-12:00"> 11:30 - 12:00</label> <label><input
								type="checkbox" name="slots_3" value="12:00-12:30">
								12:00 - 12:30</label> <label><input type="checkbox"
								name="slots_3" value="12:30-1:00"> 12:30 - 1:00</label> <label><input
								type="checkbox" name="slots_3" value="1:00-1:30"> 1:00 -
								1:30</label> <label><input type="checkbox" name="slots_3"
								value="1:30-2:00"> 1:30 - 2:00</label> <label><input
								type="checkbox" name="slots_3" value="2:00-2:30"> 2:00 -
								2:30</label> <label><input type="checkbox" name="slots_3"
								value="2:30-3:00"> 2:30 - 3:00</label> <label><input
								type="checkbox" name="slots_3" value="3:00-3:30"> 3:00 -
								3:30</label> <label><input type="checkbox" name="slots_3"
								value="3:30-4:00"> 3:30 - 4:00</label>
						</div>

						<!-- Wednesday -->
						<div class="mb-3 slot">
							<label class="fw-bold">Wednesday</label><br> <label><input
								type="checkbox" name="slots_4" value="9:00-9:30"> 9:00 -
								9:30</label> <label><input type="checkbox" name="slots_4"
								value="9:30-10:00"> 9:30 - 10:00</label> <label><input
								type="checkbox" name="slots_4" value="10:00-10:30">
								10:00 - 10:30</label> <label><input type="checkbox"
								name="slots_4" value="10:30-11:00"> 10:30 - 11:00</label> <label><input
								type="checkbox" name="slots_4" value="11:00-11:30">
								11:00 - 11:30</label> <label><input type="checkbox"
								name="slots_4" value="11:30-12:00"> 11:30 - 12:00</label> <label><input
								type="checkbox" name="slots_4" value="12:00-12:30">
								12:00 - 12:30</label> <label><input type="checkbox"
								name="slots_4" value="12:30-1:00"> 12:30 - 1:00</label> <label><input
								type="checkbox" name="slots_4" value="1:00-1:30"> 1:00 -
								1:30</label> <label><input type="checkbox" name="slots_4"
								value="1:30-2:00"> 1:30 - 2:00</label> <label><input
								type="checkbox" name="slots_4" value="2:00-2:30"> 2:00 -
								2:30</label> <label><input type="checkbox" name="slots_4"
								value="2:30-3:00"> 2:30 - 3:00</label> <label><input
								type="checkbox" name="slots_4" value="3:00-3:30"> 3:00 -
								3:30</label> <label><input type="checkbox" name="slots_4"
								value="3:30-4:00"> 3:30 - 4:00</label>
						</div>

						<!-- Thursday -->
						<div class="mb-3 slot">
							<label class="fw-bold">Thursday</label><br> <label><input
								type="checkbox" name="slots_5" value="9:00-9:30"> 9:00 -
								9:30</label> <label><input type="checkbox" name="slots_5"
								value="9:30-10:00"> 9:30 - 10:00</label> <label><input
								type="checkbox" name="slots_5" value="10:00-10:30">
								10:00 - 10:30</label> <label><input type="checkbox"
								name="slots_5" value="10:30-11:00"> 10:30 - 11:00</label> <label><input
								type="checkbox" name="slots_5" value="11:00-11:30">
								11:00 - 11:30</label> <label><input type="checkbox"
								name="slots_5" value="11:30-12:00"> 11:30 - 12:00</label> <label><input
								type="checkbox" name="slots_5" value="12:00-12:30">
								12:00 - 12:30</label> <label><input type="checkbox"
								name="slots_5" value="12:30-1:00"> 12:30 - 1:00</label> <label><input
								type="checkbox" name="slots_5" value="1:00-1:30"> 1:00 -
								1:30</label> <label><input type="checkbox" name="slots_5"
								value="1:30-2:00"> 1:30 - 2:00</label> <label><input
								type="checkbox" name="slots_5" value="2:00-2:30"> 2:00 -
								2:30</label> <label><input type="checkbox" name="slots_5"
								value="2:30-3:00"> 2:30 - 3:00</label> <label><input
								type="checkbox" name="slots_5" value="3:00-3:30"> 3:00 -
								3:30</label> <label><input type="checkbox" name="slots_5"
								value="3:30-4:00"> 3:30 - 4:00</label>
						</div>


						<!-- Friday -->
						<div class="mb-3 slot">
							<label class="fw-bold">Friday</label><br> <label><input
								type="checkbox" name="slots_6" value="9:00-9:30"> 9:00 -
								9:30</label> <label><input type="checkbox" name="slots_6"
								value="9:30-10:00"> 9:30 - 10:00</label> <label><input
								type="checkbox" name="slots_6" value="10:00-10:30">
								10:00 - 10:30</label> <label><input type="checkbox"
								name="slots_6" value="10:30-11:00"> 10:30 - 11:00</label> <label><input
								type="checkbox" name="slots_6" value="11:00-11:30">
								11:00 - 11:30</label> <label><input type="checkbox"
								name="slots_6" value="11:30-12:00"> 11:30 - 12:00</label> <label><input
								type="checkbox" name="slots_6" value="12:00-12:30">
								12:00 - 12:30</label> <label><input type="checkbox"
								name="slots_6" value="12:30-1:00"> 12:30 - 1:00</label> <label><input
								type="checkbox" name="slots_6" value="1:00-1:30"> 1:00 -
								1:30</label> <label><input type="checkbox" name="slots_6"
								value="1:30-2:00"> 1:30 - 2:00</label> <label><input
								type="checkbox" name="slots_6" value="2:00-2:30"> 2:00 -
								2:30</label> <label><input type="checkbox" name="slots_6"
								value="2:30-3:00"> 2:30 - 3:00</label> <label><input
								type="checkbox" name="slots_6" value="3:00-3:30"> 3:00 -
								3:30</label> <label><input type="checkbox" name="slots_6"
								value="3:30-4:00"> 3:30 - 4:00</label>
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
	</div>



<!-- Edit Room Modal -->
<div class="modal fade" id="editRoomModal" tabindex="-1" aria-labelledby="editRoomLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header bg-maroon text-white">
        <h5 class="modal-title" id="editRoomLabel">Edit Room</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <form action="<%=request.getContextPath() %>/EditRoomServlet" method="post">
          <input type="hidden" id="editRoomId" name="roomId">

          <div class="mb-3">
            <label for="editRoomCapacity" class="form-label">Student Capacity</label>
            <input type="number" class="form-control" id="editRoomCapacity" name="roomCapacity" required>
          </div>

          <div class="mb-3">
            <label for="editRoomType" class="form-label">Type</label>
            <select class="form-select" id="editRoomType" name="roomType" required>
              <option value="THEORY">THEORY</option>
              <option value="LAB">LAB</option>
            </select>
          </div>

          <div class="mb-3" id="editLabTypeWrapper" style="display:none;">
            <label for="editLabType" class="form-label">Lab Type</label>
            <select class="form-select" id="editLabType" name="labType">
              <option value="COMPUTER">COMPUTER</option>
              <option value="ELECTRONIC">ELECTRONIC</option>
              <option value="GENERAL">GENERAL</option>
            </select>
          </div>

          <!-- Weekly Availability -->
          <h6 class="mt-3">Weekly Availability</h6>
          <% String[] days = {"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"}; %>
          <% for(int d=0; d<7; d++){ %>
            <div class="mb-3 slot">
              <label class="fw-bold"><%=days[d]%></label><br>
             <label><input
								type="checkbox" name="slots_<%=d%>" value="9:00-9:30"> 9:00 -
								9:30</label> <label><input type="checkbox" name="slots_<%=d%>"
								value="9:30-10:00"> 9:30 - 10:00</label> <label><input
								type="checkbox" name="slots_<%=d%>" value="10:00-10:30">
								10:00 - 10:30</label> <label><input type="checkbox"
								name="slots_<%=d%>" value="10:30-11:00"> 10:30 - 11:00</label> <label><input
								type="checkbox" name="slots_<%=d%>" value="11:00-11:30">
								11:00 - 11:30</label> <label><input type="checkbox"
								name="slots_<%=d%>" value="11:30-12:00"> 11:30 - 12:00</label> <label><input
								type="checkbox" name="slots_<%=d%>" value="12:00-12:30">
								12:00 - 12:30</label> <label><input type="checkbox"
								name="slots_<%=d%>" value="12:30-1:00"> 12:30 - 1:00</label> <label><input
								type="checkbox" name="slots_<%=d%>" value="1:00-1:30"> 1:00 -
								1:30</label> <label><input type="checkbox" name="slots_<%=d%>"
								value="1:30-2:00"> 1:30 - 2:00</label> <label><input
								type="checkbox" name="slots_<%=d%>" value="2:00-2:30"> 2:00 -
								2:30</label> <label><input type="checkbox" name="slots_<%=d%>"
								value="2:30-3:00"> 2:30 - 3:00</label> <label><input
								type="checkbox" name="slots_<%=d%>" value="3:00-3:30"> 3:00 -
								3:30</label> <label><input type="checkbox" name="slots_<%=d%>"
								value="3:30-4:00"> 3:30 - 4:00</label>
            </div>
          <% } %>

          <div class="modal-footer">
            <button type="submit" class="btn btn-success">Update Room</button>
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
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
	<script>
  const roomTypeSelect = document.getElementById('roomType');
  const editRoomTypeSelect = document.getElementById('editRoomType');
  const labTypeWrapper = document.getElementById('labTypeWrapper');
  const editLabTypeWrapper = document.getElementById('editLabTypeWrapper');
  const labTypeSelect = document.getElementById('labType');
  const editLabTypeSelect = document.getElementById('editLabType');

  roomTypeSelect.addEventListener('change', () => {
    if (roomTypeSelect.value === 'LAB') {
      labTypeWrapper.style.display = 'block';
      labTypeSelect.required = true; // enforce required only for LAB
    } else {
      labTypeWrapper.style.display = 'none';
      labTypeSelect.value = "";      // clear value when hidden
      labTypeSelect.required = false;
    }
  });
  
  
  editRoomTypeSelect.addEventListener('change', () => {
	    if (editRoomTypeSelect.value === 'LAB') {
	      editLabTypeWrapper.style.display = 'block';
	      editLabTypeSelect.required = true; // enforce required only for LAB
	    } else {
	      editLabTypeWrapper.style.display = 'none';
	      editLabTypeSelect.value = "";      // clear value when hidden
	      editLabTypeSelect.required = false;
	    }
	  });
</script>

<script>
  const editRoomModal = document.getElementById('editRoomModal');
  editRoomModal.addEventListener('show.bs.modal', event => {
	    const button = event.relatedTarget;
	    
	    const roomId = button.getAttribute('data-roomid');
	    const capacity = button.getAttribute('data-capacity');
	    const roomType = button.getAttribute('data-roomtype');
	    const labType = button.getAttribute('data-labtype');

	    document.getElementById('editRoomId').value = roomId;
	    document.getElementById('editRoomCapacity').value = capacity;
	    document.getElementById('editRoomType').value = roomType;

	    if(roomType === 'LAB'){
	      document.getElementById('editLabTypeWrapper').style.display = 'block';
	      document.getElementById('editLabType').value = labType;
	    } else {
	      document.getElementById('editLabTypeWrapper').style.display = 'none';
	      document.getElementById('editLabType').value = "";
	    }
	    
	    
	    
	    // ... (rest of your existing data retrieval)
	    const slotsData = button.getAttribute('data-slots'); 

	    // Reset all checkboxes first (important for switching between different rooms)
	    editRoomModal.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);

	    if (slotsData) {
	        const slotsArr = slotsData.split(',');
	        editRoomModal.querySelectorAll('input[type="checkbox"]').forEach(cb => {
	            // cb.name is "slots_0", "slots_1", etc. 
	            // We extract the "0", "1" part.
	            const dayIndex = cb.name.split('_')[1];
	            const uniqueKey = dayIndex + "_" + cb.value;
	            
	            if (slotsArr.includes(uniqueKey)) {
	                cb.checked = true;
	            }
	        });
	    }
	});
</script>


	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
