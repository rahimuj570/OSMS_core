package local_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import entity.Teacher;
import helper.ConnectionProvider;
import jakarta.servlet.http.HttpSession;

public class TeacherData {

	public enum TN {
		A_K_M_MONZURUL_ISLAM, // A.K.M. Monzurul Islam
		ABU_SUFIAN_MD_SHAHED, // Abu Sufian Md. Shahed
		ARPITA_ROY, ASIB_MUSTAKIM_FONY, ASHIF_MAHMUD_JOY, DR_ROWSANARA_AKHTER, // Dr. Rowsanara Akhter
		DR_SAIFUL_ISLAM, // Dr. Saiful Islam
		H_M_IKRAM_KAYS, // H M Ikram Kays
		MAHBUB_E_SOBHANI, // Mahbub E Sobhani
		MAHBUBUR_RAHMAN, MAMOON_AL_RASHEED, MD_AHSAN_ARIF, // Md. Ahsan Arif
		MD_NAHID, // Md. Nahid
		MD_NURUL_ISLAM, // Md. Nurul Islam
		MD_RAHAD_ISLAM_BHUIYAN, // Md. Rahad Islam Bhuiyan
		NUR_MUHAMMAD_FAHAD, REDUANUL_BARI_SHUVON, SABIKUN_NAHAR_ZERIN, SADIA_NUR_NAZIFA, SALEHIN_MAHBUB, SAMIA_SABAH,
		SHAILA_SHARMIN, SM_MONIRUZZAMAN, SUMITRA_GHOSH, SYEDA_TASFIA, TUHIN_HOSSAIN;
	}

//	// t1 and t2 were already given by you
//			static Teacher t1 = new Teacher(TN.A_K_M_MONZURUL_ISLAM.name(),
//					new boolean[] { false, true, true, true, false, false, true }); // Fri, Sun, Mon, Tue
//
//			static Teacher t2 = new Teacher(TN.ABU_SUFIAN_MD_SHAHED.name(),
//					new boolean[] { false, true, false, false, false, false, false }); // Sun
//
//			static Teacher t3 = new Teacher(TN.ARPITA_ROY.name(), new boolean[] { false, true, true, false, false, false, true }); // Fri,
//			// Sun,
//			// Mon
//
//			static Teacher t4 = new Teacher(TN.ASIB_MUSTAKIM_FONY.name(),
//					new boolean[] { true, true, true, false, false, false, false }); // Sat, Sun, Mon
//
//			static Teacher t5 = new Teacher(TN.ASHIF_MAHMUD_JOY.name(),
//					new boolean[] { true, true, true, true, false, false, false }); // Sat, Sun, Mon, Tue
//
//			static Teacher t6 = new Teacher(TN.DR_ROWSANARA_AKHTER.name(),
//					new boolean[] { true, false, true, false, false, false, false }); // Sat, Mon
//
//			static Teacher t7 = new Teacher(TN.DR_SAIFUL_ISLAM.name(),
//					new boolean[] { false, true, true, true, false, false, false }); // Sun,
//																						// Mon,
//																						// Tue
//
//			static Teacher t8 = new Teacher(TN.H_M_IKRAM_KAYS.name(),
//					new boolean[] { true, true, true, true, true, false, false }); // Sat–Wed
//
//			static Teacher t9 = new Teacher(TN.MAHBUB_E_SOBHANI.name(),
//					new boolean[] { false, true, true, true, false, false, true }); // Fri, Sun, Mon, Tue
//
//			static Teacher t10 = new Teacher(TN.MAHBUBUR_RAHMAN.name(),
//					new boolean[] { true, true, true, true, false, false, true }); // Fri, Sat, Sun, Mon, Tue
//
//			static Teacher t11 = new Teacher(TN.MAMOON_AL_RASHEED.name(),
//					new boolean[] { true, true, true, true, false, false, false }); // Sat, Sun, Mon, Tue
//
//			static Teacher t12 = new Teacher(TN.MD_AHSAN_ARIF.name(),
//					new boolean[] { false, false, true, true, false, false, false }); // Fri,
//			// Mon,
//			// Tue
//
//			static Teacher t13 = new Teacher(TN.MD_NAHID.name(), new boolean[] { false, false, false, true, false, false, true }); // Fri
//			// +
//			// Tue
//
//			static Teacher t14 = new Teacher(TN.MD_NURUL_ISLAM.name(),
//					new boolean[] { false, false, true, false, true, false, true }); // Fri,
//																						// Mon,
//																						// Wed
//
//			static Teacher t15 = new Teacher(TN.MD_RAHAD_ISLAM_BHUIYAN.name(),
//					new boolean[] { false, true, true, false, false, false, false }); // Sun, Mon
//
//			static Teacher t16 = new Teacher(TN.NUR_MUHAMMAD_FAHAD.name(),
//					new boolean[] { true, true, true, true, true, true, true }); // Fri, Sun–Wed
//
//			static Teacher t17 = new Teacher(TN.REDUANUL_BARI_SHUVON.name(),
//					new boolean[] { true, true, true, true, false, false, false }); // Sat–Tue
//
//			static Teacher t18 = new Teacher(TN.SABIKUN_NAHAR_ZERIN.name(),
//					new boolean[] { true, true, true, false, true, false, false }); // Sat, Sun, Mon, Wed
//
//			static Teacher t19 = new Teacher(TN.SADIA_NUR_NAZIFA.name(),
//					new boolean[] { false, true, true, true, false, false, true }); // Fri, Sun–Tue
//
//			static Teacher t20 = new Teacher(TN.SALEHIN_MAHBUB.name(),
//					new boolean[] { false, false, false, true, false, false, true }); // Fri,
//																						// Tue
//
//			static Teacher t21 = new Teacher(TN.SAMIA_SABAH.name(),
//					new boolean[] { true, false, false, true, true, false, false }); // Sat,
//																						// Tue,
//																						// Wed
//
//			static Teacher t22 = new Teacher(TN.SHAILA_SHARMIN.name(),
//					new boolean[] { true, true, true, true, false, false, true }); // Fri,
//																					// Sat,
//																					// Sun–Tue
//
//			static Teacher t23 = new Teacher(TN.SM_MONIRUZZAMAN.name(),
//					new boolean[] { false, false, false, true, false, false, false }); // Tue
//
//			static Teacher t24 = new Teacher(TN.SUMITRA_GHOSH.name(),
//					new boolean[] { false, false, false, true, false, false, false }); // Tue
//
//			static Teacher t25 = new Teacher(TN.SYEDA_TASFIA.name(),
//					new boolean[] { true, true, true, false, false, false, true }); // Fri,
//																					// Sat,
//																					// Sun,
//																					// Mon
//
//			static Teacher t26 = new Teacher(TN.TUHIN_HOSSAIN.name(),
//					new boolean[] { true, true, true, false, true, false, false }); // Sat,
//																					// Sun,
//					
//			
//			
//			
//			
//			public static Map<String, Teacher> teachers = Map.ofEntries(Map.entry(t1.id, t1), Map.entry(t2.id, t2), Map.entry(t3.id, t3),
//					Map.entry(t4.id, t4), Map.entry(t5.id, t5), Map.entry(t6.id, t6), Map.entry(t7.id, t7),
//					Map.entry(t8.id, t8), Map.entry(t9.id, t9), Map.entry(t10.id, t10), Map.entry(t11.id, t11),
//					Map.entry(t12.id, t12), Map.entry(t13.id, t13), Map.entry(t14.id, t14), Map.entry(t15.id, t15),
//					Map.entry(t16.id, t16), Map.entry(t17.id, t17), Map.entry(t18.id, t18), Map.entry(t19.id, t19),
//					Map.entry(t20.id, t20), Map.entry(t21.id, t21), Map.entry(t22.id, t22), Map.entry(t23.id, t23),
//					Map.entry(t24.id, t24), Map.entry(t25.id, t25), Map.entry(t26.id, t26));

	public static Map<Integer, Teacher> teachers = new LinkedHashMap<>();

	public static Map<Integer, Teacher> getTeachers(HttpSession sc) {
		teachers.clear();

		Connection con = ConnectionProvider.getCon();
		PreparedStatement pst = null;
		ResultSet res = null;
		try {
			pst = con.prepareStatement("select * from teachers " + "where dept_type='"
					+ sc.getAttribute("dept_type") +"' order by teacher_name");
			res = pst.executeQuery();

			while (res.next()) {
				int teacherId = res.getInt("teacher_id");
				String name = res.getString("teacher_name");
				int maxSlotHours = res.getInt("max_slot_hours");
				ArrayList<Boolean> avail = new ArrayList<Boolean>();

				avail.add(res.getInt("saturday") == 0 ? false : true);
				avail.add(res.getInt("sunday") == 0 ? false : true);
				avail.add(res.getInt("monday") == 0 ? false : true);
				avail.add(res.getInt("tuesday") == 0 ? false : true);
				avail.add(res.getInt("wednesday") == 0 ? false : true);
				avail.add(res.getInt("thursday") == 0 ? false : true);
				avail.add(res.getInt("friday") == 0 ? false : true);

				Teacher teacher = new Teacher(teacherId, name, avail,maxSlotHours );
				teachers.put(teacherId, teacher);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try { if (res != null) res.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (pst != null) pst.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
		return teachers;

	}

}
