import java.util.List;

public class Practicum {
    public static void main(String[] args) {
        // Создаем расписание
        Timetable timetable = new Timetable();

        // Создаем тренеров
        Coach coach1 = new Coach("Иванов", "Иван", "Иванович");
        Coach coach2 = new Coach("Петрова", "Мария", "Сергеевна");
        Coach coach3 = new Coach("Сидоров", "Алексей", "Владимирович");

        // Создаем группы
        Group yogaAdults = new Group("Йога для взрослых", Age.ADULT, 60);
        Group acroKids = new Group("Акробатика для детей", Age.CHILD, 45);
        Group fitnessAdults = new Group("Фитнес", Age.ADULT, 90);

        // Добавляем тренировки в расписание
        timetable.addNewTrainingSession(new TrainingSession(yogaAdults, coach1,
                DayOfWeek.MONDAY, new TimeOfDay(9, 0)));
        timetable.addNewTrainingSession(new TrainingSession(acroKids, coach2,
                DayOfWeek.MONDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(fitnessAdults, coach3,
                DayOfWeek.MONDAY, new TimeOfDay(18, 0)));

        timetable.addNewTrainingSession(new TrainingSession(yogaAdults, coach1,
                DayOfWeek.WEDNESDAY, new TimeOfDay(9, 0)));
        timetable.addNewTrainingSession(new TrainingSession(acroKids, coach2,
                DayOfWeek.WEDNESDAY, new TimeOfDay(10, 0)));

        timetable.addNewTrainingSession(new TrainingSession(fitnessAdults, coach3,
                DayOfWeek.FRIDAY, new TimeOfDay(18, 0)));

        // Демонстрация работы программы

        System.out.println("=== РАСПИСАНИЕ ТРЕНИРОВОК ===");
        System.out.println();

        // 1. Показать все тренировки в понедельник
        System.out.println("Все тренировки в понедельник:");
        List<TrainingSession> mondaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        for (TrainingSession session : mondaySessions) {
            System.out.println("- " + session.getGroup().getTitle() +
                    " в " + String.format("%02d:%02d",
                    session.getTimeOfDay().getHours(),
                    session.getTimeOfDay().getMinutes()) +
                    ", тренер: " + session.getCoach().getSurname());
        }
        System.out.println();

        // 2. Показать тренировки в понедельник в 10:00
        System.out.println("Тренировки в понедельник в 10:00:");
        List<TrainingSession> monday10am = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(10, 0));
        for (TrainingSession session : monday10am) {
            System.out.println("- " + session.getGroup().getTitle() +
                    ", тренер: " + session.getCoach().getSurname());
        }
        System.out.println();

        // 3. Показать статистику по тренерам
        System.out.println("Статистика по тренерам (количество тренировок в неделю):");
        List<Timetable.CoachTrainingCount> coachStats = timetable.getCountByCoaches();
        for (Timetable.CoachTrainingCount stat : coachStats) {
            System.out.println("- " + stat.getCoach().getSurname() + " " +
                    stat.getCoach().getName() + ": " + stat.getCount() + " тренировок");
        }

        System.out.println("\n=== ТЕСТИРОВАНИЕ ===");
        // Запускаем тесты
        TestRunner.runTests();
    }
}