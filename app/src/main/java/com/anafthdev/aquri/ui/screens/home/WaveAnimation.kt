package com.anafthdev.aquri.ui.screens.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
@Preview
fun WaveAnimationPreview() {
    MaterialTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(300.dp)
        ) {
            WaveAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                color = Color(0xFF1E88E5), // Biru
                amplitude = 45f,
                frequency = 0.01f,
                animationSpeed = 1500
            )
        }
    }
}

/**
 * Composable untuk menampilkan animasi gelombang yang bisa dikontrol dengan progress.
 *
 * @param modifier Modifier untuk kustomisasi layout.
 * @param progress Nilai progres dari 0.0f hingga 1.0f untuk mengatur ketinggian gelombang.
 * @param color Warna utama gelombang.
 * @param amplitude Ketinggian (amplitudo) dari osilasi gelombang.
 * @param frequency Kerapatan (frekuensi) gelombang.
 * @param animationSpeed Kecepatan animasi pergerakan gelombang dalam milidetik.
 */
@Composable
fun WaveAnimation(
    modifier: Modifier = Modifier,
    progress: Float,
    color: Color = MaterialTheme.colorScheme.primary,
    amplitude: Float = 25f,
    frequency: Float = 0.05f,
    animationSpeed: Int = 1500
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_transition")

    // Menganimasikan fase gelombang (komponen waktu) untuk membuatnya bergerak.
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationSpeed, easing = { it }),
            repeatMode = RepeatMode.Restart
        ), label = "wave_phase"
    )

    // Pastikan progress berada di antara 0 dan 1
    val coercedProgress = progress.coerceIn(0f, 1f)

    Canvas(modifier = modifier.clipToBounds()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Hitung posisi Y dasar dari gelombang berdasarkan progress.
        // Saat progress 0, waveBaseY = canvasHeight (di bawah).
        // Saat progress 1, waveBaseY = 0 (di atas).
        val waveBaseY = canvasHeight * (1f - coercedProgress)

        val wavePath = Path().apply {
            // Mulai path dari tepi kiri bawah canvas.
            moveTo(0f, canvasHeight)

            // Garis vertikal ke titik awal gelombang.
            // Ini penting agar area di bawah gelombang terisi warna dengan benar.
            val firstY = waveBaseY + amplitude * sin(phase)
            lineTo(0f, firstY)

            // Iterasi sepanjang lebar canvas untuk menggambar titik-titik gelombang
            for (x in 0..canvasWidth.toInt()) {
                // Rumus gelombang, tapi sekarang berosilasi di sekitar waveBaseY
                val y = amplitude * sin(frequency * x + phase)
                lineTo(x.toFloat(), waveBaseY + y)
            }

            // Tutup path dengan menggambar garis ke kanan bawah lalu ke kiri bawah.
            lineTo(canvasWidth, canvasHeight)
            lineTo(0f, canvasHeight)
            close()
        }

        // Gambar path yang sudah dibuat ke canvas
        drawPath(
            path = wavePath,
            color = color,
//            brush = Brush.verticalGradient(
//                colors = listOf(color.copy(alpha = 0.5f), color.copy(alpha = 0.1f)),
//                startY = verticalCenter - amplitude,
//                endY = canvasHeight
//            )
        )

        // Border
//        drawPath(
//            path = wavePath,
//            color = color,
//            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
//        )
    }
}

// Komponen utama yang akan menampilkan animasi gelombang.
@Composable
fun WaveAnimation(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    amplitude: Float = 50f, // Amplitudo (tinggi) gelombang dalam pixel
    frequency: Float = 0.03f, // Frekuensi atau kerapatan gelombang
    animationSpeed: Int = 2000 // Durasi animasi dalam milidetik untuk satu siklus
) {
    // rememberInfiniteTransition digunakan untuk membuat animasi yang berjalan terus-menerus.
    val infiniteTransition = rememberInfiniteTransition(label = "wave_transition")

    // Menganimasikan fase gelombang dari 0 hingga 2*PI (satu siklus penuh).
    // Ini akan menjadi komponen 'waktu' (t) dalam rumus gelombang kita.
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationSpeed, easing = { it }), // Linear easing
            repeatMode = RepeatMode.Restart
        ), label = "wave_phase"
    )

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val verticalCenter = canvasHeight / 2f

        val wavePath = Path().apply {
            // Mulai path dari tepi kiri bawah
            moveTo(0f, canvasHeight)

            // Gambar garis ke titik awal gelombang di tepi kiri
            // Ini diperlukan agar area di bawah gelombang bisa diisi warna
            val firstY = verticalCenter + amplitude * sin(phase)
            lineTo(0f, firstY)

            // Iterasi sepanjang lebar canvas untuk menggambar titik-titik gelombang
            for (x in 0..canvasWidth.toInt()) {
                // Rumus Fisika Gelombang Sinus: y(x, t) = A * sin(k*x + ω*t)
                // Di sini:
                // y = posisi vertikal
                // A = amplitude
                // k = frequency (mengontrol kerapatan)
                // x = posisi horizontal
                // ω*t = phase (mengontrol pergerakan/animasi)
                val y = amplitude * sin(frequency * x + phase)

                // Tambahkan titik ke path. Posisinya di sekitar pusat vertikal canvas.
                lineTo(x.toFloat(), verticalCenter + y)
            }

            // Tutup path dengan menggambar garis ke kanan bawah lalu ke kiri bawah
            lineTo(canvasWidth, canvasHeight)
            lineTo(0f, canvasHeight)
            close()
        }

        // Gambar path yang sudah dibuat ke canvas
        drawPath(
            path = wavePath,
            color = color,
//            brush = Brush.verticalGradient(
//                colors = listOf(color.copy(alpha = 0.5f), color.copy(alpha = 0.1f)),
//                startY = verticalCenter - amplitude,
//                endY = canvasHeight
//            )
        )

        // Border
//        drawPath(
//            path = wavePath,
//            color = color,
//            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
//        )
    }
}
