import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MatchCards {
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }

    }

    String[] cardList = { // track cardNames
            "darkness",
            "double",
            "fairy",
            "fighting",
            "fire",
            "grass",
            "lightning",
            "metal",
            "psychic",
            "water" };

    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<Card> cardSet; // create a deck with cardname and cardImangeIcon
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth; // 5 * 90
    int boardHeight = rows * cardHeight; // 4 * 128

    JFrame frame = new JFrame("Pokemon Match Cards");
    JLabel textLabel = new JLabel();
    JPanel textJPanel = new JPanel();
    JPanel boardJPanel = new JPanel();

    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();
    int errorCount = 0;

    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = false;

    JButton card1Selected;
    JButton card2Selected;

    MatchCards() {
        setupCards();
        shuffleCards();

        // window
        frame.setResizable(false);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // text error
        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Error: " + Integer.toString(errorCount));

        textJPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textJPanel.add(textLabel);
        frame.add(textJPanel, BorderLayout.NORTH);

        // card game board
        board = new ArrayList<JButton>();
        boardJPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameReady) {
                        return;
                    }
                    JButton tile = (JButton) e.getSource();
                    if (tile.getIcon() == cardBackImageIcon) {
                        if (card1Selected == null) {
                            card1Selected = tile;
                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                        } else if (card2Selected == null) {
                            card2Selected = tile;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                            if (card1Selected.getIcon() != card2Selected.getIcon()) {
                                errorCount += 1;
                                textLabel.setText("Error: " + Integer.toString(errorCount));
                                hideCardTimer.start();
                            } else {
                                card1Selected = null;
                                card2Selected = null;
                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardJPanel.add(tile);
        }
        frame.add(boardJPanel);

        // restart game button
        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setFocusable(false);
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setText("Restart Game");
        restartButton.setEnabled(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameReady) {
                    return;
                }
                gameReady = false;
                restartButton.setEnabled(false);
                card1Selected = null;
                card2Selected = null;
                shuffleCards();

                // re assign buttons with new cards
                for (int i = 0; i < cardSet.size(); i++) {
                    board.get(i).setIcon(cardSet.get(i).cardImageIcon);
                }

                errorCount = 0;
                textLabel.setText("Error: " + Integer.toString(errorCount));
                hideCardTimer.start();
            }
        });
        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.pack();// when all component added
        frame.setVisible(true);

        // start game
        hideCardTimer = new Timer(1500, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }

        });
        hideCardTimer.setRepeats(false);
        hideCardTimer.start();
    }

    public void setupCards() {
        cardSet = new ArrayList<Card>();
        for (int i = 0; i < 10; i++) {
            // load each card image
            Image carImage = new ImageIcon(getClass().getResource("/img/" + cardList[i] + ".jpg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(
                    carImage.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));

            // create card object and add to cardSet
            Card card = new Card(cardList[i], cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet);

        // load the back card image
        Image cardBackImage = new ImageIcon(getClass().getResource("/img/back.jpg")).getImage();
        cardBackImageIcon = new ImageIcon(
                cardBackImage.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
    }

    public void shuffleCards() {
        System.out.println("CardSet: ");
        System.out.println(cardSet);

        // shuffle
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size()); // get random index
            Card temp = cardSet.get(i);

            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
        System.out.println("CardSet after shuffle: ");
        System.out.println(cardSet);
    }

    public void hideCards() {
        if (gameReady && card1Selected != null && card2Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;
        } else {
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }
}
