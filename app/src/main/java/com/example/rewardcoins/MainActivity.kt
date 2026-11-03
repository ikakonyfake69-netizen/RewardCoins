
package com.example.rewardcoins

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rewardcoins.ui.theme.RewardCoinsTheme
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

private val Fondo = Color(0xFF111827)
private val Tarjeta = Color(0xFF1F2937)
private val Morado = Color(0xFF7C3AED)
private val Dorado = Color(0xFFFBBF24)

class MainActivity : ComponentActivity() {

    private var rewardedAd: RewardedAd? = null

    private lateinit var monedasDataStore: MonedasDataStore

    private val adUnitId =
        "ca-app-pub-3940256099942544/5224354917"

    private var monedas by mutableIntStateOf(0)
    private var cargando by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        monedasDataStore = MonedasDataStore(applicationContext)

        lifecycleScope.launch {
            monedasDataStore.monedas.collect { saldoGuardado ->
                monedas = saldoGuardado
            }
        }

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

        RewardedAd.load(
            this,
            adUnitId,
            AdRequest.Builder().build(),
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
                        "No se pudo cargar el anuncio",
                        Toast.LENGTH_SHORT
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

                lifecycleScope.launch {
                    monedasDataStore.agregarMonedas(50)

                    Toast.makeText(
                        this@MainActivity,
                        "¡Ganaste 50 monedas!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "🪙 RewardCoins",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Gana monedas viendo anuncios",
            color = Color.LightGray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF251B43)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "TU SALDO ACTUAL",
                    color = Color(0xFFC4B5FD),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🪙",
                    fontSize = 55.sp
                )

                Text(
                    text = "$monedas",
                    color = Color.White,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Monedas acumuladas",
                    color = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Tarjeta
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(
                    text = "🎁 Recompensa diaria",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Mira un anuncio de prueba y gana monedas virtuales.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    color = Color(0xFF31215D),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "+50 🪙 por anuncio completado",
                        color = Dorado,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onVerAnuncio,
                    enabled = !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Morado,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (cargando)
                            "Cargando anuncio..."
                        else
                            "▶ Ver anuncio y ganar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "✓ Anuncios de prueba de Google AdMob",
            color = Color(0xFF34D399),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}
