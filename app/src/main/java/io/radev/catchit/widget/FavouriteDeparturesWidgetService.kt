package io.radev.catchit.widget

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint
import io.radev.catchit.domain.UpdateFavouriteDeparturesAlertUseCase
import javax.inject.Inject

/*
 * Created by radoslaw on 25/07/2020.
 * radev.io 2020.
 */
@AndroidEntryPoint
class FavouriteDeparturesWidgetService : RemoteViewsService() {

    @Inject
    lateinit var updateFavouriteDeparturesAlertUseCase: UpdateFavouriteDeparturesAlertUseCase
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory = FavoDepRemoteViewsFactory(
        this.applicationContext,
        intent,
        updateFavouriteDeparturesAlertUseCase
    )


}