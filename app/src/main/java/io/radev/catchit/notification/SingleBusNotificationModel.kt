package io.radev.catchit.notification

import io.radev.catchit.network.DepartureDetails

/*
 * Created by radoslaw on 13/06/2020.
 * radev.io 2020.
 */

data class SingleBusNotificationModel(
    val line: String,
    val direction: String,
    val waitTime: String,
    val error:Boolean = false
)
