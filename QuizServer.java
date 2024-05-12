import java.io.*;
import java.net.*;
import java.util.*;


public class QuizServer {
    private static Map<ClientHandler, String[]> clientResponses = new HashMap<>();
    private static List<String> questions = new ArrayList<>();
    private static Map<String, String[]> answersMap = new HashMap<>();
    private static int nextClientId = 1;
    private static int numQuestions;



    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4800);

            // Ask for the number of questions
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the number of questions: ");
            numQuestions = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            // Receive questions and answers from the user
            for (int i = 0; i < numQuestions; i++) {
                System.out.print("\nEnter question " + (i + 1) + ": ");
                String question = scanner.nextLine();
                if(question.isEmpty()) {
                    System.out.println("Question cannot be empty. Please enter again.");
                    i--;
                    continue;
                }
                questions.add(question);

                String[] options = new String[4];
                for (int j = 0; j < options.length; j++) {
                    System.out.print("Enter option " + (char)('A' + j) + ": ");
                    String input = scanner.nextLine();
                    if(input.isEmpty()) {
                        System.out.println("Option cannot be empty. Please enter again.");
                        j--;
                        continue;
                    }
                    options[j] = input;
                }
                answersMap.put(question, options);
            }

            System.out.println("Server Started....");
            System.out.println("Waiting for students to connect...");

            //Close scanner after receiving input
            scanner.close();

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

    public static Map<ClientHandler, String[]> getClientResponses() {
        return clientResponses;
    }

    public static int getNumQuestions() {
        return numQuestions;
    }

    public static List<String> getQuestions() {
        return questions;
    }

    public static Map<String, String[]> getAnswersMap() {
        return answersMap;
    }

    public static synchronized int getNextClientId() {
        return nextClientId++;
    }

    public static void addClientResponse(ClientHandler clientHandler, String[] responses) {
        clientResponses.put(clientHandler, responses);
    }

    // get the clients responses
    public static void printClientResponses() {
        for (Map.Entry<ClientHandler, String[]> entry : clientResponses.entrySet()) {
            ClientHandler client = entry.getKey();
            String[] responses = entry.getValue();
            System.out.println("Client ID: " + client.getClientId());
            System.out.println("Client Name: " + client.getClientName());
            for(int i=0; i<responses.length; i++) {
                System.out.println("Question "+(i+1)+": "+responses[i]);
            }
            System.out.println("-----------------------------");
        }
    }
}

class ClientHandler implements Runnable {
    private int clientId;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.clientId = QuizServer.getNextClientId();
        this.clientName = ""; // Initialize clientName
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Get client name
            clientName = in.readLine();

            // Send questions one by one to client
            
            List<String> questions = QuizServer.getQuestions();
            Map<String, String[]> answersMap = QuizServer.getAnswersMap();
            int numQuestions = QuizServer.getNumQuestions();
            out.println(numQuestions); // Send number of questions to client

            String[] responses = new String[questions.size()]; // Initialize responses array

            for (int i = 0; i < questions.size(); i++) {
                String question = questions.get(i);
                out.println(question);
                String[] options = answersMap.get(question);
                for (String option : options) {
                    out.println(option);
                }

                // Get response from client
                //out.println("Enter your response: ");
                String answer = in.readLine();
                responses[i] = answer;
            }

            QuizServer.addClientResponse(this, responses); // Store client responses
            clientSocket.close();
            QuizServer.printClientResponses(); // Print the map after the conversation ends
        } catch (SocketException e) {
            System.out.println("Client disconnected unexpectedly: " + clientName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }
}