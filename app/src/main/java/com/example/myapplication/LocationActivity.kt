package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.android.gms.location.*

class LocationActivity : ComponentActivity() {

    // client Google che gestisce la posizione, inizializzato dopo in onCreate
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // riceve gli aggiornamenti continui di posizione
    private lateinit var locationCallback: LocationCallback

    // lo stato Compose: ogni volta che cambia, la UI si ridisegna automaticamente
    private val locationState = mutableStateOf("Posizione non disponibile")

    // Launcher per la richiesta permessi
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                getLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                Toast.makeText(this, "Posizione approssimativa", Toast.LENGTH_SHORT).show()
                getLocation()
            }
            else -> {
                Toast.makeText(this, "Permesso negato", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            MyApplicationTheme {
                Text(
                    text = locationState.value,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Richiesta permessi
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getLocation() {
        val fineGranted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) return

        // Mostra subito l'ultima posizione cached
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                locationState.value = if (fineGranted) {
                    "Precisa: Lat ${it.latitude}, Lon ${it.longitude}"
                } else {
                    "Approssimativa: Lat ${it.latitude}, Lon ${it.longitude}"
                }
            }
        }

        // Imposta priorità in base al permesso
        val priority = if (fineGranted) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val locationRequest = LocationRequest.Builder(priority, 5000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        // object : crea una classe anonima al volo, senza darle un nome
        // LocationCallback() è la classe che estende
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    locationState.value = if (fineGranted) {
                        "Precisa: Lat ${it.latitude}, Lon ${it.longitude}"
                    } else {
                        "Approssimativa: Lat ${it.latitude}, Lon ${it.longitude}"
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}