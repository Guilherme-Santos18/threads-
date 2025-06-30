import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static final int PORTA = 12345;
    private static final List<PrintWriter> clientes = Collections.synchronizedList(new ArrayList<>());
    private static int numeroSecreto;
    private static boolean jogoAtivo = true;

    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(PORTA);
        numeroSecreto = new Random().nextInt(100) + 1;

        while (true) {
            Socket clienteSocket = servidor.accept();
            Thread threadCliente = new Thread(new ClienteHandler(clienteSocket));
            threadCliente.start();
        }
    }

    private static class ClienteHandler implements Runnable {
        private Socket socket;
        private BufferedReader entrada;
        private PrintWriter saida;
        private String nome;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                saida = new PrintWriter(socket.getOutputStream(), true);

                clientes.add(saida);
                saida.println("Digite seu nome:");
                nome = entrada.readLine();

                broadcast(nome + " entrou no jogo.");

                while (jogoAtivo) {
                    String linha = entrada.readLine();
                    int palpite;

                    try {
                        palpite = Integer.parseInt(linha);
                    } catch (NumberFormatException e) {
                        saida.println("Digite um número válido.");
                        continue;
                    }

                    if (palpite == numeroSecreto) {
                        broadcast(nome + " ACERTOU! O número era " + numeroSecreto + ".");
                        jogoAtivo = false;
                    } else if (palpite < numeroSecreto) {
                        broadcast(nome + " disse " + palpite + ": MAIOR");
                    } else {
                        broadcast(nome + " disse " + palpite + ": MENOR");
                    }
                }

                entrada.close();
                saida.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void broadcast(String mensagem) {
            synchronized (clientes) {
                for (PrintWriter cliente : clientes) {
                    cliente.println(mensagem);
                }
            }
        }
    }
}
