<%
  String currentPath = request.getRequestURI();
  String contextPath = request.getContextPath();
  String servletName = currentPath.substring(contextPath.length() + 1);
%>
<nav class="navbar">
    <div class="logo">A.U.R.G.</div>
    <ul class="nav-links">
      <li><a href="courses.jsp"<%= currentPath.contains("courses.jsp") ? " class=\"active\"" : "" %>>Courses</a></li>
      <li><a href="sections.jsp"<%= currentPath.contains("sections.jsp") ? " class=\"active\"" : "" %>>Sections</a></li>
      <li><a href="teachers.jsp"<%= currentPath.contains("teachers.jsp") ? " class=\"active\"" : "" %>>Teachers</a></li>
      <li><a href="rooms.jsp"<%= currentPath.contains("rooms.jsp") ? " class=\"active\"" : "" %>>Rooms</a></li>
      <li><a href="effeciency_choose.jsp"<%= currentPath.contains("effeciency_choose.jsp") ? " class=\"active\"" : "" %>>Generate Routine</a></li>
      <li><a href="logout.jsp"<%= currentPath.contains("logout.jsp") ? " class=\"active\"" : "" %>>Logout</a></li>
    </ul>
    <div class="menu-toggle">&#9776;</div>
  </nav>
