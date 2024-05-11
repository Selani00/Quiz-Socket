import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket; 

public class QuizClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4800);
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            

            // Get client name from user
            System.out.print("Enter your name: ");
            String clientName = userInput.readLine();
            out.println(clientName);
            int numQuestions = Integer.parseInt(serverIn.readLine());   
            System.out.println("Number of questions: " + numQuestions);

            // Receive and answer questions from the server
            String question;
                    
            
            for(int j = 1; j <= numQuestions; j++) {
                question = serverIn.readLine();
                System.out.println((String)("Question "+j)+" : "+ question);
                for (int i = 0; i < 4; i++) {
                    String option = serverIn.readLine();
                    System.out.println((char)('A' + i) + ". " + option);
                    

                }

                // Get user's answer
                System.out.print("Your answer: ");
                String answer = userInput.readLine();

                out.println(answer);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}