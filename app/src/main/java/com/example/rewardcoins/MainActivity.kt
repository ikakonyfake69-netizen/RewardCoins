
package com.example.rewardcoins

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rewardcoins.ui.theme.RewardCoinsTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.AdError

class MainActivity : ComponentActivity() {

    private var rewardedAd: RewardedAd? = null

    private val adUnitId =
        "ca-app-pub-3940256099942544/5224354917"

    private var monedas by mutableIntStateOf(0)
    private var cargando by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RewardCoinsTheme {
                PantallaPrincipal(
                    monedas = monedas,
                    cargando = cargando,
                    onVerAnuncio = { mostrarAnuncio() }
                )
            }
        }

        Thread {
            MobileAds.initialize(this@MainActivity) {
                runOnUiThread { cargarAnuncio() }
            }
        }.start()
    }

    private fun cargarAnuncio() {
        cargando = true

        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            this,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    cargando = false

                    ad.fullScreenContentCallback =
                        object : FullScreenContentCallback() {

                            override fun onAdDismissedFullScreenContent() {
                                rewardedAd = null
                                cargarAnuncio()
                            }

                            override fun onAdFailedToShowFullScreenContent(
                                error: AdError
                            ) {
                                rewardedAd = null
                                cargarAnuncio()
                            }
                        }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    cargando = false
                    Toast.makeText(
                        this@MainActivity,
                        "Error al cargar anuncio: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    private fun mostrarAnuncio() {
        val anuncio = rewardedAd

        if (anuncio != null) {
            rewardedAd = null
            cargando = true

            anuncio.show(this) {
                monedas += 50

                Toast.makeText(
                    this,
                    "¡Ganaste 50 monedas!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this,
                "Anuncio no disponible",
                Toast.LENGTH_SHORT
            ).show()
            cargarAnuncio()
        }
    }
}

@Composable
fun PantallaPrincipal(
    monedas: Int,
    cargando: Boolean,
    onVerAnuncio: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "RewardCoins",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Monedas acumuladas",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "🪙 $monedas",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onVerAnuncio,
            enabled = !cargando
        ) {
            Text(
                if (cargando)
                    "Cargando anuncio..."
                else
                    "Ver anuncio y ganar 50 monedas"
            )
        }
    }
}
