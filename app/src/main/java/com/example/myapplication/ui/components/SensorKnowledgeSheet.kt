package com.example.myapplication.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.BrickRedPrimary
import com.example.myapplication.ui.theme.GarisJarakThemeTokens
import com.example.myapplication.ui.theme.MutedSageAccent
import com.example.myapplication.ui.theme.TechnicalCodeStyle

/**
 * Modal Bottom Sheet berisi panduan sains dan prinsip teknis Multi-Sensor Android
 * untuk presentasi tugas di depan guru penguji (SMK RPL).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorKnowledgeSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    val colors = GarisJarakThemeTokens.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Dialog
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GARIS / JARAK · CATATAN TEORI",
                        style = TechnicalCodeStyle,
                        color = colors.accentBrick,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Prinsip Multi-Sensor",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.sageLight)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SMK RPL",
                        style = TechnicalCodeStyle,
                        color = colors.accentSage
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Modul Accelerometer (Motion)
            TheorySectionBox(
                badge = "01 · MOTION",
                title = "Sensor Accelerometer (TYPE_ACCELEROMETER)",
                accentColor = BrickRedPrimary
            ) {
                Text(
                    text = "Membaca percepatan inersia fisik dan gaya gravitasi bumi pada sumbu X, Y, Z dalam satuan m/s².",
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                TheoryPointItem(label = "Sumbu X & Y", desc = "Digunakan untuk mengendalikan posisi objek interaktif pada arena geometris.")
                TheoryPointItem(label = "Sumbu Z", desc = "Membaca gravitasi vertikal ~9.81 m/s² saat HP diletakkan mendatar di atas meja.")
                TheoryPointItem(label = "Solusi Anti-Getar (Anti-Jitter)", desc = "Menerapkan Exponential Moving Average (EMA 78%) dan Dead Zone 0.16 m/s² sehingga saat HP diam, objek tidak bergetar liar.")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Modul Proximity (Position)
            TheorySectionBox(
                badge = "02 · POSITION",
                title = "Sensor Proximity (TYPE_PROXIMITY)",
                accentColor = MutedSageAccent
            ) {
                Text(
                    text = "Mendeteksi keberadaan objek/tangan yang mendekati speaker atas ponsel melalui pantulan sensor optik inframerah.",
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                TheoryPointItem(label = "Status DEKAT", desc = "Terdeteksi saat jarak < 5 cm atau 0.0 cm (misal: tangan menutup bagian atas layar). Memicu radar medan jarak aktif di arena.")
                TheoryPointItem(label = "Status JAUH", desc = "Terdeteksi saat tidak ada halangan (jarak >= 5 cm). Arena kembali ke mode pengukuran garis standar.")
                TheoryPointItem(label = "Kategori Berbeda", desc = "Memenuhi syarat tugas guru: Accelerometer (Motion) dan Proximity (Position) berasal dari dua kategori Android berbeda.")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Modul Lifecycle Android Wajib
            TheorySectionBox(
                badge = "03 · LIFECYCLE WAJIB",
                title = "Manajemen Daur Hidup Sensor",
                accentColor = colors.accentSage
            ) {
                Text(
                    text = "Sensor perangkat keras wajib dikelola sesuai daur hidup (lifecycle) Activity:",
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                TheoryPointItem(
                    label = "onResume()",
                    desc = "Mendaftarkan listener sensor (registerListener) saat Activity aktif di layar depan. Menggunakan SENSOR_DELAY_GAME untuk Accelerometer dan SENSOR_DELAY_NORMAL untuk Proximity."
                )
                TheoryPointItem(
                    label = "onPause()",
                    desc = "Melepas listener sensor (unregisterListener) segera saat Activity dijeda guna menghentikan konsumsi baterai dan mencegah memory leak."
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tombol Tutup
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accentBrick,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Tutup Panduan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun TheorySectionBox(
    badge: String,
    title: String,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    val colors = GarisJarakThemeTokens.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceAlt)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = badge,
                style = TechnicalCodeStyle,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        content()
    }
}

@Composable
private fun TheoryPointItem(label: String, desc: String) {
    val colors = GarisJarakThemeTokens.colors

    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(
            text = "• $label",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Text(
            text = desc,
            fontSize = 12.sp,
            color = colors.textSecondary,
            lineHeight = 16.sp,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}
