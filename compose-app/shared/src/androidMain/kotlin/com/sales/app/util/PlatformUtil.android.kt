package com.sales.app.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.print.PageRange
import android.print.PrintDocumentInfo
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.webkit.WebView
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream


actual fun isDesktop(): Boolean = false

class AndroidPlatformShare(private val context: Context) : PlatformShare {

    override fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Share via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    override fun sharePdf(data: PrintData) {
        try {
            val pdfFile = generatePdf(data)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            val chooser = Intent.createChooser(intent, "Share Invoice")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to text share if PDF fails
            shareText("${data.title}\n${data.subtitle}\nTotal: ${data.total}")
        }
    }

    override fun print(data: PrintData) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "${data.title} Document"

        // Use WebView for simpler printing compatible with HTML
        val webView = WebView(context)
        val htmlContent = generateHtml(data)
        
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)

        // Wait for load, but for simplicity here we rely on standard behavior or basic text
        // Ideally we use a custom PrintDocumentAdapter or wait for WebView
        // Since we can't easily wait in this sync function without callback,
        // we'll try to just pass the adapter directly.
        // NOTE: WebView needs to be attached to window or loaded fully.
        // A better approach for native is custom adapter drawing canvas.
        
        // Let's use a custom adapter that draws similar to PDF
        // But for simplicity in this iteration, let's just generate the PDF and print it?
        // No, printing PDF is not direct in older APIs without Renderer.
        
        // Let's use the WebView approach as it's standard for complex layouts.
        // But we need to ensure it's loaded.
        
        // Alternative: Use PDF Document Adapter
        
        // For now, let's implement the custom adapter for PDF drawing
         printManager.print(jobName, SimplePdfPrintAdapter(context, data), null)
    }

    private fun generatePdf(data: PrintData): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        // Draw Content
        paint.color = Color.BLACK
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText(data.title, 50f, 50f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText(data.subtitle, 50f, 80f, paint)

        // Draw Meta
        var y = 110f
        data.meta.forEach { (key, value) ->
            canvas.drawText("$key: $value", 50f, y, paint)
            y += 20f
        }

        // Draw Items Header
        y += 20f
        paint.isFakeBoldText = true
        canvas.drawText("Item", 50f, y, paint)
        canvas.drawText("Qty", 300f, y, paint)
        canvas.drawText("Price", 400f, y, paint)
        canvas.drawText("Total", 500f, y, paint)
        
        paint.isFakeBoldText = false
        y += 20f
        canvas.drawLine(50f, y, 550f, y, paint)
        y += 20f

        // Draw Items
        data.items.forEach { item ->
            canvas.drawText(item.name, 50f, y, paint)
            canvas.drawText(item.qty, 300f, y, paint)
            canvas.drawText(item.price, 400f, y, paint)
            canvas.drawText(item.total, 500f, y, paint)
            y += 20f
        }

        y += 10f
        canvas.drawLine(50f, y, 550f, y, paint)
        y += 30f
        
        // Total
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Grand Total: ${data.total}", 350f, y, paint)

        document.finishPage(page)

        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()
        
        val file = File(reportsDir, "SalesApp_Invoice.pdf")
        val outputStream = FileOutputStream(file)
        document.writeTo(outputStream)
        document.close()
        outputStream.close()

        return file
    }

    private fun generateHtml(data: PrintData): String {
        // Fallback or future usage
        return "<html><body><h1>${data.title}</h1></body></html>"
    }
}

class SimplePdfPrintAdapter(private val context: Context, private val data: PrintData) : PrintDocumentAdapter() {
    
    private var pdfDocument: PdfDocument? = null

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }
        
        val builder = PrintDocumentInfo.Builder("sales_invoice.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1)
            
        callback?.onLayoutFinished(builder.build(), true)
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: android.os.ParcelFileDescriptor?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        // Generate PDF pages
        if (destination == null) return
        
        try {
            pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument!!.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            
            // Draw Code (Duplicated from above for simplicity in snippet)
             paint.color = Color.BLACK
            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText(data.title, 50f, 50f, paint)
    
            paint.textSize = 14f
            paint.isFakeBoldText = false
            canvas.drawText(data.subtitle, 50f, 80f, paint)
    
            // Draw Meta
            var y = 110f
            data.meta.forEach { (key, value) ->
                canvas.drawText("$key: $value", 50f, y, paint)
                y += 20f
            }
    
            // Draw Items Header
            y += 20f
            paint.isFakeBoldText = true
            canvas.drawText("Item", 50f, y, paint)
            canvas.drawText("Qty", 300f, y, paint)
            canvas.drawText("Price", 400f, y, paint)
            canvas.drawText("Total", 500f, y, paint)
            
            paint.isFakeBoldText = false
            y += 20f
            canvas.drawLine(50f, y, 550f, y, paint)
            y += 20f
    
            // Draw Items
            data.items.forEach { item ->
                canvas.drawText(item.name, 50f, y, paint)
                canvas.drawText(item.qty, 300f, y, paint)
                canvas.drawText(item.price, 400f, y, paint)
                canvas.drawText(item.total, 500f, y, paint)
                y += 20f
            }
    
            y += 10f
            canvas.drawLine(50f, y, 550f, y, paint)
            y += 30f
            
            // Total
            paint.textSize = 18f
            paint.isFakeBoldText = true
            canvas.drawText("Grand Total: ${data.total}", 350f, y, paint)

            pdfDocument!!.finishPage(page)
            
            // Write to destination
            pdfDocument!!.writeTo(FileOutputStream(destination.fileDescriptor))
            
            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            
        } catch (e: Exception) {
            callback?.onWriteFailed(e.message)
        } finally {
            pdfDocument?.close()
            pdfDocument = null
        }
    }
}

actual fun getPlatformShare(): PlatformShare {
    val context = AndroidPlatformContext.get() 
        ?: throw IllegalStateException("Android Context not initialized. Call AndroidPlatformContext.set(context) in Activity.")
    return AndroidPlatformShare(context)
}
