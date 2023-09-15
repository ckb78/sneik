import java.awt.EventQueue
import javax.swing.JFrame


class Sneik : JFrame() {

    init {
        initUI()
    }

    private fun initUI() {
        add(World())

        title = "Alle skal med"

        isResizable = false
        pack()

        setLocationRelativeTo(null)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater {
                val ex = Sneik()
                ex.isVisible = true
            }
        }
    }
}
