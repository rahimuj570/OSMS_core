package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import algorithm.CSPSolver;
import algorithm.CSPState;
import algorithm.Value;
import algorithm.Variable;
import entity.Course;
import entity.CourseType;
import entity.LabType;
import entity.Room;
import entity.RoomType;
import entity.Section;
import entity.Teacher;

public class Main {

	public static boolean timeout = false;
	static final int DAYS = 7;
	static final int SLOTS_PER_DAY = 14;

	static List<Variable> generateVariables(List<Course> courses, Map<String, Section> sections) {
		List<Variable> vars = new ArrayList<>();

		for (Course c : courses) {
			for (String secId : c.sectionIds) {
				Section sec = sections.get(secId);
				switch (c.type) {
				case THEORY -> {
					vars.add(new Variable(c.id + "_" + secId + "_1", c, sec));
					vars.add(new Variable(c.id + "_" + secId + "_2", c, sec));
				}
				case LAB -> {
					vars.add(new Variable(c.id + "_" + secId + "_LAB", c, sec));
				}
				case LAB_ORIENTED_THEORY -> {
					vars.add(new Variable(c.id + "_" + secId + "_1", c, sec));
					vars.add(new Variable(c.id + "_" + secId + "_2", c, sec));
				}
				}
			}
		}
		return vars;
	}

	static void generateDomains(List<Variable> vars, Map<String, Teacher> teachers, Map<String, Room> rooms) {

		for (Variable v : vars) {

			int slotCount = switch (v.course.type) {
			case THEORY -> 3;
			case LAB -> 6;
			case LAB_ORIENTED_THEORY -> 3;
			};

			for (int day = 0; day < DAYS; day++) {

				for (Teacher t : teachers.values()) {

// check blacklist
					if (v.course.forbiddenTeachers != null && v.course.forbiddenTeachers.contains(t.id))
						continue;

// check availability
					if (!t.availability[day])
						continue;

					for (Room r : rooms.values()) {

						if (!roomAllowed(v.course.type, r))
							continue;

						if (v.course.type == CourseType.LAB) {
							if (r.type != RoomType.LAB)
								continue;
							if (r.labType != v.course.requiredLab)
								continue;
						}

						if (v.course.type == CourseType.THEORY && r.type == RoomType.LAB)
							continue;

						if (r.capacity < v.section.students)
							continue;

						long roomDayMask = r.availability[day];

						for (int start = 0; start + slotCount <= SLOTS_PER_DAY; start++) {

							long neededMask = ((1L << slotCount) - 1) << start;

							if ((roomDayMask & neededMask) != neededMask)
								continue;

							v.domain.add(new Value(day, start, slotCount, r.id, t.id));
						}
					}
				}
			}

// sort domain (preferred teacher first)
			v.domain.sort((a, b) -> {
				boolean aPref = v.course.preferredTeachers != null && v.course.preferredTeachers.contains(a.teacherId);

				boolean bPref = v.course.preferredTeachers != null && v.course.preferredTeachers.contains(b.teacherId);

				return Boolean.compare(bPref, aPref); // preferred first
			});
		}
	}

	static boolean roomAllowed(CourseType type, Room r) {
		return switch (type) {
		case THEORY -> r.type == RoomType.THEORY;
		case LAB -> r.type == RoomType.LAB;
		case LAB_ORIENTED_THEORY -> true;
		};
	}

	static void buildNeighbors(List<Variable> vars) {
		for (int i = 0; i < vars.size(); i++) {
			for (int j = i + 1; j < vars.size(); j++) {

				Variable a = vars.get(i);
				Variable b = vars.get(j);

				// only section-based conflict
				if (a.section.id.equals(b.section.id)) {
					a.neighbors.add(b.id);
					b.neighbors.add(a.id);
				}
			}
		}
	}

	public static void main(String[] args) {

		long FULL_DAY = (1L << SLOTS_PER_DAY) - 1;
		long LUNCH_MASK = ~((1L << 6) | (1L << 7));
		long DAY_WITHOUT_LUNCH = FULL_DAY & LUNCH_MASK;

		Room r1 = new Room("R801", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
		Room r2 = new Room("R803", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
		Room r3 = new Room("R804", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
		Room r4 = new Room("R1502", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
		Room r5 = new Room("R1702", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
		Room r6 = new Room("R1704", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
		Room r7 = new Room("R8011", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
		Room r8 = new Room("R8012", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);
		Room r9 = new Room("R80123", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH }, null);

		Room l1 = new Room(
				"L802", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
						DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH },
				LabType.ELECTRONIC);
		Room l2 = new Room("L902", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH },
				LabType.GENERAL);
		Room l3 = new Room("L903", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH },
				LabType.COMPUTER);
		Room l4 = new Room("L1503", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH },
				LabType.COMPUTER);
		Room l5 = new Room("R1703", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH },
				LabType.COMPUTER);

		enum CN {
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
		enum TN {
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
			NUR_MUHAMMAD_FAHAD, REDUANUL_BARI_SHUVON, SABIKUN_NAHAR_ZERIN, SADIA_NUR_NAZIFA, SALEHIN_MAHBUB,
			SAMIA_SABAH, SHAILA_SHARMIN, SM_MONIRUZZAMAN, SUMITRA_GHOSH, SYEDA_TASFIA, TUHIN_HOSSAIN;
		}

		// t1 and t2 were already given by you
		Teacher t1 = new Teacher(TN.A_K_M_MONZURUL_ISLAM.name(),
				new boolean[] { false, true, true, true, false, false, true }); // Fri, Sun, Mon, Tue

		Teacher t2 = new Teacher(TN.ABU_SUFIAN_MD_SHAHED.name(),
				new boolean[] { false, true, false, false, false, false, false }); // Sun

		Teacher t3 = new Teacher(TN.ARPITA_ROY.name(), new boolean[] { false, true, true, false, false, false, true }); // Fri,
		// Sun,
		// Mon

		Teacher t4 = new Teacher(TN.ASIB_MUSTAKIM_FONY.name(),
				new boolean[] { true, true, true, false, false, false, false }); // Sat, Sun, Mon

		Teacher t5 = new Teacher(TN.ASHIF_MAHMUD_JOY.name(),
				new boolean[] { true, true, true, true, false, false, false }); // Sat, Sun, Mon, Tue

		Teacher t6 = new Teacher(TN.DR_ROWSANARA_AKHTER.name(),
				new boolean[] { true, false, true, false, false, false, false }); // Sat, Mon

		Teacher t7 = new Teacher(TN.DR_SAIFUL_ISLAM.name(),
				new boolean[] { false, true, true, true, false, false, false }); // Sun,
																					// Mon,
																					// Tue

		Teacher t8 = new Teacher(TN.H_M_IKRAM_KAYS.name(),
				new boolean[] { true, true, true, true, true, false, false }); // Sat–Wed

		Teacher t9 = new Teacher(TN.MAHBUB_E_SOBHANI.name(),
				new boolean[] { false, true, true, true, false, false, true }); // Fri, Sun, Mon, Tue

		Teacher t10 = new Teacher(TN.MAHBUBUR_RAHMAN.name(),
				new boolean[] { true, true, true, true, false, false, true }); // Fri, Sat, Sun, Mon, Tue

		Teacher t11 = new Teacher(TN.MAMOON_AL_RASHEED.name(),
				new boolean[] { true, true, true, true, false, false, false }); // Sat, Sun, Mon, Tue

		Teacher t12 = new Teacher(TN.MD_AHSAN_ARIF.name(),
				new boolean[] { false, false, true, true, false, false, false }); // Fri,
		// Mon,
		// Tue

		Teacher t13 = new Teacher(TN.MD_NAHID.name(), new boolean[] { false, false, false, true, false, false, true }); // Fri
		// +
		// Tue

		Teacher t14 = new Teacher(TN.MD_NURUL_ISLAM.name(),
				new boolean[] { false, false, true, false, true, false, true }); // Fri,
																					// Mon,
																					// Wed

		Teacher t15 = new Teacher(TN.MD_RAHAD_ISLAM_BHUIYAN.name(),
				new boolean[] { false, true, true, false, false, false, false }); // Sun, Mon

		Teacher t16 = new Teacher(TN.NUR_MUHAMMAD_FAHAD.name(),
				new boolean[] { true, true, true, true, true, true, true }); // Fri, Sun–Wed

		Teacher t17 = new Teacher(TN.REDUANUL_BARI_SHUVON.name(),
				new boolean[] { true, true, true, true, false, false, false }); // Sat–Tue

		Teacher t18 = new Teacher(TN.SABIKUN_NAHAR_ZERIN.name(),
				new boolean[] { true, true, true, false, true, false, false }); // Sat, Sun, Mon, Wed

		Teacher t19 = new Teacher(TN.SADIA_NUR_NAZIFA.name(),
				new boolean[] { false, true, true, true, false, false, true }); // Fri, Sun–Tue

		Teacher t20 = new Teacher(TN.SALEHIN_MAHBUB.name(),
				new boolean[] { false, false, false, true, false, false, true }); // Fri,
																					// Tue

		Teacher t21 = new Teacher(TN.SAMIA_SABAH.name(),
				new boolean[] { true, false, false, true, true, false, false }); // Sat,
																					// Tue,
																					// Wed

		Teacher t22 = new Teacher(TN.SHAILA_SHARMIN.name(),
				new boolean[] { true, true, true, true, false, false, true }); // Fri,
																				// Sat,
																				// Sun–Tue

		Teacher t23 = new Teacher(TN.SM_MONIRUZZAMAN.name(),
				new boolean[] { false, false, false, true, false, false, false }); // Tue

		Teacher t24 = new Teacher(TN.SUMITRA_GHOSH.name(),
				new boolean[] { false, false, false, true, false, false, false }); // Tue

		Teacher t25 = new Teacher(TN.SYEDA_TASFIA.name(),
				new boolean[] { true, true, true, false, false, false, true }); // Fri,
																				// Sat,
																				// Sun,
																				// Mon

		Teacher t26 = new Teacher(TN.TUHIN_HOSSAIN.name(),
				new boolean[] { true, true, true, false, true, false, false }); // Sat,
																				// Sun,
																				// Mon,
																				// Wed

		Section s18A = new Section("18A", 40);
		Section s18B = new Section("18B", 35);
		Section s17A = new Section("17A", 35);
		Section s17B = new Section("17B", 35);
		Section s16A = new Section("16A", 35);
		Section s16B = new Section("16B", 35);
		Section s15A = new Section("15A", 35);
		Section s15B = new Section("15B", 35);
		Section s15C = new Section("15C", 35);
		Section s14A = new Section("14A", 35);
		Section s14B = new Section("14B", 35);
		Section s13A = new Section("13A", 35);
		Section s13B = new Section("13B", 35);
		Section s11_12A = new Section("11&12A", 35);
		Section s11_12B = new Section("11&12B", 35);
		Section s11_12C = new Section("11&12C", 35);

		// Batch 18 (Freshman level) - Introductory courses
		Course c1 = new Course(CN.CSE_1213_STRUCTURED_PROGRAMMING_LANGUAGE.name(), CourseType.THEORY,
				Set.of(TN.MD_AHSAN_ARIF.name(), TN.SADIA_NUR_NAZIFA.name()), Set.of(s18A.id, s18B.id), Set.of(), null);

		Course c2 = new Course(CN.CSE_1214_STRUCTURED_PROGRAMMING_LANGUAGE_LAB.name(), CourseType.LAB,
				Set.of(TN.SYEDA_TASFIA.name(), TN.SADIA_NUR_NAZIFA.name(), TN.MD_AHSAN_ARIF.name()),
				Set.of(s18A.id, s18B.id), Set.of(), LabType.COMPUTER);

		Course c3 = new Course(CN.CSE_2141_OBJECT_ORIENTED_PROGRAMMING_LANGUAGE.name(), CourseType.THEORY,
				Set.of(TN.ASHIF_MAHMUD_JOY.name(), TN.H_M_IKRAM_KAYS.name()), Set.of(s17A.id, s17B.id), Set.of(), null);

		Course c4 = new Course(CN.CSE_2142_OBJECT_ORIENTED_PROGRAMMING_LANGUAGE_LAB.name(), CourseType.LAB,
				Set.of(TN.ASHIF_MAHMUD_JOY.name(), TN.H_M_IKRAM_KAYS.name()), Set.of(s17A.id, s17B.id), Set.of(), LabType.COMPUTER);

		Course c5 = new Course(
				CN.CSE_1203_DISCRETE_MATHEMATICS.name(), CourseType.THEORY, Set.of(TN.SYEDA_TASFIA.name(),
						TN.H_M_IKRAM_KAYS.name(), TN.TUHIN_HOSSAIN.name(), TN.SUMITRA_GHOSH.name()),
				Set.of(s17A.id, s17B.id, s16A.id, s16B.id), Set.of(), null);

		Course c6 = new Course(CN.CSE_2105_COMPETITIVE_PROGRAMMING_I.name(), CourseType.THEORY,
				Set.of(TN.SADIA_NUR_NAZIFA.name(), TN.MAHBUB_E_SOBHANI.name()), Set.of(s17A.id, s17B.id), Set.of(),
				null);

		Course c7 = new Course(CN.CSE_3223_DATABASE_MANAGEMENT_SYSTEM.name(), CourseType.THEORY,
				Set.of(TN.ARPITA_ROY.name(), TN.MD_AHSAN_ARIF.name()), Set.of(s16A.id, s16B.id), Set.of(), null);

		Course c8 = new Course(CN.CSE_3224_DATABASE_MANAGEMENT_SYSTEM_LAB.name(), CourseType.LAB,
				Set.of(TN.ARPITA_ROY.name(), TN.MD_AHSAN_ARIF.name()), Set.of(s16A.id, s16B.id), Set.of(), LabType.COMPUTER);

		Course c9 = new Course(CN.CSE_2144_ENGINEERING_DRAWING.name(), CourseType.LAB_ORIENTED_THEORY,
				Set.of(TN.TUHIN_HOSSAIN.name(), TN.SABIKUN_NAHAR_ZERIN.name()), Set.of(s16A.id, s16B.id), Set.of(),
				LabType.COMPUTER);

		Course c10 = new Course(CN.CSE_2215_DATA_STRUCTURES_AND_ALGORITHMS.name(), CourseType.THEORY,
				Set.of(TN.SHAILA_SHARMIN.name(), TN.SABIKUN_NAHAR_ZERIN.name()), Set.of(s15A.id, s15B.id, s15C.id),
				Set.of(), null);

		Course c11 = new Course(CN.CSE_2216_DATA_STRUCTURES_AND_ALGORITHMS_LAB.name(), CourseType.LAB,
				Set.of(TN.SHAILA_SHARMIN.name(), TN.SYEDA_TASFIA.name()), Set.of(s15A.id, s15B.id, s15C.id), Set.of(),
				LabType.COMPUTER);

		Course c12 = new Course(CN.CSE_2221_SYSTEM_ANALYSIS_AND_DESIGN.name(), CourseType.THEORY,
				Set.of(TN.TUHIN_HOSSAIN.name(), TN.SADIA_NUR_NAZIFA.name(), TN.SYEDA_TASFIA.name()),
				Set.of(s15A.id, s15B.id, s15C.id), Set.of(), null);

		Course c13 = new Course(CN.CSE_2222_SYSTEM_ANALYSIS_AND_DESIGN_LAB.name(), CourseType.LAB,
				Set.of(TN.TUHIN_HOSSAIN.name(), TN.SADIA_NUR_NAZIFA.name(), TN.SYEDA_TASFIA.name()),
				Set.of(s15A.id, s15B.id, s15C.id), Set.of(), LabType.COMPUTER);

		Course c14 = new Course(CN.CSE_2234_NUMERICAL_ANALYSIS_WITH_MATLAB.name(), CourseType.THEORY,
				Set.of(TN.H_M_IKRAM_KAYS.name(), TN.SABIKUN_NAHAR_ZERIN.name(), TN.TUHIN_HOSSAIN.name()),
				Set.of(s15A.id, s15B.id, s15C.id), Set.of(), null);

		Course c15 = new Course(CN.CSE_3151_DIGITAL_LOGIC_DESIGN.name(), CourseType.THEORY,
				Set.of(TN.REDUANUL_BARI_SHUVON.name(), TN.MAHBUB_E_SOBHANI.name()), Set.of(s14A.id, s14B.id), Set.of(),
				null);

		Course c16 = new Course(CN.CSE_3152_DIGITAL_LOGIC_DESIGN_LAB.name(), CourseType.LAB,
				Set.of(TN.REDUANUL_BARI_SHUVON.name(), TN.MAHBUB_E_SOBHANI.name()), Set.of(s14A.id, s14B.id), Set.of(),
				LabType.COMPUTER);

		Course c17 = new Course(CN.CSE_3253_MICROPROCESSOR_AND_ASSEMBLY_LANGUAGE.name(), CourseType.THEORY,
				Set.of(TN.MD_NURUL_ISLAM.name()), Set.of(s14A.id, s14B.id), Set.of(), null);

		Course c18 = new Course(CN.CSE_3254_MICROPROCESSOR_AND_ASSEMBLY_LANGUAGE_LAB.name(), CourseType.LAB,
				Set.of(TN.MD_NURUL_ISLAM.name()), Set.of(s14A.id, s14B.id), Set.of(), LabType.COMPUTER);

		Course c19 = new Course(CN.CSE_2205_COMPETITIVE_PROGRAMMING_II.name(), CourseType.THEORY,
				Set.of(TN.MAHBUB_E_SOBHANI.name(), TN.SHAILA_SHARMIN.name()),
				Set.of(s14A.id, s14B.id, s16A.id, s16B.id), Set.of(), null);

		Course c20 = new Course(CN.CSE_3219_COMPILER_CONSTRUCTION.name(), CourseType.THEORY,
				Set.of(TN.REDUANUL_BARI_SHUVON.name(), TN.ARPITA_ROY.name()), Set.of(s13A.id, s13B.id), Set.of(), null);

		Course c21 = new Course(CN.CSE_3220_COMPILER_CONSTRUCTION_LAB.name(), CourseType.LAB,
				Set.of(TN.REDUANUL_BARI_SHUVON.name(), TN.ARPITA_ROY.name()), Set.of(s13A.id, s13B.id), Set.of(), LabType.COMPUTER);

		Course c22 = new Course(CN.CSE_3246_WEB_PROGRAMMING.name(), CourseType.THEORY,
				Set.of(TN.MD_NURUL_ISLAM.name(), TN.ASHIF_MAHMUD_JOY.name()), Set.of(s13A.id, s13B.id), Set.of(), null);

		Course c23 = new Course(CN.CSE_3132_DATA_COMMUNICATION_AND_NETWORKING.name(), CourseType.THEORY,
				Set.of(TN.A_K_M_MONZURUL_ISLAM.name(), TN.MAHBUBUR_RAHMAN.name()), Set.of(s13A.id, s13B.id), Set.of(),
				null);

		Course c24 = new Course(CN.CSE_3132_DATA_COMMUNICATION_AND_NETWORKING_LAB.name(), CourseType.LAB,
				Set.of(TN.A_K_M_MONZURUL_ISLAM.name(), TN.MAHBUBUR_RAHMAN.name()), Set.of(s13A.id, s13B.id), Set.of(),
				LabType.COMPUTER);

		Course c25 = new Course(CN.CSE_4229_ARTIFICIAL_INTELLIGENCE.name(), CourseType.THEORY,
				Set.of(TN.DR_SAIFUL_ISLAM.name(), TN.REDUANUL_BARI_SHUVON.name(), TN.MAMOON_AL_RASHEED.name()),
				Set.of(s11_12A.id, s11_12B.id, s11_12C.id), Set.of(), null);

		Course c26 = new Course(CN.CSE_4230_ARTIFICIAL_INTELLIGENCE_LAB.name(), CourseType.LAB,
				Set.of(TN.DR_SAIFUL_ISLAM.name(), TN.REDUANUL_BARI_SHUVON.name(), TN.MAMOON_AL_RASHEED.name()),
				Set.of(s11_12A.id, s11_12B.id, s11_12C.id), Set.of(), LabType.COMPUTER);

		Course c27 = new Course(CN.CSE_4201_DATA_MINING_AND_MACHINE_LEARNING.name(), CourseType.THEORY,
				Set.of(TN.ASHIF_MAHMUD_JOY.name()), Set.of(s11_12A.id, s11_12B.id, s11_12C.id), Set.of(), null);

		Course c28 = new Course(CN.CSE_4137_OPERATING_SYSTEM.name(), CourseType.THEORY,
				Set.of(TN.SABIKUN_NAHAR_ZERIN.name(), TN.H_M_IKRAM_KAYS.name(), TN.MAHBUBUR_RAHMAN.name(),
						TN.A_K_M_MONZURUL_ISLAM.name()),
				Set.of(s11_12A.id, s11_12B.id, s11_12C.id, s13A.id, s13B.id), Set.of(), null);

		Course c29 = new Course(CN.CSE_4138_OPERATING_SYSTEM_LAB.name(), CourseType.LAB,
				Set.of(TN.SABIKUN_NAHAR_ZERIN.name(), TN.MAHBUBUR_RAHMAN.name()),
				Set.of(s11_12A.id, s11_12B.id, s11_12C.id), Set.of(), LabType.COMPUTER);

		Course c30 = new Course(CN.CSE_4135_CYBER_SECURITY_AND_LAW.name(), CourseType.THEORY,
				Set.of(TN.SHAILA_SHARMIN.name(), TN.MAHBUBUR_RAHMAN.name()), Set.of(s11_12A.id, s11_12B.id, s11_12C.id),
				Set.of(), null);

		Course c31 = new Course(CN.MATH_2217_MATH_IV_PROBABILITY_AND_STATISTICS.name(), CourseType.THEORY,
				Set.of(TN.NUR_MUHAMMAD_FAHAD.name(), TN.MD_RAHAD_ISLAM_BHUIYAN.name()),
				Set.of(s15A.id, s15B.id, s15C.id), Set.of(), null);

		Course c32 = new Course(CN.GED_3115_PRINCIPLE_OF_ACCOUNTING.name(), CourseType.THEORY,
				Set.of(TN.SUMITRA_GHOSH.name(), TN.SAMIA_SABAH.name()),
				Set.of(s13A.id, s13B.id, s14A.id, s14B.id, s15A.id, s15B.id, s15C.id), Set.of(), null);

		Course c33 = new Course(CN.MATH_1111_MATHEMATICS_I.name(), CourseType.THEORY,
				Set.of(TN.ABU_SUFIAN_MD_SHAHED.name()), Set.of(s18A.id, s18B.id), Set.of(), null);

		Course c34 = new Course(CN.MATH_1213_MATHEMATICS_II.name(), CourseType.THEORY,
				Set.of(TN.ASIB_MUSTAKIM_FONY.name()), Set.of(s17A.id, s17B.id), Set.of(), null);

		Course c35 = new Course(CN.MATH_2115_MATHEMATICS_III.name(), CourseType.THEORY,
				Set.of(TN.ASIB_MUSTAKIM_FONY.name(), TN.DR_ROWSANARA_AKHTER.name()), Set.of(s16A.id, s16B.id), Set.of(),
				null);

		Course c36 = new Course(CN.HUM_1111_ORGANIZATIONAL_BEHAVIOR.name(), CourseType.THEORY,
				Set.of(TN.A_K_M_MONZURUL_ISLAM.name()), Set.of(s16A.id, s16B.id), Set.of(), null);

		Course c37 = new Course(CN.HUM_1113_BANGLADESH_STUDIES.name(), CourseType.THEORY, Set.of(TN.ARPITA_ROY.name()),
				Set.of(s18A.id, s18B.id), Set.of(), null);

		Course c38 = new Course(CN.HUM_2125_ARTS_OF_PRESENTATION.name(), CourseType.THEORY,
				Set.of(TN.MAMOON_AL_RASHEED.name()), Set.of(s17A.id, s17B.id), Set.of(), null);

		Course c39 = new Course(CN.ENG_1213_COMMUNICATIVE_ENGLISH.name(), CourseType.THEORY,
				Set.of(TN.SHAILA_SHARMIN.name()), Set.of(s17A.id, s17B.id), Set.of(), null);

		Course c40 = new Course(CN.PHY_1111_PHYSICS_PLUS_LAB.name(), CourseType.LAB_ORIENTED_THEORY,
				Set.of(TN.SM_MONIRUZZAMAN.name()), Set.of(s18A.id, s18B.id), Set.of(), LabType.GENERAL);

		Course c41 = new Course(CN.CHE_1111_CHEMISTRY.name(), CourseType.THEORY,
				Set.of(TN.SALEHIN_MAHBUB.name(), TN.MD_NAHID.name()), Set.of(s18A.id, s18B.id), Set.of(), null);

		Course c42 = new Course(CN.CHE_1112_CHEMISTRY_LAB.name(), CourseType.LAB, Set.of(TN.SALEHIN_MAHBUB.name()),
				Set.of(s18A.id), Set.of(), LabType.GENERAL);

		Course c43 = new Course(CN.EEE_1211_ELECTRICAL_CIRCUIT_ANALYSIS.name(), CourseType.THEORY,
				Set.of(TN.NUR_MUHAMMAD_FAHAD.name()), Set.of(s14A.id, s14B.id), Set.of(), null);

		Course c44 = new Course(CN.EEE_1212_ELECTRICAL_CIRCUIT_ANALYSIS_LAB.name(), CourseType.LAB,
				Set.of(TN.NUR_MUHAMMAD_FAHAD.name()), Set.of(s14A.id, s14B.id), Set.of(), LabType.ELECTRONIC);

		List<Course> courses = List.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18,
				c19, c20, c21, c22, c23, c24, c25, c26, c27, c28, c29, c30, c31, c32, c33, c34, c35, c36, c37, c38, c39,
				c40, c41, c42, c43, c44);

		Map<String, Section> sections = Map.ofEntries(Map.entry(s18A.id, s18A), Map.entry(s18B.id, s18B),
				Map.entry(s17A.id, s17A), Map.entry(s17B.id, s17B), Map.entry(s16A.id, s16A), Map.entry(s16B.id, s16B),
				Map.entry(s15A.id, s15A), Map.entry(s15B.id, s15B), Map.entry(s15C.id, s15C), Map.entry(s14A.id, s14A),
				Map.entry(s14B.id, s14B), Map.entry(s13A.id, s13A), Map.entry(s13B.id, s13B),
				Map.entry(s11_12A.id, s11_12A), Map.entry(s11_12B.id, s11_12B), Map.entry(s11_12C.id, s11_12C));

		Map<String, Teacher> teachers = Map.ofEntries(Map.entry(t1.id, t1), Map.entry(t2.id, t2), Map.entry(t3.id, t3),
				Map.entry(t4.id, t4), Map.entry(t5.id, t5), Map.entry(t6.id, t6), Map.entry(t7.id, t7),
				Map.entry(t8.id, t8), Map.entry(t9.id, t9), Map.entry(t10.id, t10), Map.entry(t11.id, t11),
				Map.entry(t12.id, t12), Map.entry(t13.id, t13), Map.entry(t14.id, t14), Map.entry(t15.id, t15),
				Map.entry(t16.id, t16), Map.entry(t17.id, t17), Map.entry(t18.id, t18), Map.entry(t19.id, t19),
				Map.entry(t20.id, t20), Map.entry(t21.id, t21), Map.entry(t22.id, t22), Map.entry(t23.id, t23),
				Map.entry(t24.id, t24), Map.entry(t25.id, t25), Map.entry(t26.id, t26));

		Map<String, Room> rooms = Map.ofEntries(Map.entry(r1.id, r1), Map.entry(r2.id, r2), Map.entry(r3.id, r3),
				Map.entry(r4.id, r4), Map.entry(r5.id, r5), Map.entry(l1.id, l1), Map.entry(l2.id, l2),
				Map.entry(l3.id, l3), Map.entry(l4.id, l4), Map.entry(l5.id, l5)

				, Map.entry(r6.id, r6), Map.entry(r7.id, r7), Map.entry(r8.id, r8), Map.entry(r9.id, r9)

		);

		List<Variable> vars = generateVariables(courses, sections);
		generateDomains(vars, teachers, rooms);

		// Debug output
		for (Variable v : vars) {
			System.out.println(v.id + " → domain size: " + v.domain.size());
		}

		// Debug Output
		for (Variable v : vars) {
			for (Value val : v.domain) {
				if ((val.slotMask & ((1L << 6) | (1L << 7))) != 0) {
					System.out.println(" *** Lunch violation: " + v.id);
				}
			}
		}

		CSPState state = new CSPState(teachers, rooms, sections);

		for (Variable v : vars) {
			state.variables.put(v.id, v);
		}
		buildNeighbors(vars);

		CSPSolver.reset();
		boolean solved = CSPSolver.solve(state);

		if (!CSPSolver.getBestAssignment().isEmpty()) {
			System.out.println("Solution FOUND");
		} else {
			System.out.println("NO solution exists");
		}

		int countAssigned = 0;
		int countUnassigned = 0;
		int countUnassignedTheory = 0;
		int countUnassignedLab = 0;
		int countUnasignedLabOrientedTheory = 0;
		ArrayList<String>unassigedVarId=new ArrayList<String>();
		for (var a : state.variables.values()) {
			if (a.assigned) {
				countAssigned++;
			} else {
				countUnassigned++;
				if (a.course.type == CourseType.LAB) {
					countUnassignedLab++;
				} else if (a.course.type == CourseType.THEORY) {
					countUnassignedTheory++;
				} else {
					countUnasignedLabOrientedTheory++;
				}
				unassigedVarId.add(a.id);
			}
		}

//		 restore best found assignment
		if (!CSPSolver.getBestAssignment().isEmpty()) {

			// clear occupation maps first
			state.clearOccupations();

			for (Variable v : state.variables.values()) {
				v.assigned = true;
				v.assignedValue = CSPSolver.getBestAssignment().get(v.id);

				Value val = v.assignedValue;

				state.teacherOccupied.get(val.teacherId)[val.day] |= val.slotMask;
				state.roomOccupied.get(val.roomId)[val.day] |= val.slotMask;
				state.sectionOccupied.get(v.section.id)[val.day] |= val.slotMask;
			}

			solved = true;
		}

		System.out.println("\nSolved: " + solved);
		System.out.println("Timeout: " + Main.timeout);
		System.out.println("BestAssignment size: " + CSPSolver.getBestAssignment().size());

		if (CSPSolver.getBestAssignment().isEmpty()) {
			System.out.println("Total Variable= " + state.variables.size());
			System.out.println("Total Assigned Variable= " + countAssigned);
			System.out.println("Total Unassigned Variable= " + countUnassigned);
			System.out.println("Total Unassigned Lab Variable= " + countUnassignedLab);
			System.out.println("Total Unassigned Theory Variable= " + countUnassignedTheory);
			System.out.println("Total Unassigned Lab_oriented_theory Variable= " + countUnasignedLabOrientedTheory);
			for( var id : unassigedVarId) {
				System.out.println(id);
			}

		} else {
			System.out.println("⚠️ Timeout reached — best found solution used");
			RoutinePrinter.print(state);
		}

	}

}
