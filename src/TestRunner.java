import java.util.List;

public class TestRunner {

    public static void runTests() {
        testSingleSession();
        testMultipleSessions();
        testSessionByTime();
        testMultipleAtSameTime();
        testSingleCoachStats();
        testMultipleCoachesStats();
        testEmptyTimetable();

        System.out.println("✓ Все тесты пройдены успешно!");
    }

    static void testSingleSession() {
        System.out.println("\nТест 1: Одна тренировка в понедельник");
        Timetable timetable = new Timetable();
        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        List<TrainingSession> mondaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        if (mondaySessions.size() != 1) {
            throw new AssertionError("Должна быть 1 тренировка");
        }
        if (!mondaySessions.get(0).equals(singleTrainingSession)) {
            throw new AssertionError("Должна быть та же тренировка");
        }

        List<TrainingSession> tuesdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);
        if (!tuesdaySessions.isEmpty()) {
            throw new AssertionError("Список должен быть пустым");
        }

        System.out.println("✓ PASSED");
    }

    static void testMultipleSessions() {
        System.out.println("\nТест 2: Несколько тренировок в разные дни");
        Timetable timetable = new Timetable();
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");

        Group groupAdult = new Group("Акробатика для взрослых", Age.ADULT, 90);
        TrainingSession thursdayAdultTrainingSession = new TrainingSession(groupAdult, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(20, 0));

        timetable.addNewTrainingSession(thursdayAdultTrainingSession);

        Group groupChild = new Group("Акробатика для детей", Age.CHILD, 60);
        TrainingSession mondayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        TrainingSession thursdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(13, 0));
        TrainingSession saturdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.SATURDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(mondayChildTrainingSession);
        timetable.addNewTrainingSession(thursdayChildTrainingSession);
        timetable.addNewTrainingSession(saturdayChildTrainingSession);

        List<TrainingSession> mondaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        if (mondaySessions.size() != 1) {
            throw new AssertionError("За понедельник должна быть 1 тренировка");
        }

        List<TrainingSession> thursdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY);
        if (thursdaySessions.size() != 2) {
            throw new AssertionError("За четверг должно быть 2 тренировки");
        }

        List<TrainingSession> tuesdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);
        if (!tuesdaySessions.isEmpty()) {
            throw new AssertionError("За вторник не должно быть тренировок");
        }

        System.out.println("✓ PASSED");
    }

    static void testSessionByTime() {
        System.out.println("\nТест 3: Поиск тренировок по времени");
        Timetable timetable = new Timetable();
        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        List<TrainingSession> sessionsAt1300 = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        if (sessionsAt1300.size() != 1) {
            throw new AssertionError("Должна быть 1 тренировка в 13:00");
        }

        List<TrainingSession> sessionsAt1400 = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(14, 0));
        if (!sessionsAt1400.isEmpty()) {
            throw new AssertionError("Не должно быть тренировок в 14:00");
        }

        System.out.println("✓ PASSED");
    }

    static void testMultipleAtSameTime() {
        System.out.println("\nТест 4: Несколько тренировок в одно время");
        Timetable timetable = new Timetable();
        Coach coach1 = new Coach("Иванов", "Иван", "Иванович");
        Coach coach2 = new Coach("Петров", "Петр", "Петрович");

        Group group1 = new Group("Йога", Age.ADULT, 60);
        Group group2 = new Group("Пилатес", Age.ADULT, 60);

        TrainingSession session1 = new TrainingSession(group1, coach1,
                DayOfWeek.MONDAY, new TimeOfDay(10, 0));
        TrainingSession session2 = new TrainingSession(group2, coach2,
                DayOfWeek.MONDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(session1);
        timetable.addNewTrainingSession(session2);

        List<TrainingSession> sessions = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(10, 0));
        if (sessions.size() != 2) {
            throw new AssertionError("Должно быть 2 тренировки в одно время");
        }

        System.out.println("✓ PASSED");
    }

    static void testSingleCoachStats() {
        System.out.println("\nТест 5: Статистика для одного тренера");
        Timetable timetable = new Timetable();
        Coach coach = new Coach("Сидоров", "Алексей", "Владимирович");
        Group group = new Group("Стретчинг", Age.ADULT, 45);

        timetable.addNewTrainingSession(new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(9, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach,
                DayOfWeek.WEDNESDAY, new TimeOfDay(9, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach,
                DayOfWeek.FRIDAY, new TimeOfDay(9, 0)));

        List<Timetable.CoachTrainingCount> counts = timetable.getCountByCoaches();
        if (counts.size() != 1) {
            throw new AssertionError("Должен быть 1 тренер");
        }
        if (counts.get(0).getCount() != 3) {
            throw new AssertionError("Должно быть 3 тренировки");
        }

        System.out.println("✓ PASSED");
    }

    static void testMultipleCoachesStats() {
        System.out.println("\nТест 6: Статистика для нескольких тренеров");
        Timetable timetable = new Timetable();
        Coach coach1 = new Coach("Иванов", "Иван", "Иванович");
        Coach coach2 = new Coach("Петров", "Петр", "Петрович");
        Coach coach3 = new Coach("Сидоров", "Сергей", "Сергеевич");

        Group group = new Group("Фитнес", Age.ADULT, 60);

        timetable.addNewTrainingSession(new TrainingSession(group, coach1, DayOfWeek.MONDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach1, DayOfWeek.TUESDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach1, DayOfWeek.WEDNESDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach1, DayOfWeek.THURSDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach1, DayOfWeek.FRIDAY, new TimeOfDay(10, 0)));

        timetable.addNewTrainingSession(new TrainingSession(group, coach2, DayOfWeek.MONDAY, new TimeOfDay(18, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach2, DayOfWeek.WEDNESDAY, new TimeOfDay(18, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach2, DayOfWeek.FRIDAY, new TimeOfDay(18, 0)));

        timetable.addNewTrainingSession(new TrainingSession(group, coach3, DayOfWeek.SATURDAY, new TimeOfDay(12, 0)));

        List<Timetable.CoachTrainingCount> counts = timetable.getCountByCoaches();
        if (counts.size() != 3) {
            throw new AssertionError("Должно быть 3 тренера");
        }
        if (counts.get(0).getCount() != 5) {
            throw new AssertionError("Первый тренер должен иметь 5 тренировок");
        }
        if (counts.get(1).getCount() != 3) {
            throw new AssertionError("Второй тренер должен иметь 3 тренировки");
        }
        if (counts.get(2).getCount() != 1) {
            throw new AssertionError("Третий тренер должен иметь 1 тренировку");
        }

        System.out.println("✓ PASSED");
    }

    static void testEmptyTimetable() {
        System.out.println("\nТест 7: Пустое расписание");
        Timetable timetable = new Timetable();

        List<TrainingSession> sessions = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        if (!sessions.isEmpty()) {
            throw new AssertionError("Расписание должно быть пустым");
        }

        List<TrainingSession> specificSessions = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(10, 0));
        if (!specificSessions.isEmpty()) {
            throw new AssertionError("Не должно быть тренировок в указанное время");
        }

        List<Timetable.CoachTrainingCount> counts = timetable.getCountByCoaches();
        if (!counts.isEmpty()) {
            throw new AssertionError("Статистика по тренерам должна быть пустой");
        }

        System.out.println("✓ PASSED");
    }
}