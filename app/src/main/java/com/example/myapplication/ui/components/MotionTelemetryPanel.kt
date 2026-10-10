package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.sensor.AccelerometerState
import com.example.myapplication.sensor.SensorStatus
import com.example.myapplication.ui.theme.GarisJarakThemeTokens
import com.example.myapplication.ui.theme.StatusActiveGreen
import com.example.myapplication.ui.theme.StatusErrorRed
import com.example.myapplication.ui.theme.StatusWaitingAmber
import com.example.myapplication.ui.theme.TechnicalCodeStyle
import com.example.myapplication.ui.theme.TelemetryMonospaceStyle
import java.util.Locale
import kotlin.math.abs

/**
 * Panel Telemetri Sensor Accelerometer (Kategori Motion).
 * Menampilkan data terfilter (stabil) berdampingan dengan data mentah (raw),
 * serta visual meter analog defleksi sumbu.
 */
@Composable
fun MotionTelemetryPanel(
    data: AccelerometerState,
    modifier: Modifier = Modifier
) {
    val colors = GarisJarakThemeTokens.colors

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
                    text = "MOTION · SENSOR",
                    style = TechnicalCodeStyle,
                    color = colors.accentBrick,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "TYPE_ACCELEROMETER",
                    fontSize = 11.sp,
                    color = colors.textMuted
                )
            }

            // Lencana Status Sensor
            SensorStatusBadge(status = data.status)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Label Transparansi Filter Sinyal (Anti-Jitter)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(colors.surfaceAlt)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(colors.accentSage)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "FILTER: EMA 78% + DEAD ZONE 0.16 m/s² (ANTI-GETAR)",
                style = TechnicalCodeStyle.copy(fontSize = 9.5.sp),
                color = colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (data.status == SensorStatus.NOT_AVAILABLE) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceAlt)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Perangkat keras Accelerometer tidak ditemukan pada perangkat ini.",
                    fontSize = 12.sp,
                    color = StatusErrorRed
                )
            }
        } else {
            // Tampilan 3 Kolom Sumbu X, Y, Z (Filtered + Raw)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AxisCard(
                    axis = "X",
                    filtered = data.filteredX,
                    raw = data.rawX,
                    modifier = Modifier.weight(1f)
                )
                AxisCard(
                    axis = "Y",
                    filtered = data.filteredY,
                    raw = data.rawY,
                    modifier = Modifier.weight(1f)
                )
                AxisCard(
                    axis = "Z",
                    filtered = data.filteredZ,
                    raw = data.rawZ,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Baris Telemetri Sekunder (Magnitudo, Pitch, Roll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceAlt)
                    .border(1.dp, colors.border.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL GRAVITASI",
                        style = TechnicalCodeStyle,
                        color = colors.textMuted
                    )
                    Text(
                        text = String.format(Locale.US, "%.2f m/s²", data.magnitude),
                        style = TelemetryMonospaceStyle.copy(fontSize = 13.sp),
                        color = colors.textPrimary
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.border))

                Column {
                    Text(
                        text = "PITCH (ELEVASI)",
                        style = TechnicalCodeStyle,
                        color = colors.textMuted
                    )
                    Text(
                        text = String.format(Locale.US, "%+.1f°", data.pitch),
                        style = TelemetryMonospaceStyle.copy(fontSize = 13.sp),
                        color = colors.textPrimary
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.border))

                Column {
                    Text(
                        text = "ROLL (MIRING)",
                        style = TechnicalCodeStyle,
                        color = colors.textMuted
                    )
                    Text(
                        text = String.format(Locale.US, "%+.1f°", data.roll),
                        style = TelemetryMonospaceStyle.copy(fontSize = 13.sp),
                        color = colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun AxisCard(
    axis: String,
    filtered: Float,
    raw: Float,
    modifier: Modifier = Modifier
) {
    val colors = GarisJarakThemeTokens.colors
    val normalizedDeflection = (filtered / 9.81f).coerceIn(-1f, 1f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceAlt)
            .border(1.dp, colors.border.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AXIS $axis",
                style = TechnicalCodeStyle,
                color = colors.accentBrick,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "m/s²",
                fontSize = 10.sp,
                color = colors.textMuted
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Nilai Terfilter (Stabil & Mudah Dibaca)
        Text(
            text = String.format(Locale.US, "%+.2f", filtered),
            style = TelemetryMonospaceStyle,
            color = colors.textPrimary
        )

        // Nilai Mentah (Raw Asli dari Hardware)
        Text(
            text = String.format(Locale.US, "raw: %+.2f", raw),
            style = TechnicalCodeStyle.copy(fontSize = 9.sp),
            color = colors.textMuted
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Batang Meteran Analog Defleksi Sumbu
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(colors.border)
        ) {
            // Garis tengah netral
            Box(
                modifier = Modifier
                    .width(1.5.dp)
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .background(colors.textMuted)
            )

            // Defleksi bar
            if (normalizedDeflection != 0f) {
                val fraction = (abs(normalizedDeflection) * 0.5f).coerceIn(0f, 0.5f)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .align(if (normalizedDeflection > 0) Alignment.CenterEnd else Alignment.CenterStart)
                        .background(colors.accentBrick)
                )
            }
        }
    }
}

@Composable
fun SensorStatusBadge(
    status: SensorStatus,
    modifier: Modifier = Modifier
) {
    val colors = GarisJarakThemeTokens.colors

    val (dotColor, textLabel) = when (status) {
        SensorStatus.ACTIVE -> Pair(StatusActiveGreen, "Aktif")
        SensorStatus.WAITING_FOR_DATA -> Pair(StatusWaitingAmber, "Menunggu Data")
        SensorStatus.NOT_AVAILABLE -> Pair(StatusErrorRed, "Tidak Tersedia")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .background(colors.surfaceAlt)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = textLabel,
            style = TechnicalCodeStyle,
            color = colors.textPrimary
        )
    }
}
