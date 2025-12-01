package `in`.wyco.salesapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "YCo Sales App",
    ) {
        App()
    }
}