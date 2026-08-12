package local_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import entity.Section;
import helper.ConnectionProvider;
import jakarta.servlet.http.HttpSession;

public class SectionData {

//	static Section s18A = new Section("18A", 40);
//	static Section s18B = new Section("18B", 35);
//	static Section s17A = new Section("17A", 35);
//	static Section s17B = new Section("17B", 35);
//	static Section s16A = new Section("16A", 35);
//	static Section s16B = new Section("16B", 35);
//	static Section s15A = new Section("15A", 35);
//	static Section s15B = new Section("15B", 35);
//	static Section s15C = new Section("15C", 35);
//	static Section s14A = new Section("14A", 35);
//	static Section s14B = new Section("14B", 35);
//	static Section s13A = new Section("13A", 35);
//	static Section s13B = new Section("13B", 35);
//	static Section s11_12A = new Section("11&12A", 35);
//	static Section s11_12B = new Section("11&12B", 35);
//	static Section s11_12C = new Section("11&12C", 35);
//
//	public static Map<String, Section> sections = Map.ofEntries(Map.entry(s18A.id, s18A), Map.entry(s18B.id, s18B),
//			Map.entry(s17A.id, s17A), Map.entry(s17B.id, s17B), Map.entry(s16A.id, s16A), Map.entry(s16B.id, s16B),
//			Map.entry(s15A.id, s15A), Map.entry(s15B.id, s15B), Map.entry(s15C.id, s15C), Map.entry(s14A.id, s14A),
//			Map.entry(s14B.id, s14B), Map.entry(s13A.id, s13A), Map.entry(s13B.id, s13B),
//			Map.entry(s11_12A.id, s11_12A), Map.entry(s11_12B.id, s11_12B), Map.entry(s11_12C.id, s11_12C));
	
	public static Map<String, Section> sections = new LinkedHashMap<String, Section>();
	
	public static Map<String, Section> getSections(HttpSession sc){
		sections.clear();
		
		Connection con = ConnectionProvider.getCon();
		PreparedStatement pst = null;
		ResultSet res = null;
		try {
			pst = con.prepareStatement("select * from sections " + "where dept_type='"
					+ sc.getAttribute("dept_type") +"' order by section_id");
			res = pst.executeQuery();
			
			while(res.next()) {
				Section s =new Section(res.getString("section_id"),res.getInt("students"));
				sections.put(s.id, s);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try { if (res != null) res.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (pst != null) pst.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
		
		
		return  sections;
	}

}
