package taskmanager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends taskmanager.model.Task {
    private final List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description, Status status) {
        super(title, description, status, Duration.ZERO, null);
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove((Integer) id);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void calcTime(List<Subtask> subs) {
        if (subs == null || subs.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
            this.endTime = null;
            return;
        }

        LocalDateTime early = null;
        LocalDateTime late = null;
        Duration total = Duration.ZERO;

        for (Subtask sub : subs) {
            if (sub.getStartTime() != null) {
                if (early == null || sub.getStartTime().isBefore(early)) {
                    early = sub.getStartTime();
                }

                LocalDateTime end = sub.getEndTime();
                if (end != null && (late == null || end.isAfter(late))) {
                    late = end;
                }
            }

            if (sub.getDuration() != null) {
                total = total.plus(sub.getDuration());
            }
        }

        this.startTime = early;
        this.duration = total;
        this.endTime = late;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, endTime);
    }

    @Override
    public String toString() {
        long mins = duration != null ? duration.toMinutes() : 0;
        return String.format("Epic{id=%d, title='%s', status=%s, duration=%d, startTime=%s, subtasks=%d}",
                id, title, status, mins, startTime, subtaskIds.size());
    }
}