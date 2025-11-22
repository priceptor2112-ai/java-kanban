import java.util.*;

public class Timetable {
    private final Map<DayOfWeek, TreeMap<TimeOfDay, List<TrainingSession>>> timetable;

    public Timetable() {
        timetable = new EnumMap<>(DayOfWeek.class);

        for (DayOfWeek day : DayOfWeek.values()) {
            timetable.put(day, new TreeMap<>(
                    (time1, time2) -> {
                        if (time1.getHours() != time2.getHours()) {
                            return Integer.compare(time1.getHours(), time2.getHours());
                        }
                        return Integer.compare(time1.getMinutes(), time2.getMinutes());
                    }
            ));
        }
    }

    public void addNewTrainingSession(TrainingSession trainingSession) {
        DayOfWeek day = trainingSession.getDayOfWeek();
        TimeOfDay time = trainingSession.getTimeOfDay();

        TreeMap<TimeOfDay, List<TrainingSession>> daySchedule = timetable.get(day);

        daySchedule.computeIfAbsent(time, k -> new ArrayList<>())
                .add(trainingSession); // добавляем тренировку в список
    }

    public List<TrainingSession> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        TreeMap<TimeOfDay, List<TrainingSession>> daySchedule = timetable.get(dayOfWeek);
        List<TrainingSession> result = new ArrayList<>();

        for (List<TrainingSession> sessions : daySchedule.values()) {
            result.addAll(sessions);
        }

        return result;
    }

    public List<TrainingSession> getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        TreeMap<TimeOfDay, List<TrainingSession>> daySchedule = timetable.get(dayOfWeek);

        List<TrainingSession> sessions = daySchedule.get(timeOfDay);

        return sessions != null ? new ArrayList<>(sessions) : new ArrayList<>();
    }

    public List<CoachTrainingCount> getCountByCoaches() {
        Map<Coach, Integer> coachCounts = new HashMap<>();

        for (TreeMap<TimeOfDay, List<TrainingSession>> daySchedule : timetable.values()) {
            for (List<TrainingSession> sessions : daySchedule.values()) {
                for (TrainingSession session : sessions) {
                    Coach coach = session.getCoach();
                    coachCounts.put(coach, coachCounts.getOrDefault(coach, 0) + 1);
                }
            }
        }

        List<CoachTrainingCount> result = new ArrayList<>();

        for (Map.Entry<Coach, Integer> entry : coachCounts.entrySet()) {
            result.add(new CoachTrainingCount(entry.getKey(), entry.getValue()));
        }

        result.sort(Comparator.comparingInt(CoachTrainingCount::getCount).reversed());

        return result;
    }

    public static class CoachTrainingCount {
        private final Coach coach;
        private final int count;

        public CoachTrainingCount(Coach coach, int count) {
            this.coach = coach;
            this.count = count;
        }

        public Coach getCoach() {
            return coach;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return coach.getSurname() + " " + coach.getName() + " " + coach.getMiddleName() +
                    ": " + count + " тренировок";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CoachTrainingCount that = (CoachTrainingCount) o;
            return count == that.count && Objects.equals(coach, that.coach);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coach, count);
        }
    }
}