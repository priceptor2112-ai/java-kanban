import java.util.List;

public class TimetableSimpleTest {

    public static void main(String[] args) {
        testGetTrainingSessionsForDaySingleSession();
        testGetTrainingSessionsForDayMultipleSessions();
        testGetTrainingSessionsForDayAndTime();
        testMultipleSessionsSameTime();
        testGetCountByCoachesSingleCoach();
        testGetCountByCoachesMultipleCoaches();
        testEmptyTimetable();

        System.out.println("Все тесты пройдены успешно!");
    }

    static void testGetTrainingSessionsForDaySingleSession() {
        Timetable timetable = new Timetable();
        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        List<TrainingSession> mondaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        assert mondaySessions.size() == 1 : "Должна быть 1 тренировка";
        assert mondaySessions.get(0).equals(singleTrainingSession) : "Должна быть та же тренировка";

        List<TrainingSession> tuesdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);
        assert tuesdaySessions.isEmpty() : "Список должен быть пустым";

        System.out.println("testGetTrainingSessionsForDaySingleSession: PASSED");
    }

    static void testGetTrainingSessionsForDayMultipleSessions() {
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
        assert mondaySessions.size() == 1 : "За понедельник должна быть 1 тренировка";

        List<TrainingSession> thursdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY);
        assert thursdaySessions.size() == 2 : "За четверг должно быть 2 тренировки";

        List<TrainingSession> tuesdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);
        assert tuesdaySessions.isEmpty() : "За вторник не должно быть тренировок";

        System.out.println("testGetTrainingSessionsForDayMultipleSessions: PASSED");
    }

    static void testGetTrainingSessionsForDayAndTime() {
        Timetable timetable = new Timetable();
        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        List<TrainingSession> sessionsAt1300 = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        assert sessionsAt1300.size() == 1 : "Должна быть 1 тренировка в 13:00";

        List<TrainingSession> sessionsAt1400 = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(14, 0));
        assert sessionsAt1400.isEmpty() : "Не должно быть тренировок в 14:00";

        System.out.println("testGetTrainingSessionsForDayAndTime: PASSED");
    }

    static void testMultipleSessionsSameTime() {
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
        assert sessions.size() == 2 : "Должно быть 2 тренировки в одно время";

        System.out.println("testMultipleSessionsSameTime: PASSED");
    }

    static void testGetCountByCoachesSingleCoach() {
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
        assert counts.size() == 1 : "Должен быть 1 тренер";
        assert counts.get(0).getCount() == 3 : "Должно быть 3 тренировки";

        System.out.println("testGetCountByCoachesSingleCoach: PASSED");
    }

    static void testGetCountByCoachesMultipleCoaches() {
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
        assert counts.size() == 3 : "Должно быть 3 тренера";
        assert counts.get(0).getCount() == 5 : "Первый тренер должен иметь 5 тренировок";
        assert counts.get(1).getCount() == 3 : "Второй тренер должен иметь 3 тренировки";
        assert counts.get(2).getCount() == 1 : "Третий тренер должен иметь 1 тренировку";

        System.out.println("testGetCountByCoachesMultipleCoaches: PASSED");
    }

    static void testEmptyTimetable() {
        Timetable timetable = new Timetable();

        List<TrainingSession> sessions = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        assert sessions.isEmpty() : "Расписание должно быть пустым";

        List<TrainingSession> specificSessions = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(10, 0));
        assert specificSessions.isEmpty() : "Не должно быть тренировок в указанное время";

        List<Timetable.CoachTrainingCount> counts = timetable.getCountByCoaches();
        assert counts.isEmpty() : "Статистика по тренерам должна быть пустой";

        System.out.println("testEmptyTimetable: PASSED");
    }
}