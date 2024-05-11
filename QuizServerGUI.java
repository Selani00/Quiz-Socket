import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class QuizServerGUI extends JFrame {
    private JLabel numQuestionsLabel;
    private JTextField numQuestionsField;
    private JButton startButton;
    private JTextArea questionArea;
    private JTextField[] answerFields;
    private JButton submitButton;
    private ServerSocket serverSocket;
    private List<String> questions;
    private Map<String, String[]> answersMap;

    public QuizServerGUI() {
        setTitle("Quiz Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridLayout(0, 1));

        numQuestionsLabel = new JLabel("Number of questions:");
        numQuestionsField = new JTextField();
        startButton = new JButton("Start Quiz");
        questionArea = new JTextArea();
        questionArea.setEditable(false);
        answerFields = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            answerFields[i] = new JTextField();
        }
        submitButton = new JButton("Submit");

        add(numQuestionsLabel);
        add(numQuestionsField);
        add(startButton);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startQuiz();
            }
        });

        add(questionArea);
        for (int i = 0; i < 4; i++) {
            add(answerFields[i]);
        }
        add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitQuestion();
            }
        });
    }

    private void startQuiz() {
        try {
            int numQuestions = Integer.parseInt(numQuestionsField.getText());
            questions = new ArrayList<>();
            answersMap = new HashMap<>();
            for (int i = 0; i < numQuestions; i++) {
                String question = JOptionPane.showInputDialog("Enter question " + (i + 1) + ":");
                questions.add(question);
                String[] options = new String[4];
                for (int j = 0; j < 4; j++) {
                    options[j] = JOptionPane.showInputDialog("Enter option " + (char) ('A' + j) + " for question " + (i + 1) + ":");
                }
                answersMap.put(question, options);
            }
            questionArea.setText("Questions created. You can now start the server.");
            startButton.setEnabled(false);
            submitButton.setEnabled(true);
            serverSocket = new ServerSocket(4800);
        } catch (NumberFormatException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input or error occurred.");
        }
    }

    private void submitQuestion() {
        try {
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            int numQuestions = questions.size();
            out.println(numQuestions);

            for (String question : questions) {
                out.println(question);
                String[] options = answersMap.get(question);
                for (String option : options) {
                    out.println(option);
                }
            }

            clientSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new QuizServerGUI().setVisible(true);
            }
        });
    }
}
