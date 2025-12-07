package com.example.proposedtodolist;
// imports
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.RadioGroup;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    private Context context;
    private List<TaskInfo> TaskDetails = new ArrayList<>();
    private View updateDialog;
    private TaskInfo taskInfo;
    private EditText etTaskName_Edit, etTaskDeadLine_Edit, etTaskDesc_Edit;
    private RadioGroup rgTask_Category_Edit;
    public RVAdapter(Context context, List<TaskInfo> TaskDetails){
        this.context = context;
        this.TaskDetails = TaskDetails;
    }
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_info_cv, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    public void onBindViewHolder(@NonNull ViewHolder holder, final int positon){
        holder.tvTaskName.setText(TaskDetails.get(positon).getTask_Name());
        holder.tvTaskCat.setText(TaskDetails.get(positon).getTask_Cat());
        holder.tvTaskDeadLine.setText(TaskDetails.get(positon).getTask_DeadLine());
        holder.tvTaskDesc.setText(TaskDetails.get(positon).getTask_Desc());
        holder.ivArrow.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(holder.isCVExpanded == false){
                    holder.expanded_task_info_cv.setVisibility(View.VISIBLE);
                    holder.ivArrow.animate().rotation(180).setDuration(200).start();
                    holder.isCVExpanded = true;
                } else {
                    holder.expanded_task_info_cv.setVisibility(View.GONE);
                    holder.ivArrow.animate().rotation(0).setDuration(200).start();
                    holder.isCVExpanded = false;
                }
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder notif1 = new AlertDialog.Builder(context);
                notif1.setTitle("Alert !!!");
                notif1.setMessage("Do you want to delete this task ?");
                notif1.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        try {
                            DbHelper dbHelper = new DbHelper(context);
                            boolean isDeleted = dbHelper.deleteTaskFromTable(TaskDetails.get(positon).getTask_Id());
                            if(isDeleted == true){
                                holder.isCVExpanded = false;
                                TaskDetails.remove(positon);
                                notifyItemRemoved(positon);
                                notifyItemRangeChanged(positon, TaskDetails.size());
                                notifyDataSetChanged();
                            }
                            if(TaskDetails.isEmpty() == true){
                                ((Activity) context).runOnUiThread(new Runnable(){
                                    public void run(){
                                        TextView tvNoOgAct = ((Activity) context).findViewById(R.id.tvNoOgAct);
                                        RecyclerView rvFolderTaskList = ((Activity) context).findViewById(R.id.rvListofTask);
                                        tvNoOgAct.setVisibility(View.VISIBLE);
                                        rvFolderTaskList.setVisibility(View.GONE);
                                    }
                                });
                            }
                        } catch(Exception e){
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                notif1.setNegativeButton("No", null);
                notif1.create().show();
            }
        });
        holder.ivUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                taskInfo = TaskDetails.get(positon);
                updateDialog = LayoutInflater.from(context).inflate(R.layout.add_task_ui, null);
                etTaskName_Edit = updateDialog.findViewById(R.id.etTask_Name);
                rgTask_Category_Edit = updateDialog.findViewById(R.id.rgTask_Category);
                etTaskDeadLine_Edit = updateDialog.findViewById(R.id.etTask_DeadLine);
                etTaskDeadLine_Edit.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        final Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog deadlinePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay){
                                String dateTaskDeadline = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                                etTaskDeadLine_Edit.setText(dateTaskDeadline);
                            }
                        },
                                year, month, day
                        );
                        deadlinePickerDialog.show();
                    }
                });
                etTaskDesc_Edit = updateDialog.findViewById(R.id.etTask_Desc);
                // 2
                etTaskName_Edit.setText(taskInfo.getTask_Name());
                if (taskInfo.getTask_Cat().equals("Work/School")) {
                    rgTask_Category_Edit.check(R.id.rbWorkSchool_Cat);
                } else if (taskInfo.getTask_Cat().equals("Personal")) {
                    rgTask_Category_Edit.check(R.id.rbPersonal_Cat);
                } else if (taskInfo.getTask_Cat().equals("Others")) {
                    rgTask_Category_Edit.check(R.id.rbOthers_Cat);
                }
                etTaskDeadLine_Edit.setText(taskInfo.getTask_DeadLine());
                etTaskDesc_Edit.setText(taskInfo.getTask_Desc());
                // 3
                AlertDialog.Builder updateDialog2 = new AlertDialog.Builder(context);
                updateDialog2.setTitle("Update Task");
                updateDialog2.setView(updateDialog);
                updateDialog2.setPositiveButton("Update Task", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String updatedTask_Name = etTaskName_Edit.getText().toString().trim();
                            int updatedSelectedTask_CategoryId = rgTask_Category_Edit.getCheckedRadioButtonId();
                            String updatedTask_Category = "";
                            if(updatedSelectedTask_CategoryId == R.id.rbWorkSchool_Cat){
                                updatedTask_Category = "Work/School";
                            } else if (updatedSelectedTask_CategoryId == R.id.rbPersonal_Cat) {
                                updatedTask_Category = "Personal";
                            } else if (updatedSelectedTask_CategoryId == R.id.rbOthers_Cat) {
                                updatedTask_Category= "Others";
                            }
                            String updatedTaskDeadLine = etTaskDeadLine_Edit.getText().toString().trim();
                            String updatedTaskDesc = etTaskDesc_Edit.getText().toString().trim();
                            if(updatedTask_Name.isEmpty() || updatedTask_Category.isEmpty() || updatedTaskDeadLine.isEmpty() || updatedTaskDesc.isEmpty()){
                                Toast.makeText(context, "Fill All the details",Toast.LENGTH_LONG).show();
                            } else {
                                DbHelper updateTaskInfo = new DbHelper(context);
                                boolean isUpdated = updateTaskInfo.updateTaskFromTable(taskInfo.getTask_Id(), updatedTask_Name, updatedTask_Category, updatedTaskDeadLine, updatedTaskDesc);
                                updateTaskInfo.close();
                                if(isUpdated == true){
                                    taskInfo.setTask_Name(updatedTask_Name);
                                    taskInfo.setTask_Cat(updatedTask_Category);
                                    taskInfo.setTask_DeadLine(updatedTaskDeadLine);
                                    taskInfo.setTask_Desc(updatedTaskDesc);
                                    notifyItemChanged(positon);
                                    Toast.makeText(context, "Update Successfully", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch(Exception e){
                            Toast.makeText(context, "Update Failed, error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                updateDialog2.setNegativeButton("Cancel", null);
                updateDialog2.create().show();
            }
        });
    }
    public int getItemCount(){
        return TaskDetails.size();
    }
    public void updateTaskList(ArrayList<TaskInfo> TaskDetails){
        this.TaskDetails.clear();
        this.TaskDetails.addAll(TaskDetails);
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskName, tvTaskCat, tvTaskDeadLine, tvTaskDesc;
        private ImageView ivArrow, ivDelete, ivUpdate;
        private View expanded_task_info_cv;
        private boolean isCVExpanded = false;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskCat = itemView.findViewById(R.id.tvTaskCat);
            tvTaskDeadLine = itemView.findViewById(R.id.tvTaskDeadLine);
            tvTaskDesc = itemView.findViewById(R.id.tvTaskDesc);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            expanded_task_info_cv = itemView.findViewById(R.id.expanded_task_info_cv);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivUpdate = itemView.findViewById(R.id.ivUpdate);
        }
    }
}
