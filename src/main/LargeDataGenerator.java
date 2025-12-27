//package main;
//
//import java.util.Random;
//
//public class LargeDataGenerator {
//
//    public static InputData generate(
//            int sectionCount,
//            int coursePerSection,
//            int teacherCount,
//            int theoryRooms,
//            int labRooms) {
//
//        InputData d = new InputData();
//        Random rand = new Random(42); // fixed seed
//
//        // ---------- Sections ----------
//        for (int i = 1; i <= sectionCount; i++) {
//            int students = 30 + rand.nextInt(15);
//            d.sections.put("S" + i, new Section("S" + i, students));
//        }
//
//        // ---------- Rooms ----------
//        for (int i = 1; i <= theoryRooms; i++) {
//            d.rooms.put("T" + i,
//                new Room("T" + i, RoomType.THEORY, 120, 5, 16));
//        }
//
//        for (int i = 1; i <= labRooms; i++) {
//            d.rooms.put("L" + i,
//                new Room("L" + i, RoomType.LAB, 40, 5, 16));
//        }
//
//        // ---------- Teachers ----------
//        for (int i = 1; i <= teacherCount; i++) {
//            Teacher t = new Teacher("TCH" + i, 5, 16);
//            d.teachers.put(t.id, t);
//        }
//
//        // ---------- Courses ----------
//        int cid = 1;
//        CourseType[] types = CourseType.values();
//
//        for (Section s : d.sections.values()) {
//            for (int j = 0; j < coursePerSection; j++) {
//
//                CourseType type = types[rand.nextInt(types.length)];
//                Teacher t = pickTeacher(d.teachers.values(), rand);
//
//                Course c = new Course(
//                        "C" + cid++,
//                        type,
//                        t.id,
//                        s.id
//                );
//
//                t.qualifiedCourseIds.add(c.id);
//                d.courses.add(c);
//            }
//        }
//
//        return d;
//    }
//
//    private static Teacher pickTeacher(Collection<Teacher> list, Random r) {
//        int idx = r.nextInt(list.size());
//        return list.stream().skip(idx).findFirst().get();
//    }
//}
