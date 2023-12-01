package hr.foi.rampu.memento

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.wearable.Wearable
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import hr.foi.rampu.memento.adapters.MainPagerAdapter
import hr.foi.rampu.memento.database.TasksDatabase
import hr.foi.rampu.memento.fragments.CompletedFragment
import hr.foi.rampu.memento.fragments.NewsFragment
import hr.foi.rampu.memento.fragments.PendingFragment
import hr.foi.rampu.memento.helpers.MockDataLoader
import hr.foi.rampu.memento.helpers.TaskDeletionServiceHelper
import hr.foi.rampu.memento.sync.WearableSynchronizer

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var navDrawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    private lateinit var mainPagerAdapter: MainPagerAdapter
    private val taskDeletionServiceHelper by lazy { TaskDeletionServiceHelper(applicationContext) }

    private val dataClient by lazy { Wearable.getDataClient(this) }
    private lateinit var onSharedPreferencesListener: OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeMainPagerAdapter()

        TasksDatabase.buildInstance(applicationContext)
        MockDataLoader.loadMockData()

        connectViewPagerWithTabLayout()
        connectNavDrawerWithViewPager()

        prepareServices()
        applyUserSettings()
    }

    private fun connectViewPagerWithTabLayout() {
        tabLayout = findViewById(R.id.tabs)
        viewPager2 = findViewById(R.id.viewpager)

        viewPager2.adapter = mainPagerAdapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setText(mainPagerAdapter.fragmentItems[position].titleRes)
            tab.setIcon(mainPagerAdapter.fragmentItems[position].iconRes)
        }.attach()
    }

    private fun initializeMainPagerAdapter() {
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager, lifecycle)
        fillAdapterWithFragments()
    }

    private fun fillAdapterWithFragments() {
        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.tasks_pending,
                R.drawable.baseline_assignment_late_24,
                PendingFragment::class
            )
        )
        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.tasks_completed,
                R.drawable.baseline_assignment_turned_in_24,
                CompletedFragment::class
            )
        )
        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.news,
                R.drawable.baseline_wysiwyg_24,
                NewsFragment::class
            )
        )
    }

    private fun connectNavDrawerWithViewPager() {
        navDrawerLayout = findViewById(R.id.nav_drawer_layout)
        navView = findViewById(R.id.nav_view)

        fillNavDrawerWithFragments()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.title) {
                getString(R.string.tasks_pending) -> viewPager2.setCurrentItem(0, true)
                getString(R.string.tasks_completed) -> viewPager2.setCurrentItem(1, true)
                getString(R.string.news) -> viewPager2.setCurrentItem(2, true)
            }
            navDrawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                navView.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun fillNavDrawerWithFragments() {
        navView.menu.setGroupDividerEnabled(true)

        var newNavMenuIndex = 0

        mainPagerAdapter.fragmentItems.withIndex().forEach { (index, fragmentItem) ->
            newNavMenuIndex++
            navView.menu
                .add(0, index, index, fragmentItem.titleRes)
                .setIcon(fragmentItem.iconRes)
                .setCheckable(true)
                .setChecked((index == 0))
                .setOnMenuItemClickListener {
                    viewPager2.setCurrentItem(index, true)
                    navDrawerLayout.closeDrawers()
                    return@setOnMenuItemClickListener true
                }
        }

        newNavMenuIndex++

        navView.menu
            .add(
                newNavMenuIndex,
                0,
                newNavMenuIndex,
                getString(R.string.sync_wear_os)
            )
            .setIcon(R.drawable.baseline_watch_24)
            .setOnMenuItemClickListener {
                WearableSynchronizer.sendTasks(
                    TasksDatabase
                        .getInstance()
                        .getTasksDao()
                        .getAllTasks(false),
                    dataClient
                )
                return@setOnMenuItemClickListener true
            }

        newNavMenuIndex++

        navView.menu.apply {
            val menuItem = add(
                newNavMenuIndex,
                newNavMenuIndex,
                newNavMenuIndex,
                ""
            )
            menuItem.isEnabled = false
            attachMenuItemToTasksCreatedCount(menuItem)
        }

        newNavMenuIndex++

        navView.menu
            .add(3, newNavMenuIndex, newNavMenuIndex, getString(R.string.settings_menu_item))
            .setIcon(R.drawable.baseline_settings_24)
            .setOnMenuItemClickListener {
                val intent = Intent(baseContext, PreferencesActivity::class.java)
                startActivity(intent)
                navDrawerLayout.closeDrawers()
                return@setOnMenuItemClickListener true
            }

    }

    private fun attachMenuItemToTasksCreatedCount(tasksCounterItem: MenuItem) {
        val sharedPreferences = getSharedPreferences("tasks_preferences", Context.MODE_PRIVATE)
        onSharedPreferencesListener =
            OnSharedPreferenceChangeListener { _, key ->
                if (key == "tasks_created_counter") {
                    updateTasksCreatedCounter(tasksCounterItem, sharedPreferences)
                }
            }
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferencesListener)
        updateTasksCreatedCounter(tasksCounterItem, sharedPreferences)
    }

    private fun updateTasksCreatedCounter(
        tasksCounterItem: MenuItem,
        sharedPreferences: SharedPreferences
    ) {
        val tasksCreated = sharedPreferences.getInt("tasks_created_counter", 0)
        tasksCounterItem.title = "${getString(R.string.tasks_created)} $tasksCreated"
    }

    private fun prepareServices() {
        createTaskTimerNotificationChannel()
        activateTaskDeletionService()
    }

    private fun createTaskTimerNotificationChannel() {
        val channel = NotificationChannel(
            "task-timer",
            "Task Timer Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun activateTaskDeletionService() {
        taskDeletionServiceHelper
            .activateTaskDeletionService { deletedTaskId ->
                supportFragmentManager.setFragmentResult(
                    "task_deleted",
                    bundleOf("task_id" to deletedTaskId)
                )
            }
    }

    private fun applyUserSettings() {
        PreferenceManager.getDefaultSharedPreferences(this)?.let { pref ->
            PreferencesActivity.switchDarkMode(
                pref.getBoolean("preference_dark_mode", false)
            )
        }
    }

    override fun onDestroy() {
        taskDeletionServiceHelper.deactivateTaskDeletionService()
        super.onDestroy()
    }
}
