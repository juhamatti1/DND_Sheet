package com.example.dnd_sheet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dnd_sheet.databinding.ActivityMainBinding
import com.example.dnd_sheet.ui.Tools
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    private var TAG: String = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHost.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_basic_name,
                R.id.navigation_status,
                R.id.navigation_equipment,
                R.id.navigation_personal_traits
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)

        val menuButton = findViewById<Button>(R.id.menu_button)
        menuButton.setOnClickListener { anchor ->
            showPopupMenu(anchor)
        }

        openFileActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }

            val fileUri: Uri = result.data?.data ?: return@registerForActivityResult
            val jsonString = readTextFromUri(fileUri)
            if (jsonString.isEmpty()) {
                return@registerForActivityResult
            }
            try {
                val character = Json.decodeFromString<Character>(jsonString)
                // Save loaded character to singleton instance
                Character.getInstance(character)
                Tools.saveCharacterToFile(applicationContext)

                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as? NavHostFragment
                        ?: return@registerForActivityResult
                val currentFragment =
                    navHostFragment.childFragmentManager.primaryNavigationFragment
                        ?: return@registerForActivityResult

                // Reloading views for current fragment so updated Character data is shown
                var ft = navHostFragment.childFragmentManager.beginTransaction()
                ft.setReorderingAllowed(true)
                ft.detach(currentFragment).commit()
                ft = navHostFragment.childFragmentManager.beginTransaction()
                ft.attach(currentFragment)
                ft.commit()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error parsing file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showPopupMenu(anchor: View) {
        val context = anchor.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val parentForInflation = anchor.rootView as ViewGroup

        val popupView = inflater.inflate(R.layout.menu_buttons, parentForInflation, false)

        popupView.findViewById<Button>(R.id.saveButton).setOnClickListener {
            Log.i(TAG, "save button pressed")

            val character =
                Tools.loadCharacterFromFile(applicationContext) ?: return@setOnClickListener
            val characterString = Json.encodeToString(character)

            try {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TITLE, "DND_Sheet")
                    putExtra(Intent.EXTRA_TEXT, characterString)
                    type = "text/plain"
                }

                val chooserIntent =
                    Intent.createChooser(sendIntent, "Share Character sheet as json")

                if (sendIntent.resolveActivity(packageManager) != null) {
                    startActivity(chooserIntent)
                } else {
                    Toast.makeText(this, "No app found to handle sharing", Toast.LENGTH_SHORT)
                        .show()
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

        popupView.findViewById<Button>(R.id.loadButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, filesDir)
            }

            openFileActivityLauncher.launch(intent)
        }

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.showAsDropDown(anchor)
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

    // Helper function to read text from a Uri
    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "Error reading text from Uri", e)
            Toast.makeText(this, "Error reading file: ${e.message}", Toast.LENGTH_LONG).show()

        }
        return stringBuilder.toString()
    }
}