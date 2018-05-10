package br.ufsm.csi.seguranca;

import br.ufsm.csi.maico.EnviaRecebeDataGrama;
import br.ufsm.csi.seguranca.pila.model.PilaCoin;
import br.ufsm.csi.seguranca.util.RSAUtil;

import java.io.*;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws Exception {

        new Thread(new EnviaRecebeDataGrama()).start();
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
}