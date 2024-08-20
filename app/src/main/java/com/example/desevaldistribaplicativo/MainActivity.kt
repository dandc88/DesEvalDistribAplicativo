package com.example.desevaldistribaplicativo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.desevaldistribaplicativo.databinding.ActivityMainBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cambiar el color de fondo según el flavor
        val backgroundColor = when (BuildConfig.FLAVOR) {
            "dev" -> R.color.dev_background
            "qa" -> R.color.qa_background
            "prod" -> R.color.prod_background
            else -> R.color.white // Default por si acaso
        }
        binding.main.setBackgroundColor(getColor(backgroundColor))

        // Mostrar el nombre del flavor en el TextView
        binding.textView.text = BuildConfig.FLAVOR

        // Configurar el click listener para probar Crashlytics
        binding.myButton.setOnClickListener {
            val logMessage = "Este es un mensaje de prueba(\"Desarrollo de aplicaciones empresariales Android\")"
            val buildVariant = BuildConfig.FLAVOR

            // Enviar log y key a Firebase Crashlytics
            FirebaseCrashlytics.getInstance().log(logMessage)
            FirebaseCrashlytics.getInstance().setCustomKey("app_env", buildVariant)

            // Lanza una excepción intencional para probar Crashlytics
            throw RuntimeException("Test Crash")
        }
    }
}