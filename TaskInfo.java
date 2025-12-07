package com.example.proposedtodolist;

public class TaskInfo {
    private int Task_Id; // primary id
    private String Task_Name;
    private String Task_Cat;
    private String Task_DeadLine;
    private String Task_Desc;

    // for creating Task
    public TaskInfo(String Task_Name, String Task_Cat, String Task_DeadLine, String Task_Desc){
        this.Task_Name = Task_Name;
        this.Task_Cat = Task_Cat;
        this.Task_DeadLine = Task_DeadLine;
        this.Task_Desc = Task_Desc;
    }
    // get data from the db
    public TaskInfo(int Task_Id, String Task_Name, String Task_Cat, String Task_DeadLine, String Task_Desc){
        this.Task_Id = Task_Id;
        this.Task_Name = Task_Name;
        this.Task_Cat = Task_Cat;
        this.Task_DeadLine = Task_DeadLine;
        this.Task_Desc = Task_Desc;
    }
    public int getTask_Id(){
        return Task_Id;
    }
    public void setTask_Id(int Task_Id){
        this.Task_Id = Task_Id;
    }
    public String getTask_Name(){
        return Task_Name;
    }
    public void setTask_Name(String Task_Name){
        this.Task_Name = Task_Name;
    }
    public String getTask_Cat(){
        return Task_Cat;
    }
    public void setTask_Cat(String Task_Cat){
        this.Task_Cat = Task_Cat;
    }
    public String getTask_DeadLine(){
        return Task_DeadLine;
    }
    public void setTask_DeadLine(String Task_DeadLine){
        this.Task_DeadLine = Task_DeadLine;
    }
    public String getTask_Desc(){
        return Task_Desc;
    }
    public void setTask_Desc(String Task_Desc){
        this.Task_Desc = Task_Desc;
    }
}
