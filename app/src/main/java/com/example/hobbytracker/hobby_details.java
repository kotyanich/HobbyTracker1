package com.example.hobbytracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class hobby_details extends AppCompatActivity implements ProjAdapter.onGoalsChangedListener, TasksDeleteInterface,IdeaDeleteInterface, ProjectAdapter.OnDeleteListener{

    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<Links> links = new ArrayList<>();
    private RecyclerView taskRecyclerView;
    RecyclerView ideasRecyclerView;
    private ViewPager2 projRecyclerView;
    private ImageView taskList, rightArrow, leftArrow;
    ImageView taskButtonIn, projectsButtonIn, addTask, addProj, deleteProject, addIdea;
    private FrameLayout taskLayout, projectLayout, taskInLayout, projectInLayout, taskButtonsLayout;
    private LinearLayout projectButtonsLayout;
    private TaskAdapter adapter;
    private ProjAdapter projAdapter;
    private IdeasAdapter ideasAdapter;
    private String hobbyName, projName;
    int hobbyLogo;
    private Hobby currHobby;
    private Map<String, ArrayList<String>> mapData = new HashMap<>();
    private ArrayList<ProjectList> allProjectLists = new ArrayList<>();
    private ProjectList currProj;
    private int currProjList = 0;
    ArrayList<Project> goals = new ArrayList<>();

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
        taskRecyclerView = findViewById(R.id.taskItems);
        projRecyclerView = findViewById(R.id.projRecyclerView);
        ideasRecyclerView = findViewById(R.id.ideasItems);
        addTask = findViewById(R.id.addTask);
        TextView hobby = findViewById(R.id.hobby);
        ImageView logo = findViewById(R.id.logohobby);
        ImageView addTime = findViewById(R.id.addTime);
        ImageView home = findViewById(R.id.home);
        ImageView shop = findViewById(R.id.shop);
        ImageView profile = findViewById(R.id.profile);
        deleteProject = findViewById(R.id.deleteProject);
        taskList = findViewById(R.id.taskList);
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

        hobbyName = getIntent().getStringExtra("hobbyName");
        hobby.setText(hobbyName);
        hobbyLogo = getIntent().getIntExtra("hobbyLogo", -1);
        logo.setImageResource(hobbyLogo);
        currHobby = new Hobby(hobbyName, hobbyLogo);

        loadTasks();
        loadProjectLists();
        loadTimeData();
        loadIdeas();

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this, tasks, this);
        taskRecyclerView.setAdapter(adapter);

        ideasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ideasAdapter = new IdeasAdapter(this, links, this);
        ideasRecyclerView.setAdapter(ideasAdapter);

        if (!allProjectLists.isEmpty()) {
            currProj = allProjectLists.get(currProjList);
        } else {
            currProj = new ProjectList(getString(R.string.project), new ArrayList<>());
            allProjectLists.add(currProj);
            saveData("ProjectsData", allProjectLists);
        }
        loadGoals(currProj);
        projName = currProj.getName();
        goals = currProj.getProjects();

        projAdapter = new ProjAdapter(this, allProjectLists, this, this);
        projRecyclerView.setAdapter(projAdapter);
        projRecyclerView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currProjList = position;
                currProj = allProjectLists.get(position);
                projName = currProj.getName();
                goals = currProj.getProjects();
            }
        });

        changeView(true);

        currHobby.setMapData(mapData);
        updateWholeTimeInfo();
        updateDayInfo();
        updateWeekInfo();
        updateMonthInfo();

        addTask.setOnClickListener(v -> showAddTaskDialog());
        addTime.setOnClickListener(v -> showAddTimeDialog());
        addProj.setOnClickListener(v -> showAddProjDialog());
        addIdea.setOnClickListener(v -> showAddIdeaDialog());
        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        profile.setOnClickListener(v ->{
            Intent intent = new Intent(this, achievements.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        shop.setOnClickListener(v ->{
            Intent intent = new Intent(this, Shop.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        deleteProject.setOnClickListener(v -> showDeleteConfirmationDialog());
        taskButtonIn.setOnClickListener(v-> changeView(true));
        projectsButtonIn.setOnClickListener(v-> changeView(false)
        );
        leftArrow.setOnClickListener(v ->{
            if (currProjList > 0){
                currProjList--;
                showCurrProj();
            }
        });
        rightArrow.setOnClickListener(v ->{
            if (currProjList < allProjectLists.size() -1){
                currProjList++;
                showCurrProj();
            }
        });
    }

    private void changeView(boolean isTask){
        taskRecyclerView.setVisibility(isTask? View.VISIBLE : View.INVISIBLE);
        taskButtonsLayout.setVisibility(isTask? View.VISIBLE: View.INVISIBLE);
        taskList.setVisibility(isTask? View.VISIBLE: View.INVISIBLE);
        taskInLayout.setVisibility(isTask? View.INVISIBLE: View.VISIBLE);
        taskLayout.setVisibility(isTask? View.VISIBLE: View.INVISIBLE);
        projRecyclerView.setVisibility(isTask? View.INVISIBLE: View.VISIBLE);
        projectButtonsLayout.setVisibility(isTask? View.INVISIBLE: View.VISIBLE);
        projectLayout.setVisibility(isTask? View.INVISIBLE: View.VISIBLE);
        projectInLayout.setVisibility(isTask? View.VISIBLE: View.INVISIBLE);
        rightArrow.setVisibility(isTask? View.INVISIBLE: View.VISIBLE);
        leftArrow.setVisibility(isTask? View.INVISIBLE: View.VISIBLE);

    }

    private void showCurrProj() {
        if (!allProjectLists.isEmpty() && currProjList >= 0 && currProjList < allProjectLists.size()) {
            currProj = allProjectLists.get(currProjList);
            projName = currProj.getName();
            goals = currProj.getProjects();
            if (projAdapter == null) {
                projAdapter = new ProjAdapter(this, allProjectLists, this, this);
                projRecyclerView.setAdapter(projAdapter);
            }
            projRecyclerView.setCurrentItem(currProjList, true);
        }
    }

    private void showDeleteConfirmationDialog(){
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
    private void showAddTaskDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.add_goal_dialog,null);
        EditText taskInput = dialogView.findViewById(R.id.goalName);
        ImageView ok = dialogView.findViewById(R.id.okGoal);
        ImageView cancel = dialogView.findViewById(R.id.cancelGoal);
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        ok.setOnClickListener(v -> {
            String taskText = taskInput.getText().toString().trim();
            if(!taskText.isEmpty()){
                Task newTask = new Task(taskText);
                tasks.add(newTask);
                adapter.notifyDataSetChanged();
                saveData("HobbyTasks", tasks);
            }
            else{
                Toast.makeText(hobby_details.this, "Задача не может быть пустой", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAddIdeaDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.add_idea_dialog,null);
        EditText ideaInput = dialogView.findViewById(R.id.link);
        EditText ideaDescr = dialogView.findViewById(R.id.descr_idea);
        ImageView ok = dialogView.findViewById(R.id.okIdea);
        ImageView cancel = dialogView.findViewById(R.id.cancelIdea);
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        ok.setOnClickListener(v -> {
            String url = ideaInput.getText().toString().trim();
            String description = ideaDescr.getText().toString().trim();
            if(!url.isEmpty() && !description.isEmpty()){
                Links link = new Links(description, url);
                links.add(link);
                ideasAdapter.notifyDataSetChanged();
                saveData("HobbyIdeas", links);
            }
            else{
                Toast.makeText(hobby_details.this, "Ссылка не может быть пустой", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAddProjDialog(){
        View dialogView = getLayoutInflater().inflate(R.layout.add_proj_dialog, null);
        EditText projInput = dialogView.findViewById(R.id.projName);
        ImageView ok = dialogView.findViewById(R.id.okProj);
        ImageView cancel = dialogView.findViewById(R.id.cancelProj);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        ok.setOnClickListener(v ->{
            String projText = projInput.getText().toString().trim();
            if (!projText.isEmpty()) {
                ProjectList newProject = new ProjectList(projText, new ArrayList<>());
                allProjectLists.add(newProject);
                saveData("ProjectsData", allProjectLists);
                currProjList = allProjectLists.size() - 1;
                currProj = newProject;
                projName = currProj.getName();
                goals = currProj.getProjects();
                projAdapter.notifyDataSetChanged();
                projRecyclerView.setCurrentItem(currProjList, true);
            }
            else{
                Toast.makeText(hobby_details.this, "Проект не может быть пустым", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void showAddTimeDialog(){
        View dialogView = getLayoutInflater().inflate(R.layout.addtimedialog,null);
        NumberPicker minPicker = dialogView.findViewById(R.id.minPicker);
        ImageView ok = dialogView.findViewById(R.id.ok);
        ImageView cancel = dialogView.findViewById(R.id.cancel);

        minPicker.setMinValue(0);
        minPicker.setMaxValue(59);
        minPicker.setWrapSelectorWheel(true);

        NumberPicker hourPicker = dialogView.findViewById(R.id.hoursPicker);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(24);
        hourPicker.setWrapSelectorWheel(true);

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        ok.setOnClickListener(v -> {
            int hours = hourPicker.getValue();
            int min = minPicker.getValue();
            int totalMin = hours*60 + min ;
            String timeText = String.valueOf(totalMin);
            LocalDate currDate = LocalDate.now();
            String date = currDate.toString();
            if (!mapData.containsKey(date)){
                mapData.put(date, new ArrayList<>());
            }
            Objects.requireNonNull(mapData.get(date)).add(timeText);
            currHobby.setMapData(mapData);
            saveData("TimeData", mapData);
            updateWholeTimeInfo();
            updateDayInfo();
            updateWeekInfo();
            updateMonthInfo();
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateWholeTimeInfo(){
        TextView wholeTimeInfo = findViewById(R.id.wholeTimeInfo);
        int[] totalTime = currHobby.sumTime(mapData);
        int hours = totalTime[0];
        int minutes = totalTime[1];
        String wholeTime = getString(R.string.total_time, hours, minutes);
        wholeTimeInfo.setText(wholeTime);
    }

    private void updateDayInfo(){
        TextView dayInfo = findViewById(R.id.dayInfo);
        LocalDate currDate = LocalDate.now();
        String date = currDate.toString();
        int[] totalTime = currHobby.getDayTime(date);
        int hours = totalTime[0];
        int minutes = totalTime[1];
        dayInfo.setText(getString(R.string.daily_activity, hours, minutes));
    }

    private void updateWeekInfo(){
        TextView weekInfo = findViewById(R.id.weekInfo);
        int[] totalTime = currHobby.getWeekTime();
        int hours = totalTime[0];
        int minutes = totalTime[1];
        weekInfo.setText(getString(R.string.week_activity, hours, minutes));
    }

    private void updateMonthInfo(){
        TextView monthInfo = findViewById(R.id.monthInfo);
        int[] totalTime = currHobby.getMonthTime();
        int hours = totalTime[0];
        int minutes = totalTime[1];
        monthInfo.setText(getString(R.string.month_activity, hours, minutes));
    }


    public void saveData(String key, Object data){
        SharedPreferences sharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(hobbyName, json);
        editor.apply();
    }

    private <T> T loadData(String key, Type type){
        SharedPreferences sharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(hobbyName, null);
        if (json != null){
            try{
                return gson.fromJson(json, type);
            }
            catch (JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void saveGoals(ProjectList currProj){
        SharedPreferences prefs = getSharedPreferences("GoalsData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        ArrayList<Project> goals = currProj.getProjects();
        Gson gson = new Gson();
        String json = gson.toJson(goals);
        editor.putString("goals_"+ currProj.getName(), json);
        editor.apply();
    }

    private void loadGoals(ProjectList currProj){
        SharedPreferences prefs = getSharedPreferences("GoalsData", Context.MODE_PRIVATE);
        String json = prefs.getString("goals_" + currProj.getName(), null);
        if (json!= null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Project>>() {}.getType();
            ArrayList<Project> goals = gson.fromJson(json, type);
            currProj.setProjects(goals);
        }
        else{
            currProj.setProjects(new ArrayList<>());
        }
    }
    private void loadTasks() {
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        ArrayList<Task> loadedTasks = loadData("HobbyTasks", type);
        tasks = loadedTasks != null? loadedTasks : new ArrayList<>();
    }

    private void loadIdeas(){
        Type type = new TypeToken<ArrayList<Links>>() {}.getType();
        ArrayList<Links> loadedIdeas = loadData("HobbyIdeas", type);
        links = loadedIdeas != null? loadedIdeas : new ArrayList<>();
    }

    private void loadTimeData(){
        Type type = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
        Map<String, ArrayList<String>> loadedTimeData = loadData("TimeData", type);
        mapData = loadedTimeData!=null? loadedTimeData: new HashMap<>();
    }
    private void loadProjectLists() {
        Type type = new TypeToken<ArrayList<ProjectList>>() {}.getType();
        ArrayList<ProjectList> loadedLists = loadData("ProjectsData", type);
        allProjectLists = loadedLists != null ? loadedLists : new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteProject(){
        allProjectLists.remove(currProjList);
        if (currProjList >= allProjectLists.size()){
            currProjList = allProjectLists.size() - 1;
        }
        currProj = allProjectLists.get(currProjList);
        projName = currProj.getName();
        goals = currProj.getProjects();
        saveData("ProjectsData", allProjectLists);
        projAdapter.notifyDataSetChanged();
        projRecyclerView.setCurrentItem(currProjList, true);
        SharedPreferences sharedPreferences = getSharedPreferences("GoalsData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("goals_" + projName);
        editor.apply();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onGoalsChanged(ArrayList<Project> goals, ProjectAdapter adapter) {
        this.goals = goals;
        currProj.setProjects(goals);
        allProjectLists.set(currProjList,currProj);
        adapter.notifyDataSetChanged();
        saveGoals(currProj);
        saveData("ProjectsData", allProjectLists);
    }

    @Override
    public void onClickDelete(int position) {
        tasks.remove(position);
        adapter.notifyItemRemoved(position);
        saveData("HobbyTasks", tasks);
    }

    @Override
    public void onDeleteChanged(ArrayList<Project> goals, int pos, ProjectAdapter adapter) {
        this.goals = goals;
        goals.remove(pos);
        adapter.notifyItemRemoved(pos);
        currProj.setProjects(goals);
        allProjectLists.set(currProjList, currProj);
        saveGoals(currProj);
        saveData("ProjectsData", allProjectLists);
    }

    @Override
    public void onClickDeleteIdea(int position) {
        links.remove(position);
        ideasAdapter.notifyItemRemoved(position);
        saveData("HobbyIdeas", links);
    }
}