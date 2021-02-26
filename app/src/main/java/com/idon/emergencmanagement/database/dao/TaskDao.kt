package com.zine.ketotime.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shin.tmsuser.database.entity.ConfigTb

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: ConfigTb)


    @Update
    suspend fun updateTask(task: ConfigTb)

//    @Query("select * from ConfigTb WHERE sku = :sku")
//    suspend fun getCartProduct(sku: String):List<ConfigTb>
//
//
    @Query("DELETE FROM ConfigTb WHERE task_id = :id")
    suspend fun deleteTask(id: Int)

    @Query("select * from ConfigTb")
    fun getAllDataCoroutine(): LiveData<List<ConfigTb>>

    @Query("select * from ConfigTb where sendername LIKE :sendername AND task_status = :task_status")
    fun getAllData(sendername:String, task_status: Boolean = true): List<ConfigTb>

    @Query("select * from ConfigTb")
    fun getAllData(): List<ConfigTb>



}