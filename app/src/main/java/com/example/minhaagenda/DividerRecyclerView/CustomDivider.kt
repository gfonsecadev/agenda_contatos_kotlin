package com.example.minhaagenda.DividerRecyclerView

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

//classe customizada para desenhar um divisor no recyclerView
class CustomDivider : RecyclerView.ItemDecoration() {
    private val paint = Paint()

    //inicializamos o objeto criado acima
    init {
        paint.color = 0xFF000000.toInt() // Cor da linha de divisão (preto)
        paint.strokeWidth = 0.5f // Espessura da linha
    }

    // Este método é responsável por desenhar elementos como linhas, formas ou imagens diretamente no Canvas associado ao RecyclerView.
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.left + 240
        val right = parent.width - 80

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top

            c.drawLine(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }

    /*getItemOffsets é chamado pelo RecyclerView quando ele está medindo e posicionando os itens na tela.
      Você define os valores de outRect para indicar o espaçamento que deve ser aplicado ao redor de cada item.
      Por exemplo, outRect.set(0, 0, 0, 10) adicionará um espaçamento de 10 pixels na parte inferior de cada item.*/
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.set(0, 0, 0, 0)
    }
}