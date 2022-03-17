package com.example.logintry2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.chrisbanes.photoview.PhotoView

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val photoView: PhotoView =findViewById(R.id.metro_con)
        photoView.setImageResource(R.drawable.hong)
    }
}
