import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.Timer


class World : JPanel(), ActionListener {

    private val worldWidth = 300
    private val worldHeight = 300
    private val linkSize = 10
    private val maxLinks = 900
    private val randomConstant = 29
    private val speed = 140

    private val x = IntArray(maxLinks);
    private val y = IntArray(maxLinks);

    private var numLinks: Int = 0
    private var sadFaceX: Int = 0
    private var sadFaceY: Int = 0

    private var facingLeft = false
    private var facingRight = true
    private var facingUp = false
    private var facingDown = false
    private var gameInProgress = true

    private var timer: Timer? = null
    private var link: Image? = null
    private var sadFace: Image? = null
    private var face: Image? = null

    private var gameOverTimer: Timer? = null
    private var welcomeScreen = true
    private var gameStarted = false
    private var highScore = 0
    private var scoreLabel: JLabel? = null

    init {
        addKeyListener(TAdapter())
        background = Color.BLACK
        isFocusable = true

        preferredSize = Dimension(worldWidth, worldHeight)
        loadGraphics()

        scoreLabel = JLabel("Score: ${numLinks - 3}")
        scoreLabel?.foreground = Color.GREEN
        scoreLabel?.bounds = Rectangle(10, worldHeight - 30, 100, 20)
        add(scoreLabel)
    }

    private fun loadGraphics() {
        val linkImg = ImageIcon("src/main/resources/happy.png")
        link = linkImg.image

        val sadFaceImg = ImageIcon("src/main/resources/sad.png")
        sadFace = sadFaceImg.image

        val faceImg = ImageIcon("src/main/resources/face.png")
        face = faceImg.image
    }

    private fun initGame() {

        numLinks = 3

        for (z in 0 until numLinks) {
            x[z] = 50 - z * 10
            y[z] = 50
        }

        locatePickup()
        timer = Timer(speed, this)
        timer!!.start()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        if (welcomeScreen) {
            drawWelcomeScreen(g)
        } else if (gameInProgress) {
            drawGraphics(g)
        } else {
            drawGameOverScreen(g)
        }
    }

    private fun drawGraphics(g: Graphics) {

        if (gameInProgress) {
            scoreLabel?.isVisible = true
            g.drawImage(sadFace, sadFaceX, sadFaceY, this)

            for (z in 0 until numLinks) {
                if (z == 0) {
                    g.drawImage(face, x[z], y[z], this)
                } else {
                    g.drawImage(link, x[z], y[z], this)
                }
            }

            Toolkit.getDefaultToolkit().sync()

            // Update the score label's text
            scoreLabel?.text = "Score: ${numLinks - 3}"
        } else {
            scoreLabel?.isVisible = false
            drawGameOverScreen(g)
        }
    }

    private fun drawWelcomeScreen(g: Graphics) {
        scoreLabel?.isVisible = false
        val welcomeText = "- ALLE SKAL MED -"
        val descriptionText = "(En slags sosialdemokratisk Snake-klone)"
        val authorText = "av Christian Bjørnsrud"
        val highScoreText = "Dagens highscore: $highScore"
        val pressAnyKeyText = "- Trykk en tast for å starte -"

        val largeFont = Font("Helvetica", Font.BOLD, 24)  // Define a larger font
        val mediumFont = Font("Helvetica", Font.BOLD, 16)
        val smallFont = Font("Helvetica", Font.BOLD, 14)  // Define a smaller font
        val miniFont = Font("Helvetica", Font.BOLD, 12)  // Define a smaller font

        val fontMetrics = getFontMetrics(largeFont)
        val miniFontMetrics = getFontMetrics(miniFont)
        val smallFontMetrics = getFontMetrics(smallFont)
        val mediumFontMetrics = getFontMetrics(mediumFont)
        val largeLineHeight = fontMetrics.height
        val smallLineHeight = smallFontMetrics.height

        val rh = RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
        )

        rh[RenderingHints.KEY_RENDERING] = RenderingHints.VALUE_RENDER_QUALITY

        (g as Graphics2D).setRenderingHints(rh)

        // Calculate the center position for the first line (higher on the screen)
        val centerX = (worldWidth - fontMetrics.stringWidth(welcomeText)) / 2
        val centerY1 = (worldHeight / 4) - largeLineHeight  // Adjust this value for vertical positioning

        val centerY2 = centerY1 + 1 * largeLineHeight

        val centerY3 = centerY2 + 2 * smallLineHeight

        val centerY4 = centerY3 + 5 * smallLineHeight

        val centerY5 = centerY4 + 7 * smallLineHeight

        // Draw the title line with the larger font
        g.color = Color.ORANGE
        g.font = largeFont
        g.drawString(welcomeText, centerX, centerY1)

        // Draw the second line with the smaller font
        g.color = Color.DARK_GRAY
        g.font = miniFont
        g.drawString(descriptionText, (worldWidth - miniFontMetrics.stringWidth(descriptionText)) / 2, centerY2)

        // Draw the author line with the smaller font
        g.color = Color.LIGHT_GRAY
        g.font = miniFont
        g.drawString(authorText, (worldWidth - miniFontMetrics.stringWidth(authorText)) / 2, centerY3)

        // Draw the score line with the smaller font
        g.color = Color.GREEN
        g.font = mediumFont
        g.drawString(highScoreText, (worldWidth - mediumFontMetrics.stringWidth(highScoreText)) / 2, centerY4)

        // Draw the input line with the smaller font (at the bottom of the screen)
        g.color = Color.YELLOW
        g.font = smallFont
        g.drawString(pressAnyKeyText, (worldWidth - smallFontMetrics.stringWidth(pressAnyKeyText)) / 2, centerY5)
    }

    private fun drawGameOverScreen(g: Graphics) {
        scoreLabel?.isVisible = false
        val msg = "Game Over"
        val score = "Du fikk med ${numLinks - 3}!"
        val newHighScoreText = if ((numLinks - 3) > highScore) {
            highScore = (numLinks - 3) // Update high score
            "Dette er ny highscore!"
        } else {
            ""
        }
        val largeFont = Font("Helvetica", Font.BOLD, 20)
        val smallFont = Font("Helvetica", Font.BOLD, 14)
        val fontMetrics = getFontMetrics(smallFont)

        val rh = RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
        )

        rh[RenderingHints.KEY_RENDERING] = RenderingHints.VALUE_RENDER_QUALITY

        (g as Graphics2D).setRenderingHints(rh)

        val msgWidth = fontMetrics.stringWidth(msg)
        val scoreWidth = fontMetrics.stringWidth(score)
        val newHighScoreWidth = fontMetrics.stringWidth(newHighScoreText)

        val msgX = ((worldWidth - msgWidth) / 3) + 27
        val msgY = (worldHeight - fontMetrics.height - 90) / 2 // Center vertically

        val scoreX = (worldWidth - scoreWidth) / 2
        val scoreY = msgY + fontMetrics.height + 10 // Add spacing between lines

        val newHighScoreX = (worldWidth - newHighScoreWidth) / 2
        val newHighScoreY = scoreY + fontMetrics.height + 15

        g.color = Color.RED
        g.font = largeFont
        g.drawString(msg, msgX, msgY)

        if (numLinks > highScore) {
            g.font = smallFont
            g.color = Color.YELLOW // Change the color for the new high score text
            g.drawString(newHighScoreText, newHighScoreX, newHighScoreY)
        }

        g.color = Color.GREEN
        g.font = smallFont
        g.drawString(score, scoreX, scoreY)

        if (gameOverTimer == null) {
            gameOverTimer = Timer(4000, ActionListener {
                welcomeScreen = true
                gameStarted = false
                gameOverTimer?.stop()
                gameOverTimer = null
                repaint()
            })
            gameOverTimer?.start()
        }
    }

    private fun startGame() {
        // Reset the game state
        numLinks = 3
        facingLeft = false
        facingRight = true
        facingUp = false
        facingDown = false
        gameInProgress = true
        initGame()

        // Start the game
        gameStarted = true
        welcomeScreen = false
        repaint()
    }

    private fun checkPickups() {
        if (x[0] == sadFaceX && y[0] == sadFaceY) {
            numLinks++
            locatePickup()
        }
    }

    private fun move() {
        for (z in numLinks downTo 1) {
            x[z] = x[z - 1]
            y[z] = y[z - 1]
        }
        if (facingLeft) {
            x[0] -= linkSize
        }
        if (facingRight) {
            x[0] += linkSize
        }
        if (facingUp) {
            y[0] -= linkSize
        }
        if (facingDown) {
            y[0] += linkSize
        }
    }

    private fun checkCollision() {

        for (z in numLinks downTo 1) {
            if (z > 4 && x[0] == x[z] && y[0] == y[z]) {
                gameInProgress = false
            }
        }
        if (y[0] >= worldHeight) {
            gameInProgress = false
        }

        if (y[0] < 0) {
            gameInProgress = false
        }

        if (x[0] >= worldWidth) {
            gameInProgress = false
        }

        if (x[0] < 0) {
            gameInProgress = false
        }

        if (!gameInProgress) {
            timer!!.stop()
        }
    }

    private fun locatePickup() {
        var r = (Math.random() * randomConstant).toInt()
        sadFaceX = r * linkSize

        r = (Math.random() * randomConstant).toInt()
        sadFaceY = r * linkSize

    }

    override fun actionPerformed(e: ActionEvent?) {
        if (gameInProgress) {
            checkPickups()
            checkCollision()
            move()
        }
        repaint()
    }

    private inner class TAdapter : KeyAdapter() {

        override fun keyPressed(e: KeyEvent?) {
            if (welcomeScreen) {
                startGame()
            } else if (gameInProgress) {  // Only handle key presses when the game is in progress
                val key = e!!.keyCode

                if (key == KeyEvent.VK_LEFT && !facingRight) {
                    facingLeft = true
                    facingUp = false
                    facingDown = false
                }

                if (key == KeyEvent.VK_RIGHT && !facingLeft) {
                    facingRight = true
                    facingUp = false
                    facingDown = false
                }

                if (key == KeyEvent.VK_UP && !facingDown) {
                    facingUp = true
                    facingRight = false
                    facingLeft = false
                }

                if (key == KeyEvent.VK_DOWN && !facingUp) {
                    facingDown = true
                    facingRight = false
                    facingLeft = false
                }
            }
        }
    }
}
