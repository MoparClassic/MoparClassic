package org.moparscape.msc.gs.util;

/*************************************************************************
 *  Compilation:  javac RSA.java
 *  Execution:    java RSA N
 *  
 *  Generate an N-bit public and private RSA key and use to encrypt
 *  and decrypt a random message.
 * 
 *  % java RSA 50
 *  public  = 65537
 *  private = 553699199426609
 *  modulus = 825641896390631
 *  message   = 48194775244950
 *  encrpyted = 321340212160104
 *  decrypted = 48194775244950
 *
 *  Known bugs (not addressed for simplicity)
 *  -----------------------------------------
 *  - It could be the case that the message >= modulus. To avoid, use
 *    a do-while loop to generate key until modulus happen to be exactly N bits.
 *
 *  - It's possible that gcd(phi, publicKey) != 1 in which case
 *    the key generation fails. This will only happen if phi is a
 *    multiple of 65537. To avoid, use a do-while loop to generate
 *    keys until the gcd is 1.
 *
 *************************************************************************/

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {
	private static KeyFactory keyFactory;
	private static PublicKey pubKey;
	private static PrivateKey privKey;
	static {
		try {
			keyFactory = KeyFactory.getInstance("RSA");
			pubKey = keyFactory
					.generatePublic(new X509EncodedKeySpec(
							new BigInteger(
									"258483531987721813854435365666199783121097212864526576114955744050873252978581213214062885665119329089273296913884093898593877564098511382732309048889240854054459372263273672334107564088395710980478911359605768175143527864461996266529749955416370971506195317045377519645018157466830930794446490944537605962330090699836840861268493872513762630835769942133970804813091619416385064187784658945")
									.toByteArray()));
			privKey = keyFactory
					.generatePrivate(new PKCS8EncodedKeySpec(
							new BigInteger(
									"126389230230897930352385109045517175528919326976939639978192012327670621489047580094424093866394604083620710942258641479882605306217705080633073206358002116577711775977035174649355992840385149147227804978220431329886904749249047207172981994234190017310148273188617548379533657419013879671232142718169518429371391936849151606245205570182197305333898616362563500262673852314745836689909720746451418490347388427381974609081779036643692442896601636017446393710362921966444628494554137329105609231821252714960402427087902143625637826354987050179190296311361184976459578647089802255916487029562372894817902016349527418080110572755696829671001527629662007011502494795321796989748708894483748787746164007093796775322700601606206239680220934740393355136437625692864876018489463040975412867784876767858234777778613227623572162881295529316433265197827292214481807179049611685053128209907494051691218003161010138655935539925662842276881932027193524730598562717449099166747466602094321757382874332291191770626601705016723177033286335759178872988144726412991304849553921854275796353460611722080118921976660955130059428940619614317969278356912087565839497213220194655672243883744862866647835331423918525974671607339058850826043973690788036967549648769172496014048685165109400959786151179359607410101378890865847238149636112094448917842512853709764865360978231071734030322981843223939320472985117985802975969976688950242865772248639234517663026594659960052526081995926396802458422109485608674299402862528020107837865224663685601410678473927829")
									.toByteArray()));
		} catch (Exception e) {
		}
	}

	/**
	 * Decrypt text using private key
	 * 
	 * @param text
	 *            The encrypted text
	 * @param key
	 *            The private key
	 * @return The unencrypted text
	 * @throws java.lang.Exception
	 */

	public static byte[] decrypt(byte[] text) throws Exception {

		byte[] dectyptedText = null;
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privKey);
		dectyptedText = cipher.doFinal(text);
		return dectyptedText;

	}

	/**
	 * Encrypt a text using public key.
	 * 
	 * @param text
	 *            The original unencrypted text
	 * @param key
	 *            The public key
	 * @return Encrypted text
	 * @throws java.lang.Exception
	 */
	public static byte[] encrypt(byte[] text) throws Exception {

		byte[] cipherText = null;
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		cipherText = cipher.doFinal(text);
		return cipherText;

	}

	/**
	 * Generate key which contains a pair of privae and public key using 1024
	 * bytes
	 * 
	 * @return key pair
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair generateKey() throws NoSuchAlgorithmException {

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair key = keyGen.generateKeyPair();
		return key;

	}
}
