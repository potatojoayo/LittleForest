package com.benhan.bluegreen

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.benhan.bluegreen.bell.FragmentBell
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.plus.PlusGalleryActivity
import com.benhan.bluegreen.search.FragmentSearch
import com.benhan.bluegreen.tree.FragmentTree
import com.benhan.bluegreen.user.FragmentUser
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission


class HomeActivity : AppCompatActivity() {


    val apiClient = ApiClient()
    val apiInterface: ApiInterface = apiClient.getApiClient().create(
        ApiInterface::class.java
    )

    //    val update: Call<ServerResonse> = apiInterface.updatePlaceBestPost()
    lateinit var tree: ImageView
    private lateinit var search: ImageView
    private lateinit var bell: ImageView
    lateinit var user: ImageView
    private lateinit var treeFragment: Fragment
    private lateinit var searchFragment: Fragment
    private lateinit var bellFragment: Fragment
    private lateinit var userFragment: Fragment
    var active: Fragment? = null
    val fm = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        tree = findViewById(R.id.tree)
        search = findViewById(R.id.search)
        bell = findViewById(R.id.bell)
        user = findViewById(R.id.user)
        treeFragment = FragmentTree()
        searchFragment = FragmentSearch()
        bellFragment = FragmentBell()
        userFragment = FragmentUser()
        active = treeFragment


        clickHandler(tree)
        clickHandler(search)
        clickHandler(user)
        clickHandler(bell)


        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val permissionListener = object : PermissionListener {

            override fun onPermissionGranted() {
                startActivity(Intent(this@HomeActivity, PlusGalleryActivity::class.java))

            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(
                    this@HomeActivity, "권한 거부\n" + deniedPermissions.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        val plus = findViewById<ImageView>(R.id.plus)
        plus.setOnClickListener {
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                TedPermission.with(this)
                    .setPermissionListener(permissionListener)
                    .setRationaleMessage("사진첩을 열기 위해서는 갤러리 접근 권한이 필요해요")
                    .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있어요")
                    .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check()
            } else if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, PlusGalleryActivity::class.java)
                startActivity(intent)
            }
        }

        addAllFragment()


    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 앱이 종료됩니다", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun addAllFragment() {
        fm.beginTransaction().add(R.id.frameLayout, treeFragment).commit()
        fm.beginTransaction().add(R.id.frameLayout, searchFragment).hide(searchFragment).commit()
        fm.beginTransaction().add(R.id.frameLayout, bellFragment).hide(bellFragment).commit()
        fm.beginTransaction().add(R.id.frameLayout, userFragment).hide(userFragment).commit()
    }


    fun clickHandler(view: ImageView) {
        view.setOnClickListener {

            when (view.id) {
                R.id.tree -> {
//                    addFragment(treeFragment)
                    tree.setImageResource(R.drawable.tree_selected)
                    search.setImageResource(R.drawable.search)
                    bell.setImageResource(R.drawable.bell)
                    user.setImageResource(R.drawable.user)
                    fm.beginTransaction().hide(active!!).show(treeFragment).commit()
                    active = treeFragment


                }
                R.id.search -> {
//                    addFragment(searchFragment)
                    tree.setImageResource(R.drawable.tree)
                    search.setImageResource(R.drawable.search_selected)
                    bell.setImageResource(R.drawable.bell)
                    user.setImageResource(R.drawable.user)
                    fm.beginTransaction().hide(active!!).show(searchFragment).commit()
                    active = searchFragment
                }
                R.id.bell -> {
//                    addFragment(bellFragment)
                    tree.setImageResource(R.drawable.tree)
                    search.setImageResource(R.drawable.search)
                    bell.setImageResource(R.drawable.bell_selected)
                    user.setImageResource(R.drawable.user)
                    fm.beginTransaction().hide(active!!).show(bellFragment).commit()
                    active = bellFragment
                }
                R.id.user -> {
//                    addFragment(userFragment)
                    tree.setImageResource(R.drawable.tree)
                    search.setImageResource(R.drawable.search)
                    bell.setImageResource(R.drawable.bell)
                    user.setImageResource(R.drawable.user_selected)
                    fm.beginTransaction().hide(active!!).show(userFragment).commit()
                    active = userFragment
                }
            }
        }
    }



}

























