package com.example.myapplication.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sign
import kotlin.math.sqrt

/**
 * Controller utama pengelola Multi-Sensor (Accelerometer & Proximity, dengan fallback Light).
 *
 * Menerapkan:
 * 1. Low-Pass Filter (Exponential Moving Average) untuk meredam fluktuasi/getaran kasar saat HP diam.
 * 2. Dead-Zone filter (0.16 m/s²) agar objek interaktif tidak bergetar ketika HP stabil.
 * 3. Deteksi kondisi DEKAT / JAUH dari sensor Proximity aktual (Kategori Position).
 * 4. Pengelolaan Lifecycle ketat (register saat onResume, unregister saat onPause).
 */
class SensorController(context: Context) : SensorEventListener {

    private val appContext = context.applicationContext
    private val sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Sensor 1: Accelerometer (Kategori Motion) - Wajib
    private val accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Sensor 2: Proximity (Kategori Position) - Sensor Utama Kedua
    private val proximitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    // Sensor 3: Light (Kategori Environment) - Fallback jika Proximity tidak tersedia
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    // State terpusat untuk antarmuka Compose
    private val _uiState = MutableStateFlow(
        GarisJarakUiState(
            accelerometer = AccelerometerState(
                status = if (accelerometerSensor != null) SensorStatus.WAITING_FOR_DATA else SensorStatus.NOT_AVAILABLE
            ),
            proximity = ProximityStateData(
                maxRangeCm = proximitySensor?.maximumRange ?: 5f,
                sensorName = proximitySensor?.name ?: "Proximity Sensor",
                status = if (proximitySensor != null) SensorStatus.WAITING_FOR_DATA else SensorStatus.NOT_AVAILABLE,
                state = if (proximitySensor != null) ProximityState.WAITING else ProximityState.UNAVAILABLE
            ),
            lightFallback = LightFallbackData(
                isAvailable = lightSensor != null,
                status = if (lightSensor != null) SensorStatus.WAITING_FOR_DATA else SensorStatus.NOT_AVAILABLE
            )
        )
    )
    val uiState: StateFlow<GarisJarakUiState> = _uiState.asStateFlow()

    private var isListening = false

    // Parameter Filter Sinyal Accelerometer
    private var filteredX = 0f
    private var filteredY = 0f
    private var filteredZ = 9.81f
    private val alphaDisplay = 0.78f // 78% data stabil sebelumnya + 22% data mentah baru

    // Ambang Batas Zona Mati (Dead Zone) untuk Kontrol Objek (m/s²)
    private val deadZoneThreshold = 0.16f

    // Kalibrasi Orientasi (Tara Nol)
    private var calOffsetX = 0f
    private var calOffsetY = 0f
    private var isCalibrated = false

    /**
     * Daftarkan listener sensor sesuai lifecycle Activity onResume().
     */
    fun startListening() {
        if (isListening) return

        var accelRegistered = false
        if (accelerometerSensor != null) {
            accelRegistered = sensorManager.registerListener(
                this,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        var proxRegistered = false
        if (proximitySensor != null) {
            proxRegistered = sensorManager.registerListener(
                this,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        var lightRegistered = false
        if (lightSensor != null) {
            lightRegistered = sensorManager.registerListener(
                this,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        isListening = true

        _uiState.update { current ->
            current.copy(
                accelerometer = current.accelerometer.copy(
                    status = when {
                        accelerometerSensor == null -> SensorStatus.NOT_AVAILABLE
                        accelRegistered -> SensorStatus.WAITING_FOR_DATA
                        else -> SensorStatus.NOT_AVAILABLE
                    }
                ),
                proximity = current.proximity.copy(
                    status = when {
                        proximitySensor == null -> SensorStatus.NOT_AVAILABLE
                        proxRegistered -> SensorStatus.WAITING_FOR_DATA
                        else -> SensorStatus.NOT_AVAILABLE
                    },
                    state = if (proximitySensor == null) ProximityState.UNAVAILABLE else ProximityState.WAITING
                ),
                lightFallback = current.lightFallback.copy(
                    status = when {
                        lightSensor == null -> SensorStatus.NOT_AVAILABLE
                        lightRegistered -> SensorStatus.WAITING_FOR_DATA
                        else -> SensorStatus.NOT_AVAILABLE
                    }
                )
            )
        }
    }

    /**
     * Lepas listener sensor sesuai lifecycle Activity onPause().
     */
    fun stopListening() {
        if (!isListening) return
        sensorManager.unregisterListener(this)
        isListening = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                handleAccelerometer(event.values)
            }
            Sensor.TYPE_PROXIMITY -> {
                handleProximity(event.values[0])
            }
            Sensor.TYPE_LIGHT -> {
                handleLight(event.values[0])
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Callback akurasi standar
    }

    /**
     * Pemrosesan sinyal Accelerometer:
     * 1. Menyimpan data mentah (rawX, rawY, rawZ).
     * 2. Menerapkan Exponential Moving Average untuk visual display yang stabil.
     * 3. Menerapkan Dead Zone pada deviasi kalibrasi agar objek tidak bergetar saat HP diam.
     */
    private fun handleAccelerometer(values: FloatArray) {
        val rawX = values[0]
        val rawY = values[1]
        val rawZ = values[2]

        // Low-Pass Filter (EMA) untuk meredam kebisingan getaran sensor
        filteredX = (alphaDisplay * filteredX) + ((1f - alphaDisplay) * rawX)
        filteredY = (alphaDisplay * filteredY) + ((1f - alphaDisplay) * rawY)
        filteredZ = (alphaDisplay * filteredZ) + ((1f - alphaDisplay) * rawZ)

        // Magnitudo total percepatan gravitasi bumi
        val mag = sqrt((filteredX * filteredX) + (filteredY * filteredY) + (filteredZ * filteredZ))

        // Estimasi sudut kemiringan (derajat)
        val pitch = (atan2(filteredY.toDouble(), sqrt((filteredX * filteredX + filteredZ * filteredZ).toDouble())) * (180.0 / Math.PI)).toFloat()
        val roll = (atan2(-filteredX.toDouble(), filteredZ.toDouble()) * (180.0 / Math.PI)).toFloat()

        // Deviasi relatif terhadap titik nol tara kalibrasi
        val deltaX = filteredX - calOffsetX
        val deltaY = filteredY - calOffsetY

        // Penerapan Dead Zone Filter:
        // Jika deviasi lebih kecil dari 0.16 m/s², abaikan sebagai noise getaran tangan -> objek tetap tenang
        val effectiveDeltaX = if (abs(deltaX) < deadZoneThreshold) {
            0f
        } else {
            sign(deltaX) * (abs(deltaX) - deadZoneThreshold)
        }

        val effectiveDeltaY = if (abs(deltaY) < deadZoneThreshold) {
            0f
        } else {
            sign(deltaY) * (abs(deltaY) - deadZoneThreshold)
        }

        // Pemetaan ke koordinat normalisasi arena (-1f .. +1f)
        // Miring kanan -> deltaX negatif -> bola ke kanan (+X)
        // Miring depan -> deltaY negatif -> bola ke depan/atas (-Y)
        val normX = (-effectiveDeltaX / 6.5f).coerceIn(-1f, 1f)
        val normY = (effectiveDeltaY / 6.5f).coerceIn(-1f, 1f)

        _uiState.update { current ->
            current.copy(
                accelerometer = current.accelerometer.copy(
                    rawX = rawX,
                    rawY = rawY,
                    rawZ = rawZ,
                    filteredX = filteredX,
                    filteredY = filteredY,
                    filteredZ = filteredZ,
                    magnitude = mag,
                    pitch = pitch,
                    roll = roll,
                    status = SensorStatus.ACTIVE
                ),
                arenaObject = current.arenaObject.copy(
                    normalizedX = normX,
                    normalizedY = normY
                )
            )
        }
    }

    /**
     * Pemrosesan data Proximity aktual:
     * Mengukur jarak (cm) dan mendeteksi kondisi DEKAT (Near) atau JAUH (Far).
     */
    private fun handleProximity(distance: Float) {
        val maxRange = proximitySensor?.maximumRange ?: 5f
        // Pada Android, kondisi dekat terpenuhi bila distance < maxRange atau distance == 0f
        val isNear = (distance < maxRange) || (distance == 0f)
        val proxState = if (isNear) ProximityState.NEAR else ProximityState.FAR

        _uiState.update { current ->
            current.copy(
                proximity = current.proximity.copy(
                    distanceCm = distance,
                    maxRangeCm = maxRange,
                    isNear = isNear,
                    state = proxState,
                    status = SensorStatus.ACTIVE
                ),
                arenaObject = current.arenaObject.copy(
                    isNearFieldActive = isNear
                )
            )
        }
    }

    /**
     * Pemrosesan data Light sensor (fallback jika proximity tidak tersedia pada device).
     */
    private fun handleLight(lux: Float) {
        _uiState.update { current ->
            current.copy(
                lightFallback = current.lightFallback.copy(
                    lux = lux,
                    status = SensorStatus.ACTIVE
                )
            )
        }
    }

    /**
     * Tara kemiringan saat ini sebagai referensi netral (0, 0).
     */
    fun recalibrate() {
        calOffsetX = filteredX
        calOffsetY = filteredY
        isCalibrated = true

        _uiState.update { current ->
            current.copy(
                calibration = CalibrationData(
                    offsetX = calOffsetX,
                    offsetY = calOffsetY,
                    isCalibrated = true
                ),
                arenaObject = current.arenaObject.copy(
                    normalizedX = 0f,
                    normalizedY = 0f
                )
            )
        }
    }

    /**
     * Mengembalikan objek ke tengah secara mandiri tanpa menghapus tara.
     */
    fun resetOrb() {
        _uiState.update { current ->
            current.copy(
                arenaObject = current.arenaObject.copy(
                    resetCounter = current.arenaObject.resetCounter + 1L,
                    normalizedX = 0f,
                    normalizedY = 0f
                )
            )
        }
    }

    fun setDetailsVisible(visible: Boolean) {
        _uiState.update { current ->
            current.copy(isDetailsVisible = visible)
        }
    }
}
