package br.ufsm.csi.seguranca;

import br.ufsm.csi.seguranca.pila.model.Mensagem;
import br.ufsm.csi.seguranca.pila.model.PilaCoin;
import br.ufsm.csi.seguranca.util.RSAUtil;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws Exception {
        enviarDataGrama();
        PilaCoin pilaCoin = new PilaCoin();
        pilaCoin.setChaveCriador(RSAUtil.getPublicKey("public_key.der"));
        pilaCoin.setIdCriador("M. Camargo");
        pilaCoin.setDataCriacao(new Date());
    }

    /**
     * serealiza o obj recebido por parametro
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] serealizarObjeto(Serializable obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(obj);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * deserealizar o obj recebido por parametro
     * @param obj
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Serializable deserializarObjeto(byte[] obj) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(obj);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return (Serializable) objectInputStream.readObject();
    }

    public static void enviarDataGrama() throws Exception {
        final String meuId="Maico C.";
        final int porta= 3333;
        //msg envia pro servidor
        Mensagem mensagem = new Mensagem();
        mensagem.setIdOrigem(meuId);
        mensagem.setTipo(Mensagem.TipoMensagem.DISCOVER);
        mensagem.setChavePublica(RSAUtil.getPublicKey("public_key.der")); // minha chave publica
        mensagem.setPorta(4444); // porta onde eu recebo a resposta

        //socket cliente - onde são enviados os dados
        DatagramSocket clientSocket = new DatagramSocket(4444);
        String  servidor = "127.0.0.1";
        InetAddress IPAddress = InetAddress.getByName(servidor);
        System.out.println("   -- mensagem criada  para mandar para o datagrama -> \n   ");
        byte[] mensagemSerealizada = serealizarObjeto(mensagem);// serealiza a msg

        //envia pacote UDP para servidor
        DatagramPacket sendPacket = new DatagramPacket(mensagemSerealizada,mensagemSerealizada.length, IPAddress, porta);
        System.out.println("Enviando pacote UDP para: " + servidor + ":" + porta);
        clientSocket.send(sendPacket);

        byte[] response = new byte[1500];//resposta que recebo do servidor UDP
        while (true){

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
        }


    }
}