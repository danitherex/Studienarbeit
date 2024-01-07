package com.example.studienarbeit.presentation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class Navigator {
    private val _sharedFlow =
        MutableSharedFlow<NavTarget>(extraBufferCapacity = 1)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun navigateTo(navTarget: NavTarget) {
        _sharedFlow.tryEmit(navTarget)
    }

    enum class NavTarget(val label:String){
        MAP("Map"),
        LOGIN("Login"),
        PROFILE("Profile"),
        //TODO:make this better, maybe with objects :https://hitherejoe.medium.com/nested-navigation-graphs-in-jetpack-compose-dc0ada1d4726
        ROOT("Logged-in")
    }
}
