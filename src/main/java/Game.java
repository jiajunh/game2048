
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Game extends JPanel{
    static int width = 400;
    static int height = 500;
    private static final Color BG_COLOR = new Color(220,220,220);
    private static final String FONT_NAME = "Arial";
    private static final int TILE_SIZE = 72;
    private static final int TILES_MARGIN = 20;
    private static final int SCORE_HEIGHT = 20;

    private int score;
    private int state;
    private int[][] numbers;
    private Random r;
    private JLabel[][] squares = new JLabel[4][4];

    public Game() {
        this.score = 0;
        this.state = 0;
        this.r = new Random();
        this.numbers = new int[4][4];
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++)
                numbers[i][j] = 0;
        }
        setBackground(BG_COLOR);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    resetGame();
                }
                if (!canMove()) {
                    state = 1;
                }
                if (state == 0) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            left();
                            break;
                        case KeyEvent.VK_RIGHT:
                            right();
                            break;
                        case KeyEvent.VK_DOWN:
                            down();
                            break;
                        case KeyEvent.VK_UP:
                            up();
                            break;
                        default:
                            break;
                    }
                }

                if (!canMove()) {
                    state = 1;
                }
                repaint();
            }
        });
        resetGame();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                drawTile(g, numbers[i][j], i, j);
            }
        }
        if (state == 1) {
            drawLose(g);
        }
    }

    private void drawLose(Graphics g2) {
        Graphics2D g = ((Graphics2D) g2);
        final Font font = new Font(FONT_NAME, Font.BOLD, 36);
        final FontMetrics fm = getFontMetrics(font);
        String s = "Game Over";
        //g.setFont(new Font(FONT_NAME, Font.PLAIN, 36));
        final int w = fm.stringWidth(s);
        g.setFont(font);
        g.drawString("Game Over", width/2-w/2, 65);
    }

    private void drawTile(Graphics g2, int num, int r, int c) {
        Graphics2D g = ((Graphics2D) g2);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        g.setColor(getBackground(num));
        int offsetX = width-4*TILE_SIZE-3*TILES_MARGIN;
        int offsetY = height-4*TILE_SIZE-3*TILES_MARGIN-SCORE_HEIGHT;
        g.fillRoundRect(offsetX/2+c*(TILES_MARGIN+TILE_SIZE), offsetY/2+SCORE_HEIGHT+r*(TILES_MARGIN+TILE_SIZE), TILE_SIZE, TILE_SIZE, 14, 14);
        g.setColor(getForeground(num));

        String s = String.valueOf(num);
        final Font font = new Font(FONT_NAME, Font.BOLD, 32);
        final FontMetrics fm = getFontMetrics(font);
        g.setFont(font);

        final int w = fm.stringWidth(s);
        final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

        if (num != 0)
            g.drawString(s, offsetX/2+c*(TILES_MARGIN+TILE_SIZE) + (TILE_SIZE - w) / 2, offsetY/2+SCORE_HEIGHT+r*(TILES_MARGIN+TILE_SIZE) + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        g.drawString("Score: " + score, 20, 30);
    }

    public Color getForeground(int num) {
        return num < 16 ? new Color(0x776e65) :  new Color(0xf9f6f2);
    }

    public Color getBackground(int val) {
        switch (val) {
            case 0:    return new Color(0xbbada0);
            case 2:    return new Color(0xeee4da);
            case 4:    return new Color(0xede0c8);
            case 8:    return new Color(0xf2b179);
            case 16:   return new Color(0xf59563);
            case 32:   return new Color(0xf67c5f);
            case 64:   return new Color(0xf65e3b);
            case 128:  return new Color(0xedcf72);
            case 256:  return new Color(0xedcc61);
            case 512:  return new Color(0xedc850);
            case 1024: return new Color(0xedc53f);
            default: return new Color(0xedc22e);
        }
    }

    public boolean canMove() {
        List<Integer> temp = availableSpace();
        if (!temp.isEmpty())
            return true;
        int[] dir_x = {-1, 0, 1, 0};
        int[] dir_y = {0, -1, 0, 1};
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                for (int k = 0; k < 4; k++) {
                    if (i + dir_x[k] >= 0 && i + dir_x[k] < 4 && j + dir_y[k] >= 0 && j+dir_y[k]<4) {
                        if (numbers[i][j] == numbers[i+dir_x[k]][j+dir_y[k]])
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public void resetGame() {
        score = 0;
        state = 0;
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                numbers[i][j] = 0;
            }
        }
        generateNewBlock();
        generateNewBlock();
    }

    public List<Integer> availableSpace() {
        List<Integer> l = new ArrayList<Integer>();
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                if (numbers[i][j] == 0) {
                    l.add(i*4+j);
                }
            }
        }
        return l;
    }

    public void generateNewBlock() {
        List<Integer> l = availableSpace();
        //System.out.println(l.size());
        if (!l.isEmpty()) {
            int len = l.size();
            int pos = r.nextInt(len);
            int row = l.get(pos)/4;
            int col = l.get(pos)%4;
            int num = r.nextInt();
            numbers[row][col] = num<=0.5 ? 2:4;
        }
    }

    public void left() {
        int[][] st = new int[4][4];
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++)
                st[i][j] = numbers[i][j];
        }
        for (int i=0; i<4; i++) {
            int[] temp = numbers[i];
            int idx = 0;
            for (int j=0; j<4; j++) {
                if (temp[j] != 0)
                    numbers[i][idx++] = temp[j];
            }
            for (int j=idx ;j<4; j++)
                numbers[i][j] = 0;
            for (int j=1; j<4; j++) {
                if (numbers[i][j] != 0 && numbers[i][j]==numbers[i][j-1]) {
                    numbers[i][j-1] *= 2;
                    score += numbers[i][j];
                    numbers[i][j] = 0;
                }
            }
        }
        if (!cmp(numbers, st))
            generateNewBlock();
    }

    public void right() {
        int[][] st = new int[4][4];
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++)
                st[i][j] = numbers[i][j];
        }
        for (int i=0; i<4; i++) {
            int[] temp = numbers[i];
            int idx = 3;
            for (int j=3; j>=0; j--) {
                if (temp[j] != 0)
                    numbers[i][idx--] = temp[j];
            }
            for (int j=idx ;j>=0; j--)
                numbers[i][j] = 0;
            for (int j=2; j>=0; j--) {
                if (numbers[i][j] != 0 && numbers[i][j]==numbers[i][j+1]) {
                    numbers[i][j+1] *= 2;
                    score += numbers[i][j];
                    numbers[i][j] = 0;
                }
            }
        }
        if (!cmp(numbers, st))
            generateNewBlock();
    }

    public void up() {
        int[][] st = new int[4][4];
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++)
                st[i][j] = numbers[i][j];
        }
        for (int j=0; j<4; j++) {
            int[] temp = new int[4];
            for (int k=0; k<4; k++)
                temp[k] = numbers[k][j];
            int idx = 0;
            for (int i=0; i<4; i++) {
                if (temp[i] != 0)
                    numbers[idx++][j] = temp[i];
            }
            for (int i=idx ;i<4; i++)
                numbers[i][j] = 0;
            for (int i=1; i<4; i++) {
                if (numbers[i][j] != 0 && numbers[i][j]==numbers[i-1][j]) {
                    numbers[i-1][j] *= 2;
                    score += numbers[i][j];
                    numbers[i][j] = 0;
                }
            }
        }
        if (!cmp(numbers, st))
            generateNewBlock();
    }

    public void down() {
        int[][] st = new int[4][4];
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++)
                st[i][j] = numbers[i][j];
        }
        for (int j=0; j<4; j++) {
            int[] temp = new int[4];
            for (int k=0; k<4; k++)
                temp[k] = numbers[k][j];
            int idx = 3;
            for (int i=3; i>=0; i--) {
                if (temp[i] != 0)
                    numbers[idx--][j] = temp[i];
            }
            for (int i=idx ;i>=0; i--)
                numbers[i][j] = 0;
            for (int i=2; i>=0; i--) {
                if (numbers[i][j] != 0 && numbers[i][j]==numbers[i+1][j]) {
                    numbers[i+1][j] *= 2;
                    score += numbers[i][j];
                    numbers[i][j] = 0;
                }
            }
        }
        if (!cmp(numbers, st))
            generateNewBlock();
    }

    public boolean cmp(int[][] n, int[][] tp) {
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                if (n[i][j] != tp[i][j])
                    return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        JFrame game = new JFrame();
        game.setTitle("2048");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(width, height);
        game.setResizable(false);
        game.add(new Game());
        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }
}
