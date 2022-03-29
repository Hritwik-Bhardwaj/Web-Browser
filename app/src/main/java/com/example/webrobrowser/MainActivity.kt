package com.example.webrobrowser
import HistoryDialogFragment
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), HistoryDialogFragment .WebHistory {
    override fun getWebView() = webView
    lateinit var fullscreenView: View
    var searchView:androidx.appcompat.widget.SearchView?=null
    var webview:WebView?=null
    var progressBar:ProgressBar?=null
    var inputMethodManager:InputMethodManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.search_view)
        webview = findViewById(R.id.webView)
        progressBar=findViewById(R.id.progress_bar)
        inputMethodManager=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        webview!!.settings.javaScriptEnabled=true
        webview!!.webViewClient= object:WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url:String
            ): Boolean {
                view!!.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if(!(progressBar!!.isShown))
                {
                    progressBar?.visibility=View.VISIBLE
                    view!!.visibility=View.GONE
                    mainScreen?.visibility=View.GONE
                }
                searchView!!.onActionViewExpanded()
                searchView!!.setQuery(webview!!.url,false)
                searchView!!.clearFocus()
               
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if((progressBar!!.isShown))
                {
                    progressBar?.visibility=View.GONE
                    view!!.visibility=View.VISIBLE
                }
                backward.isEnabled=true
                backward.setImageResource(R.drawable.undo_orange)
                if(!(webview!!.canGoForward()))
                {
                    forward.isEnabled=false
                    forward.setImageResource(R.drawable.redo_blue)
                }
                home.isEnabled=true
                home.setImageResource(R.drawable.home_orange)
            }
        }
        searchView?.
        setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                try {
                    val bool: Boolean = URLUtil.isValidUrl(query)
                    if (bool) {
                        webview!!.loadUrl(query)
                    } else {
                        webview?.loadUrl(
                            "https://www.google.com/search?q=" + query.replace(
                                oldValue = "",
                                newValue = "%20"
                            )
                        )
                    }
                    backward.isEnabled = true
                    backward.setImageResource(R.drawable.undo_orange)
                    home?.isEnabled = true
                    home.setImageResource(R.drawable.home_orange)
                    inputMethodManager!!.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }
                catch (e:Exception)
                {
                    Toast.makeText(this@MainActivity,""+e.printStackTrace(),Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return false
            }

        })

    }
    fun imageClicked(view:View){
        val id=view.id
        val ourId:String
        if(isInternetConnected())
        {
            ourId=view.resources.getResourceEntryName(id)
            webview?.loadUrl("https://www.$ourId.com")
            webview!!.visibility=View.VISIBLE
            mainScreen!!.visibility=View.GONE
            searchView!!.onActionViewExpanded()
            searchView!!.setQuery(webview!!.url,false)
            searchView!!.clearFocus()
            backward.isEnabled=true
            backward.setImageResource(R.drawable.undo_orange)
            home.isEnabled=true
            home.setImageResource(R.drawable.home_orange)
            forward.isEnabled=false
            forward.setImageResource(R.drawable.redo_blue)
        }
        else
        {
            Toast.makeText(this, "Internet connection Required. Please turn on your mobile data ", Toast.LENGTH_SHORT).show()
        }

    }
    fun iconClicked(view: View){
        when(view.id)
        {
            R.id.home->{
                homeDisplay()
            }
            R.id.backward->{
                backwardDisplay()
            }
            R.id.forward->{
                forwardDisplay()
            }
            R.id.refresh->
                refreshDisplay()
            R.id.bookmark->{
               getHistoryDialog(true)
                true
               // getHistoryDialog(false)
              //  true
                //-Toast.makeText(this, "Page Added in Bookmark", Toast.LENGTH_SHORT).show()
            }
        }
    }
     private fun refreshDisplay() {
         webview!!.reload()
    }
    fun getHistoryDialog(backAdapter: Boolean) {
        val historyDialogFragment = HistoryDialogFragment()
        val bundle = Bundle()
        bundle.putBoolean(historyDialogFragment.selectBackAdapter, backAdapter)
        historyDialogFragment.arguments = bundle
        historyDialogFragment.show(supportFragmentManager, "History")
    }
//    fun getForwardHistory():ArrayList<String>{
//        val webForwardHistory = webView.copyBackForwardList()
//        val historyList = ArrayList<String>()
//        for(i in 0 until webForwardHistory.size - webForwardHistory.currentIndex-1)
//            historyList.add(webForwardHistory.getItemAtIndex(
//                webForwardHistory.currentIndex + i+1
//            ).title)
//        return historyList
//    }
//    private fun getBackHistory():ArrayList<String>{
//        val webBackHistory = webView.copyBackForwardList()
//        val historyList = ArrayList<String>()
//        for(i in  0 until  webBackHistory.currentIndex)
//            historyList.add(webBackHistory.getItemAtIndex(i).title)
//        historyList.reverse()
//        return historyList
//    }
    private fun homeDisplay()
    {
        if(webview?.isShown as Boolean)
        {
            webview?.visibility=View.GONE
            mainScreen?.visibility=View.VISIBLE
        }
        backward?.isEnabled=false
        forward?.isEnabled=false
        home?.isEnabled=false
        home.setImageResource(R.drawable.gome_blue)
        backward?.setImageResource(R.drawable.undo_blue)
        forward?.setImageResource(R.drawable.redo_blue)
        searchView!!.onActionViewExpanded()
        searchView!!.setQuery("",false)
        searchView!!.clearFocus()
        inputMethodManager!!.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
    private fun backwardDisplay()
    {
        if(webview!!.canGoBack())
        {
            webview!!.goBack()
            if(webview!!.canGoForward())
            {
                forward?.isEnabled=true
                forward?.setImageResource(R.drawable.redo_orange)
            }
            if(!(webview!!.isShown))
            {
                webview!!.visibility=View.VISIBLE
                mainScreen.visibility=View.GONE
            }
        }
        else if (webview!!.isShown)
        {
            webview!!.visibility=View.GONE
            mainScreen!!.visibility=View.VISIBLE
            backward?.isEnabled=false
            backward?.setImageResource(R.drawable.undo_blue)
            if(webview!!.canGoForward())
                forward.isEnabled=true
            forward.setImageResource(R.drawable.redo_orange)
        }
    }
    private fun forwardDisplay()
    {
        if(webview!!.canGoForward())
        {
            webview!!.goForward()
            backward.isEnabled=true
            backward.setImageResource(R.drawable.undo_orange)
            home.isEnabled=true
            home.setImageResource(R.drawable.home_orange)
            searchView!!.onActionViewExpanded()
            searchView!!.setQuery(webview!!.url,false)
            searchView!!.clearFocus()
            inputMethodManager!!.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            home.isEnabled=true
            home.setImageResource(R.drawable.home_orange)
            if(!(webview!!.isShown))
            {
                webview!!.visibility=View.VISIBLE
                mainScreen.visibility=View.GONE
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_BACK && webview!!.canGoBack())
        {
            webview!!.goBack()
            return true
        }else if (!(mainScreen.isShown))
        {
            mainScreen.visibility=View.VISIBLE
            webview!!.visibility=View.GONE
            searchView!!.setQuery("",false)
            searchView!!.clearFocus()
            backward.isEnabled=false
            backward.setImageResource(R.drawable.undo_blue)
            if(webview!!.canGoForward())
            {
                forward.isEnabled=true
                forward.setImageResource(R.drawable.redo_orange)
            }
        }
        return super.onKeyDown(keyCode, event)
    }
    private fun isInternetConnected():Boolean{
        val connectivityManager:ConnectivityManager=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnected
    }
//    override fun webpageSelected(webTitle: String) {
//        val webHistory=webView.copyBackForwardList()
//        for(i in 0 until webHistory.size){
//            if(webHistory.getItemAtIndex(i).title.equals(webTitle))
//            {
//                webview!!.goBackOrForward(i-webHistory.currentIndex)
//                break
//            }
//        }
//    }

}