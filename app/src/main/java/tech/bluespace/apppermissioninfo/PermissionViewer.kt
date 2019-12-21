package tech.bluespace.apppermissioninfo

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import kotlinx.android.synthetic.main.permission_viewer.*

class PermissionViewer: AppCompatActivity() {
    lateinit var permissions: List<String>
    lateinit var permissionViews: List<View>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permission_viewer)

        supportActionBar?.title = intent.getStringExtra("appName")!!
        loadPermission(intent.getStringExtra("package")!!)
    }

    private fun loadPermission(packageName: String) {
        packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions?.let { allPermissions ->

            permissions = if (allPermissions.contains(internet)) {
                listOf(internet) + (allPermissions.toList() - internet)
            } else {
                allPermissions.toList()
            }

            permissionViews = permissions.map { permission ->
                TextView(this).apply {
                    text = permission
                    if (permission == internet) {
                        setTextColor(getColor(R.color.red))
                    }
                    setPadding(8)
                }
            }

            permissionListView.adapter = object : BaseAdapter() {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup?) = permissionViews[position]

                override fun getItem(position: Int) = permissions[position]

                override fun getItemId(position: Int) = position.toLong()

                override fun getCount() = permissions.size
            }
        } ?: {
            noPermissionTextView.visibility = View.VISIBLE
            permissionListView.visibility = View.INVISIBLE
        }()
    }

    companion object {
        const val internet = "android.permission.INTERNET"

        fun make(context: Context, appName: String, packageName: String) = Intent(context, PermissionViewer::class.java)
            .putExtra("appName", appName)
            .putExtra("package", packageName)
    }
}
