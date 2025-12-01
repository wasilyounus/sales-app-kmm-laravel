import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sales.app.MainView

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}