import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val processedMarkdown = markdown.replace("\\n", "\n")
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                setTextIsSelectable(true)
            }
        },
        update = { textView ->
            val markwon = Markwon.create(textView.context)
            markwon.setMarkdown(textView, processedMarkdown)
        },
        modifier = modifier
    )
}
