package com.example.dnd_sheet

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dnd_sheet.databinding.ActivityMainBinding
import com.example.dnd_sheet.ui.Tools
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class MainActivity : AppCompatActivity() {

    private var TAG: String = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tools.init(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        openFileActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            result ->

            val jsonIntent: Intent? = result.data
            val jsonString = jsonIntent?.getStringExtra("result_key") ?: ""
        }

        // Pressed callback for share button
        findViewById<Button>(R.id.shareButton).setOnClickListener {

            val character = Tools.loadFromLocalJson() ?: return@setOnClickListener
            val characterString = Json.encodeToString(character)

            try {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TITLE, "DND_Sheet")
                    putExtra(Intent.EXTRA_TEXT, characterString)
                    type = "text/plain"
                }

                val chooserIntent = Intent.createChooser(sendIntent, "Share Character sheet as json")

                if(sendIntent.resolveActivity(packageManager) != null) {
                    startActivity(chooserIntent)
                } else {
                    Toast.makeText(this, "No app found to handle sharing", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IllegalArgumentException) {
                // This can happen if FileProvider is not configured correctly
                e.printStackTrace()
                Toast.makeText(this, "FileProvider error: ${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error sharing file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        findViewById<Button>(R.id.openButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, filesDir)
            }

            openFileActivityLauncher.launch(intent)
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

    private lateinit var openFileActivityLauncher: ActivityResultLauncher<Intent>

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
}