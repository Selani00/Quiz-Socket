import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class QuizServerGUI extends JFrame {
    private Map<ClientHandler, String[]> clientResponses = new HashMap<>();
    private List<String> questions = new ArrayList<>();
    private Map<String, String[]> answersMap = new HashMap<>();
    private int numQuestions;
    private int questionIndex = 0;

    private JLabel questionLabel;
    private JTextField questionField;
    private JTextField[] answerFields;

    public QuizServerGUI() {
        setTitle("Quiz Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the frame on screen

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel questionPanel = new JPanel(new GridLayout(6, 1));
        JPanel buttonPanel = new JPanel();

        questionLabel = new JLabel("Question:");
        questionField = new JTextField();
        answerFields = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            answerFields[i] = new JTextField();
        }

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (questionIndex < numQuestions) {
                    String question = questionField.getText();
                    if (question.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Question cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    questions.add(question);

                    String[] options = new String[4];
                    for (int i = 0; i < options.length; i++) {
                        String answer = answerFields[i].getText();
                        if (answer.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Option cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        options[i] = answer;
                    }
                    answersMap.put(question, options);

                    questionIndex++;
                    if (questionIndex < numQuestions) {
                        questionLabel.setText("Question " + (questionIndex + 1) + ":");
                        questionField.setText("");
                        for (int i = 0; i < 4; i++) {
                            answerFields[i].setText("");
                        }
                    } else {
                        startServer();
                    }
                }
            }
        });

        questionPanel.add(questionLabel);
        questionPanel.add(questionField);
        for (int i = 0; i < 4; i++) {
            questionPanel.add(answerFields[i]);
        }

        buttonPanel.add(nextButton);

        mainPanel.add(questionPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(4800);
            System.out.println("Server Started....");
            System.out.println("Waiting for students to connect...");

            // Start accepting client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new QuizServerGUI();
            }
        });
    }

    // Inner classes remain the same as in your original code
}
