package com.happyday.android.alarmslist

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.happyday.android.compose.AlarmEditForm
import com.happyday.android.compose.ListContent
import com.happyday.android.repository.*
import com.happyday.android.utils.loge
import com.happyday.android.utils.viewModelBuilder
import com.happyday.android.viewmodel.AlarmsViewModel
import java.util.*
import com.happyday.android.utils.isM
import com.happyday.android.utils.requestOverlayPermission
import com.happyday.android.viewmodel.AlarmUi

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
        viewModel.listState.observe(this) { state ->
            setContent {
                if (!state.overlayPermission && isM()) {
                    OverlayPermissionDialog {
                        requestOverlayPermission()
                    }
                }
                alarmsList()
            }
        }
    }

    @Composable
    private fun alarmsList() {
        @Suppress("ACTUAL_FUNCTION_WITH_DEFAULT_ARGUMENTS")
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "list" ) {
            composable("list") {
                ListContent(
                    activity = this@AlarmsListActivity,
                    onAddAlarm = {
                        navController.navigate("edit")
                    },
                    onDelete = { itemsToDelete ->
                        viewModel.deleteAlarms(itemsToDelete)
                    },
                    onAlarmClicked = { alarm ->
                        navController.navigate("edit?alarmId=${alarm.model.id}")
                    },
                )
            }
            composable("edit?alarmId={alarmId}",
                arguments = listOf(navArgument("alarmId") {
                    type= NavType.StringType
                    nullable = true
                })
            ) { backStackEntry ->
                val alarmId = backStackEntry.arguments?.getString("alarmId")
                AlarmEditForm(
                    alarm = viewModel.alarmById(
                        if (alarmId != null) {
                            UUID.fromString(alarmId)
                        } else null
                    ) ?: viewModel.newAlarm(),
                    onSave = { alarmModel ->
                        viewModel.addOrUpdate(alarmModel, alarmId)
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                ) // TODO supply an actual selected model
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.updateOverlayPermission()
    }
}
