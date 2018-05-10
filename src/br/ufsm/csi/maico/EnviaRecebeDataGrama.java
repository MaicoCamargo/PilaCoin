package br.ufsm.csi.maico;

import br.ufsm.csi.seguranca.pila.model.Mensagem;
import br.ufsm.csi.seguranca.util.RSAUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static br.ufsm.csi.seguranca.Main.deserializarObjeto;
import static br.ufsm.csi.seguranca.Main.serealizarObjeto;

public class EnviaRecebeDataGrama implements Runnable {

    final String meuId="Maico C.";
    final int porta= 3333;
    DatagramSocket clientSocket;



    public void enviarDataGrama() throws Exception {
        clientSocket = new DatagramSocket(4444);
        //msg envia pro servidor
        Mensagem mensagem = new Mensagem();
        mensagem.setIdOrigem(meuId);
        mensagem.setTipo(Mensagem.TipoMensagem.DISCOVER);
        mensagem.setChavePublica(RSAUtil.getPublicKey("public_key.der")); // minha chave publica
        mensagem.setPorta(4444); // porta onde eu recebo a resposta

        //socket cliente - onde são enviados os dados
        String  servidor = "127.0.0.1";
        InetAddress IPAddress = InetAddress.getByName(servidor);
        System.out.println("   -- mensagem criada  para mandar para o datagrama -> \n   ");
        byte[] mensagemSerealizada = serealizarObjeto(mensagem);// serealiza a msg

        //envia pacote UDP para servidor
        DatagramPacket sendPacket = new DatagramPacket(mensagemSerealizada,mensagemSerealizada.length, IPAddress, porta);
        System.out.println("Enviando pacote UDP para: " + servidor + ":" + porta);
        clientSocket.send(sendPacket);
    }

    public void recebeDataGrama() throws IOException, ClassNotFoundException {
        byte[] response = new byte[1500];//resposta que recebo do servidor UDP
            //recebe de volta pacote UDP do servidor
            DatagramPacket receivePacket = new DatagramPacket(response, response.length);
            clientSocket.receive(receivePacket);
            Mensagem respostaServidor = (Mensagem) deserializarObjeto(receivePacket.getData());//desserealizo a msg recebida
            if (respostaServidor.getTipo() == Mensagem.TipoMensagem.DISCOVER){
                System.out.println("minha mensagem !");
            }else{
                System.out.println("não é minha mensagem *-*");
                System.out.println("- "+respostaServidor.getIdOrigem() + " ip: "+respostaServidor.getEndereco() + " Porta: "+respostaServidor.getPorta());
                System.out.println("- MASTER: "+respostaServidor.isMaster());
                System.out.println("- TIPO: "+respostaServidor.getTipo());
            }
            System.out.println("final do socket !");
            clientSocket.close();
    }


    @Override
    public void run() {
        try {
            while (true){
                enviarDataGrama();
                recebeDataGrama();
                Thread.sleep((long)1500);
            }
        } catch (Exception e) {
            System.out.println("erro na thread envia e recebe datagrama");
            e.printStackTrace();
        }
    }
}
