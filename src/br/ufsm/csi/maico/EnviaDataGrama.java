package br.ufsm.csi.maico;

import br.ufsm.csi.seguranca.pila.model.Mensagem;
import br.ufsm.csi.seguranca.util.RSAUtil;
import br.ufsm.csi.seguranca.util.Utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static br.ufsm.csi.seguranca.util.Utils.serealizarObjeto;

public class EnviaDataGrama implements Runnable {

    final int porta = 3333;// onde vai ser enviada a resposta do servidor
    //final String ip= "127.0.0.1";
    final String ip= "255.255.255.255";
    //final String ip= "192.168.90.221";
    private DatagramSocket clientSocket = null;
    String meuId;

    public EnviaDataGrama(DatagramSocket clientSocket, String meuId) {
        this.clientSocket = clientSocket;
        this.meuId = meuId;
    }

    @Override
    public void run() {
        try {
            clientSocket.setBroadcast(true);
            while(true) {
                Mensagem msg = new Mensagem();
                msg.setTipo(Mensagem.TipoMensagem.DISCOVER);
                msg.setChavePublica(RSAUtil.getPublicKey("public_key.der"));
                msg.setIdOrigem(meuId);
                msg.setMaster(false);
                msg.setPorta(3333);
                msg.setAssinatura(null);
                msg.setEndereco(InetAddress.getByName("192.168.90.66"));

                byte[] buffer = Utils.serealizarObjeto(msg);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), porta);
                clientSocket.send(packet);
                 //System.out.println("Enviado o msg pro servidor ");
                Thread.sleep((long) 15000);
            }
        }catch (Exception e){
            e.printStackTrace();

        }

    }
}
