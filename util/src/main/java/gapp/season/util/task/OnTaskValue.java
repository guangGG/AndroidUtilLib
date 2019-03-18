package gapp.season.util.task;

public interface OnTaskValue<T> {
    void onTaskDone(T returnValue);
}
