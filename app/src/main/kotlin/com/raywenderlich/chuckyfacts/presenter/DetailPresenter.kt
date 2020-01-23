package com.raywenderlich.chuckyfacts.presenter

import com.raywenderlich.chuckyfacts.BaseApplication
import com.raywenderlich.chuckyfacts.DetailContract
import com.raywenderlich.chuckyfacts.entity.Joke
import ru.terrakok.cicerone.Router

class DetailPresenter(private var view: DetailContract.View?) : DetailContract.Presenter {

    private val router: Router? by lazy { BaseApplication.INSTANCE.cicerone.router }

    override fun backButtonClicked() {

    }

    override fun onViewCreated(joke: Joke) {
        view?.showJokeData(joke.id.toString(), joke.text)
    }

    override fun onDestroy() {
        view = null
    }
}