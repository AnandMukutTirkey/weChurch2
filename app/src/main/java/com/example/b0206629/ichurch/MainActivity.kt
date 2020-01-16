package com.example.b0206629.ichurch

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.Window
import com.example.b0206629.ichurch.fragments.FavouriteFragment
import com.example.b0206629.ichurch.fragments.HomeFragment
import com.example.b0206629.ichurch.fragments.NotesFragment
import com.example.b0206629.ichurch.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(){
    private lateinit var toolBar : ActionBar
    private var lastTheme = "light"
    private var isAutomatic = true
    private val navigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.navigation_home ->  {
                loadFragment((HomeFragment.newInstance()));
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_note -> {
                loadFragment(NotesFragment.newInstance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favorite -> {
                loadFragment(FavouriteFragment.newInstance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                loadFragment(SettingsFragment.newInstance(lastTheme, isAutomatic))
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        lastTheme = getSharedPreferences("appData", Context.MODE_PRIVATE).getString("theme","light")
        isAutomatic = getSharedPreferences("appData", Context.MODE_PRIVATE).getBoolean("isAutomatic",true)
        if (isAutomatic){
            var timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (timeOfDay >= 18|| timeOfDay <= 6){
                lastTheme = "dark"
            } else {
                lastTheme = "light"
            }
        }
        setTheme(lastTheme)
        setContentView(R.layout.activity_main)
        toolBar = supportActionBar!! ; toolBar.hide()
        navigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        var lastFragment : String = getSharedPreferences("appData", Context.MODE_PRIVATE).getString("lastFragment","HomeFragment")
        loadLastFragment(lastFragment)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("tag", "backpressed")
    }

    private fun setTheme(theme : String){
        when(theme){
            "light"->setTheme(R.style.AppTheme)
            "dark"->setTheme(R.style.DarkTheme)
        }
    }

    private fun loadLastFragment(fragmentName : String){
        when(fragmentName){
            "FavouriteFragment"->{
                loadFragment(FavouriteFragment.newInstance())
                navigationView.selectedItemId = R.id.navigation_favorite
            }
            "HomeFragment"->{
                loadFragment(HomeFragment.newInstance())
                navigationView.selectedItemId = R.id.navigation_home
            }
            "NotesFragment"->{
                loadFragment(NotesFragment.newInstance())
                navigationView.selectedItemId = R.id.navigation_note
            }
            "SettingsFragment"->{
                loadFragment(SettingsFragment.newInstance(lastTheme, isAutomatic))
                navigationView.selectedItemId = R.id.navigation_settings
            }
            else->{
                loadFragment(HomeFragment.newInstance())
                navigationView.selectedItemId = R.id.navigation_home
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
