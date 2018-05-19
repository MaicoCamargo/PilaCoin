package br.ufsm.csi.seguranca;

import br.ufsm.csi.maico.MineraPilacoin;

public class Main {

    public static void main(String[] args) throws Exception {
        new Thread(new MineraPilacoin()).start();
    }

}