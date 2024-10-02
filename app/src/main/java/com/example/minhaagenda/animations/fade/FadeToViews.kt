package com.example.minhaagenda.animations.fade

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

object FadeToViews {

    // Método para mostrar a view imediatamente
    fun fadeInImmediately(view: View) {
        // Cancela qualquer animação anterior em andamento
        view.animate().cancel()

        // Verifica se a view já está visível antes de aplicar a animação
        if (view.visibility != View.VISIBLE) {
            view.apply {
                animate()
                    .alpha(1f) // Define a opacidade para 100% (totalmente visível)
                    .setDuration(0) // Animação instantânea (duração zero)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            view.visibility = View.VISIBLE
                        }
                    })
            }
        }
    }

    // Método para ocultar a view gradualmente
    fun fadeOut(view: View, duration: Long = 300) {
        // Cancela qualquer animação anterior em andamento
        view.animate().cancel()

        // Verifica se a view já está invisível antes de aplicar a animação
        if (view.visibility != View.GONE) {
            view.apply {
                animate()
                    .alpha(0f) // Define a opacidade para 0% (totalmente invisível)
                    .setDuration(duration) // Define a duração da animação (300ms por padrão)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            view.visibility = View.GONE
                        }
                    })
            }
        }
    }
}
