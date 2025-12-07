package com.example.proposedtodolist;
// imports
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
public class DbHelper extends SQLiteOpenHelper {
    public static final String Task_Table_Name = "MyToDoListTB";
    public static final String Column_Task_Id = "Task_Id";
    public static final String Column_Task_Name = "Task_Name";
    public static final String Column_Task_Desc = "Task_Description";
    public static final String Column_Task_Cat = "Task_Category";
    public static final String Column_Task_DeadLine = "Task_Deadline";
    public DbHelper(Context context){
        super(context, "ToDoTask.db", null, 1);
    }
    public void onCreate(SQLiteDatabase database){
        String createTableQuery = "CREATE TABLE " + Task_Table_Name + " (" +
                Column_Task_Id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Column_Task_Name + " TEXT, " +
                Column_Task_Desc + " TEXT, " +
                Column_Task_Cat + " TEXT, " +
                Column_Task_DeadLine + " TEXT)";
        database.execSQL(createTableQuery);
    }
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){

    }
    public boolean insertTaskToTable(TaskInfo newTask){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Column_Task_Name, newTask.getTask_Name());
        cv.put(Column_Task_Cat, newTask.getTask_Cat());
        cv.put(Column_Task_DeadLine, newTask.getTask_DeadLine());
        cv.put(Column_Task_Desc, newTask.getTask_Desc());
        long insertIntoTable = database.insert(Task_Table_Name, null, cv);
        if(insertIntoTable == -1){
            return false;
        } else {
            return true;
        }
    }
    public ArrayList<TaskInfo> selectTaskFromTable(){
        ArrayList <TaskInfo> allTaskFromTable = new ArrayList<>();
        String selectAllFromTableQuery = "SELECT * FROM " + Task_Table_Name;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectAllFromTableQuery, null, null);
        if(cursor.moveToFirst()){
            do {
                int Task_Id = cursor.getInt(0);
                String Task_Name = cursor.getString(1);
                String Task_Desc = cursor.getString(2);
                String Task_Cat = cursor.getString(3);
                String Task_Deadline = cursor.getString(4);
                TaskInfo taskInfo = new TaskInfo(Task_Id, Task_Name, Task_Cat, Task_Deadline, Task_Desc);
                allTaskFromTable.add(taskInfo);
            } while(cursor.moveToNext());
        } else {

        }
        cursor.close();
        database.close();
        return allTaskFromTable;
    }
    public boolean deleteTaskFromTable(int Folder_Id){
        SQLiteDatabase db = this.getWritableDatabase();
        int deleteTaskInfoRows = db.delete(Task_Table_Name, Column_Task_Id + "=?", new String[]{String.valueOf(Folder_Id)});
        if(deleteTaskInfoRows > 0){
            return true;
        } else {
            return false;
        }
    }
    public boolean updateTaskFromTable(int Task_Id, String Task_Name, String Task_Cat, String Task_DeadLine, String Task_Desc){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Column_Task_Name, Task_Name);
        cv.put(Column_Task_Cat, Task_Cat);
        cv.put(Column_Task_DeadLine, Task_DeadLine);
        cv.put(Column_Task_Desc, Task_Desc);
        int rows = db.update(Task_Table_Name, cv, Column_Task_Id + "=?", new String[]{String.valueOf(Task_Id)});
        if(rows > 0){
            return true;
        } else {
            return false;
        }
    }

}
