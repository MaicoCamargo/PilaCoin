package br.ufsm.csi.maico;

import br.ufsm.csi.seguranca.pila.model.Mensagem;
import java.io.*;
import java.net.Socket;

public class RecebePila implements Runnable {


    @Override
    public void run() {


        try {
            System.out.println("\n\n\n\nrecebendo pila !\n\n\n\n");
            Socket socket = new Socket("192.168.83.185", 3333);


            //recebo a resposta do servidor
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Mensagem msgResposta = (Mensagem) in.readObject();
            //desrealiza o obj recebido do servidor usando minha chave de sessao
            System.out.println("?"+ msgResposta.getIdOrigem());

        }catch (Exception e){
            e.printStackTrace();
        }



    }
}
