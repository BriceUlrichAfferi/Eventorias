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

    // Provide SharedPreferences instance
    single<SharedPreferences> {
        get<Context>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    // Provide NotificationViewModel
    viewModel { NotificationViewModel(get()) }


    // Provide FirebaseFirestore instance
    single { FirebaseFirestore.getInstance() }

    // Provide EventRepository
    single<EventRepository> { EventRepository(get()) }

    // Provide EventViewModel
    viewModel { EventViewModel() }

}
