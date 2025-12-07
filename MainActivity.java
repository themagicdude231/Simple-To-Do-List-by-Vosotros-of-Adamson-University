// do take note that i used material design
// go to project > gradle script > build.gradle.kts (Module: app)
// paste "implementation("com.google.android.material:material:1.11.0")" inside dependecies then synce now
package com.example.proposedtodolist;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
// imports
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.content.pm.ActivityInfo;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.TextView;
// libraries used in the notification stuff
import android.os.Build;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    // declaration of elements and variable
    private ExtendedFloatingActionButton fabOptions, fabOptionAddTask, fabOptionOrganizeTask, fabOptionNotifyTask, fabOptionAboutApp;
    private boolean isFabOptionsItemVisible;
    private View addDialog, orgListDialog;
    private EditText etTask_Name, etTask_DeadLine, etTask_Desc;
    private RadioGroup rgTask_Category, rgFilterCategory;
    private RecyclerView rvListofTask;
    private  RVAdapter  adapter;
    private DbHelper dbHelper;
    ArrayList<TaskInfo> allTask;
    private TextView tvNoOgAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // calling methods
        initViews();
        btnInteract();
        theRVofListOfTask();
        notifChannel();
    }
    // initialize elements or variable and others
    private void initViews(){
        fabOptions = findViewById(R.id.fabOptions);
        fabOptionAddTask = findViewById(R.id.fabOptionAddTask);
        fabOptionOrganizeTask = findViewById(R.id.fabOptionOrganizeTask);
        isFabOptionsItemVisible = false;
        rvListofTask =  findViewById(R.id.rvListofTask);
        dbHelper = new DbHelper(MainActivity.this);
        tvNoOgAct = findViewById(R.id.tvNoOgAct);
        fabOptionNotifyTask = findViewById(R.id.fabOptionNotifyTask);
        fabOptionAboutApp = findViewById(R.id.fabOptionAboutApp);
    }
    // creating for the reaction of the button
    private void btnInteract(){
        // for the options button and it items
        // options button
        fabOptions.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(isFabOptionsItemVisible == false){
                    fabOptionAddTask.show();
                    fabOptionOrganizeTask.show();
                    fabOptionNotifyTask.show();
                    fabOptionAboutApp.show();
                    isFabOptionsItemVisible = true;
                } else {
                    fabOptionAddTask.hide();
                    fabOptionOrganizeTask.hide();
                    fabOptionNotifyTask.hide();
                    fabOptionAboutApp.hide();
                    isFabOptionsItemVisible = false;
                }
            }
        });
        // notify user addressing the task need to be finished
        // add task button
        fabOptionAddTask.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                // interface and interactivity of xml inisde of an dialog
                addDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_task_ui, null);
                etTask_Name = addDialog.findViewById(R.id.etTask_Name);
                rgTask_Category = addDialog.findViewById(R.id.rgTask_Category);
                etTask_DeadLine = addDialog.findViewById(R.id.etTask_DeadLine);
                etTask_DeadLine.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        final Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog deadlinePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay){
                                String dateTaskDeadline = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                                etTask_DeadLine.setText(dateTaskDeadline);
                            }
                        },
                                year, month, day
                        );
                        deadlinePickerDialog.show();
                    }
                });
                etTask_Desc = addDialog.findViewById(R.id.etTask_Desc);
                AlertDialog.Builder addTask = new AlertDialog.Builder(MainActivity.this);
                addTask.setTitle("Add Task");
                addTask.setView(addDialog);
                addTask.setPositiveButton("Create Task", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        try{
                            String Task_Name = etTask_Name.getText().toString().trim();
                            int SelectedTask_CategoryId = rgTask_Category.getCheckedRadioButtonId();
                            String Task_Category = "";
                            if(SelectedTask_CategoryId == R.id.rbWorkSchool_Cat){
                                Task_Category = "Work/School";
                            } else if (SelectedTask_CategoryId == R.id.rbPersonal_Cat) {
                                Task_Category = "Personal";
                            } else if (SelectedTask_CategoryId == R.id.rbOthers_Cat) {
                                Task_Category = "Others";
                            }
                            String Task_Deadline = etTask_DeadLine.getText().toString().trim();
                            String Task_Desc = etTask_Desc.getText().toString().trim();
                            if(Task_Name.isEmpty() || Task_Category.isEmpty() || Task_Deadline.isEmpty() || Task_Desc.isEmpty()){
                                Toast.makeText(MainActivity.this, "Fill All the details",Toast.LENGTH_LONG).show();
                            } else {
                                TaskInfo createTask = new TaskInfo(Task_Name, Task_Category, Task_Deadline, Task_Desc);
                                dbHelper.insertTaskToTable(createTask);
                                Toast.makeText(MainActivity.this, "Task Listed Successfully",Toast.LENGTH_LONG).show();
                                etTask_Name.setText("");
                                etTask_DeadLine.setText("");
                                etTask_Desc.setText("");
                                theRVofListOfTask();
                            }
                        } catch(Exception e){
                            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                addTask.setNegativeButton("Cancel", null);
                addTask.create().show();
            }
        });
        // organize task button
        fabOptionOrganizeTask.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                orgListDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.organize_tasklist_ui, null);
                rgFilterCategory = orgListDialog.findViewById(R.id.rgFilterCategory);
                AlertDialog.Builder orgDialog = new AlertDialog.Builder(MainActivity.this);
                orgDialog.setView(orgListDialog);
                orgDialog.setPositiveButton("Apply Changes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        int selectedId = rgFilterCategory.getCheckedRadioButtonId();
                        String selectedCat = null;
                        if(selectedId == R.id.rbWorkSchool){
                            selectedCat = "Work/School";
                        } else if(selectedId == R.id.rbPersonal){
                            selectedCat = "Personal";
                        } else if(selectedId == R.id.rbOthers){
                            selectedCat = "Others";
                        } else if(selectedId == R.id.rbAllTask){
                            selectedCat = "All Task";
                        }
                        dbHelper = new DbHelper(MainActivity.this);
                        ArrayList<TaskInfo> allTasks = dbHelper.selectTaskFromTable();
                        ArrayList<TaskInfo> filteredList = new ArrayList<>();
                        if(selectedCat == null || selectedCat.equals("All Task")){
                            filteredList.addAll(allTasks);
                        } else {
                            for(TaskInfo tasks : allTasks){
                                if(tasks.getTask_Cat().equals(selectedCat)){
                                    filteredList.add(tasks);
                                }
                            }
                        }
                        adapter.updateTaskList(filteredList);
                    }
                });
                orgDialog.setNegativeButton("Cancel", null);
                orgDialog.create().show();
            }
        });
        // notify Users about pending task
        fabOptionNotifyTask.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                ArrayList<TaskInfo> taskNearDL = new ArrayList<>();
                ArrayList<TaskInfo> allTask = dbHelper.selectTaskFromTable();
                LocalDate currentDayToday = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
                for (TaskInfo task : allTask) {
                    String deadline = task.getTask_DeadLine();
                    if (deadline == null || deadline.trim().isEmpty() || deadline.equalsIgnoreCase("Indefinite")) {
                        continue;
                    }
                    LocalDate taskDeadline;
                    try {
                        taskDeadline = LocalDate.parse(deadline, formatter);
                    } catch (Exception e) {
                        continue;
                    }
                    long daysDiff = ChronoUnit.DAYS.between(currentDayToday, taskDeadline);
                    if (daysDiff >= 0 && daysDiff <= 3) {
                        taskNearDL.add(task);
                    }
                }
                Collections.sort(taskNearDL, new Comparator<TaskInfo>(){
                    public int compare(TaskInfo Task1, TaskInfo Task2){
                        try {
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
                            LocalDate Date1 = LocalDate.parse(Task1.getTask_DeadLine(), dateFormatter);
                            LocalDate Date2 = LocalDate.parse(Task2.getTask_DeadLine(), dateFormatter);
                            return Date1.compareTo(Date2);
                        } catch(Exception e){
                            return 0;
                        }
                    }
                });
                NotificationCompat.Builder notifBuild = new NotificationCompat.Builder(MainActivity.this, "List of Deadline");
                NotificationManagerCompat notifMng = NotificationManagerCompat.from(MainActivity.this);
                if(taskNearDL.isEmpty()){
                    notifBuild.setSmallIcon(R.drawable.ic_launcher_foreground);
                    notifBuild.setContentTitle("Notice");
                    notifBuild.setStyle(new NotificationCompat.BigTextStyle().bigText("no task near deadline yet"));
                    notifBuild.setPriority(NotificationCompat.PRIORITY_HIGH);
                    notifBuild.setAutoCancel(true);
                    notifMng.notify(1001, notifBuild.build());
                } else {
                    StringBuilder listOfUpTask = new StringBuilder();
                    for(TaskInfo Tasks : taskNearDL){
                        listOfUpTask.append("- ");
                        listOfUpTask.append(Tasks.getTask_Name());
                        listOfUpTask.append("| due: ");
                        listOfUpTask.append(Tasks.getTask_DeadLine());
                        listOfUpTask.append("\n");
                    }
                    notifBuild.setSmallIcon(R.drawable.ic_launcher_foreground);
                    notifBuild.setContentTitle("Notice");
                    notifBuild.setStyle(new NotificationCompat.BigTextStyle().bigText(listOfUpTask.toString()));
                    notifBuild.setPriority(NotificationCompat.PRIORITY_HIGH);
                    notifBuild.setAutoCancel(true);
                    notifMng.notify(1001, notifBuild.build());
                }
            }
        });
        fabOptionAboutApp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                AlertDialog.Builder notice = new AlertDialog.Builder(MainActivity.this);
                notice.setTitle("Notice");
                notice.setMessage("This app is created by sebastian vosotros of Adamson University a 3rd year student of BSIT at CCIT. this a personal project of studying android studio with java." +
                        "but became a project to save time." + "if your gonna use this project as a insparation please give credits to the owner");
                notice.setPositiveButton("I understand", null);
                notice.create().show();
            }
        });
    }
    //fetches the task from the table in the database and displayed them on the user's screen
    private void theRVofListOfTask(){
        allTask = dbHelper.selectTaskFromTable();
        adapter  = new RVAdapter(MainActivity.this, allTask);
        rvListofTask.setAdapter(adapter);
        rvListofTask.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        if(allTask.isEmpty()){
            tvNoOgAct.setVisibility(View.VISIBLE);
            rvListofTask.setVisibility(View.GONE);
        } else {
            tvNoOgAct.setVisibility(View.GONE);
            rvListofTask.setVisibility(View.VISIBLE);
        }
    }
    // for the notifcation stuff
    private void notifChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence Title = "REMINDER !!!";
            String NotifDesc = "Here your task that needs to be Done ASAP";
            int Importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel Channel = new NotificationChannel("List of Deadline", Title, Importance);
            Channel.setDescription(NotifDesc);
            NotificationManager notifMng = getSystemService(NotificationManager.class);
            notifMng.createNotificationChannel(Channel);
        }
    }
}
