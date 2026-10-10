package com.example.myapplication.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.sensor.LightFallbackData
import com.example.myapplication.sensor.ProximityState
import com.example.myapplication.sensor.ProximityStateData
import com.example.myapplication.sensor.SensorStatus
import com.example.myapplication.ui.theme.BigStatusStyle
import com.example.myapplication.ui.theme.BrickRedPrimary
import com.example.myapplication.ui.theme.GarisJarakThemeTokens
import com.example.myapplication.ui.theme.MutedSageAccent
import com.example.myapplication.ui.theme.StatusErrorRed
import com.example.myapplication.ui.theme.TechnicalCodeStyle
import com.example.myapplication.ui.theme.TelemetryMonospaceStyle
import java.util.Locale

/**
 * Panel Sensor Kedua: Proximity (Kategori Position) & Fallback Lingkungan.
 * Menampilkan status DEKAT / JAUH aktual, pembacaan jarak (cm),
 * radar optik, serta status sensor cahaya cadangan jika diperlukan.
 */
@Composable
fun PositionSensorPanel(
    proximityData: ProximityStateData,
    lightFallbackData: LightFallbackData,
    modifier: Modifier = Modifier
) {
    val colors = GarisJarakThemeTokens.colors

    val statusBgColor by animateColorAsState(
        targetValue = when (proximityData.state) {
            ProximityState.NEAR -> BrickRedPrimary.copy(alpha = 0.12f)
            ProximityState.FAR -> MutedSageAccent.copy(alpha = 0.12f)
            else -> colors.surfaceAlt
        },
        animationSpec = tween(350),
        label = "proxBgAnim"
    )

    val statusTextColor by animateColorAsState(
        targetValue = when (proximityData.state) {
            ProximityState.NEAR -> BrickRedPrimary
            ProximityState.FAR -> MutedSageAccent
            else -> colors.textMuted
        },
        animationSpec = tween(350),
        label = "proxTextAnim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        // Baris Header Panel & Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "POSITION · SENSOR",
                    style = TechnicalCodeStyle,
                    color = colors.accentSage,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "TYPE_PROXIMITY (JARAK DEKAT)",
                    fontSize = 11.sp,
                    color = colors.textMuted
                )
            }

            // Lencana Status Sensor
            SensorStatusBadge(status = proximityData.status)
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (proximityData.status == SensorStatus.NOT_AVAILABLE) {
            // Tampilan saat Proximity tidak ada (dengan opsi fallback ke Light Sensor)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceAlt)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Sensor Proximity perangkat keras tidak terdeteksi pada ponsel ini.",
                    fontSize = 12.sp,
                    color = StatusErrorRed,
                    fontWeight = FontWeight.Medium
                )

                if (lightFallbackData.isAvailable) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Alternatif Aktif: Light Sensor (Environment) terbaca: ${String.format(Locale.US, "%.1f", lightFallbackData.lux)} lx",
                        fontSize = 11.sp,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Tampilan Status Besar: DEKAT / JAUH
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "KONDISI DETEKSI",
                        style = TechnicalCodeStyle,
                        color = colors.textMuted
                    )
                    Text(
                        text = proximityData.state.label,
                        style = BigStatusStyle,
                        color = statusTextColor
                    )
                }

                // Kotak Meteran Status Visual
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusBgColor)
                        .border(1.5.dp, statusTextColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (proximityData.state == ProximityState.NEAR) "HALANGAN AKTIF" else "BEBAS HAMBATAN",
                        style = TechnicalCodeStyle,
                        color = statusTextColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rincian Nilai Jarak Riil
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceAlt)
                    .border(1.dp, colors.border.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "JARAK AKTUAL",
                        style = TechnicalCodeStyle,
                        color = colors.textMuted
                    )
                    Text(
                        text = if (proximityData.distanceCm >= 0f) {
                            String.format(Locale.US, "%.1f cm", proximityData.distanceCm)
                        } else {
                            "Menunggu data..."
                        },
                        style = TelemetryMonospaceStyle.copy(fontSize = 14.sp),
                        color = colors.textPrimary
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.border))

                Column {
                    Text(
                        text = "RENTANG MAKSIMUM",
                        style = TechnicalCodeStyle,
                        color = colors.textMuted
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f cm", proximityData.maxRangeCm),
                        style = TelemetryMonospaceStyle.copy(fontSize = 14.sp),
                        color = colors.textPrimary
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.border))

                Column {
                    Text(
                        text = "TIPE DETEKTOR",
                        style = TechnicalCodeStyle,
                        color = colors.textMuted
                    )
                    Text(
                        text = if (proximityData.maxRangeCm <= 5f) "BINER OPTIK" else "KONTINU",
                        style = TelemetryMonospaceStyle.copy(fontSize = 13.sp),
                        color = colors.accentSage
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Catatan Eksperimen untuk Siswa
            Text(
                text = "Dekatkan tangan ke speaker atas ponsel untuk mengaktifkan medan jarak (Near-Field Shield) pada arena.",
                fontSize = 11.sp,
                color = colors.textMuted,
                lineHeight = 15.sp
            )
        }
    }
}
