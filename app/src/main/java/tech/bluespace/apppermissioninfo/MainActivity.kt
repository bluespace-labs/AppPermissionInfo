package tech.bluespace.apppermissioninfo

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_item_view.view.*

class MainActivity : AppCompatActivity() {
    var allApps = listOf<String>()
    var allViews = mapOf<String, View>()
    var appNames = mapOf<String, String>()
    var appIcons = mapOf<String, Drawable>()

    private lateinit var adapter: BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadAllApps()
    }

    private fun loadAllApps() {
        val apps = packageManager.getInstalledApplications(0)
        allApps = apps.map { it.packageName }.sorted()
        appNames = apps.associate { Pair(it.packageName, packageManager.getApplicationLabel(it).toString()) }
        allViews = allApps.associateWith { packageName ->
            LayoutInflater.from(this).inflate(R.layout.app_item_view, null).apply {
                appNameTextView.text = appNames.getValue(packageName)
                packageNameTextView.text = packageName
            }
        }

        adapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?) = allViews[allApps[position]]

            override fun getItem(position: Int) = allApps[position]

            override fun getItemId(position: Int) = position.toLong()

            override fun getCount() = allApps.size
        }
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ -> onClick(allApps[position]) }

        Thread(Runnable { allApps.forEach { loadIcon(it) } }).start()
        Thread(Runnable { allApps.forEach { loadInternetPermission(it) } }).start()
    }

    private fun loadInternetPermission(packageName: String) {
        val permissions = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
        if (permissions?.contains(PermissionViewer.internet) == true) {
            allViews.getValue(packageName).appNameTextView.setTextColor(getColor(R.color.red))
        }
    }

    private fun loadIcon(packageName: String) {
        val icon = packageManager.getApplicationIcon(packageName)
        appIcons = appIcons + Pair(packageName, icon)
        runOnUiThread { allViews.getValue(packageName).iconImageView.setImageDrawable(icon) }
    }

    private fun onClick(packageName: String) = startActivity(PermissionViewer.make(this, appNames.getValue(packageName), packageName))
}
