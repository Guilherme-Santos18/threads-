import java.io.*;
import java.net.*;

public class Cliente {
    private static final String SERVIDOR = "localhost";
    private static final int PORTA = 12345;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVIDOR, PORTA);

        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        Thread recebimento = new Thread(() -> {
            try {
                String linha;
                while ((linha = entrada.readLine()) != null) {
                    System.out.println(linha);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        recebimento.start();

        String linha;
        while ((linha = teclado.readLine()) != null) {
            saida.println(linha);
        }

        socket.close();
    }
}
