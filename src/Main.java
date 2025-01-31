import com.google.gson.*;
import com.google.gson.stream.*;
import model.*;
import server.BaseHttpHandler;
import service.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    static TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

    public static void main(String[] args) {

        Task task = new Task("Task 1", "Description 1", LocalDateTime.now(), Duration.ZERO);
        Task task2 = new Task("Task 2", "Description 2", LocalDateTime.now(), Duration.ZERO);
        Epic epic = new Epic("Epic 1", "Description 1", LocalDateTime.now(), Duration.ZERO);
        taskManager.addTask(task);
        System.out.println(taskManager.getTask(task.getId()));
        Gson gson = new GsonBuilder()

                        .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .registerTypeAdapter(Duration.class, new DurationAdapter())
                        .create();
        String jsonString = gson.toJson(task);
        System.out.println(jsonString);
        taskManager.removeTask(task.getId());
        task = gson.fromJson(jsonString, Task.class);
        System.out.println(task);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId(), LocalDateTime.now(), Duration.ZERO);
        taskManager.addSubtask(subtask);
        String jsonSubtask = gson.toJson(subtask);
        System.out.println(jsonSubtask);

        List<Task> tasks = taskManager.getTasks();
        String jsonList = gson.toJson(tasks);
        System.out.println(jsonList);
        String jsonEpic = gson.toJson(epic);
        System.out.println(jsonEpic);
        taskManager.removeEpic(epic.getId());
        epic = gson.fromJson(jsonEpic, Epic.class);
        taskManager.addEpic(epic);
        System.out.println(epic);


    }

    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
            if (localDate == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(localDate.format(dtf));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            String dateString = jsonReader.nextString();
            if (dateString == null) {
                return null;
            }
            return LocalDateTime.parse(dateString, dtf);
        }
    }



    static class SubtaskAdapter implements JsonSerializer<Subtask> {
        @Override
        public JsonElement serialize(Subtask subtask, Type type, JsonSerializationContext context) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("type", subtask.getType().toString());
            jsonObj.addProperty("id", subtask.getId());
            jsonObj.addProperty("status", subtask.getStatus().toString());
            jsonObj.addProperty("epicId", subtask.getEpicId());
            jsonObj.addProperty("name", subtask.getTitle());
            jsonObj.addProperty("description", subtask.getDescription());
            jsonObj.add("startTime", context.serialize(subtask.getStartTime()));
            jsonObj.add("durationInMinutes", context.serialize(subtask.getDuration()));
            return jsonObj;
        }
    }

    static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(duration.toMinutes());
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            String durationString = jsonReader.nextString();
            if (durationString == null) {
                return null;
            }
            return Duration.ofMinutes(Integer.parseInt(durationString));
        }
    }

}