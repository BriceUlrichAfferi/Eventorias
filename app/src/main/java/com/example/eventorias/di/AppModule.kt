package com.example.eventorias.di

import android.content.Context
import android.content.SharedPreferences
import com.example.eventorias.presentation.event.EventRepository
import com.example.eventorias.presentation.event.EventViewModel
import com.example.eventorias.presentation.notification.NotificationViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Instances
    single { FirebaseFirestore.getInstance() }

    single<SharedPreferences> {
        get<Context>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    // ViewModels
    viewModel { NotificationViewModel(get()) }
    viewModel { EventViewModel() }

    // Repositories
    single<EventRepository> { EventRepository(get()) }



}
