package local_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import entity.Section;
import helper.ConnectionProvider;
import jakarta.servlet.http.HttpSession;

public class SectionData {

	public static Map<String, Section> getSections(HttpSession sc) {
		Map<String, Section> sections = new LinkedHashMap<>();
		String deptType = sc.getAttribute("dept_type") != null ? sc.getAttribute("dept_type").toString() : "";
		String sql = "select * from sections where dept_type=? order by section_id";

		try (Connection con = ConnectionProvider.getCon();
				PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, deptType);
			try (ResultSet res = pst.executeQuery()) {
				while (res.next()) {
					Section s = new Section(res.getString("section_id"), res.getInt("students"));
					sections.put(s.id, s);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return sections;
	}

}
