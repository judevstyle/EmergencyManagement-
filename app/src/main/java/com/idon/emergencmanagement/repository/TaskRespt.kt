package com.panuphong.smssender.repository

import androidx.lifecycle.LiveData
import com.shin.tmsuser.database.entity.ConfigTb
import com.zine.ketotime.database.dao.TaskDao

class TaskRespt(val taskDao: TaskDao){


    suspend fun addTask(task:ConfigTb){
        taskDao.insertTask(task)

    }
    suspend fun updateTask(task:ConfigTb){

        taskDao.updateTask(task)
    }


    suspend fun deleteTask(task_id:Int){
        taskDao.deleteTask(task_id)
    }


    fun getAllData():LiveData<List<ConfigTb>>{
        return taskDao.getAllDataCoroutine()
    }


    suspend fun getData():List<ConfigTb>{
        return taskDao.getAllData()
    }


}