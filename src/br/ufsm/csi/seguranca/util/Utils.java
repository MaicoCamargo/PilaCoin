package br.ufsm.csi.seguranca.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Utils {

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

    /**
     * metodo para criptografar um byte array de acordo com o algoritmo recebido por parametro
     * @param algoritmo
     * @param chave
     * @param objeto
     * @return
     * @throws Exception
     */
    public static byte[] cripografar(String algoritmo, Key chave, byte[] objeto) throws Exception {
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.ENCRYPT_MODE, chave);
        return cipher.doFinal(objeto);
    }

    /**
     * metodo para descriptografar um byte array de acordo com o algoritmo recebido por parametro
     * @param algoritmo
     * @param chave
     * @param objeto
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] descriptografar(String algoritmo, Key chave, byte[] objeto) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.DECRYPT_MODE, chave);
        return cipher.doFinal(objeto);
    }
}
