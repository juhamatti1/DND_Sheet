package com.example.dnd_sheet

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dnd_sheet.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException


class MainActivity : AppCompatActivity() {

    private var TAG: String = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val name = "character.json"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Pressed callback for share button
        findViewById<Button>(R.id.shareButton).setOnClickListener {
            val character = loadFromJson() ?: return@setOnClickListener
            val characterString = Json.encodeToString(character)

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, "DND_Sheet")
                putExtra(Intent.EXTRA_TEXT, characterString)
                type = "text/plain"
            }

            startActivity(sendIntent)
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_status, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        // Hide keyboard if touching outside of any EditText views
        if (event.action == MotionEvent.ACTION_DOWN) {
            val currentView = currentFocus
            if (currentView is EditText) {
                val viewRectangle = Rect()
                currentView.getGlobalVisibleRect(viewRectangle)
                if (!viewRectangle.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    currentView.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun saveToJson(characterViewModel: Character): String {
        val jsonString = Json.encodeToString(characterViewModel)
        val jsonByteArray = jsonString.toByteArray()
        applicationContext.openFileOutput(name, Context.MODE_PRIVATE).use {
            it.write(jsonByteArray)
            it.close()
        }
        return jsonString
    }

    /**
     * Loads character from JSON file
     * /param characterViewModel - view model of character where json will be loaded
     */
    fun loadFromJson(): Character? {
        val bytes: ByteArray
        try {
            applicationContext.openFileInput(name).use {
                bytes = it.readBytes()
                it.close()
            }
        } catch (e: FileNotFoundException) {
            Log.w(TAG, "\"$name\" file not found")
            return null
        }
        var character: Character? = null
        val error = kotlin.runCatching {
            character = Json.decodeFromString<Character>(bytes.decodeToString())
        }
        if(character == null || error.isFailure) {
            Log.e(TAG, error.exceptionOrNull().toString())
            return null
        }
        return character
    }
}