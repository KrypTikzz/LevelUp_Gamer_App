package com.example.levelup_gamerapp.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.levelup_gamerapp.remote.PedidoResponseDTO
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun generarBoletaPdf(
    context: Context,
    pedido: PedidoResponseDTO
) {
    try {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val left = 60f
        var y = 80

        // ===== TÍTULO =====
        paint.textSize = 32f
        paint.isFakeBoldText = true
        canvas.drawText("LEVEL UP GAMER", left, y.toFloat(), paint)

        y += 50

        // ===== DATOS BOLETA =====
        paint.textSize = 20f
        paint.isFakeBoldText = false
        canvas.drawText("Boleta N° ${pedido.id}", left, y.toFloat(), paint)

        y += 30
        canvas.drawText(
            "Fecha: ${formatearFecha(pedido.fechaCreacion)}",
            left,
            y.toFloat(),
            paint
        )

        y += 50

        // ===== ENCABEZADO =====
        paint.textSize = 22f
        paint.isFakeBoldText = true
        canvas.drawText("Detalle de compra", left, y.toFloat(), paint)

        y += 30
        paint.isFakeBoldText = false
        paint.textSize = 18f

        // ===== ITEMS =====
        pedido.detalles.forEach { det ->
            canvas.drawText(
                det.nombreProducto,
                left,
                y.toFloat(),
                paint
            )

            y += 22

            canvas.drawText(
                "Cantidad: ${det.cantidad}    Subtotal: $${formatearPrecio(det.subtotal)}",
                left + 20,
                y.toFloat(),
                paint
            )

            y += 35
        }

        // ===== TOTAL =====
        y += 20
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText(
            "TOTAL: $${formatearPrecio(pedido.total)}",
            left,
            y.toFloat(),
            paint
        )

        pdfDocument.finishPage(page)

        // ===== GUARDADO =====
        val fileName = "boleta_${pedido.id}.pdf"

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            resolver.openOutputStream(it)?.use { output ->
                pdfDocument.writeTo(output)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(it, values, null, null)
            }
        }

        pdfDocument.close()

        Toast.makeText(context, "Boleta descargada en Descargas", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Error al generar PDF: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun formatearFecha(fechaIso: String): String {
    return try {
        val dateTime = LocalDateTime.parse(fechaIso)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        dateTime.format(formatter)
    } catch (e: Exception) {
        fechaIso
    }
}

private fun formatearPrecio(monto: Double): String {
    val formato = NumberFormat.getNumberInstance(Locale("es", "CL"))
    formato.maximumFractionDigits = 0
    return formato.format(monto)
}
