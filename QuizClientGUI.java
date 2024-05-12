import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class QuizClientGUI extends JFrame {
    private Socket socket;
    private BufferedReader serverIn;
    private PrintWriter out;
    private BufferedReader userInput;
    private JLabel nameLabel;
    private JTextField nameField;
    private JButton startButton;
    private JLabel questionLabel;
    private JLabel[] optionLabels;
    private JTextField answerField;
    private JButton submitButton;
    private int numQuestions;
    private int currentQuestion = 0;

    public QuizClientGUI() {
        setTitle("Quiz Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridLayout(0, 1));

        nameLabel = new JLabel("Enter your name:");
        nameField = new JTextField();
        startButton = new JButton("Start Quiz");

        questionLabel = new JLabel();
        optionLabels = new JLabel[4];
        for (int i = 0; i < 4; i++) {
            optionLabels[i] = new JLabel();
        }
        answerField = new JTextField();
        submitButton = new JButton("Submit Answer");
        submitButton.setEnabled(false); // Disable until questions loaded

        add(nameLabel);
        add(nameField);
        add(startButton);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startQuiz();
            }
        });

        add(questionLabel);
        for (int i = 0; i < 4; i++) {
            add(optionLabels[i]);
        }
        add(answerField);
        add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitAnswer();
            }
        });

        // Center the window on the screen
        setLocationRelativeTo(null);
    }

    // 

    private void startQuiz() {
        try {
            socket = new Socket("192.168.1.60", 4800);
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));

            out.println(nameField.getText());
            numQuestions = Integer.parseInt(serverIn.readLine());
            System.out.println("Number of questions: " + numQuestions);

            startButton.setEnabled(false);
            submitButton.setEnabled(true);
            loadQuestion();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadQuestion() {
        try {
            String question = serverIn.readLine();
            if (question == null) {
                // Quiz ended
                JOptionPane.showMessageDialog(this, "Quiz ended. Thank you!");
                socket.close();
                return;
            }
            questionLabel.setText("Question " + (currentQuestion + 1) + ": " + question);

            // Clear previous answer
            answerField.setText("");

            for (int i = 0; i < 4; i++) {
                String option = serverIn.readLine();
                optionLabels[i].setText((char) ('A' + i) + ". " + option);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void submitAnswer() {
        try {
            String answer = answerField.getText();
            out.println(answer);
            currentQuestion++;
            if (currentQuestion < numQuestions) {
                loadQuestion();
            } else {
                JOptionPane.showMessageDialog(this, "Quiz ended. Thank you!");
                socket.close();
                // Close the window and terminate the program
                dispose(); // Close the window
                System.exit(0); // Terminate the program
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new QuizClientGUI().setVisible(true);
            }
        });
    }
}
