package com.example.myapplication.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.sensor.ArenaObjectState
import com.example.myapplication.ui.theme.BrickRedCore
import com.example.myapplication.ui.theme.BrickRedDeep
import com.example.myapplication.ui.theme.BrickRedGlow
import com.example.myapplication.ui.theme.BrickRedPrimary
import com.example.myapplication.ui.theme.GarisJarakThemeTokens
import com.example.myapplication.ui.theme.ProximityFieldBorder
import com.example.myapplication.ui.theme.TechnicalCodeStyle
import java.util.Locale
import kotlin.math.hypot
import kotlin.math.roundToInt

/**
 * Komponen Utama: Arena Geometris "GARIS / JARAK".
 *
 * Menggabungkan dua sensor secara nyata:
 * 1. Accelerometer menggerakkan objek interaktif sepanjang sumbu X & Y secara stabil (anti-jitter).
 * 2. Proximity Sensor memicu medan jarak (near-field radar / aura) saat sensor mendeteksi jarak DEKAT.
 */
@Composable
fun GeometricArena(
    arenaState: ArenaObjectState,
    modifier: Modifier = Modifier
) {
    val colors = GarisJarakThemeTokens.colors
    val density = LocalDensity.current

    // Penghitungan rasio defleksi total dari titik tengah
    val deflectionRatio = hypot(arenaState.normalizedX, arenaState.normalizedY).coerceIn(0f, 1f)
    val deflectionPercent = (deflectionRatio * 100f).roundToInt()

    // Animasi denyut radar saat Proximity dalam kondisi DEKAT
    val infiniteTransition = rememberInfiniteTransition(label = "proximityRadar")
    val proximityPulseRadius by infiniteTransition.animateFloat(
        initialValue = 28f,
        targetValue = 46f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "proximityPulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(
                width = if (arenaState.isNearFieldActive) 1.5.dp else 1.dp,
                color = if (arenaState.isNearFieldActive) ProximityFieldBorder else colors.border,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        // Baris Header Arena & Indikator Sensor Ganda
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (arenaState.isNearFieldActive) ProximityFieldBorder else colors.accentBrick)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = "ARENA GEOMETRIS",
                    style = TechnicalCodeStyle,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Indikator Interaksi Proximity & Defleksi
            if (arenaState.isNearFieldActive) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BrickRedPrimary.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "● MEDAN JARAK: DEKAT",
                        style = TechnicalCodeStyle,
                        color = BrickRedPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = String.format(Locale.US, "DEFLEKSI: %d%%", deflectionPercent),
                    style = TechnicalCodeStyle,
                    color = if (deflectionPercent > 65) colors.accentBrick else colors.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Kanvas Area Eksperimen Geometris dengan Batas Responsif
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceAlt)
                .border(1.dp, colors.border, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            val objectSizeDp = 42.dp
            val objectSizePx = with(density) { objectSizeDp.toPx() }

            // Batas fisik gerak objek dihitung proporsional dari ukuran canvas aktual
            val maxBoundX = (constraints.maxWidth.toFloat() - objectSizePx) / 2f
            val maxBoundY = (constraints.maxHeight.toFloat() - objectSizePx) / 2f

            // Target offset dari accelerometer yang telah terfilter dead zone (-1f .. +1f)
            val targetPxX = (arenaState.normalizedX * maxBoundX).coerceIn(-maxBoundX, maxBoundX)
            val targetPxY = (arenaState.normalizedY * maxBoundY).coerceIn(-maxBoundY, maxBoundY)

            // Animasi fisika pegas berbobot alami (bergerak stabil tanpa getaran liar)
            val animatedX by animateFloatAsState(
                targetValue = targetPxX,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "objectX"
            )

            val animatedY by animateFloatAsState(
                targetValue = targetPxY,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "objectY"
            )

            // Menggambar kisi-kisi ukur, garis sumbu X/Y, cincin konsentris, dan vektor arah
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                val minDimension = minOf(size.width, size.height)

                val ringRadius1 = minDimension * 0.16f
                val ringRadius2 = minDimension * 0.32f
                val ringRadius3 = minDimension * 0.46f

                val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

                // Garis Kisi-Kisi Geometris Halus
                val step = 30.dp.toPx()
                var x = step
                while (x < size.width) {
                    drawLine(
                        color = colors.gridLine,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += step
                }
                var y = step
                while (y < size.height) {
                    drawLine(
                        color = colors.gridLine,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += step
                }

                // Cincin Konsentris Skala Jarak
                drawCircle(
                    color = colors.axisLine,
                    radius = ringRadius1,
                    center = centerOffset,
                    style = Stroke(width = 1.2f, pathEffect = dashedEffect)
                )
                drawCircle(
                    color = colors.axisLine,
                    radius = ringRadius2,
                    center = centerOffset,
                    style = Stroke(width = 1.2f, pathEffect = dashedEffect)
                )
                drawCircle(
                    color = colors.axisLine.copy(alpha = 0.6f),
                    radius = ringRadius3,
                    center = centerOffset,
                    style = Stroke(width = 1.8f)
                )

                // Garis Sumbu Kartesian X & Y
                drawLine(
                    color = colors.axisLine,
                    start = Offset(0f, centerOffset.y),
                    end = Offset(size.width, centerOffset.y),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = colors.axisLine,
                    start = Offset(centerOffset.x, 0f),
                    end = Offset(centerOffset.x, size.height),
                    strokeWidth = 1.5f
                )

                // Reticle Target Pusat
                drawCircle(
                    color = colors.textPrimary,
                    radius = 3.5.dp.toPx(),
                    center = centerOffset
                )

                // Vektor Garis Arah Gerakan
                val objectCenter = Offset(centerOffset.x + animatedX, centerOffset.y + animatedY)
                drawLine(
                    color = BrickRedPrimary.copy(alpha = 0.45f),
                    start = centerOffset,
                    end = objectCenter,
                    strokeWidth = 2.dp.toPx()
                )

                // EFES NYATA SENSOR 2 (PROXIMITY): Gambar Aura Radar Medan Jarak jika DEKAT
                if (arenaState.isNearFieldActive) {
                    drawCircle(
                        color = BrickRedPrimary.copy(alpha = 0.18f),
                        radius = (proximityPulseRadius * density.density),
                        center = objectCenter
                    )
                    drawCircle(
                        color = BrickRedPrimary.copy(alpha = 0.65f),
                        radius = (proximityPulseRadius * density.density),
                        center = objectCenter,
                        style = Stroke(width = 1.5.dp.toPx(), pathEffect = dashedEffect)
                    )
                }
            }

            // Label Sumbu di Empat Sisi Geometris
            Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                Text(
                    text = "−Y (DEPAN)",
                    style = TechnicalCodeStyle,
                    color = colors.textMuted,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Text(
                    text = "+Y (BELAKANG)",
                    style = TechnicalCodeStyle,
                    color = colors.textMuted,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                Text(
                    text = "−X (KIRI)",
                    style = TechnicalCodeStyle,
                    color = colors.textMuted,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "+X (KANAN)",
                    style = TechnicalCodeStyle,
                    color = colors.textMuted,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            // Objek Interaktif Geometris (Batu Ukur / Disc Merah Bata)
            Box(
                modifier = Modifier
                    .offset { IntOffset(animatedX.roundToInt(), animatedY.roundToInt()) }
                    .size(objectSizeDp)
                    .shadow(8.dp, CircleShape, ambientColor = BrickRedGlow, spotColor = BrickRedPrimary)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                BrickRedCore,
                                BrickRedPrimary,
                                BrickRedDeep
                            )
                        )
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.75f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Titik Reticle Presisi di Pusat Objek
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Petunjuk Operasional & Nilai Koordinat Bawah
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (arenaState.isNearFieldActive) "Sensor Proximity mendeteksi halangan dekat." else "Miringkan HP untuk gerak. Tutup sensor atas untuk jarak.",
                fontSize = 11.sp,
                color = if (arenaState.isNearFieldActive) BrickRedPrimary else colors.textSecondary,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = String.format(Locale.US, "NORM [%+.2f, %+.2f]", arenaState.normalizedX, arenaState.normalizedY),
                style = TechnicalCodeStyle,
                color = colors.textMuted
            )
        }
    }
}
