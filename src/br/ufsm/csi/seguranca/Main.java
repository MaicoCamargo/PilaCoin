package br.ufsm.csi.seguranca;

import br.ufsm.csi.maico.EnviaDataGrama;
import br.ufsm.csi.maico.RecebeDataGrama;

import java.net.DatagramSocket;

public class Main {

    public static void main(String[] args) throws Exception {
        final String meuId="Maico C.";
        DatagramSocket clientSocket = new DatagramSocket(3333);
        new Thread(new EnviaDataGrama(clientSocket,meuId)).start();
        new Thread(new RecebeDataGrama(clientSocket,meuId)).start();

    }

}