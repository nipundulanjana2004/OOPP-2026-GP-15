package common;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

public class SampleDataSeeder {

    private static final String[][] COURSES = {
            {"ICT1113", "Programming Fundamentals", "2", "1", "1", "ICT"},
            {"ICT1122", "Mathematics for Computing", "2", "0", "1", "ICT"},
            {"ICT1213", "Web Development Basics", "1", "1", "2", "ICT"},
            {"ICT1222", "Computer Organization", "2", "0", "2", "ICT"},
            {"ICT2112", "Data Structures and Algorithms", "2", "1", "3", "ICT"},
            {"ICT2122", "Database Management Systems", "2", "1", "3", "ICT"},
            {"ICT2132", "Object Oriented Programming Practicum", "0", "2", "3", "ICT"},
            {"ICT2152", "Computer Networks", "2", "0", "3", "ICT"},
            {"ICT2162", "Management Information Systems", "2", "0", "3", "ICT"},
            {"ET1112", "Engineering Mathematics", "2", "0", "1", "ET"},
            {"ET1212", "Engineering Drawing", "1", "1", "2", "ET"},
            {"ET2112", "Circuit Theory", "2", "1", "3", "ET"},
            {"ET2122", "Engineering Materials", "2", "0", "3", "ET"},
            {"BST1112", "Biology for Technology", "2", "0", "1", "BST"},
            {"BST1212", "Chemistry Basics", "1", "1", "2", "BST"},
            {"BST2112", "Biochemistry", "2", "1", "3", "BST"},
            {"BST2122", "Plant Science", "2", "0", "3", "BST"}
    };
    private static final int CURRENT_SEMESTER = 3;
    private static final String UNPUBLISHED_FINAL_COURSE = "ICT2162";   // final exam not held yet -> grades "Pending"
    private static final LocalDate SEMESTER_START = LocalDate.of(2026, 7, 6);

    private static final String[][] STUDENTS = {
            {"Kavindu", "Perera", "ICT", "PROPER", "82"}, {"Sanduni", "Silva", "ICT", "PROPER", "76"},
            {"Nimal", "Fernando", "ICT", "PROPER", "68"}, {"Tharushi", "Jayawardena", "ICT", "PROPER", "91"},
            {"Dinuka", "Rajapaksha", "ICT", "PROPER", "55"}, {"Hansini", "Weerasinghe", "ICT", "PROPER", "73"},
            {"Lahiru", "Bandara", "ICT", "PROPER", "64"}, {"Chathurika", "Dissanayake", "ICT", "PROPER", "38"},
            {"Isuru", "Gunawardena", "ICT", "PROPER", "59"}, {"Methmi", "Senanayake", "ICT", "PROPER", "87"},
            {"Pasindu", "Abeysekara", "ICT", "PROPER", "71"}, {"Rashmi", "Karunaratne", "ICT", "PROPER", "66"},
            {"Sachith", "Amarasinghe", "ICT", "REPEAT", "45"}, {"Dilshan", "Wickramasinghe", "ICT", "REPEAT", "52"},
            {"Vindya", "Ekanayake", "ICT", "BATCH_MISSED", "79"}, {"Yasiru", "Jayasinghe", "ICT", "BATCH_MISSED", "61"},
            {"Oshada", "Liyanage", "ET", "PROPER", "74"}, {"Praveen", "Kulasekara", "ET", "PROPER", "58"},
            {"Sewwandi", "Mendis", "BST", "PROPER", "83"}, {"Anuradha", "Pathirana", "BST", "PROPER", "67"}
    };
    private static final String[] REASONS = {"Fever and flu", "Dengue - hospital admission", "Food poisoning",
            "Minor surgery recovery", "Viral infection", "Severe migraine"};

    private static final Random RND = new Random(2026);

    private SampleDataSeeder() {
    }

    private static int insert(Connection c, String sql, Object... p) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < p.length; i++) ps.setObject(i + 1, p[i]);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                return k.next() ? k.getInt(1) : 0;
            }
        }
    }

    private static int addUser(Connection c, String username, String pw, String first, String last, Role role, String tel) throws SQLException {
        String email = (first + "." + last).toLowerCase() + "@fot.ruh.ac.lk";
        return insert(c, "INSERT INTO app_user (username,password_hash,first_name,last_name,email,contact_number,address,profile_picture,role,is_active,created_date) VALUES (?,?,?,?,?,?,?,?,?,1,?)",
                username, PasswordUtil.hash(pw), first, last, email, tel, "Matara, Sri Lanka", "", role.name(), Date.valueOf(LocalDate.of(2026, 6, 1)));
    }

    private static int clamp(double v) {
        return (int) Math.max(0, Math.min(100, Math.round(v)));
    }


    public static void seed(Connection c) throws DatabaseException {
        try {
            c.setAutoCommit(false);
            doSeed(c);
            c.commit();
        } catch (SQLException e) {
            try { c.rollback(); } catch (SQLException ignored) { /* nothing to do */ }
            throw new DatabaseException("Could not insert the sample data: " + e.getMessage(), e);
        } finally {
            try { c.setAutoCommit(true); } catch (SQLException ignored) { /* nothing to do */ }
        }
    }

    private static void doSeed(Connection c) throws SQLException {
        Object[][] grades = {{"A+", 4.0, 85, 100}, {"A", 4.0, 75, 84.99}, {"A-", 3.7, 70, 74.99}, {"B+", 3.3, 65, 69.99},
                {"B", 3.0, 60, 64.99}, {"B-", 2.7, 55, 59.99}, {"C+", 2.3, 50, 54.99}, {"C", 2.0, 45, 49.99},
                {"C-", 1.7, 40, 44.99}, {"D+", 1.3, 35, 39.99}, {"D", 1.0, 30, 34.99}, {"E", 0.0, 0, 29.99}};
        for (Object[] g : grades) {
            insert(c, "INSERT INTO grade_scale (grade,grade_point,min_marks,max_marks) VALUES (?,?,?,?)", g[0], g[1], g[2], g[3]);
        }

        Map<String, Integer> dept = new LinkedHashMap<>();
        dept.put("ICT", insert(c, "INSERT INTO department (department_code,department_name) VALUES ('ICT','Information & Communication Technology')"));
        dept.put("ET", insert(c, "INSERT INTO department (department_code,department_name) VALUES ('ET','Engineering Technology')"));
        dept.put("BST", insert(c, "INSERT INTO department (department_code,department_name) VALUES ('BST','Bio-systems Technology')"));
        int batch2024 = insert(c, "INSERT INTO batch (batch_name,academic_year) VALUES ('Batch 2024','2024/2025')");
        int batch2023 = insert(c, "INSERT INTO batch (batch_name,academic_year) VALUES ('Batch 2023','2023/2024')");

        int adminId = addUser(c, "admin", "Admin@123", "Kamal", "Perera", Role.ADMIN, "0712345601");
        insert(c, "INSERT INTO admin (user_id) VALUES (?)", adminId);

        String[][] lecs = {{"Saman", "Wijesinghe", "Senior Lecturer", "ICT"}, {"Nilmini", "Fernando", "Lecturer", "ICT"},
                {"Ruwan", "Jayasuriya", "Senior Lecturer", "ICT"}, {"Dilani", "Rathnayake", "Lecturer", "ET"},
                {"Chamara", "Gunasekara", "Lecturer", "BST"}};
        int[] lecId = new int[5];
        for (int i = 0; i < 5; i++) {
            lecId[i] = addUser(c, "lecturer" + (i + 1), "Lecturer@123", lecs[i][0], lecs[i][1], Role.LECTURER, "07123457" + (10 + i));
            insert(c, "INSERT INTO lecturer (user_id,designation,department_id) VALUES (?,?,?)", lecId[i], lecs[i][2], dept.get(lecs[i][3]));
        }

        // ---------- technical officers (4) ----------
        String[][] tos = {{"Kasun", "Silva", "ICT"}, {"Ishara", "Madushani", "ICT"}, {"Pradeep", "Kumara", "ET"}, {"Thilini", "Herath", "BST"}};
        int[] toId = new int[4];
        Map<String, Integer> officerOfDept = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            toId[i] = addUser(c, "officer" + (i + 1), "Officer@123", tos[i][0], tos[i][1], Role.TECH_OFFICER, "07123458" + (10 + i));
            insert(c, "INSERT INTO technical_officer (user_id,department_id) VALUES (?,?)", toId[i], dept.get(tos[i][2]));
            officerOfDept.putIfAbsent(tos[i][2], toId[i]);
        }

        // ---------- courses + lecturer assignment ----------
        Map<String, Integer> courseId = new LinkedHashMap<>();
        Map<String, double[]> sessionHours = new HashMap<>();
        for (int i = 0; i < COURSES.length; i++) {
            String[] k = COURSES[i];
            int id = insert(c, "INSERT INTO course (course_code,course_name,theory_credits,practical_credits,semester,department_id) VALUES (?,?,?,?,?,?)",
                    k[0], k[1], Integer.parseInt(k[2]), Integer.parseInt(k[3]), Integer.parseInt(k[4]), dept.get(k[5]));
            courseId.put(k[0], id);
            int lec = k[5].equals("ICT") ? lecId[i % 3] : k[5].equals("ET") ? lecId[3] : lecId[4];
            insert(c, "INSERT INTO course_lecturer (course_id,lecturer_id,role_in_course) VALUES (?,?,'Coordinator')", id, lec);
            if (k[0].equals("ICT2132")) {   // second lecturer on the OOP practicum
                insert(c, "INSERT INTO course_lecturer (course_id,lecturer_id,role_in_course) VALUES (?,?,'Lecturer')", id, lecId[1]);
            }
        }

        // ---------- undergraduates (20) + registrations ----------
        int n = STUDENTS.length;
        int[] ugId = new int[n];
        for (int i = 0; i < n; i++) {
            String[] s = STUDENTS[i];
            boolean old = !s[3].equals("PROPER");
            ugId[i] = addUser(c, String.format("ug%02d", i + 1), "Student@123", s[0], s[1], Role.UNDERGRADUATE, "07712340" + (10 + i));
            String reg = String.format("TG/%d/%04d", old ? 2023 : 2024, 1001 + i);
            insert(c, "INSERT INTO undergraduate (user_id,registration_number,batch_id,department_id,academic_status) VALUES (?,?,?,?,?)",
                    ugId[i], reg, old ? batch2023 : batch2024, dept.get(s[2]), s[3]);
            for (String[] k : COURSES) {
                if (!k[5].equals(s[2])) continue;
                boolean current = Integer.parseInt(k[4]) == CURRENT_SEMESTER;
                insert(c, "INSERT INTO course_registration (undergraduate_id,course_id,academic_year,attempt_type) VALUES (?,?,?,?)",
                        ugId[i], courseId.get(k[0]), current ? "2025/2026" : "2024/2025", current ? s[3] : "PROPER");
            }
        }

        // ---------- evaluation components ----------
        Map<String, List<Object[]>> comps = new HashMap<>();   // code -> {id, type, weight}
        for (String[] k : COURSES) {
            int cid = courseId.get(k[0]);
            String[][] defs = Integer.parseInt(k[2]) == 0
                    ? new String[][]{{"Lab Assessment", "CA", "30"}, {"Mini Project", "CA", "30"}, {"Practical Exam", "FINAL", "40"}}
                    : new String[][]{{"Quiz 1", "CA", "10"}, {"Mid Exam", "CA", "20"}, {"Assignment", "CA", "10"}, {"Final Exam", "FINAL", "60"}};
            List<Object[]> list = new ArrayList<>();
            for (String[] d : defs) {
                int id = insert(c, "INSERT INTO evaluation_component (course_id,component_name,component_type,max_marks,weightage) VALUES (?,?,?,100,?)",
                        cid, d[0], d[1], Double.parseDouble(d[2]));
                list.add(new Object[]{id, d[1], Double.parseDouble(d[2])});
            }
            comps.put(k[0], list);
        }

        // ---------- sessions (15 theory + 15 practical) for the CURRENT semester ----------
        Map<String, int[]> sessionIds = new HashMap<>();
        Map<String, LocalDate[]> sessionDates = new HashMap<>();
        for (String[] k : COURSES) {
            if (Integer.parseInt(k[4]) != CURRENT_SEMESTER) continue;
            for (SessionType t : SessionType.values()) {
                int credits = Integer.parseInt(t == SessionType.THEORY ? k[2] : k[3]);
                if (credits == 0) continue;
                int[] ids = new int[15];
                LocalDate[] dates = new LocalDate[15];
                for (int s = 0; s < 15; s++) {
                    dates[s] = SEMESTER_START.plusWeeks(s).plusDays(t == SessionType.THEORY ? 0 : 2);
                    ids[s] = insert(c, "INSERT INTO class_session (course_id,session_type,session_number,session_date,duration_hours) VALUES (?,?,?,?,?)",
                            courseId.get(k[0]), t.name(), s + 1, Date.valueOf(dates[s]), 2.0 * credits);
                }
                sessionIds.put(k[0] + "|" + t, ids);
                sessionDates.put(k[0] + "|" + t, dates);
            }
        }

        // ---------- attendance + medicals (7 patterns rotate => every scenario appears in every course) ----------
        // 0: >80% (14/15)  1: exactly 80% (12/15)  2: <80% no medicals (9/15)  3: >80% with medicals
        // 4: <80% even with medicals  5: perfect  6: <80% with REJECTED / PENDING medicals
        int[] present = {14, 12, 9, 11, 8, 15, 10};
        String[][] medPlan = {{}, {}, {}, {"APPROVED", "APPROVED"}, {"APPROVED", "APPROVED"}, {}, {"REJECTED", "PENDING"}};
        Map<String, Boolean> attendanceOk = new HashMap<>();
        int courseIdx = 0;
        for (String[] k : COURSES) {
            if (Integer.parseInt(k[4]) != CURRENT_SEMESTER) continue;
            courseIdx++;
            int indexInDept = 0;
            for (int i = 0; i < n; i++) {
                if (!STUDENTS[i][2].equals(k[5])) continue;
                int pattern = (indexInDept + courseIdx) % 7;
                indexInDept++;
                double totalH = 0, okH = 0;
                for (SessionType t : SessionType.values()) {
                    int[] ids = sessionIds.get(k[0] + "|" + t);
                    if (ids == null) continue;
                    LocalDate[] dates = sessionDates.get(k[0] + "|" + t);
                    double hours = 2.0 * Integer.parseInt(t == SessionType.THEORY ? k[2] : k[3]);
                    List<Integer> order = new ArrayList<>();
                    for (int s = 0; s < 15; s++) order.add(s);
                    Collections.shuffle(order, RND);
                    Set<Integer> absent = new HashSet<>(order.subList(0, 15 - present[pattern]));
                    int medNo = 0;
                    int excused = 0;
                    for (int s = 0; s < 15; s++) {
                        Integer medId = null;
                        if (absent.contains(s)) {
                            String status = medNo < medPlan[pattern].length ? medPlan[pattern][medNo] : null;
                            medNo++;
                            if (status != null) {
                                medId = insert(c, "INSERT INTO medical (undergraduate_id,date_from,date_to,reason,document_path,approval_status,verified_by_officer_id,submitted_date) VALUES (?,?,?,?,?,?,?,?)",
                                        ugId[i], Date.valueOf(dates[s]), Date.valueOf(dates[s]), REASONS[RND.nextInt(REASONS.length)],
                                        "medicals/ug" + (i + 1) + "_" + k[0] + "_" + (s + 1) + ".pdf", status,
                                        status.equals("PENDING") ? null : officerOfDept.get(k[5]), Date.valueOf(dates[s].plusDays(1)));
                                if (status.equals("APPROVED")) excused++;
                            }
                        }
                        insert(c, "INSERT INTO attendance (undergraduate_id,session_id,status,recorded_by_officer_id,medical_id) VALUES (?,?,?,?,?)",
                                ugId[i], ids[s], absent.contains(s) ? "ABSENT" : "PRESENT", officerOfDept.get(k[5]), medId);
                    }
                    totalH += 15 * hours;
                    okH += (present[pattern] + excused) * hours;
                }
                attendanceOk.put(ugId[i] + "|" + k[0], okH / totalH * 100.0 + 1e-9 >= 80.0);
            }
        }

        // ---------- marks ----------
        for (String[] k : COURSES) {
            boolean current = Integer.parseInt(k[4]) == CURRENT_SEMESTER;
            for (int i = 0; i < n; i++) {
                if (!STUDENTS[i][2].equals(k[5])) continue;
                double ability = Double.parseDouble(STUDENTS[i][4]);
                if (!current) ability = Math.max(ability, 50);      // earlier semesters: everybody completed
                double caSum = 0, caW = 0;
                List<Object[]> cl = comps.get(k[0]);
                for (Object[] comp : cl) {
                    if (!"CA".equals(comp[1])) continue;
                    int mark = clamp(ability + RND.nextInt(19) - 9);
                    insert(c, "INSERT INTO mark (undergraduate_id,component_id,marks_obtained,uploaded_by_lecturer_id,upload_date) VALUES (?,?,?,?,?)",
                            ugId[i], comp[0], mark, lecId[0], Date.valueOf(LocalDate.of(2026, 9, 20)));
                    caSum += mark * (Double) comp[2];
                    caW += (Double) comp[2];
                }
                boolean caOk = caSum / caW + 1e-9 >= 40.0;
                boolean attOk = !current || attendanceOk.get(ugId[i] + "|" + k[0]);
                boolean finalHeld = !k[0].equals(UNPUBLISHED_FINAL_COURSE);
                if (caOk && attOk && finalHeld) {
                    for (Object[] comp : cl) {
                        if (!"FINAL".equals(comp[1])) continue;
                        insert(c, "INSERT INTO mark (undergraduate_id,component_id,marks_obtained,uploaded_by_lecturer_id,upload_date) VALUES (?,?,?,?,?)",
                                ugId[i], comp[0], clamp(ability + RND.nextInt(21) - 10), lecId[0], Date.valueOf(LocalDate.of(2026, 10, 5)));
                    }
                }
            }
        }

        // ---------- timetable (current semester, Batch 2024) ----------
        String[][] tt = {
                {"ICT2112", "THEORY", "Monday", "08:00", "12:00", "LH-01"}, {"ICT2112", "PRACTICAL", "Wednesday", "13:00", "15:00", "Lab-A"},
                {"ICT2122", "THEORY", "Tuesday", "08:00", "12:00", "LH-02"}, {"ICT2122", "PRACTICAL", "Thursday", "13:00", "15:00", "Lab-B"},
                {"ICT2132", "PRACTICAL", "Friday", "08:00", "12:00", "Lab-A"},
                {"ICT2152", "THEORY", "Wednesday", "08:00", "12:00", "LH-01"}, {"ICT2162", "THEORY", "Thursday", "08:00", "12:00", "LH-03"},
                {"ET2112", "THEORY", "Monday", "13:00", "17:00", "ET-Hall"}, {"ET2112", "PRACTICAL", "Tuesday", "13:00", "15:00", "ET-Lab"},
                {"ET2122", "THEORY", "Wednesday", "13:00", "17:00", "ET-Hall"},
                {"BST2112", "THEORY", "Monday", "08:00", "12:00", "BST-Hall"}, {"BST2112", "PRACTICAL", "Thursday", "13:00", "15:00", "BST-Lab"},
                {"BST2122", "THEORY", "Tuesday", "08:00", "12:00", "BST-Hall"}};
        Map<String, String> deptOfCourse = new HashMap<>();
        for (String[] k : COURSES) deptOfCourse.put(k[0], k[5]);
        for (String[] t : tt) {
            insert(c, "INSERT INTO timetable (course_id,session_type,day_of_week,start_time,end_time,venue,batch_id,department_id,admin_id) VALUES (?,?,?,?,?,?,?,?,?)",
                    courseId.get(t[0]), t[1], t[2], t[3] + ":00", t[4] + ":00", t[5], batch2024, dept.get(deptOfCourse.get(t[0])), adminId);
        }

        // ---------- course materials ----------
        insert(c, "INSERT INTO course_material (course_id,lecturer_id,title,file_path,upload_date) VALUES (?,?,?,?,?)", courseId.get("ICT2112"), lecId[0], "Lecture 01 - Arrays and Lists", "materials/ICT2112_L01.pdf", Date.valueOf("2026-07-07"));
        insert(c, "INSERT INTO course_material (course_id,lecturer_id,title,file_path,upload_date) VALUES (?,?,?,?,?)", courseId.get("ICT2122"), lecId[1], "ER Diagrams Slides", "materials/ICT2122_ER.pdf", Date.valueOf("2026-07-08"));
        insert(c, "INSERT INTO course_material (course_id,lecturer_id,title,file_path,upload_date) VALUES (?,?,?,?,?)", courseId.get("ICT2132"), lecId[0], "OOP Mini Project Brief", "materials/ICT2132_Mini_Project.pdf", Date.valueOf("2026-08-01"));

        // ---------- notices ----------
        Object[][] notices = {
                {"Welcome to Level II Semester I", "Lectures begin on 06 July 2026. Please check the timetable regularly.", "ALL", null, null, null},
                {"Mini Project submission", "ICT2132 mini project: report + Git repository due 01 November 2026, 11.59 p.m. Presentations on 05 November.", "ROLE", "UNDERGRADUATE", null, null},
                {"Medical submission", "Medical certificates must be handed to the Technical Officer within 7 days of the absence.", "ROLE", "UNDERGRADUATE", null, null},
                {"Marks entry deadline", "Lecturers: please upload all CA marks before the end of October.", "ROLE", "LECTURER", null, null},
                {"ICT lab maintenance", "Labs A and B will be closed on Saturday for maintenance.", "DEPARTMENT", null, dept.get("ICT"), null},
                {"Batch 2023 repeat students", "Repeat / batch-missed students: meet your department officer to confirm your registrations.", "BATCH", null, null, batch2023}};
        int day = 1;
        for (Object[] nt : notices) {
            insert(c, "INSERT INTO notice (title,content,target_type,target_role,department_id,batch_id,posted_by,posted_date) VALUES (?,?,?,?,?,?,?,?)",
                    nt[0], nt[1], nt[2], nt[3], nt[4], nt[5], adminId, Date.valueOf(LocalDate.of(2026, 10, day++)));
        }
    }
}
