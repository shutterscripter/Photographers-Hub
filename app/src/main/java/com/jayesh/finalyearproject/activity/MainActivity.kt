package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.data.User
import com.jayesh.finalyearproject.fragment.*
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity() {
    //main variables
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var tbMain: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var navMain: NavigationView
    var previousMenuItemSelected: MenuItem? = null

    lateinit var tvEditProfile: TextView
    lateinit var tvLocation: TextView
    lateinit var tvUserName: TextView
    lateinit var profileImage: CircleImageView
    lateinit var navHeader: RelativeLayout
    lateinit var dbref: DatabaseReference
    private lateinit var auth: FirebaseAuth


    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        tbMain = findViewById(R.id.tbMain)
        frameLayout = findViewById(R.id.frameLayout)
        drawerLayout = findViewById(R.id.drawerLayout)
        navMain = findViewById(R.id.navMain)
        auth = FirebaseAuth.getInstance()
        val headerView: View = navMain.getHeaderView(0)
        tvUserName = headerView.findViewById(R.id.tvUserName)
        tvLocation = headerView.findViewById(R.id.tvLocation)
        tvEditProfile = headerView.findViewById(R.id.tvEditProfile)
        profileImage = headerView.findViewById(R.id.ivLogo)
        navHeader = headerView.findViewById(R.id.navHeader)

        navHeader.setOnClickListener {
            startActivity(Intent(this, CurrentUserProfileActivity::class.java))
        }

        navMain.menu.getItem(0).isCheckable = true
        navMain.menu.getItem(0).isChecked = true


        setUpToolBar(tbMain)


        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        actionBarDrawerToggle.drawerArrowDrawable.color = getColor(R.color.white)

        //setting on click listener on items in navigation drawer
        navMain.setNavigationItemSelectedListener {
            if (previousMenuItemSelected != null) {
                previousMenuItemSelected?.isChecked = false
            }
            previousMenuItemSelected = it
            it.isCheckable = true
            it.isChecked = true

            when (it.itemId) {
                R.id.itmHome -> {
                    defaultFragment()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Home"
                }

                R.id.itmBookmark -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout, BookmarkFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Favourites"
                }
                R.id.itmMyHires -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout, MyHiresFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "My hires"
                }
                R.id.itmBookedDates -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout, BookedDatesFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "My booked dates"
                }
                R.id.itmFaq -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout, FAQFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Frequently asked questions"
                }

                R.id.itmSettings -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout, SettingFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = " "
                }

            }

            return@setNavigationItemSelectedListener true
        }

        defaultFragment()

        setProfileInfo(navMain)

        checkUPIPresent(auth.currentUser?.uid.toString())
    }

    //checking if the upi id present or not
    private fun checkUPIPresent(uid: String) {
        FirebaseDatabase.getInstance().getReference("users")
            .child(uid)
            .child("payment_details")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Log.i("Snapshot", "not exist")
                        val snackBar = Snackbar.make(
                            coordinatorLayout,
                            "Payment details is not found.",
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction("FILL UP", View.OnClickListener {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        GetPaymentDetails::class.java
                                    )
                                )
                            }).show()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Snapshot error ", error.toString())
                }
            }
            )
    }

    private fun defaultFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.frameLayout, HomeFragment()
        ).commit()
        navMain.setCheckedItem(R.id.itmHome)
        supportActionBar?.title = "Home"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frameLayout)) {
            !is HomeFragment -> {
                navMain.menu.getItem(0).isChecked = true
                defaultFragment()
            }
            else -> super.onBackPressed()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpToolBar(toolbar: androidx.appcompat.widget.Toolbar) {
        toolbar.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(toolbar)
//        supportActionBar?.title = "Home"
        supportActionBar?.title = " "
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
        super.onResume()
    }

    private fun restartApp() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setProfileInfo(navMain: NavigationView) {
        dbref = FirebaseDatabase.getInstance().getReference("users")
            .child(auth.currentUser?.uid.toString())
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    tvUserName.text = user?.name?.uppercase()
                    tvLocation.text = user?.location?.capitalize()
                    Glide.with(this@MainActivity).load(user?.profileImage)
                        .into(profileImage).view
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}