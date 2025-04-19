package com.example.dnd_sheet.ui.dashboard

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var  equipmentLayout : ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)

        equipmentLayout = view.findViewById(R.id.equipment_layout)

        equipmentLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //Remove the listener before proceeding
                equipmentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Change layout width x height ratio to match background image
                val equipmentBitmap = BitmapFactory.decodeResource(resources, R.drawable.equipment)

                val layoutWidth = equipmentLayout.measuredWidth

                // Using matrix to scale imageview to fit nicely to layout
                val matrix = Matrix()
                val scale = layoutWidth / equipmentBitmap.width.toFloat()
                matrix.postScale(scale, scale)

                val resizedBitmap = Bitmap.createBitmap(equipmentBitmap, 0, 0, equipmentBitmap.width,
                    equipmentBitmap.height, matrix, false)

                // Recycle old bitmap to avoid memory leak
                equipmentBitmap.recycle()

                val context = requireContext()

                val equipmentImageView = ImageView(context)
                equipmentImageView.id = View.generateViewId()
                equipmentImageView.setImageBitmap(resizedBitmap)
                equipmentLayout.addView(equipmentImageView)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}