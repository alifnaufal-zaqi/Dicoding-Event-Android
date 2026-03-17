package com.alif.dicodingevent.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.alif.dicodingevent.R
import com.alif.dicodingevent.data.local.datastore.SettingPreferences
import com.alif.dicodingevent.ui.view_model.SettingViewModel
import com.alif.dicodingevent.ui.view_model.ViewModelFactory
import com.alif.dicodingevent.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit

class SettingFragment : PreferenceFragmentCompat() {

    private var themePreference: SwitchPreferenceCompat? = null
    private var notificationPreference: SwitchPreferenceCompat? = null
    
    private val settingViewModel: SettingViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var workManager: WorkManager

    companion object {
        private const val WORK_NAME = "DailyReminderWorker"
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        themePreference = findPreference(getString(R.string.theme_key))
        notificationPreference = findPreference(getString(R.string.reminder_key))

        themePreference?.setOnPreferenceChangeListener { _, newValue ->
            settingViewModel.saveSetting(SettingPreferences.THEME_KEY, newValue as Boolean)
            true
        }

        notificationPreference?.setOnPreferenceChangeListener { _, newValue ->
            settingViewModel.saveSetting(SettingPreferences.NOTIFICATION_KEY, newValue as Boolean)
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workManager = WorkManager.getInstance(requireActivity())

        settingViewModel.themeSetting.observe(viewLifecycleOwner) { isDarkMode ->
            themePreference?.isChecked = isDarkMode
        }

        settingViewModel.notificationSetting.observe(viewLifecycleOwner) { isEnabled ->
            notificationPreference?.isChecked = isEnabled

            if (isEnabled) {
                startPeriodicTask()
            } else {
                stopPeriodicTask()
            }
        }
    }

    private fun startPeriodicTask() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicWorkRequest = PeriodicWorkRequest.Builder(DailyReminderWorker::class.java, 1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    private fun stopPeriodicTask() {
        workManager.cancelUniqueWork(WORK_NAME)
    }
}