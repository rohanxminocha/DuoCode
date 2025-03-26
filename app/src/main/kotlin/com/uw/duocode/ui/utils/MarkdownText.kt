import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon


@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val processedMarkdown = markdown.replace("\\n", "\n")
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()

    AndroidView(
        factory = { context ->
            TextView(context).apply {
                setTextIsSelectable(true)
            }
        },
        update = { textView ->
            textView.setTextColor(textColor)
            val markwon = Markwon.create(textView.context)
            markwon.setMarkdown(textView, processedMarkdown)
        },
        modifier = modifier
    )
}
