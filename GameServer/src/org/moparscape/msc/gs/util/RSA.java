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
									"30820122300d06092a864886f70d01010105000382010f003082010a02820101009bc61e7f0b33c443ede4cc5abc340890cf32395f54e54116ad18b81fd8f88d0e380cf36bcd410d582cd0c9bdceeb0534a9905dcddf77a9999c53de89a968f91b7bfb8ad43604dcf070bb506db168d935df1619ca839368923bfe5a2c131e40e963e1787247c3e5e72676d422821eddc2ef65fba1485fb36720834bd4121042afbea57a1721a98d42cdecb67c03136f11cc86d4e87cb6fa6f2d7cde41a6d721a10e457bb7338e8ccd85eb2a4f727131dc411c23acf57810108c9edfe1b12e9c7b67d1313ae6ba7d05a21b13dc655bda8a38116274aeeae702870d0130f34788d9486ec2e9f591cb229235dcac99066d4112373caefb7d73abb69cc190e8a86a4d0203010001", 16)
									.toByteArray()));
			privKey = keyFactory
					.generatePrivate(new PKCS8EncodedKeySpec(
							new BigInteger(
									"308204be020100300d06092a864886f70d0101010500048204a8308204a402010002820101009bc61e7f0b33c443ede4cc5abc340890cf32395f54e54116ad18b81fd8f88d0e380cf36bcd410d582cd0c9bdceeb0534a9905dcddf77a9999c53de89a968f91b7bfb8ad43604dcf070bb506db168d935df1619ca839368923bfe5a2c131e40e963e1787247c3e5e72676d422821eddc2ef65fba1485fb36720834bd4121042afbea57a1721a98d42cdecb67c03136f11cc86d4e87cb6fa6f2d7cde41a6d721a10e457bb7338e8ccd85eb2a4f727131dc411c23acf57810108c9edfe1b12e9c7b67d1313ae6ba7d05a21b13dc655bda8a38116274aeeae702870d0130f34788d9486ec2e9f591cb229235dcac99066d4112373caefb7d73abb69cc190e8a86a4d020301000102820100550e8c0e60c6866f75118e8828e5dc9b7d9775fe58f3c963a166b2fb28d526e851b0b8a5ef708ae1cfaebdc9b7975ac353a731a13d5930b160cbe6e2922f707e1267da795f79ae4e57afe00dd56663dc6790c0ca74a7e3dc9d87982322d94d266354751929988d0b775441270a97e20d13fd42b5e137e854f24adef7d059329fbbbf09a1eda121d6cf34bc7c072f6f3dbe8d6d30ae012e59e4fefaf978d4c7b6c142cc61fb426152481c711c60809c6616f1f6e76577d67e201bc612e53e16c98aeb23a1a00c499d93532f6d8796f4504506e73df443e5e07fc942d6f16fd85a9d058ea48240752716a4a8388ca81302a783b3306fdfca7ac76325740d87bca102818100cc0b1971a1283ba1cee9c5bbaa1cbd2e32218cdd32da27e13a10c80a99611d7f97e6b60055fd86bd4b6ff61b05cfaba19b2eedc05b809456f0dee8a5edecd7a50f276a410a834869e64989fec5a693a89b77ce45f9fca78a6e0e9830349f7293fb4b34dd2e6a9d98f69950fd014005b9b7d613e954925ce9c1c46c5f106ec94502818100c3707f0b1b245bcb7f2b25af2c6baa086b108bcc1c929950ed195fd4f992501443844bf6fb10faed62dc078d454548928428fd3a3d45d6ec21b2f1c8db05a5d9798410c1553930846f1de67d0c0557e2656da52c7fefa45405f3b5f0e1fa01ceb5a23cc653e5d89e1ae4f52e9a5a641a9e3ed48362b646d94b0e73e3f081b969028181009c44a3e7f294ed96aef13b0ecee9d3b95d8b7466b45b7fcf6eb90b7b929c5e0b2a3af788aa52d0ae88e7e6842b9f30c344265ccfc54464577182468b4dfce5eaf4002bd0444ce758b395fea9b09632e01eb152b9250beb0489e58388e7c475ebffb949735be92b69caf300a3c4215441381a2c85b6cfa179257d59c234c3a4410281801b125c26b7adc3a15e8e257c2c55058a80fa1bc33c39fa4ec169006349e6c07cee15474880e28b3f92b013c3b7d2af08cc91b8f1f646d44c85267a636e234c23e1b72f72f85121ad568ef7bc5e8be6ee8f4a2ed5092bb2c188c9876c448112fb0521e1f53eedd9a19cec16224c7334e69300f502f60b75a8e7c2aabeea33adf1028181008ce0d86fae1f92a9fb6d127c31dc0a301fd1c8bd70da9ba84c36f3feb6ac3d9d064ea22a403ddaaf239f64f1ce86ac14daff1408f6edc9c3d1fe105a728bd8d9fa487913ccb5244820f6881552df1eefc343c7228a0fdd3c444c61d8460a633336f7b28f71c241e47761fc58d39072b0c43e5c08298b7744f18e1e37d1fcdb00", 16)
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
	 * Generate key which contains a pair of privae and public key using 2048
	 * bytes
	 * 
	 * @return key pair
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair generateKey() throws NoSuchAlgorithmException {

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		KeyPair key = keyGen.generateKeyPair();
		return key;

	}
}
