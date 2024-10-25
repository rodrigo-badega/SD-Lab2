/**
 *
 * @Rodrigo Oliveira Badega 2207273
 * @Guilherme Henrique Soeiro Fontes 2320657
 */

import java.io.*;
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Random;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

	private static Socket socket;
	private static ServerSocket server;

	private static DataInputStream entrada;
	private static DataOutputStream saida;

	private int porta = 1025;

	public final static Path path = Paths			
			.get("fortune-br.txt");
	private int NUM_FORTUNES = 0;

	public int countFortunes() throws FileNotFoundException {

		int lineCount = 0;

		InputStream is = new BufferedInputStream(new FileInputStream(
				path.toString()));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				is))) {

			String line = "";
			while (!(line == null)) {

				if (line.equals("%"))
					lineCount++;

				line = br.readLine();

			}// fim while

			System.out.println(lineCount);
		} catch (IOException e) {
			System.out.println("SHOW: Excecao na leitura do arquivo.");
		}
		return lineCount;
	}

	public void hashFortuna(HashMap<Integer, String> hm)
			throws FileNotFoundException {

		InputStream is = new BufferedInputStream(new FileInputStream(
				path.toString()));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				is,"UTF8"))) {//se windows usar ISO-8859-1

			int lineCount = 0;

			String line = "";
			while (!(line == null)) {

				if (line.equals("%"))
					lineCount++;

				line = br.readLine();
				StringBuffer fortune = new StringBuffer();
				while (!(line == null) && !line.equals("%")) {
					fortune.append(line + "\n");
					line = br.readLine();
				}

				hm.put(lineCount, fortune.toString());
			}// fim while

		} catch (IOException e) {
			System.out.println("SHOW: Excecao na leitura do arquivo.");
		}
	}

	public String parser(String input_cliente, HashMap<Integer, String> hm){
		// System.out.println("Input do cliente: "+input_cliente);
		// Processamento do valor
		
		String resultado = "";
		if (input_cliente.contains("\n")){
				if(input_cliente.contains("read")){

					Random gerador = new Random();
                    int valorAleatorio = gerador.nextInt(NUM_FORTUNES);//gera um valor de 0 a num max de fortunas
					resultado = "{\"result\":\""+hm.get(valorAleatorio)+"\"}\n";//retorna fortuna referente ao numero 

				}else if(input_cliente.contains("write")){

					String [] tokens = input_cliente.split("[{\\\"\\,\\[\\]\\:\\}]");
					System.out.println("Split do input: "+tokens[12]);//posicao dos args no json
					String fortuna = tokens[12]+"\n%";//monta a fortuna no formato do arquivo

                    byte data[] = fortuna.getBytes();//pega o num de bytes
                    try (OutputStream out = new BufferedOutputStream(
						Files.newOutputStream(path, CREATE, APPEND))){
							out.write(data,0,data.length);//escreve no fim do arquivo
						}catch (IOException x){
							System.err.println(x);
						}

					resultado = "{\"result\":\""+fortuna+"\"}\n";//resultado do write

				}else{
					System.out.println("Tipo inválido!");
					resultado = "{\"result\":\"false\"}\n";
				}
						
		}else{
			resultado = "{\"result\":\"false\"}\n";
		}

		return resultado;
	}

	public void iniciar() {
		
		// Inicializa hashMap do arquivo de fortunas
		HashMap hm = new HashMap<Integer, String>();
			try {
				NUM_FORTUNES = countFortunes();
				hashFortuna(hm);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		
		// Criar porta de recepcao
		System.out.println("Servidor iniciado na porta: " + porta);
		try {
			server = new ServerSocket(porta);
			socket = server.accept();  //Processo fica bloqueado, ah espera de conexoes

			// Criar os fluxos de entrada e saida
			entrada = new DataInputStream(socket.getInputStream());
			saida = new DataOutputStream(socket.getOutputStream());

			//le o input do cliente
			String input_cliente = entrada.readUTF();

			//processa json e retorna o resultado
			String resultado = parser(input_cliente, hm);

			// Envio dos dados (resultado)
			saida.writeUTF(resultado);

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		new Servidor().iniciar();

	}

}
