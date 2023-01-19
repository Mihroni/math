import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketClient extends JFrame implements ActionListener, Runnable {
    JTextArea textArea = new JTextArea();
    JScrollPane jp = new JScrollPane(textArea);
    JTextField input_Text = new JTextField();
    Socket socket;
    BufferedReader bufferedReader;
    PrintWriter printWriter;
    int points = 0;
    int round = 1;
    String name;

    public SocketClient() {
        super("Zvezda");
        setFont(new Font("Arial Black", Font.PLAIN, 12));
        setForeground(new Color(173, 148, 157));
        setBackground(new Color(78, 167, 201));
        textArea.setToolTipText("Chat History");
        textArea.setForeground(new Color(255, 255, 255));
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        textArea.setBackground(new Color(131, 127, 127));

        getContentPane().add(jp, "Center");
        input_Text.setToolTipText("Enter your Message");
        input_Text.setForeground(new Color(0, 0, 0));
        input_Text.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        input_Text.setBackground(new Color(230, 230, 250));

        getContentPane().add(input_Text, "South");
        setSize(325, 411);
        setVisible(true);

        input_Text.requestFocus(); //Place cursor at run time, work after screen is shown

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        input_Text.addActionListener(this); //Event registration
    }

    public void serverConnection() {
        try {
            String IP = "127.0.0.1";
            String filepath = "/Users/Dell/Desktop/login/names.txt";
            socket = new Socket(IP, 1234);
            Login(filepath, socket);
        } catch (Exception e) {
            System.out.println(e + " Socket Connection error");
        }
    }

    public void Pw(Socket sk, String name) {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(sk.getInputStream()));

            printWriter = new PrintWriter(sk.getOutputStream(), true);
            printWriter.println(name);
            new Thread(this).start();
        } catch (Exception e) {
            System.out.println(e + " Socket Connection error");
        }

    }

    public void Login(String filepath, Socket sk) {
        try {
            name = JOptionPane.showInputDialog(this, "Please enter a nickname", JOptionPane.INFORMATION_MESSAGE);
            boolean verified = Login.verifyLogin(name, "order.txt");
            while (!verified) {
                if (!verified) {
                    JLabel messageLabel = new JLabel("<html><body><p style='width: 500px;'>" + "Username already exists!" + "</p></body></html>");
                    JOptionPane.showMessageDialog(null, messageLabel, "Error Window Title", JOptionPane.ERROR_MESSAGE);

                    name = JOptionPane.showInputDialog(this, "Please enter a name", JOptionPane.INFORMATION_MESSAGE);
                    verified = Login.verifyLogin(name, "order.txt");
                } else {
                    verified = false;
                }
            }
            Pw(sk, name);
        } catch (Exception e) {
            System.out.println(e + "--> Client run fail");
        }
    }


    public static void main(String[] args) {
        new SocketClient().serverConnection();
    }

    @Override
    public void run() {
        String data;

        try {
            while ((data = bufferedReader.readLine()) != null) {
                textArea.append(data + "\n");
                textArea.setCaretPosition(textArea.getText().length());
            }
        } catch (Exception e) {
            System.out.println(e + "--> Client run fail");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String data = input_Text.getText();
        printWriter.println(name + " => " + data);
        if (data.equals("Start")) {
            playersInGame();
            printWriter.println("Current round " + round);
            printWriter.println(WordGenerator.generateProblem());
            try {
                FileWriter writer = new FileWriter("round.txt", true);
                writer.write(Integer.toString(round));
                writer.close();
            } catch (IOException f) {
                System.out.println(f);
            }
        }
        if (Integer.parseInt(Login.round()) <= 5) {
            if (checkAnswer(Login.words(), data)) {
                try {
                    FileWriter writer = new FileWriter("winner.txt", true);
                    writer.write(name);
                    writer.write("/");
                    writer.close();
                } catch (IOException f) {
                    System.out.println(f);
                }
                points++;
                round = Integer.parseInt(Login.round());
                round++;
                printWriter.println(points + " points " + name);
                WordGenerator.deleteFileContent("round.txt");
                checkForWinner();
                try {
                    FileWriter writer = new FileWriter("round.txt", true);
                    writer.write(Integer.toString(round));
                    writer.close();
                } catch (IOException f) {
                    System.out.println(f.getMessage());
                }
                WordGenerator.deleteFileContent("word.txt");
                if (round <= 5) {
                    printWriter.println("Current round " + Login.round());
                    printWriter.println(WordGenerator.generateProblem());
                } else {
                    clearBoard();
                    for (Map.Entry<String, Integer> entry : checkForWinner().entrySet()) {
                        printWriter.println(entry.getKey() + ": " + entry.getValue());
                    }
                }
            }
        }

        input_Text.setText("");
    }

    public static boolean checkAnswer(String file, String guess) {
        return file.equals(guess);
    }

    public Map<String, Integer> checkForWinner() {
        String[] winners = Login.winners().split("/");
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : winners) {
            System.out.println(word);
            if (wordCount.containsKey(word)) {
                wordCount.put(word, wordCount.get(word) + 1);
            } else {
                wordCount.put(word, 1);
            }
        }
        return wordCount;
    }

    private void clearBoard() {
        for (int i = 0; i < 20; i++) {
            printWriter.println("");
        }
    }

    public void playersInGame() {
        String[] playersInGame = Login.players().split("/");
        for (int i = 1; i <= playersInGame.length; i++) {
            printWriter.println(i + ": " + playersInGame[i-1]);
        }
    }
}