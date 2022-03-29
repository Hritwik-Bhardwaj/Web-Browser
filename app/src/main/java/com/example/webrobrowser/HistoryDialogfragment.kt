import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.webkit.WebView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.webrobrowser.BackHistoryAdapter
import com.example.webrobrowser.ForwardHistoryAdapter

class HistoryDialogFragment : DialogFragment(){
    val selectBackAdapter = "SELECT_BACK_ADAPTER"

    interface WebHistory {
        fun getWebView(): WebView
    }

    lateinit var webHistory: WebHistory

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            webHistory = context as WebHistory
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val backAdapter = arguments?.getBoolean(selectBackAdapter)
        val recyclerView = activity?.let { RecyclerView(it) }
        if (backAdapter!!) {
            recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true)
            recyclerView?.adapter = BackHistoryAdapter(this, webHistory.getWebView())
        } else {
            recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            recyclerView?.adapter = ForwardHistoryAdapter(this, webHistory.getWebView())
        }
        val itemDecoration = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        itemDecoration.setDrawable(ColorDrawable(Color.RED))
        recyclerView?.addItemDecoration(itemDecoration)
        recyclerView?.background = ColorDrawable(Color.LTGRAY)
        val alertDialogBuilder = AlertDialog.Builder(activity)
            .setView(recyclerView)
        return alertDialogBuilder.create()

    }
}