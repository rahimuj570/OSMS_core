package local_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entity.Course;
import entity.CourseType;
import entity.LabType;
import helper.ConnectionProvider;
import jakarta.servlet.http.HttpSession;
import local_db.TeacherData.TN;

public class CourseData {

	static enum CN {
		CSE_1203_DISCRETE_MATHEMATICS, // CSE 1203-0611 Discrete Mathematics
		CSE_1213_STRUCTURED_PROGRAMMING_LANGUAGE, // CSE 1213-0613 Structured Programming Language
		CSE_1214_STRUCTURED_PROGRAMMING_LANGUAGE_LAB, // CSE 1214-0613 Structured Programming Language Lab
		CSE_2141_OBJECT_ORIENTED_PROGRAMMING_LANGUAGE, // CSE 2141-0613 Object Oriented Programming Language
		CSE_2142_OBJECT_ORIENTED_PROGRAMMING_LANGUAGE_LAB, // CSE 2142-0613 Object Oriented Programming Language Lab
		CSE_2105_COMPETITIVE_PROGRAMMING_I, // CSE 2105-0613 Competitive Programming-I
		CSE_2205_COMPETITIVE_PROGRAMMING_II, // CSE 2205-0613 Competitive Programming-II
		CSE_2215_DATA_STRUCTURES_AND_ALGORITHMS, // CSE 2215-0613 Data Structures and Algorithms
		CSE_2216_DATA_STRUCTURES_AND_ALGORITHMS_LAB, // CSE 2216-0613 Data Structures and Algorithms Lab
		CSE_2221_SYSTEM_ANALYSIS_AND_DESIGN, // CSE 2221-0613 System Analysis and Design
		CSE_2222_SYSTEM_ANALYSIS_AND_DESIGN_LAB, // CSE 2222-0613 System Analysis and Design Lab
		CSE_2234_NUMERICAL_ANALYSIS_WITH_MATLAB, // CSE 2234-0613 Numerical Analysis with MATLAB
		CSE_3132_DATA_COMMUNICATION_AND_NETWORKING, // CSE 3132-0612 Data Communication and Networking
		CSE_3132_DATA_COMMUNICATION_AND_NETWORKING_LAB, // CSE 3132-0612 Data Communication and Networking Lab
		CSE_3151_DIGITAL_LOGIC_DESIGN, // CSE 3151-0414 Digital Logic Design
		CSE_3152_DIGITAL_LOGIC_DESIGN_LAB, // CSE 3152-0414 Digital Logic Design Lab
		CSE_3219_COMPILER_CONSTRUCTION, // CSE 3219-0613 Compiler Construction
		CSE_3220_COMPILER_CONSTRUCTION_LAB, // CSE 3220-0613 Compiler Construction Lab
		CSE_3223_DATABASE_MANAGEMENT_SYSTEM, // CSE 3223-0612 Database Management System
		CSE_3224_DATABASE_MANAGEMENT_SYSTEM_LAB, // CSE 3224-0612 Database Management System Lab
		CSE_3246_WEB_PROGRAMMING, // CSE 3246-0613 Web Programming
		CSE_3253_MICROPROCESSOR_AND_ASSEMBLY_LANGUAGE, // CSE 3253-0714 Microprocessor and Assembly Language
		CSE_3254_MICROPROCESSOR_AND_ASSEMBLY_LANGUAGE_LAB, // CSE 3254-0714 Microprocessor and Assembly Language Lab
		CSE_4135_CYBER_SECURITY_AND_LAW, // CSE 4135-0612 Cyber Security and Law
		CSE_4137_OPERATING_SYSTEM, // CSE 4137-0613 Operating System
		CSE_4138_OPERATING_SYSTEM_LAB, // CSE 4138-0613 Operating System Lab
		CSE_4201_DATA_MINING_AND_MACHINE_LEARNING, // CSE 4201-0612 Data Mining and Machine Learning
		CSE_4229_ARTIFICIAL_INTELLIGENCE, // CSE 4229-0613 Artificial Intelligence
		CSE_4230_ARTIFICIAL_INTELLIGENCE_LAB, // CSE 4230-0613 Artificial Intelligence Lab

		// Non-CSE / GED / General Education courses
		MATH_1111_MATHEMATICS_I, // MATH 1111-0541 Mathematics-I
		MATH_1213_MATHEMATICS_II, // MATH 1213-0541 Mathematics-II
		MATH_2115_MATHEMATICS_III, // MATH 2115-0541 Mathematics-III
		MATH_2217_MATH_IV_PROBABILITY_AND_STATISTICS, // MATH 2217-0542 Math-IV (Probability and Statistics)
		GED_3115_PRINCIPLE_OF_ACCOUNTING, // GED 3115-0411 Principle of Accounting
		HUM_1111_ORGANIZATIONAL_BEHAVIOR, // HUM 1111-0031 Organizational Behavior
		HUM_1113_BANGLADESH_STUDIES, // HUM 1113-0222 Bangladesh Studies
		HUM_2125_ARTS_OF_PRESENTATION, // HUM 2125-0031 Arts of Presentation
		ENG_1213_COMMUNICATIVE_ENGLISH, // ENG 1213-0231 Communicative English
		PHY_1111_PHYSICS_PLUS_LAB, // PHY-1111-0533 Physics + Lab
		CHE_1111_CHEMISTRY, // CHE 1111-0531 Chemistry
		CHE_1112_CHEMISTRY_LAB, // CHE 1112-0531 Chemistry Lab
		EEE_1211_ELECTRICAL_CIRCUIT_ANALYSIS, // EEE 1211-0714 Electrical Circuit Analysis
		EEE_1212_ELECTRICAL_CIRCUIT_ANALYSIS_LAB, CSE_2144_ENGINEERING_DRAWING; // EEE 1212-0714 Electrical Circuit
																				// Analysis Lab
	}
//	
//	
//	static Course c1 = new Course(CN.CSE_1213_STRUCTURED_PROGRAMMING_LANGUAGE.name(), CourseType.THEORY,
//			Set.of(TN.MD_AHSAN_ARIF.name(), TN.SADIA_NUR_NAZIFA.name()), Set.of(SectionData.s18A.id, SectionData.s18B.id), Set.of(), null);
//
//	static Course c2 = new Course(CN.CSE_1214_STRUCTURED_PROGRAMMING_LANGUAGE_LAB.name(), CourseType.LAB,
//			Set.of(TN.SYEDA_TASFIA.name(), TN.SADIA_NUR_NAZIFA.name(), TN.MD_AHSAN_ARIF.name()),
//			Set.of(SectionData.s18A.id, SectionData.s18B.id), Set.of(), LabType.COMPUTER);
//
//	static Course c3 = new Course(CN.CSE_2141_OBJECT_ORIENTED_PROGRAMMING_LANGUAGE.name(), CourseType.THEORY,
//			Set.of(TN.ASHIF_MAHMUD_JOY.name(), TN.H_M_IKRAM_KAYS.name()), Set.of(SectionData.s17A.id, SectionData.s17B.id), Set.of(), null);
//
//	static Course c4 = new Course(CN.CSE_2142_OBJECT_ORIENTED_PROGRAMMING_LANGUAGE_LAB.name(), CourseType.LAB,
//			Set.of(TN.ASHIF_MAHMUD_JOY.name(), TN.H_M_IKRAM_KAYS.name()), Set.of(SectionData.s17A.id, SectionData.s17B.id), Set.of(),
//			LabType.COMPUTER);
//
//	static Course c5 = new Course(CN.CSE_1203_DISCRETE_MATHEMATICS.name(), CourseType.THEORY,
//			Set.of(TN.SYEDA_TASFIA.name(), TN.H_M_IKRAM_KAYS.name(), TN.TUHIN_HOSSAIN.name(), TN.SUMITRA_GHOSH.name()),
//			Set.of(SectionData.s17A.id, SectionData.s17B.id, SectionData.s16A.id, SectionData.s16B.id), Set.of(), null);
//
//	static Course c6 = new Course(CN.CSE_2105_COMPETITIVE_PROGRAMMING_I.name(), CourseType.THEORY,
//			Set.of(TN.SADIA_NUR_NAZIFA.name(), TN.MAHBUB_E_SOBHANI.name()), Set.of(SectionData.s17A.id, SectionData.s17B.id), Set.of(), null);
//
//	static Course c7 = new Course(CN.CSE_3223_DATABASE_MANAGEMENT_SYSTEM.name(), CourseType.THEORY,
//			Set.of(TN.ARPITA_ROY.name(), TN.MD_AHSAN_ARIF.name()), Set.of(SectionData.s16A.id, SectionData.s16B.id), Set.of(), null);
//
//	static Course c8 = new Course(CN.CSE_3224_DATABASE_MANAGEMENT_SYSTEM_LAB.name(), CourseType.LAB,
//			Set.of(TN.ARPITA_ROY.name(), TN.MD_AHSAN_ARIF.name()), Set.of(SectionData.s16A.id, SectionData.s16B.id), Set.of(),
//			LabType.COMPUTER);
//
//	static Course c9 = new Course(CN.CSE_2144_ENGINEERING_DRAWING.name(), CourseType.LAB_ORIENTED_THEORY,
//			Set.of(TN.TUHIN_HOSSAIN.name(), TN.SABIKUN_NAHAR_ZERIN.name()), Set.of(SectionData.s16A.id, SectionData.s16B.id), Set.of(),
//			LabType.COMPUTER);
//
//	static Course c10 = new Course(CN.CSE_2215_DATA_STRUCTURES_AND_ALGORITHMS.name(), CourseType.THEORY,
//			Set.of(TN.SHAILA_SHARMIN.name(), TN.SABIKUN_NAHAR_ZERIN.name()), Set.of(SectionData.s15A.id, SectionData.s15B.id, SectionData.s15C.id),
//			Set.of(), null);
//
//	static Course c11 = new Course(CN.CSE_2216_DATA_STRUCTURES_AND_ALGORITHMS_LAB.name(), CourseType.LAB,
//			Set.of(TN.SHAILA_SHARMIN.name(), TN.SYEDA_TASFIA.name()), Set.of(SectionData.s15A.id, SectionData.s15B.id, SectionData.s15C.id), Set.of(),
//			LabType.COMPUTER);
//
//	static Course c12 = new Course(CN.CSE_2221_SYSTEM_ANALYSIS_AND_DESIGN.name(), CourseType.THEORY,
//			Set.of(TN.TUHIN_HOSSAIN.name(), TN.SADIA_NUR_NAZIFA.name(), TN.SYEDA_TASFIA.name()),
//			Set.of(SectionData.s15A.id, SectionData.s15B.id, SectionData.s15C.id), Set.of(), null);
//
//	static Course c13 = new Course(CN.CSE_2222_SYSTEM_ANALYSIS_AND_DESIGN_LAB.name(), CourseType.LAB,
//			Set.of(TN.TUHIN_HOSSAIN.name(), TN.SADIA_NUR_NAZIFA.name(), TN.SYEDA_TASFIA.name()),
//			Set.of(SectionData.s15A.id, SectionData.s15B.id, SectionData.s15C.id), Set.of(), LabType.COMPUTER);
//
//	static Course c14 = new Course(CN.CSE_2234_NUMERICAL_ANALYSIS_WITH_MATLAB.name(), CourseType.THEORY,
//			Set.of(TN.H_M_IKRAM_KAYS.name(), TN.SABIKUN_NAHAR_ZERIN.name(), TN.TUHIN_HOSSAIN.name()),
//			Set.of(SectionData.s15A.id, SectionData.s15B.id, SectionData.s15C.id), Set.of(), null);
//
//	static Course c15 = new Course(CN.CSE_3151_DIGITAL_LOGIC_DESIGN.name(), CourseType.THEORY,
//			Set.of(TN.REDUANUL_BARI_SHUVON.name(), TN.MAHBUB_E_SOBHANI.name()), Set.of(SectionData.s14A.id, SectionData.s14B.id), Set.of(),
//			null);
//
//	static Course c16 = new Course(CN.CSE_3152_DIGITAL_LOGIC_DESIGN_LAB.name(), CourseType.LAB,
//			Set.of(TN.REDUANUL_BARI_SHUVON.name(), TN.MAHBUB_E_SOBHANI.name()), Set.of(SectionData.s14A.id, SectionData.s14B.id), Set.of(),
//			LabType.COMPUTER);
//
//	static Course c17 = new Course(CN.CSE_3253_MICROPROCESSOR_AND_ASSEMBLY_LANGUAGE.name(), CourseType.THEORY,
//			Set.of(TN.MD_NURUL_ISLAM.name()), Set.of(SectionData.s14A.id, SectionData.s14B.id), Set.of(), null);
//
//	static Course c18 = new Course(CN.CSE_3254_MICROPROCESSOR_AND_ASSEMBLY_LANGUAGE_LAB.name(), CourseType.LAB,
//			Set.of(TN.MD_NURUL_ISLAM.name()), Set.of(SectionData.s14A.id, SectionData.s14B.id), Set.of(), LabType.COMPUTER);
//
//	static Course c19 = new Course(CN.CSE_2205_COMPETITIVE_PROGRAMMING_II.name(), CourseType.THEORY,
//			Set.of(TN.MAHBUB_E_SOBHANI.name(), TN.SHAILA_SHARMIN.name()), Set.of(SectionData.s14A.id, SectionData.s14B.id, SectionData.s16A.id, SectionData.s16B.id),
//			Set.of(), null);
//
//	static Course c20 = new Course(CN.CSE_3219_COMPILER_CONSTRUCTION.name(), CourseType.THEORY,
//			Set.of(TN.REDUANUL_BARI_SHUVON.name(), TN.ARPITA_ROY.name()), Set.of(SectionData.s13A.id, SectionData.s13B.id), Set.of(), null);
//
//	static Course c21 = new Course(CN.CSE_3220_COMPILER_CONSTRUCTION_LAB.name(), CourseType.LAB,
//			Set.of(TN.REDUANUL_BARI_SHUVON.name(), TN.ARPITA_ROY.name()), Set.of(SectionData.s13A.id, SectionData.s13B.id), Set.of(),
//			LabType.COMPUTER);
//
//	static Course c22 = new Course(CN.CSE_3246_WEB_PROGRAMMING.name(), CourseType.THEORY,
//			Set.of(TN.MD_NURUL_ISLAM.name(), TN.ASHIF_MAHMUD_JOY.name()), Set.of(SectionData.s13A.id, SectionData.s13B.id), Set.of(), null);
//
//	static Course c23 = new Course(CN.CSE_3132_DATA_COMMUNICATION_AND_NETWORKING.name(), CourseType.THEORY,
//			Set.of(TN.A_K_M_MONZURUL_ISLAM.name(), TN.MAHBUBUR_RAHMAN.name()), Set.of(SectionData.s13A.id, SectionData.s13B.id), Set.of(),
//			null);
//
//	static Course c24 = new Course(CN.CSE_3132_DATA_COMMUNICATION_AND_NETWORKING_LAB.name(), CourseType.LAB,
//			Set.of(TN.A_K_M_MONZURUL_ISLAM.name(), TN.MAHBUBUR_RAHMAN.name()), Set.of(SectionData.s13A.id, SectionData.s13B.id), Set.of(),
//			LabType.COMPUTER);
//
//	static Course c25 = new Course(CN.CSE_4229_ARTIFICIAL_INTELLIGENCE.name(), CourseType.THEORY,
//			Set.of(TN.DR_SAIFUL_ISLAM.name(), TN.REDUANUL_BARI_SHUVON.name(), TN.MAMOON_AL_RASHEED.name()),
//			Set.of(SectionData.s11_12A.id, SectionData.s11_12B.id, SectionData.s11_12C.id), Set.of(), null);
//
//	static Course c26 = new Course(CN.CSE_4230_ARTIFICIAL_INTELLIGENCE_LAB.name(), CourseType.LAB,
//			Set.of(TN.DR_SAIFUL_ISLAM.name(), TN.REDUANUL_BARI_SHUVON.name(), TN.MAMOON_AL_RASHEED.name()),
//			Set.of(SectionData.s11_12A.id, SectionData.s11_12B.id, SectionData.s11_12C.id), Set.of(), LabType.COMPUTER);
//
//	static Course c27 = new Course(CN.CSE_4201_DATA_MINING_AND_MACHINE_LEARNING.name(), CourseType.THEORY,
//			Set.of(TN.ASHIF_MAHMUD_JOY.name()), Set.of(SectionData.s11_12A.id, SectionData.s11_12B.id, SectionData.s11_12C.id), Set.of(), null);
//
//	static Course c28 = new Course(CN.CSE_4137_OPERATING_SYSTEM.name(), CourseType.THEORY,
//			Set.of(TN.SABIKUN_NAHAR_ZERIN.name(), TN.H_M_IKRAM_KAYS.name(), TN.MAHBUBUR_RAHMAN.name(),
//					TN.A_K_M_MONZURUL_ISLAM.name()),
//			Set.of(SectionData.s11_12A.id, SectionData.s11_12B.id, SectionData.s11_12C.id, SectionData.s13A.id, SectionData.s13B.id), Set.of(), null);
//
//	static Course c29 = new Course(CN.CSE_4138_OPERATING_SYSTEM_LAB.name(), CourseType.LAB,
//			Set.of(TN.SABIKUN_NAHAR_ZERIN.name(), TN.MAHBUBUR_RAHMAN.name()),
//			Set.of(SectionData.s11_12A.id, SectionData.s11_12B.id, SectionData.s11_12C.id), Set.of(), LabType.COMPUTER);
//
//	static Course c30 = new Course(CN.CSE_4135_CYBER_SECURITY_AND_LAW.name(), CourseType.THEORY,
//			Set.of(TN.SHAILA_SHARMIN.name(), TN.MAHBUBUR_RAHMAN.name()), Set.of(SectionData.s11_12A.id, SectionData.s11_12B.id, SectionData.s11_12C.id),
//			Set.of(), null);
//
//	static Course c31 = new Course(CN.MATH_2217_MATH_IV_PROBABILITY_AND_STATISTICS.name(), CourseType.THEORY,
//			Set.of(TN.NUR_MUHAMMAD_FAHAD.name(), TN.MD_RAHAD_ISLAM_BHUIYAN.name()), Set.of(SectionData.s15A.id, SectionData.s15B.id, SectionData.s15C.id),
//			Set.of(), null);
//
//	static Course c32 = new Course(CN.GED_3115_PRINCIPLE_OF_ACCOUNTING.name(), CourseType.THEORY,
//			Set.of(TN.SUMITRA_GHOSH.name(), TN.SAMIA_SABAH.name()),
//			Set.of(SectionData.s13A.id, SectionData.s13B.id, SectionData.s14A.id, SectionData.s14B.id, SectionData.s15A.id, SectionData.s15B.id, SectionData.s15C.id), Set.of(), null);
//
//	static Course c33 = new Course(CN.MATH_1111_MATHEMATICS_I.name(), CourseType.THEORY,
//			Set.of(TN.ABU_SUFIAN_MD_SHAHED.name()), Set.of(SectionData.s18A.id, SectionData.s18B.id), Set.of(), null);
//
//	static Course c34 = new Course(CN.MATH_1213_MATHEMATICS_II.name(), CourseType.THEORY,
//			Set.of(TN.ASIB_MUSTAKIM_FONY.name()), Set.of(SectionData.s17A.id, SectionData.s17B.id), Set.of(), null);
//
//	static Course c35 = new Course(CN.MATH_2115_MATHEMATICS_III.name(), CourseType.THEORY,
//			Set.of(TN.ASIB_MUSTAKIM_FONY.name(), TN.DR_ROWSANARA_AKHTER.name()), Set.of(SectionData.s16A.id, SectionData.s16B.id), Set.of(),
//			null);
//
//	static Course c36 = new Course(CN.HUM_1111_ORGANIZATIONAL_BEHAVIOR.name(), CourseType.THEORY,
//			Set.of(TN.A_K_M_MONZURUL_ISLAM.name()), Set.of(SectionData.s16A.id, SectionData.s16B.id), Set.of(), null);
//
//	static Course c37 = new Course(CN.HUM_1113_BANGLADESH_STUDIES.name(), CourseType.THEORY,
//			Set.of(TN.ARPITA_ROY.name()), Set.of(SectionData.s18A.id, SectionData.s18B.id), Set.of(), null);
//
//	static Course c38 = new Course(CN.HUM_2125_ARTS_OF_PRESENTATION.name(), CourseType.THEORY,
//			Set.of(TN.MAMOON_AL_RASHEED.name()), Set.of(SectionData.s17A.id, SectionData.s17B.id), Set.of(), null);
//
//	static Course c39 = new Course(CN.ENG_1213_COMMUNICATIVE_ENGLISH.name(), CourseType.THEORY,
//			Set.of(TN.SHAILA_SHARMIN.name()), Set.of(SectionData.s17A.id, SectionData.s17B.id), Set.of(), null);
//
//	static Course c40 = new Course(CN.PHY_1111_PHYSICS_PLUS_LAB.name(), CourseType.LAB_ORIENTED_THEORY,
//			Set.of(TN.SM_MONIRUZZAMAN.name()), Set.of(SectionData.s18A.id, SectionData.s18B.id), Set.of(), LabType.GENERAL);
//
//	static Course c41 = new Course(CN.CHE_1111_CHEMISTRY.name(), CourseType.THEORY,
//			Set.of(TN.SALEHIN_MAHBUB.name(), TN.MD_NAHID.name()), Set.of(SectionData.s18A.id, SectionData.s18B.id), Set.of(), null);
//
//	static Course c42 = new Course(CN.CHE_1112_CHEMISTRY_LAB.name(), CourseType.LAB, Set.of(TN.SALEHIN_MAHBUB.name()),
//			Set.of(SectionData.s18A.id), Set.of(), LabType.GENERAL);
//
//	static Course c43 = new Course(CN.EEE_1211_ELECTRICAL_CIRCUIT_ANALYSIS.name(), CourseType.THEORY,
//			Set.of(TN.NUR_MUHAMMAD_FAHAD.name()), Set.of(SectionData.s14A.id, SectionData.s14B.id), Set.of(), null);
//
//	static Course c44 = new Course(CN.EEE_1212_ELECTRICAL_CIRCUIT_ANALYSIS_LAB.name(), CourseType.LAB,
//			Set.of(TN.NUR_MUHAMMAD_FAHAD.name()), Set.of(SectionData.s14A.id, SectionData.s14B.id), Set.of(), LabType.ELECTRONIC);

//	public static List<Course> courses = List.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16,
//			c17, c18, c19, c20, c21, c22, c23, c24, c25, c26, c27, c28, c29, c30, c31, c32, c33, c34, c35, c36, c37,
//			c38, c39, c40, c41, c42, c43, c44);

	public static List<Course> getCourses(HttpSession sc) {
		List<Course> courses = new ArrayList<>();
		String deptType = sc.getAttribute("dept_type") != null ? sc.getAttribute("dept_type").toString() : "";
		String sql = "select * from courses where dept_type=? order by course_id";

		try (Connection con = ConnectionProvider.getCon();
				PreparedStatement courseStmt = con.prepareStatement(sql)) {
			courseStmt.setString(1, deptType);
			try (ResultSet courseRes = courseStmt.executeQuery()) {
				while (courseRes.next()) {
					String courseId = courseRes.getString("course_id");
					String courseType = courseRes.getString("course_type");
					String requiredLab = courseRes.getString("required_lab");

					try (PreparedStatement sectionStmt = con.prepareStatement(
							"select * from course_sections where course_id=?")) {
						sectionStmt.setString(1, courseId);
						try (ResultSet sectionRes = sectionStmt.executeQuery()) {
							List<String> sectionList = new ArrayList<>();
							while (sectionRes.next()) {
								sectionList.add(sectionRes.getString("section_id"));
							}

							try (PreparedStatement prefStmt = con.prepareStatement(
									"select * from course_preferred_teachers where course_id=?")) {
								prefStmt.setString(1, courseId);
								try (ResultSet prefRes = prefStmt.executeQuery()) {
									List<Integer> prefs = new ArrayList<>();
									while (prefRes.next()) {
										prefs.add(prefRes.getInt("teacher_id"));
									}

									try (PreparedStatement forbStmt = con.prepareStatement(
											"select * from course_forbidden_teachers where course_id=?")) {
										forbStmt.setString(1, courseId);
										try (ResultSet forbRes = forbStmt.executeQuery()) {
											List<Integer> forbs = new ArrayList<>();
											while (forbRes.next()) {
												forbs.add(forbRes.getInt("teacher_id"));
											}

											Course c = new Course(courseId, CourseType.valueOf(courseType),
													new HashSet<>(prefs), new HashSet<>(sectionList),
													new HashSet<>(forbs),
													requiredLab == null ? null : LabType.valueOf(requiredLab));
											courses.add(c);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return courses;
	}

}
