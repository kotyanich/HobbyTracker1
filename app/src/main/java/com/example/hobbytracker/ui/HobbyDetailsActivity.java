package com.example.hobbytracker.ui;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hobbytracker.listeners.OnIdeaDeleteListener;
import com.example.hobbytracker.adapters.IdeasAdapter;
import com.example.hobbytracker.listeners.OnTaskChangedListener;
import com.example.hobbytracker.adapters.ProjectsAdapter;
import com.example.hobbytracker.R;
import com.example.hobbytracker.adapters.TasksAdapter;
import com.example.hobbytracker.listeners.OnTaskDeleteListener;
import com.example.hobbytracker.models.TimeUtils;
import com.example.hobbytracker.data.db.AppDatabase;
import com.example.hobbytracker.data.model.DetailedHobby;
import com.example.hobbytracker.data.model.Idea;
import com.example.hobbytracker.data.model.Project;
import com.example.hobbytracker.data.model.ProjectWithTasks;
import com.example.hobbytracker.data.model.Task;
import com.example.hobbytracker.data.model.TimeEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HobbyDetailsActivity extends AppCompatActivity
        implements OnTaskChangedListener,
        OnTaskDeleteListener,
        OnIdeaDeleteListener {

    private RecyclerView tasksRecyclerView;
    RecyclerView ideasRecyclerView;
    private ViewPager2 projectsRecyclerView;
    private ImageView rightArrow, leftArrow;
    ImageView taskButtonIn, projectsButtonIn, addTask, addProj, deleteProject, addIdea;
    private FrameLayout taskLayout, projectLayout, taskInLayout, projectInLayout, taskButtonsLayout;
    private LinearLayout projectButtonsLayout;
    private TasksAdapter tasksAdapter;
    private ProjectsAdapter projectsAdapter;
    private IdeasAdapter ideasAdapter;
    private String projectName;
    private DetailedHobby currentHobby;
    private List<Task> hobbyTasks = new ArrayList<>();
    private final List<Idea> ideas = new ArrayList<>();
    private List<ProjectWithTasks> projects = new ArrayList<>();
    private ProjectWithTasks currentProject;
    private int currentProjectIndex = 0;
    private List<Task> projectTasks = new ArrayList<>();
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hobby_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // DATABASE INIT
        db = AppDatabase.getInstance(this);

        // RECYCLER VIEWS
        tasksRecyclerView = findViewById(R.id.taskItems);
        projectsRecyclerView = findViewById(R.id.projectsRecyclerView);
        ideasRecyclerView = findViewById(R.id.ideasItems);

        // OTHER VIEWS
        addTask = findViewById(R.id.addTask);
        TextView hobbyView = findViewById(R.id.hobby);
        ImageView logoView = findViewById(R.id.logohobby);
        ImageView addTimeView = findViewById(R.id.addTime);
        ImageView homeView = findViewById(R.id.home);
        ImageView shopView = findViewById(R.id.shop);
        ImageView profileView = findViewById(R.id.profile);
        deleteProject = findViewById(R.id.deleteProject);
        taskButtonIn = findViewById(R.id.taskGrey);
        projectsButtonIn = findViewById(R.id.projectGrey);
        rightArrow = findViewById(R.id.rightarrow);
        leftArrow = findViewById(R.id.leftarrow);
        addProj = findViewById(R.id.addProj);
        addIdea = findViewById(R.id.addIdea);
        taskLayout = findViewById(R.id.taskactive);
        taskInLayout = findViewById(R.id.tasksinnactive);
        projectLayout = findViewById(R.id.projactive);
        projectInLayout = findViewById(R.id.projinnactive);
        projectButtonsLayout = findViewById(R.id.projectbuttons);
        taskButtonsLayout = findViewById(R.id.taskaddbutton);

        // GET HOBBY DATA
        long hobbyId = getIntent().getLongExtra("hobbyId", -1);
        currentHobby = db.hobbyDao().getDetailedHobby(hobbyId);

        if (currentHobby == null) {
            Log.e("HobbyDetailsActivity", "Hobby not found for id = " + hobbyId);
            Toast.makeText(this, "Хобби не найдено", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        projects = currentHobby.projects;
        hobbyTasks = currentHobby.hobbyTasks;

        // SET HOBBY INFO IN UI
        hobbyView.setText(currentHobby.base.name);
        logoView.setImageResource(currentHobby.base.iconId);

        // HOBBY TASKS ADAPTER
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TasksAdapter(this, hobbyTasks, this, db);
        tasksRecyclerView.setAdapter(tasksAdapter);

        // IDEAS ADAPTER
        ideasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ideasAdapter = new IdeasAdapter(this, ideas, this);
        ideasRecyclerView.setAdapter(ideasAdapter);

        // PROJECTS ADAPTER
        projectsAdapter = new ProjectsAdapter(projects, this);
        projectsRecyclerView.setAdapter(projectsAdapter);
        projectsRecyclerView.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        if (!projects.isEmpty() && position >= 0 && position < projects.size()) {
                            currentProject = projects.get(position);
                            projectName = currentProject.base.name;
                            projectTasks = currentProject.tasks;
                        }
                    }
                });

        changeView(true);
        updateTimeStatistics();

        addTask.setOnClickListener(v -> showAddHobbyTaskDialog());
        addTimeView.setOnClickListener(v -> showAddTimeDialog());
        addProj.setOnClickListener(v -> showAddProjectDialog());
        addIdea.setOnClickListener(v -> showAddIdeaDialog());

        homeView.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        profileView.setOnClickListener(v -> {
            Intent intent = new Intent(this, AchievementsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        shopView.setOnClickListener(v -> {
            Intent intent = new Intent(this, Shop.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        deleteProject.setOnClickListener(v -> showDeleteConfirmationDialog());
        taskButtonIn.setOnClickListener(v -> changeView(true));
        projectsButtonIn.setOnClickListener(v -> changeView(false)
        );
        leftArrow.setOnClickListener(v -> {
            if (currentProjectIndex > 0) {
                currentProjectIndex--;
                showCurrentProject();
            }
        });
        rightArrow.setOnClickListener(v -> {
            if (currentProjectIndex < projects.size() - 1) {
                currentProjectIndex++;
                showCurrentProject();
            }
        });
    }

    private void changeView(boolean isTask) {
        tasksRecyclerView.setVisibility(isTask ? View.VISIBLE : View.INVISIBLE);
        taskButtonsLayout.setVisibility(isTask ? View.VISIBLE : View.INVISIBLE);
        taskInLayout.setVisibility(isTask ? View.INVISIBLE : View.VISIBLE);
        taskLayout.setVisibility(isTask ? View.VISIBLE : View.INVISIBLE);
        projectsRecyclerView.setVisibility(isTask ? View.INVISIBLE : View.VISIBLE);
        projectButtonsLayout.setVisibility(isTask ? View.INVISIBLE : View.VISIBLE);
        projectLayout.setVisibility(isTask ? View.INVISIBLE : View.VISIBLE);
        projectInLayout.setVisibility(isTask ? View.VISIBLE : View.INVISIBLE);
        rightArrow.setVisibility(isTask ? View.INVISIBLE : View.VISIBLE);
        leftArrow.setVisibility(isTask ? View.INVISIBLE : View.VISIBLE);

        updateEmptyMessages(isTask);
    }

    private void showCurrentProject() {
        if (!projects.isEmpty() && currentProjectIndex >= 0 && currentProjectIndex < projects.size()) {
            currentProject = projects.get(currentProjectIndex);

            projectName = currentProject.base.name;
            projectTasks = currentProject.tasks;

            if (projectsAdapter == null) {
                projectsAdapter = new ProjectsAdapter(projects, this);
                projectsRecyclerView.setAdapter(projectsAdapter);
            }

            projectsRecyclerView.setCurrentItem(currentProjectIndex, true);
        }
    }

    private void showDeleteConfirmationDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.delete_proj, null);
        ImageView ok = dialogView.findViewById(R.id.okProjDel);
        ImageView cancel = dialogView.findViewById(R.id.cancelProjDel);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ok.setOnClickListener(v -> {
            deleteProject();
            dialog.dismiss();
        });

        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAddHobbyTaskDialog() {
        // VIEWS
        View dialogView = getLayoutInflater().inflate(R.layout.add_project_task_dialog, null);
        EditText taskInput = dialogView.findViewById(R.id.projectTaskText);
        ImageView ok = dialogView.findViewById(R.id.okProjectTask);
        ImageView cancel = dialogView.findViewById(R.id.cancelProjectTask);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ok.setOnClickListener(v -> {
            String taskText = taskInput.getText().toString().trim();
            if (!taskText.isEmpty()) {
                // ADD TASK TO DATABASE
                Task newTask = new Task();
                newTask.text = taskText;
                newTask.isDone = false;
                newTask.status = "active";
                newTask.hobbyId = currentHobby.base.id;
                newTask.id = db.taskDao().insert(newTask);

                refreshCurrentHobbyData();
                updateEmptyMessages(true);

            } else {
                Toast.makeText(
                        HobbyDetailsActivity.this,
                        "Задача не может быть пустой",
                        Toast.LENGTH_SHORT
                ).show();
            }
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAddIdeaDialog() {
        // VIEWS
        View dialogView = getLayoutInflater().inflate(R.layout.add_idea_dialog, null);
        EditText ideaInputUrl = dialogView.findViewById(R.id.link);
        EditText ideaInputTitle = dialogView.findViewById(R.id.descr_idea);
        ImageView ok = dialogView.findViewById(R.id.okIdea);
        ImageView cancel = dialogView.findViewById(R.id.cancelIdea);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ok.setOnClickListener(v -> {
            String url = ideaInputUrl.getText().toString().trim();
            String title = ideaInputTitle.getText().toString().trim();

            if (!url.isEmpty() && !title.isEmpty()) {
                Idea newIdea = new Idea();
                newIdea.hobbyId = currentHobby.base.id;
                newIdea.status = "active";
                newIdea.title = title;
                newIdea.url = url;
                newIdea.id = db.ideaDao().insert(newIdea);

                refreshCurrentHobbyData();

            } else {
                Toast.makeText(
                        HobbyDetailsActivity.this,
                        "Заполните все поля",
                        Toast.LENGTH_SHORT
                ).show();
            }
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showAddTimeDialog() {
        // VIEWS
        View dialogView = getLayoutInflater().inflate(R.layout.addtimedialog, null);
        ImageView ok = dialogView.findViewById(R.id.ok);
        ImageView cancel = dialogView.findViewById(R.id.cancel);

        // MINUTES PICKER
        NumberPicker minPicker = dialogView.findViewById(R.id.minPicker);
        minPicker.setMinValue(0);
        minPicker.setMaxValue(59);
        minPicker.setWrapSelectorWheel(true);

        // HOURS PICKER
        NumberPicker hourPicker = dialogView.findViewById(R.id.hoursPicker);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(24);
        hourPicker.setWrapSelectorWheel(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ok.setOnClickListener(v -> {
            // GET VALUES FROM PICKERS
            int hours = hourPicker.getValue();
            int min = minPicker.getValue();

            // CALCULATE TOTAL TIME (in time)
            int totalTime = hours * 3600 + min * 60;

            // CREATING A TimeEntry
            TimeEntry timeEntry = new TimeEntry();
            timeEntry.hobbyId = currentHobby.base.id;
            timeEntry.time = totalTime;
            timeEntry.date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            // SAVE TimeEntry TO DATABASE
            db.timeEntryDao().insert(timeEntry);
            currentHobby.timeEntries.add(timeEntry);

            updateTimeStatistics();

            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateEmptyMessages(boolean isTask) {
        TextView emptyProjectMessage = findViewById(R.id.emptyProjectMessage);
        TextView emptyHobbyTasksMessage = findViewById(R.id.emptyHobbyTasksMessage);

        if (isTask) {
            // Show hobby tasks message if hobbyTasks is empty, hide project message
            emptyHobbyTasksMessage.setVisibility(hobbyTasks.isEmpty() ? View.VISIBLE : View.GONE);
            emptyProjectMessage.setVisibility(View.GONE);
        } else {
            // Show project message if projects is empty, hide hobby tasks message
            emptyProjectMessage.setVisibility(projects.isEmpty() ? View.VISIBLE : View.GONE);
            emptyHobbyTasksMessage.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshCurrentHobbyData() {
        currentHobby = db.hobbyDao().getDetailedHobby(currentHobby.base.id);

        // Update projects and current project
        projects.clear();
        if (currentHobby.projects != null) {
            projects.addAll(currentHobby.projects);
        }

        hobbyTasks.clear();
        if (currentHobby.hobbyTasks != null) {
            hobbyTasks.addAll(currentHobby.hobbyTasks);
        }

        ideas.clear();
        if (currentHobby.ideas != null) {
            ideas.addAll(currentHobby.ideas);
        }

        if (!projects.isEmpty()) {
            if (currentProjectIndex < 0 || currentProjectIndex >= projects.size()) {
                currentProjectIndex = 0;
            }
            currentProject = projects.get(currentProjectIndex);
            projectName = currentProject.base.name;
            projectTasks = currentProject.tasks;
        } else {
            currentProject = null;
            projectName = null;
            projectTasks = null;
        }

        // Notify adapters
        if (projectsAdapter != null) projectsAdapter.notifyDataSetChanged();
        if (tasksAdapter != null) tasksAdapter.notifyDataSetChanged();
        if (ideasAdapter != null) ideasAdapter.notifyDataSetChanged();

        // Update time statistics
        updateTimeStatistics();
    }

    private void updateTimeStatistics() {
        List<TimeEntry> timeEntries = currentHobby.timeEntries;

        // VIEWS
        TextView totalTimeView = findViewById(R.id.wholeTimeInfo);
        TextView dayInfoView = findViewById(R.id.dayInfo);
        TextView weekInfoView = findViewById(R.id.weekInfo);
        TextView monthInfoView = findViewById(R.id.monthInfo);

        // UPDATE TOTAL TIME
        int totalSeconds = TimeUtils.sumSeconds(timeEntries);
        TimeUtils.TimeParts totalTime = TimeUtils.breakdownSeconds(totalSeconds);
        totalTimeView.setText(getString(R.string.total_time, totalTime.hours, totalTime.minutes));

        // UPDATE DAY TIME
        int todaySeconds = TimeUtils.sumSecondsToday(timeEntries);
        TimeUtils.TimeParts dayTime = TimeUtils.breakdownSeconds(todaySeconds);
        dayInfoView.setText(getString(R.string.daily_activity, dayTime.hours, dayTime.minutes));

        // UPDATE WEEK TIME (since Monday)
        int weekSeconds = TimeUtils.sumSecondsThisWeek(timeEntries);
        TimeUtils.TimeParts weekTime = TimeUtils.breakdownSeconds(weekSeconds);
        weekInfoView.setText(getString(R.string.week_activity, weekTime.hours, weekTime.minutes));

        // UPDATE MONTH TIME
        int monthSeconds = TimeUtils.sumSecondsThisMonth(timeEntries);
        TimeUtils.TimeParts monthTime = TimeUtils.breakdownSeconds(monthSeconds);
        monthInfoView.setText(getString(R.string.month_activity, monthTime.hours, monthTime.minutes));

    }

    @Override
    public void onClickHobbyTaskDelete(int position) {
        Task task = hobbyTasks.get(position);

        task.status = "archived";
        db.taskDao().update(task);

        refreshCurrentHobbyData();
        updateEmptyMessages(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAddProjectDialog() {
        // VIEWS
        View dialogView = getLayoutInflater().inflate(R.layout.add_proj_dialog, null);
        EditText projectNameInput = dialogView.findViewById(R.id.projName);
        ImageView ok = dialogView.findViewById(R.id.okProj);
        ImageView cancel = dialogView.findViewById(R.id.cancelProj);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ok.setOnClickListener(v -> {
            String projectName = projectNameInput.getText().toString().trim();

            if (!projectName.isEmpty()) {
                // ADD PROJECT BASE TO DATABASE AND GET IT'S ID
                Project newProjectBase = new Project();

                newProjectBase.hobbyId = currentHobby.base.id;
                newProjectBase.name = projectName;
                newProjectBase.status = "active";
                newProjectBase.id = db.projectDao().insert(newProjectBase);

                refreshCurrentHobbyData();
                updateEmptyMessages(false);

                currentProjectIndex = projects.size() - 1;
                projectsRecyclerView.setCurrentItem(currentProjectIndex, true);
            } else {
                Toast.makeText(
                        HobbyDetailsActivity.this,
                        "Название проекта не может быть пустым",
                        Toast.LENGTH_SHORT
                ).show();
            }
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteProject() {
        if (currentProject == null) {
            Toast.makeText(this, "Нет выбранного проекта для удаления", Toast.LENGTH_SHORT).show();
            return;
        }
        // ARCHIVE ALL TASKS IN THE PROJECT
        if (currentProject != null && currentProject.tasks != null) {
            for (Task task : currentProject.tasks) {
                task.status = "archived";
                db.taskDao().update(task);
            }
        }

        // UPDATE DATABASE
        currentProject.base.status = "archived";
        db.projectDao().update(currentProject.base);

        refreshCurrentHobbyData();
        updateEmptyMessages(false);
    }

    // Called when a single project task is updated (checkbox toggled)
    @Override
    public void onTaskCheckedChanged(Task task) {
        db.taskDao().update(task);
        refreshCurrentHobbyData();
    }

    public void addTaskToProject(long projectId, String taskText) {
        Task newTask = new Task();
        newTask.projectId = projectId;
        newTask.text = taskText;
        newTask.isDone = false;
        newTask.status = "active";
        newTask.id = db.taskDao().insert(newTask);
        refreshCurrentHobbyData();
    }

    // Called when a single project task is deleted
    public void deleteProjectTask(Task task) {
        task.status = "archived";
        db.taskDao().update(task);
        refreshCurrentHobbyData();
    }

    @Override
    public void onClickDeleteIdea(int position) {
        Idea idea = ideas.get(position);

        idea.status = "archived";
        db.ideaDao().update(idea);

        refreshCurrentHobbyData();
    }
}