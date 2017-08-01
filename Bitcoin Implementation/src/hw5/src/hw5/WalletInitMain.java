package hw5.src.hw5;

import org.bitcoinj.params.MainNetParams;

import java.io.File;

public class WalletInitMain extends WalletInit {
        public static final String WALLET_FILE_PATH = "H:\\MS Stuff\\NorthEastern Unversity\\Curriclum\\Final Semester\\Blockchain Engineering\\wallets\\wallet_main";

    public WalletInitMain() {
        super(MainNetParams.get(), "main");
    }

    public static void main(String[] args) {
        new WalletInitMain();
    }
}
