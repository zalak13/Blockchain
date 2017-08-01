package hw5.src.hw5;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Utils;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

public class CustomAddressGenerator {

	private static final String KEY_ALGORITHM           = "ECDSA";
	private static final String SIGNATURE_ALGORITHM     = "SHA256withECDSA";
	private static final String PROVIDER                = "BC";
	private static final String CURVE_NAME              = "secp256k1";

	private ECGenParameterSpec ecGenSpec;
	private KeyPairGenerator keyGen_;
	private SecureRandom random;

	public String run(String prefix) throws Exception {
		Security.addProvider(new BouncyCastleProvider());

		random = SecureRandom.getInstanceStrong();
		ecGenSpec = new ECGenParameterSpec(CURVE_NAME);
		keyGen_ = KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER);

		keyGen_.initialize(ecGenSpec, random);

		KeyPair kp = keyGen_.generateKeyPair();
		PublicKey publicKey = kp.getPublic(); //"pk" == "public key"
		PrivateKey secretKey = kp.getPrivate(); //"sk" == "secret key" == "private key"
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		//String.format("%040x", new BigInteger(1, prefix.getBytes("US-ASCII")))

		byte[] message = Utils.HEX.decode("04");
		outputStream.write( message );
		outputStream.write( secretKey.getEncoded() );
		byte concat[] = outputStream.toByteArray( );
		//String concatHex = DatatypeConverter.printHexBinary(concat);
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash256 = digest.digest(concat);
		RIPEMD160Digest d = new RIPEMD160Digest();
		d.update (hash256, 0, hash256.length);
		byte[] o = new byte[d.getDigestSize()];
		d.doFinal (o, 0);
		String msg = String.format("%x", new BigInteger(1, prefix.getBytes("US-ASCII")));
		byte[] versionbyte = Utils.HEX.decode("00"+msg);
		ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream( );
		outputStream2.write( versionbyte );
		outputStream2.write( o );
		byte concat1[] = outputStream2.toByteArray( );
		byte[] checksum = Arrays.copyOfRange(digest.digest(digest.digest(concat1)),0,4);
		outputStream2.write(checksum);
		byte finalresult[] =outputStream2.toByteArray();
		String BitCoinAddress = Base58.encode(finalresult);
		return BitCoinAddress;

	}
	/*  @param prefix	string of letters
	 *  @returns 		key whose Bitcoin address on mainnet starts with 1 followed prefix.
     */
	public static ECKey get(String prefix) {


		return null;
	}

	public static void main(String[] args) throws Exception {
		System.out.print(new CustomAddressGenerator().run("SHAH"));
	}
}
