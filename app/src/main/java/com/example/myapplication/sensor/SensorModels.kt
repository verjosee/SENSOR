package com.example.myapplication.sensor

/**
 * Status ketersediaan perangkat keras dan penerimaan data sensor.
 */
enum class SensorStatus(val label: String) {
    NOT_AVAILABLE("Tidak Tersedia"),
    WAITING_FOR_DATA("Menunggu Data"),
    ACTIVE("Aktif & Terhubung")
}

/**
 * Status kondisi sensor Proximity (Kategori Position).
 */
enum class ProximityState(val label: String) {
    NEAR("DEKAT"),
    FAR("JAUH"),
    WAITING("MENUNGGU DATA"),
    UNAVAILABLE("TIDAK TERSEDIA")
}

/**
 * Data pembacaan sensor Accelerometer (Kategori Motion).
 * Memisahkan secara transparan antara data mentah (raw) dan data stabil (filtered).
 */
data class AccelerometerState(
    val rawX: Float = 0f,
    val rawY: Float = 0f,
    val rawZ: Float = 9.81f,
    val filteredX: Float = 0f,
    val filteredY: Float = 0f,
    val filteredZ: Float = 9.81f,
    val magnitude: Float = 9.81f,
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val status: SensorStatus = SensorStatus.WAITING_FOR_DATA
)

/**
 * Data pembacaan sensor Proximity (Kategori Position).
 */
data class ProximityStateData(
    val distanceCm: Float = -1f,
    val maxRangeCm: Float = 5f,
    val isNear: Boolean = false,
    val state: ProximityState = ProximityState.WAITING,
    val status: SensorStatus = SensorStatus.WAITING_FOR_DATA,
    val sensorName: String = "Proximity Sensor"
)

/**
 * Data sensor Cahaya Lingkungan (Kategori Environment) sebagai diagnosis & fallback.
 */
data class LightFallbackData(
    val lux: Float = 0f,
    val isAvailable: Boolean = false,
    val status: SensorStatus = SensorStatus.NOT_AVAILABLE
)

/**
 * Data referensi kalibrasi kemiringan (Tara).
 */
data class CalibrationData(
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val isCalibrated: Boolean = false
)

/**
 * State koordinat objek interaktif di dalam arena geometris.
 */
data class ArenaObjectState(
    val normalizedX: Float = 0f,
    val normalizedY: Float = 0f,
    val isNearFieldActive: Boolean = false,
    val resetCounter: Long = 0L
)

/**
 * State UI terpadu untuk konsep GARIS / JARAK.
 */
data class GarisJarakUiState(
    val accelerometer: AccelerometerState = AccelerometerState(),
    val proximity: ProximityStateData = ProximityStateData(),
    val lightFallback: LightFallbackData = LightFallbackData(),
    val calibration: CalibrationData = CalibrationData(),
    val arenaObject: ArenaObjectState = ArenaObjectState(),
    val isDetailsVisible: Boolean = false
)
