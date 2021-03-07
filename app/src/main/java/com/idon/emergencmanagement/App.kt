package com.panuphong.smssender

import android.app.Application
import com.idon.emergencmanagement.viewmodel.LocationViewmodel
import com.panuphong.smssender.repository.TaskRespt
import com.panuphong.smssender.viewmodel.TaskViewModel
import com.zine.ketotime.database.KetoDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()


        initCoin()


    }

    private fun initCoin() {

        startKoin {

            androidContext(this@App)


            val roomModule = module {

                single {
                    KetoDatabase.invoke(androidApplication()).taskDao()
                }

            }


            val taskViewModel = module {
                single {
                    TaskRespt(get())
                }
                viewModel { TaskViewModel(get()) }

            }


            val attackData = module{

                viewModel { LocationViewmodel() }



            }

            modules(roomModule, taskViewModel,attackData)


        }
    }

}