package com.example.myapplication

import com.example.myapplication.sensor.ProximityState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.sign

/**
 * Unit Test untuk memverifikasi logika matematika filter anti-jitter,
 * ambang batas zona mati (dead zone), batas koordinat arena, dan deteksi proximity.
 */
class SensorLogicTest {

    private val deadZoneThreshold = 0.16f

    private fun applyDeadZone(delta: Float): Float {
        return if (abs(delta) < deadZoneThreshold) {
            0f
        } else {
            sign(delta) * (abs(delta) - deadZoneThreshold)
        }
    }

    @Test
    fun testDeadZone_suppressesMicroJitterWhenStationary() {
        // Fluktuasi kecil saat HP diletakkan di atas meja (misal: 0.05 m/s² atau -0.12 m/s²)
        // harus dipotong menjadi 0.0f (tidak ada pergerakan objek)
        assertEquals(0.0f, applyDeadZone(0.05f), 0.0001f)
        assertEquals(0.0f, applyDeadZone(-0.10f), 0.0001f)
        assertEquals(0.0f, applyDeadZone(0.15f), 0.0001f)
        assertEquals(0.0f, applyDeadZone(-0.159f), 0.0001f)

        // Gerakan nyata yang melewati ambang batas dead-zone (misal: 0.50 m/s²)
        // harus bergerak secara proporsional halus (0.50 - 0.16 = 0.34)
        assertEquals(0.34f, applyDeadZone(0.50f), 0.0001f)
        assertEquals(-0.34f, applyDeadZone(-0.50f), 0.0001f)
    }

    @Test
    fun testEMAFilter_smoothsSignalNoise() {
        // Uji rumus low-pass Exponential Moving Average: filtered = alpha * prev + (1 - alpha) * raw
        val alpha = 0.78f
        var filtered = 0.0f

        // Sinyal berfluktuasi antara 1.0 dan 1.2
        filtered = (alpha * filtered) + ((1f - alpha) * 1.0f)
        assertEquals(0.22f, filtered, 0.001f)

        filtered = (alpha * filtered) + ((1f - alpha) * 1.2f)
        // 0.78 * 0.22 + 0.22 * 1.2 = 0.1716 + 0.264 = 0.4356
        assertEquals(0.4356f, filtered, 0.001f)
    }

    @Test
    fun testProximityNearFarLogic() {
        val maxRange = 5.0f

        fun evaluateProximity(distance: Float): ProximityState {
            val isNear = (distance < maxRange) || (distance == 0.0f)
            return if (isNear) ProximityState.NEAR else ProximityState.FAR
        }

        // 0.0 cm (tangan menutup sensor) -> DEKAT
        assertEquals(ProximityState.NEAR, evaluateProximity(0.0f))
        // 2.0 cm (objek dekat) -> DEKAT
        assertEquals(ProximityState.NEAR, evaluateProximity(2.0f))
        // 5.0 cm (bebas halangan) -> JAUH
        assertEquals(ProximityState.FAR, evaluateProximity(5.0f))
        // 8.0 cm (jauh) -> JAUH
        assertEquals(ProximityState.FAR, evaluateProximity(8.0f))
    }

    @Test
    fun testArenaBoundaryClamping() {
        // Uji agar nilai normalisasi tidak pernah melompat keluar dari [-1.0, +1.0]
        val extremeDeltaPos = 30.0f
        val normXPos = (-extremeDeltaPos / 6.5f).coerceIn(-1f, 1f)
        assertEquals(-1.0f, normXPos, 0.001f)

        val extremeDeltaNeg = -40.0f
        val normXNeg = (-extremeDeltaNeg / 6.5f).coerceIn(-1f, 1f)
        assertEquals(1.0f, normXNeg, 0.001f)
    }
}
