package com.sales.app.util

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

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
        // Placeholder for desktop printing
    }
}

actual fun getPlatformShare(): PlatformShare {
    return DesktopPlatformShare()
}
