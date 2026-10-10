package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.sensor.CalibrationData
import com.example.myapplication.ui.theme.BrickRedPrimary
import com.example.myapplication.ui.theme.GarisJarakThemeTokens
import com.example.myapplication.ui.theme.TechnicalCodeStyle

/**
 * Tiga Tombol Kontrol Nyata untuk GARIS / JARAK:
 * 1. Kalibrasi Ulang (Tara Titik Nol): Menyimpan kemiringan genggaman saat ini sebagai referensi netral.
 * 2. Kembali ke Tengah: Mengembalikan posisi objek ke pusat arena secara mandiri.
 * 3. Tentang Sensor: Membuka dialog panduan teori sensor untuk presentasi.
 */
@Composable
fun LabActionControls(
    calibrationData: CalibrationData,
    onRecalibrate: () -> Unit,
    onResetOrb: () -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GarisJarakThemeTokens.colors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Baris 2 Tombol Operasional: Kalibrasi Ulang & Kembali ke Tengah
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Tombol 1: Kalibrasi Ulang
            ControlButton(
                title = "Kalibrasi Ulang",
                subtitle = if (calibrationData.isCalibrated) "Tara Tersimpan (Aktif)" else "Tetapkan Titik Nol",
                accentColor = colors.accentBrick,
                isActiveIndicator = calibrationData.isCalibrated,
                onClick = onRecalibrate,
                modifier = Modifier.weight(1f)
            )

            // Tombol 2: Kembali ke Tengah
            ControlButton(
                title = "Kembali ke Tengah",
                subtitle = "Pusatkan Objek Arena",
                accentColor = colors.accentSage,
                isActiveIndicator = false,
                onClick = onResetOrb,
                modifier = Modifier.weight(1f)
            )
        }

        // Tombol 3: Tentang Sensor (Lebar Penuh)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = colors.accentSage),
                    onClick = onOpenDetails
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(colors.sageLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "i",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentSage
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tentang Sensor & Teori",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Prinsip kerja Accelerometer, Proximity & Lifecycle Android",
                            fontSize = 11.sp,
                            color = colors.textMuted
                        )
                    }
                }

                Text(
                    text = "BUKA →",
                    style = TechnicalCodeStyle,
                    color = colors.accentSage,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ControlButton(
    title: String,
    subtitle: String,
    accentColor: Color,
    isActiveIndicator: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GarisJarakThemeTokens.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(
                width = if (isActiveIndicator) 1.5.dp else 1.dp,
                color = if (isActiveIndicator) accentColor else colors.border,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = accentColor),
                onClick = onClick
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                if (isActiveIndicator) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = colors.textMuted,
                lineHeight = 14.sp
            )
        }
    }
}
