package com.happyday.android.alarmslist

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.happyday.android.compose.AlarmEditForm
import com.happyday.android.compose.ListContent
import com.happyday.android.repository.*
import com.happyday.android.utils.loge
import com.happyday.android.utils.viewModelBuilder
import com.happyday.android.viewmodel.AlarmsViewModel
import java.util.*

class AlarmsListActivity : AppCompatActivity() {

    private val viewModel: AlarmsViewModel by viewModelBuilder {
        AlarmsViewModel(application, Repo(AlarmsDb.get(application)))
    }

    @Suppress("ACTUAL_FUNCTION_WITH_DEFAULT_ARGUMENTS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val calendar = Calendar.getInstance(Locale.GERMANY)
        loge("Today is ${calendar.get(Calendar.DAY_OF_WEEK)}")

        viewModel.byMinute.observe(this) { _ -> /*Intentionally do nothing - just need to trigger Transformations*/ }
        viewModel.getAlarms().observe(this) { allAlarms ->
            setContent {
                @Suppress("ACTUAL_FUNCTION_WITH_DEFAULT_ARGUMENTS")
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "list" ) {
                    composable("list") {
                        ListContent(
                            activity = this@AlarmsListActivity,
                            allAlarms = allAlarms,
                            onAddAlarm = {
                                loge("onAddAlarm called!")
                                navController.navigate("edit")
                            }
                        )
                    }
                    composable("edit?alarmId={alarmId}",
                        arguments = listOf(navArgument("alarmId") { nullable = true })
                    ) { backStackEntry ->
                        val alarmId = backStackEntry.arguments?.getString("alarmId")
                        AlarmEditForm(
                            alarm = viewModel.alarmById(
                                if (alarmId != null) {
                                    UUID.fromString(alarmId)
                                } else null
                            ),
                            onSave = { alarmModel ->
                                //TODO update exitisting or insert new if needed
                                loge("onSave! $alarmModel")
                            },
                            onCancel = {
                                loge("onCancel!")
                            }
                        ) // TODO supply an actual selected model
                    }
                }
            }
        }
    }

    fun dummyAddAlarm() {
        val picker = MaterialTimePicker.Builder().build()
        picker.addOnPositiveButtonClickListener {
            loge("Selected ${picker.hour}:${picker.minute}")
            val id = UUID.randomUUID()
            val alarm = AlarmModel(
                id,
                "",
                true,
                null,
                true,
                picker.hour,
                picker.minute,
                alarms = mapOf(
//                                            Weekday.Mon to SingleAlarm(id, Weekday.Mon, picker.hour, picker.minute),
//                                            Weekday.Tue to SingleAlarm(id, Weekday.Tue, picker.hour, picker.minute),
                    Weekday.Wed to SingleAlarm(id, Weekday.Wed, picker.hour, picker.minute),
//                                            Weekday.Thu to SingleAlarm(id, Weekday.Thu, picker.hour, picker.minute),
//                                            Weekday.Fri to SingleAlarm(id, Weekday.Fri, picker.hour, picker.minute),
//                                            Weekday.Sat to SingleAlarm(id, Weekday.Sat, picker.hour, picker.minute),
//                                            Weekday.Sun to SingleAlarm(id, Weekday.Sun, picker.hour, picker.minute),
                )
            )
            viewModel.addAlarm(alarm)
        }
        picker.show(this@AlarmsListActivity.supportFragmentManager, "picker")
    }
}