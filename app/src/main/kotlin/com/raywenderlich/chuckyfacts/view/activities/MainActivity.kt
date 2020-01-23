package com.raywenderlich.chuckyfacts.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.raywenderlich.chuckyfacts.BaseApplication
import com.raywenderlich.chuckyfacts.MainContract
import com.raywenderlich.chuckyfacts.R
import com.raywenderlich.chuckyfacts.entity.Joke
import com.raywenderlich.chuckyfacts.presenter.MainPresenter
import com.raywenderlich.chuckyfacts.view.adapters.JokesListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_view_custom_layout.*
import org.jetbrains.anko.toast
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

class MainActivity : BaseActivity(), MainContract.View {

    private var presenter: MainContract.Presenter? = null
    private val toolbar: Toolbar by lazy { toolbar_toolbar_view }
    private val recyclerView: RecyclerView by lazy { rv_jokes_list_activity_main }
    private val progressBar: ProgressBar by lazy { prog_bar_loading_jokes_activity_main }


    companion object {
        val TAG: String = "MainActivity"   // 1
    }

    private val navigator: Navigator? by lazy {
        object : Navigator {
            override fun applyCommand(command: Command) {   // 2
                if (command is Forward) {
                    forward(command)
                }
            }

            private fun forward(command: Forward) {   // 3
                val data = (command.transitionData as Joke)

                when (command.screenKey) {
                    DetailActivity.TAG -> startActivity(Intent
                    (this@MainActivity, DetailActivity::class.java)
                            .putExtra("data", data as Parcelable))   // 4
                    else -> Log.e("Cicerone", "Unknown screen: " + command.screenKey)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = JokesListAdapter({ joke -> presenter?.listItemClicked(joke) }, null)
    }

    override fun getToolbarInstance(): Toolbar? {
        return toolbar
    }


    override fun showLoading() {
        recyclerView.isEnabled = false
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        recyclerView.isEnabled = true
        progressBar.visibility = View.GONE
    }

    override fun publishDataList(data: List<Joke>) {
        (recyclerView.adapter as JokesListAdapter).updateData(data)
    }

    override fun showInfoMessage(msg: String) {
        toast(msg)
    }


    override fun onResume() {
        super.onResume()
        BaseApplication.INSTANCE.cicerone.navigatorHolder.setNavigator(navigator)
        presenter?.onViewCreated()
    }

    override fun onPause() {
        super.onPause()
        BaseApplication.INSTANCE.cicerone.navigatorHolder.setNavigator(navigator)
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        presenter = null
        super.onDestroy()
    }


}
