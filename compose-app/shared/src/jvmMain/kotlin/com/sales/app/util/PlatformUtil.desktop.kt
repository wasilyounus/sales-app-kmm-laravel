package com.sales.app.util

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.print.PrinterJob
import java.awt.print.Printable
import java.awt.print.PageFormat
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Color
import java.awt.Font
import java.awt.BasicStroke

actual fun isDesktop(): Boolean = true

class DesktopPlatformShare : PlatformShare {
    override fun shareText(text: String) {
        val selection = StringSelection(text)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
        println("Desktop Share: Copied to clipboard: $text")
    }

    override fun sharePdf(data: PrintData) {
        // Fallback to text copy for now
        shareText("PDF Share not supported on Desktop.\n${data.title}\n${data.subtitle}\nTotal: ${data.total}")
    }

    override fun print(data: PrintData) {
        println("Desktop Print requested for: ${data.title}")
        val job = PrinterJob.getPrinterJob()
        job.setJobName("${data.title} Document")
        job.setPrintable(DesktopPrintable(data))
        
        if (job.printDialog()) {
            try {
                job.print()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class DesktopPrintable(private val data: PrintData) : Printable {
    override fun print(graphics: Graphics, pageFormat: PageFormat, pageIndex: Int): Int {
        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE
        }

        val g2d = graphics as Graphics2D
        g2d.translate(pageFormat.imageableX, pageFormat.imageableY)

        // Draw Content
        g2d.color = Color.BLACK
        
        // Title
        g2d.font = Font("Dialog", Font.BOLD, 24)
        g2d.drawString(data.title, 50, 50)

        // Subtitle
        g2d.font = Font("Dialog", Font.PLAIN, 14)
        g2d.drawString(data.subtitle, 50, 80)

        // Meta
        var y = 110
        g2d.font = Font("Dialog", Font.PLAIN, 12)
        data.meta.forEach { (key, value) ->
            g2d.drawString("$key: $value", 50, y)
            y += 20
        }

        // Draw Items Header
        y += 20
        g2d.font = Font("Dialog", Font.BOLD, 12)
        g2d.drawString("Item", 50, y)
        g2d.drawString("Qty", 300, y)
        g2d.drawString("Price", 400, y)
        g2d.drawString("Total", 500, y)
        
        y += 10
        g2d.stroke = BasicStroke(1f)
        g2d.drawLine(50, y, 550, y)
        y += 20

        // Draw Items
        g2d.font = Font("Dialog", Font.PLAIN, 12)
        data.items.forEach { item ->
            g2d.drawString(item.name, 50, y)
            g2d.drawString(item.qty, 300, y)
            g2d.drawString(item.price, 400, y)
            g2d.drawString(item.total, 500, y)
            y += 20
        }

        y += 10
        g2d.drawLine(50, y, 550, y)
        y += 30
        
        // Total
        g2d.font = Font("Dialog", Font.BOLD, 18)
        g2d.drawString("Grand Total: ${data.total}", 350, y)

        return Printable.PAGE_EXISTS
    }
}

actual fun getPlatformShare(): PlatformShare {
    return DesktopPlatformShare()
}
