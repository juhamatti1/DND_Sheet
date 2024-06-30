package com.example.dnd_sheet.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    // ? makes possible that variable can be declared as null
    private var _binding: FragmentHomeBinding? = null
    private val TAG: String = "HomeFragment"

    // This property is only valid between onCreateView and
    // onDestroyView.
    // get() is property method which is called when variable is read
    // So when binding variable is used get() method is called and get()
    // returns _binding. "!!" marks is "non-null assertion operator" which means that we are
    // telling to Kotlin's type system _binding is not null.
    // This is pretty hacky to me.
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()

        val statsLayout: RelativeLayout = view?.findViewById(R.id.stats_layout) ?: return
        statsLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //Remove the listener before proceeding
                statsLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Change layout width x height ratio to match background image
                val statsBitmap = BitmapFactory.decodeResource(resources, R.drawable.stats)

                val layoutWidth = statsLayout.measuredWidth
                val layoutHeight = statsLayout.measuredHeight

                // Using matrix to scale imageview to fit nicely to linear layout
                val matrix = Matrix()
                val scale = layoutWidth / statsBitmap.width.toFloat()
                matrix.postScale(scale,scale)

                val resizedBitmap = Bitmap.createBitmap(statsBitmap, 0, 0, statsBitmap.width,
                    statsBitmap.height, matrix, false)

                // Recycle old bitmap to avoid memory leak
                statsBitmap.recycle()

                val context = requireContext()
                val statsImageView = ImageView(context)
                statsImageView.setImageBitmap(resizedBitmap)
                statsLayout.addView(statsImageView)

                Log.i(TAG, "stats w: fragment w:$layoutWidth h:$layoutHeight")


//                val statsDrawable = ContextCompat.getDrawable(context, R.drawable.stats)
//                statsLayout.background = ContextCompat.getDrawable(context, R.drawable.stats)
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        val statsLayout: LinearLayout = view?.findViewById(R.id.stats_layout) ?: return root
        statsLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //Remove the listener before proceeding
                statsLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Change layout width x height ratio to match background image
                val statsBitmap = BitmapFactory.decodeResource(resources, R.drawable.stats)
                val sdw = statsBitmap.width
                val sdh = statsBitmap.height

                val hw = statsLayout.measuredWidth
                val hh = statsLayout.measuredHeight
                Log.i(TAG, "stats w:$sdw h:$sdh, fragment w:$hw h:$hh")

            }
        })

//        val homeFragmentLayout: LinearLayout = layoutInflater.inflate(R.layout.fragment_home, null) as LinearLayout

        // the object keyword is used to create an anonymous object that implements the
        // ViewTreeObserver.OnGlobalLayoutListener interface

//        val l: LinearLayout = view?.findViewById(R.id.stats_layout) ?: return root
//        val hw = homeFragmentLayout.measuredWidth
//        val hh = homeFragmentLayout.measuredHeight
//        Log.i(TAG, "layout w:${l.measuredWidth} h:${l.measuredHeight} fragment w:$hw h:$hh")
//
//        homeFragmentLayout.viewTreeObserver.addOnGlobalLayoutListener (object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                //Remove the listener before proceeding
//                homeFragmentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
//
//                // Change layout width x height ratio to match background image
//                val statsBitmap = BitmapFactory.decodeResource(resources, R.drawable.stats)
//                val sdw = statsBitmap.width
//                val sdh = statsBitmap.height
//
//                val hw = homeFragmentLayout.measuredWidth
//                val hh = homeFragmentLayout.measuredHeight
//                Log.i(TAG, "stats w:$sdw h:$sdh, fragment w:$hw h:$hh")
//            }
//        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}