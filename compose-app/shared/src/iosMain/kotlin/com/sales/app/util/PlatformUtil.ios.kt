package com.sales.app.util

actual fun isDesktop(): Boolean = false

actual fun getPlatformShare(): PlatformShare = IosPlatformShare()

class IosPlatformShare : PlatformShare {
    override fun shareText(text: String) {
        // TODO: Implement using UIActivityViewController
        println("Share text (iOS stub): $text")
    }

    override fun sharePdf(data: PrintData) {
        // TODO: Implement PDF sharing
        println("Share PDF (iOS stub): ${data.title}")
    }

    override fun print(data: PrintData) {
        // TODO: Implement Printing
        println("Print (iOS stub): ${data.title}")
    }
}
