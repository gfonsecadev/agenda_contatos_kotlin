package com.example.minhaagenda.animations.fade

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

object FadeToViews {
    //mostra imediatamente a view
    fun fadeInImmediately(view: View) {
        view.apply {
            alpha = 1f
            visibility = View.VISIBLE
        }
    }

    //aplica uma animação de ocultação da view e no final a seta invisivel
    fun fadeOut(view: View, duration: Long = 300) {
        view.apply {
            animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.INVISIBLE
                    }
                })
        }
    }
}