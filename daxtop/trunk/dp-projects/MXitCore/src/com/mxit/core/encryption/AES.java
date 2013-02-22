package com.mxit.core.encryption;

import java.io.*;

/**
*
* @author Dax Booysen
* This program generates a AES key, retrieves its raw bytes, and
* then reinstantiates a AES key from the key bytes.
* The reinstantiated key is used to initialize a AES cipher for
* encryption and decryption.
*/
public final class AES
{
    /***************************************************************************
     * Encryption (AES)
     **************************************************************************/

    /**
     * an implementation of the AES (Rijndael), from FIPS-197.
     * <p>
     * For further details see: <a
     * href="http://csrc.nist.gov/encryption/aes/">http://csrc.nist.gov/encryption/aes/</a>.
     * 
     * This implementation is based on optimizations from Dr. Brian Gladman's
     * paper and C code at <a
     * href="http://fp.gladman.plus.com/cryptography_technology/rijndael/">http://fp.gladman.plus.com/cryptography_technology/rijndael/</a>
     * 
     * There are three levels of tradeoff of speed vs memory Because java has no
     * preprocessor, they are written as three separate classes from which to
     * choose
     * 
     * The fastest uses 8Kbytes of static tables to precompute round
     * calculations, 4 256 word tables for encryption and 4 for decryption.
     * 
     * The middle performance version uses only one 256 word table for each, for
     * a total of 2Kbytes, adding 12 rotate operations per round to compute the
     * values contained in the other tables from the contents of the first
     * 
     * The slowest version uses no static tables at all and computes the values
     * in each round.
     * <p>
     * This file contains the slowest performance version with no static tables
     * for round precomputation, but it has the smallest foot print.
     * 
     */

    // this header is added to the cryptotext to serve as a hint that the
    // rest of the message is encrypted. It is intended for clients that
    // do not support encryption
    public static final String XML_CLOSE = "/>";
    public static final String CRYPT_PLAIN_HEADER = "<mxitencrypted ver=\"5.2\"" + XML_CLOSE;
    public static final String CRYPT_ENCRYPTED_HEADER = "<mxit/>";

    // The S box
    private static byte[] aes_S;

    // The inverse S-box
    private static byte[] aes_Si;

    // vector used in calculating key schedule (powers of x in GF(256))
    private static final int[] aes_rcon = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8,
                                           0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4,
                                           0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91 };

    private static int AES_ROUNDS;
    private static int[][] aes_workingKey;
    private static int aes_C0, aes_C1, aes_C2, aes_C3;
    private static boolean aes_forEncryption;

    public static final int AES_BLOCK_SIZE = 16;
    // use 128 bit encryption by default
    public static final int AES_DEFAULT_KEY_SIZE = 16;
    // fill key with this value at start
    private static final long AES_INITIALISATION_KEY = 6170383452343567L;

    private static int aes_shift(int r, int shift) {
        return (r >>> shift) | (r << -shift);
    }

    /* multiply four bytes in GF(2^8) by 'x' {02} in parallel */

    private static final int aes_m1 = 0x80808080;
    private static final int aes_m2 = 0x7f7f7f7f;
    private static final int aes_m3 = 0x0000001b;

    private static int aes_FFmulX(int x) {
        return (((x & aes_m2) << 1) ^ (((x & aes_m1) >>> 7) * aes_m3));
    }

    /*
     * The following defines provide alternative definitions of FFmulX that
     * might give improved performance if a fast 32-bit multiply is not
     * available.
     * 
     * private int FFmulX(int x) { int u = x & m1; u |= (u >> 1); return ((x &
     * m2) << 1) ^ ((u >>> 3) | (u >>> 6)); } private static final int m4 =
     * 0x1b1b1b1b; private int FFmulX(int x) { int u = x & m1; return ((x & m2) <<
     * 1) ^ ((u - (u >>> 7)) & m4); }
     * 
     */

    private static int aes_mcol(int x) {
        int f2 = aes_FFmulX(x);
        return f2 ^ aes_shift(x ^ f2, 8) ^ aes_shift(x, 16) ^ aes_shift(x, 24);
    }

    private static int aes_inv_mcol(int x) {
        int f2 = aes_FFmulX(x);
        int f4 = aes_FFmulX(f2);
        int f8 = aes_FFmulX(f4);
        int f9 = x ^ f8;

        return f2 ^ f4 ^ f8 ^ aes_shift(f2 ^ f9, 8) ^ aes_shift(f4 ^ f9, 16) ^ aes_shift(f9, 24);
    }

    private static int aes_subWord(int x) {
        return (aes_S[x & 255] & 255 | ((aes_S[(x >> 8) & 255] & 255) << 8) | ((aes_S[(x >> 16) & 255] & 255) << 16) | aes_S[(x >> 24) & 255] << 24);
    }

    /**
     * Calculate the necessary round keys The number of calculations depends on
     * key size and block size AES specified a fixed block size of 128 bits and
     * key sizes 128/192/256 bits This code is written assuming those are the
     * only possible values
     */
    private static int[][] aes_generateWorkingKey(byte[] key, boolean forEncryption) {
        int KC = key.length / 4; // key length in words
        int t;

        //if (MXit.DEBUG_CHECK_ARGUMENTS && ((KC != 4) && (KC != 6) && (KC != 8)) || ((KC * 4) != key.length)) {
        //    throw new IllegalArgumentException("Key length not 128/192/256 bits.");
        //}

        AES_ROUNDS = KC + 6; // This is not always true for the generalized
        // Rijndael that allows larger block sizes
        int[][] W = new int[AES_ROUNDS + 1][4]; // 4 words in a block

        //
        // copy the key into the round key array
        //

        t = 0;
        int i = 0;
        while (i < key.length) {
            W[t >> 2][t & 3] = (key[i] & 0xff) | ((key[i + 1] & 0xff) << 8) | ((key[i + 2] & 0xff) << 16)
                | (key[i + 3] << 24);
            i += 4;
            t++;
        }

        //
        // while not enough round key material calculated
        // calculate new values
        //
        int k = (AES_ROUNDS + 1) << 2;
        for (i = KC; (i < k); i++) {
            int temp = W[(i - 1) >> 2][(i - 1) & 3];
            if ((i % KC) == 0) {
                temp = aes_subWord(aes_shift(temp, 8)) ^ aes_rcon[(i / KC) - 1];
            } else if ((KC > 6) && ((i % KC) == 4)) {
                temp = aes_subWord(temp);
            }

            W[i >> 2][i & 3] = W[(i - KC) >> 2][(i - KC) & 3] ^ temp;
        }

        if (!forEncryption) {
            for (int j = 1; j < AES_ROUNDS; j++) {
                for (i = 0; i < 4; i++) {
                    W[j][i] = aes_inv_mcol(W[j][i]);
                }
            }
        }

        return W;
    }

    /**
     * initialise an AES cipher.
     * 
     * @param forEncryption
     *            whether or not we are for encryption.
     * @param params
     *            the parameters required to set up the cipher.
     * @exception IllegalArgumentException
     *                if the params argument is inappropriate.
     */
    public static void aes_init(boolean forEncryption, byte[] key) throws Exception {
        if (aes_S == null) {           
                // Alternative mechanism for assigning the vector data,
                aes_S = base64_decode("Y3x3e/Jrb8UwAWcr/terdsqCyX36WUfwrdSir5ykcsC3/ZMmNj/3zDSl5fFx2DEVBMcjwxiWBZoHEoDi6yeydQmDLBobblqgUjvWsynjL4RT0QDtIPyxW2rLvjlKTFjP0O+q+0NNM4VF+QJ/UDyfqFGjQI+SnTj1vLbaIRD/89LNDBPsX5dEF8Snfj1kXRlzYIFP3CIqkIhG7rgU3l4L2+AyOgpJBiRcwtOsYpGV5HnnyDdtjdVOqWxW9Opleq4IunglLhymtMbo3XQfS72LinA+tWZIA/YOYTVXuYbBHZ7h+JgRadmOlJseh+nOVSjfjKGJDb/mQmhBmS0PsFS7Fg==");
                aes_Si = base64_decode("Uglq1TA2pTi/QKOegfPX+3zjOYKbL/+HNI5DRMTe6ctUe5QypsIjPe5MlQtC+sNOCC6hZijZJLJ2W6JJbYvRJXL49mSGaJgW1KRczF1ltpJscEhQ/e252l4VRlenjZ2EkNirAIy80wr35FgFuLNFBtAsHo/KPw8Cwa+9AwETims6kRFBT2fc6pfyz87wtOZzlqx0IuetNYXi+TfoHHXfbkfxGnEdKcWJb7diDqoYvhv8Vj5LxtJ5IJrbwP54zVr0H92oM4gHxzGxEhBZJ4DsX2BRf6kZtUoNLeV6n5PJnO+g4DtNrir1sMjruzyDU5lhFysEfrp31ibhaRRjVSEMfQ==");           
        }

        aes_workingKey = aes_generateWorkingKey(key, forEncryption);
        aes_forEncryption = forEncryption;
    }

    public static int aes_processBlock(byte[] in, int inOff, byte[] out, int outOff) throws Exception {
        //if (MXit.DEBUG_CHECK_ARGUMENTS) {
            if (aes_workingKey == null) {                
                throw new IllegalStateException("AES engine not initialised");
            }

            if ((inOff + (32 / 2)) > in.length) {
                throw new Exception("input buffer too short");
            }

            if ((outOff + (32 / 2)) > out.length) {
                throw new Exception("output buffer too short");
            }
        //}

        if (aes_forEncryption) {
            aes_unpackBlock(in, inOff);
            aes_encryptBlock(aes_workingKey);
            aes_packBlock(out, outOff);
        } else {
            aes_unpackBlock(in, inOff);
            aes_decryptBlock(aes_workingKey);
            aes_packBlock(out, outOff);
        }

        return AES_BLOCK_SIZE;
    }

    private static final void aes_unpackBlock(byte[] bytes, int off) throws Exception {
        aes_C0 = (bytes[off++] & 0xff) | (bytes[off++] & 0xff) << 8 | (bytes[off++] & 0xff) << 16 | bytes[off++] << 24;
        aes_C1 = (bytes[off++] & 0xff) | (bytes[off++] & 0xff) << 8 | (bytes[off++] & 0xff) << 16 | bytes[off++] << 24;
        aes_C2 = (bytes[off++] & 0xff) | (bytes[off++] & 0xff) << 8 | (bytes[off++] & 0xff) << 16 | bytes[off++] << 24;
        aes_C3 = (bytes[off++] & 0xff) | (bytes[off++] & 0xff) << 8 | (bytes[off++] & 0xff) << 16 | bytes[off++] << 24;
    }

    private static final void aes_packBlock(byte[] bytes, int off) {
        int index = off;

        bytes[index++] = (byte) aes_C0;
        bytes[index++] = (byte) (aes_C0 >> 8);
        bytes[index++] = (byte) (aes_C0 >> 16);
        bytes[index++] = (byte) (aes_C0 >> 24);

        bytes[index++] = (byte) aes_C1;
        bytes[index++] = (byte) (aes_C1 >> 8);
        bytes[index++] = (byte) (aes_C1 >> 16);
        bytes[index++] = (byte) (aes_C1 >> 24);

        bytes[index++] = (byte) aes_C2;
        bytes[index++] = (byte) (aes_C2 >> 8);
        bytes[index++] = (byte) (aes_C2 >> 16);
        bytes[index++] = (byte) (aes_C2 >> 24);

        bytes[index++] = (byte) aes_C3;
        bytes[index++] = (byte) (aes_C3 >> 8);
        bytes[index++] = (byte) (aes_C3 >> 16);
        bytes[index++] = (byte) (aes_C3 >> 24);
    }

    private static void aes_encryptBlock(int[][] KW) {
        int r, r0, r1, r2, r3;

        aes_C0 ^= KW[0][0];
        aes_C1 ^= KW[0][1];
        aes_C2 ^= KW[0][2];
        aes_C3 ^= KW[0][3];

        for (r = 1; r < AES_ROUNDS - 1;) {
            r0 = aes_mcol((aes_S[aes_C0 & 255] & 255) ^ ((aes_S[(aes_C1 >> 8) & 255] & 255) << 8)
                ^ ((aes_S[(aes_C2 >> 16) & 255] & 255) << 16) ^ (aes_S[(aes_C3 >> 24) & 255] << 24))
                ^ KW[r][0];
            r1 = aes_mcol((aes_S[aes_C1 & 255] & 255) ^ ((aes_S[(aes_C2 >> 8) & 255] & 255) << 8)
                ^ ((aes_S[(aes_C3 >> 16) & 255] & 255) << 16) ^ (aes_S[(aes_C0 >> 24) & 255] << 24))
                ^ KW[r][1];
            r2 = aes_mcol((aes_S[aes_C2 & 255] & 255) ^ ((aes_S[(aes_C3 >> 8) & 255] & 255) << 8)
                ^ ((aes_S[(aes_C0 >> 16) & 255] & 255) << 16) ^ (aes_S[(aes_C1 >> 24) & 255] << 24))
                ^ KW[r][2];
            r3 = aes_mcol((aes_S[aes_C3 & 255] & 255) ^ ((aes_S[(aes_C0 >> 8) & 255] & 255) << 8)
                ^ ((aes_S[(aes_C1 >> 16) & 255] & 255) << 16) ^ (aes_S[(aes_C2 >> 24) & 255] << 24))
                ^ KW[r++][3];
            aes_C0 = aes_mcol((aes_S[r0 & 255] & 255) ^ ((aes_S[(r1 >> 8) & 255] & 255) << 8)
                ^ ((aes_S[(r2 >> 16) & 255] & 255) << 16) ^ (aes_S[(r3 >> 24) & 255] << 24))
                ^ KW[r][0];
            aes_C1 = aes_mcol((aes_S[r1 & 255] & 255) ^ ((aes_S[(r2 >> 8) & 255] & 255) << 8)
                ^ ((aes_S[(r3 >> 16) & 255] & 255) << 16) ^ (aes_S[(r0 >> 24) & 255] << 24))
                ^ KW[r][1];
            aes_C2 = aes_mcol((aes_S[r2 & 255] & 255) ^ ((aes_S[(r3 >> 8) & 255] & 255) << 8)
                ^ ((aes_S[(r0 >> 16) & 255] & 255) << 16) ^ (aes_S[(r1 >> 24) & 255] << 24))
                ^ KW[r][2];
            aes_C3 = aes_mcol((aes_S[r3 & 255] & 255) ^ ((aes_S[(r0 >> 8) & 255] & 255) << 8)
                ^ ((aes_S[(r1 >> 16) & 255] & 255) << 16) ^ (aes_S[(r2 >> 24) & 255] << 24))
                ^ KW[r++][3];
        }

        r0 = aes_mcol((aes_S[aes_C0 & 255] & 255) ^ ((aes_S[(aes_C1 >> 8) & 255] & 255) << 8)
            ^ ((aes_S[(aes_C2 >> 16) & 255] & 255) << 16) ^ (aes_S[(aes_C3 >> 24) & 255] << 24))
            ^ KW[r][0];
        r1 = aes_mcol((aes_S[aes_C1 & 255] & 255) ^ ((aes_S[(aes_C2 >> 8) & 255] & 255) << 8)
            ^ ((aes_S[(aes_C3 >> 16) & 255] & 255) << 16) ^ (aes_S[(aes_C0 >> 24) & 255] << 24))
            ^ KW[r][1];
        r2 = aes_mcol((aes_S[aes_C2 & 255] & 255) ^ ((aes_S[(aes_C3 >> 8) & 255] & 255) << 8)
            ^ ((aes_S[(aes_C0 >> 16) & 255] & 255) << 16) ^ (aes_S[(aes_C1 >> 24) & 255] << 24))
            ^ KW[r][2];
        r3 = aes_mcol((aes_S[aes_C3 & 255] & 255) ^ ((aes_S[(aes_C0 >> 8) & 255] & 255) << 8)
            ^ ((aes_S[(aes_C1 >> 16) & 255] & 255) << 16) ^ (aes_S[(aes_C2 >> 24) & 255] << 24))
            ^ KW[r++][3];

        // the final round is a simple function of S

        aes_C0 = (aes_S[r0 & 255] & 255) ^ ((aes_S[(r1 >> 8) & 255] & 255) << 8)
            ^ ((aes_S[(r2 >> 16) & 255] & 255) << 16) ^ (aes_S[(r3 >> 24) & 255] << 24) ^ KW[r][0];
        aes_C1 = (aes_S[r1 & 255] & 255) ^ ((aes_S[(r2 >> 8) & 255] & 255) << 8)
            ^ ((aes_S[(r3 >> 16) & 255] & 255) << 16) ^ (aes_S[(r0 >> 24) & 255] << 24) ^ KW[r][1];
        aes_C2 = (aes_S[r2 & 255] & 255) ^ ((aes_S[(r3 >> 8) & 255] & 255) << 8)
            ^ ((aes_S[(r0 >> 16) & 255] & 255) << 16) ^ (aes_S[(r1 >> 24) & 255] << 24) ^ KW[r][2];
        aes_C3 = (aes_S[r3 & 255] & 255) ^ ((aes_S[(r0 >> 8) & 255] & 255) << 8)
            ^ ((aes_S[(r1 >> 16) & 255] & 255) << 16) ^ (aes_S[(r2 >> 24) & 255] << 24) ^ KW[r][3];

    }

    private static final void aes_decryptBlock(int[][] KW) {
        int r, r0, r1, r2, r3;

        aes_C0 ^= KW[AES_ROUNDS][0];
        aes_C1 ^= KW[AES_ROUNDS][1];
        aes_C2 ^= KW[AES_ROUNDS][2];
        aes_C3 ^= KW[AES_ROUNDS][3];

        for (r = AES_ROUNDS - 1; r > 1;) {
            r0 = aes_inv_mcol((aes_Si[aes_C0 & 255] & 255) ^ ((aes_Si[(aes_C3 >> 8) & 255] & 255) << 8)
                ^ ((aes_Si[(aes_C2 >> 16) & 255] & 255) << 16) ^ (aes_Si[(aes_C1 >> 24) & 255] << 24))
                ^ KW[r][0];
            r1 = aes_inv_mcol((aes_Si[aes_C1 & 255] & 255) ^ ((aes_Si[(aes_C0 >> 8) & 255] & 255) << 8)
                ^ ((aes_Si[(aes_C3 >> 16) & 255] & 255) << 16) ^ (aes_Si[(aes_C2 >> 24) & 255] << 24))
                ^ KW[r][1];
            r2 = aes_inv_mcol((aes_Si[aes_C2 & 255] & 255) ^ ((aes_Si[(aes_C1 >> 8) & 255] & 255) << 8)
                ^ ((aes_Si[(aes_C0 >> 16) & 255] & 255) << 16) ^ (aes_Si[(aes_C3 >> 24) & 255] << 24))
                ^ KW[r][2];
            r3 = aes_inv_mcol((aes_Si[aes_C3 & 255] & 255) ^ ((aes_Si[(aes_C2 >> 8) & 255] & 255) << 8)
                ^ ((aes_Si[(aes_C1 >> 16) & 255] & 255) << 16) ^ (aes_Si[(aes_C0 >> 24) & 255] << 24))
                ^ KW[r--][3];
            aes_C0 = aes_inv_mcol((aes_Si[r0 & 255] & 255) ^ ((aes_Si[(r3 >> 8) & 255] & 255) << 8)
                ^ ((aes_Si[(r2 >> 16) & 255] & 255) << 16) ^ (aes_Si[(r1 >> 24) & 255] << 24))
                ^ KW[r][0];
            aes_C1 = aes_inv_mcol((aes_Si[r1 & 255] & 255) ^ ((aes_Si[(r0 >> 8) & 255] & 255) << 8)
                ^ ((aes_Si[(r3 >> 16) & 255] & 255) << 16) ^ (aes_Si[(r2 >> 24) & 255] << 24))
                ^ KW[r][1];
            aes_C2 = aes_inv_mcol((aes_Si[r2 & 255] & 255) ^ ((aes_Si[(r1 >> 8) & 255] & 255) << 8)
                ^ ((aes_Si[(r0 >> 16) & 255] & 255) << 16) ^ (aes_Si[(r3 >> 24) & 255] << 24))
                ^ KW[r][2];
            aes_C3 = aes_inv_mcol((aes_Si[r3 & 255] & 255) ^ ((aes_Si[(r2 >> 8) & 255] & 255) << 8)
                ^ ((aes_Si[(r1 >> 16) & 255] & 255) << 16) ^ (aes_Si[(r0 >> 24) & 255] << 24))
                ^ KW[r--][3];
        }

        r0 = aes_inv_mcol((aes_Si[aes_C0 & 255] & 255) ^ ((aes_Si[(aes_C3 >> 8) & 255] & 255) << 8)
            ^ ((aes_Si[(aes_C2 >> 16) & 255] & 255) << 16) ^ (aes_Si[(aes_C1 >> 24) & 255] << 24))
            ^ KW[r][0];
        r1 = aes_inv_mcol((aes_Si[aes_C1 & 255] & 255) ^ ((aes_Si[(aes_C0 >> 8) & 255] & 255) << 8)
            ^ ((aes_Si[(aes_C3 >> 16) & 255] & 255) << 16) ^ (aes_Si[(aes_C2 >> 24) & 255] << 24))
            ^ KW[r][1];
        r2 = aes_inv_mcol((aes_Si[aes_C2 & 255] & 255) ^ ((aes_Si[(aes_C1 >> 8) & 255] & 255) << 8)
            ^ ((aes_Si[(aes_C0 >> 16) & 255] & 255) << 16) ^ (aes_Si[(aes_C3 >> 24) & 255] << 24))
            ^ KW[r][2];
        r3 = aes_inv_mcol((aes_Si[aes_C3 & 255] & 255) ^ ((aes_Si[(aes_C2 >> 8) & 255] & 255) << 8)
            ^ ((aes_Si[(aes_C1 >> 16) & 255] & 255) << 16) ^ (aes_Si[(aes_C0 >> 24) & 255] << 24))
            ^ KW[r--][3];

        // the final round's table is a simple function of Si

        aes_C0 = (aes_Si[r0 & 255] & 255) ^ ((aes_Si[(r3 >> 8) & 255] & 255) << 8)
            ^ ((aes_Si[(r2 >> 16) & 255] & 255) << 16) ^ (aes_Si[(r1 >> 24) & 255] << 24) ^ KW[0][0];
        aes_C1 = (aes_Si[r1 & 255] & 255) ^ ((aes_Si[(r0 >> 8) & 255] & 255) << 8)
            ^ ((aes_Si[(r3 >> 16) & 255] & 255) << 16) ^ (aes_Si[(r2 >> 24) & 255] << 24) ^ KW[0][1];
        aes_C2 = (aes_Si[r2 & 255] & 255) ^ ((aes_Si[(r1 >> 8) & 255] & 255) << 8)
            ^ ((aes_Si[(r0 >> 16) & 255] & 255) << 16) ^ (aes_Si[(r3 >> 24) & 255] << 24) ^ KW[0][2];
        aes_C3 = (aes_Si[r3 & 255] & 255) ^ ((aes_Si[(r2 >> 8) & 255] & 255) << 8)
            ^ ((aes_Si[(r1 >> 16) & 255] & 255) << 16) ^ (aes_Si[(r0 >> 24) & 255] << 24) ^ KW[0][3];
    }

    /*
     * This is the main function that is used to encrypt / decrypt message data
     * @param forEncryption true if data should be encrypted, false if data
     * should be decrypted @param key the key that is to be used for encryption,
     * has to be 128/192/256 bits @param input the input data for encryption (in
     * binary). If the data is not on a 128 bit boundary it is padded with
     * spaces (ascii 32) @param return the data is returned as either a base64
     * encoded string if encryption was requested, or the plaintext string if
     * decryption was requested.
     */
    synchronized public static byte[] aes_process(boolean forEncryption, String password, byte[] data, int ofs, int len)
        throws Exception {
        // convert key string to byte vector (and pad if necessary)
        byte[] passwordBytes = password.getBytes("UTF-8");
        byte[] key = Long.toString(AES_INITIALISATION_KEY).getBytes();
        System.arraycopy(passwordBytes, 0, key, 0, Math.min(AES_DEFAULT_KEY_SIZE, passwordBytes.length));

        aes_init(forEncryption, key);

        // header that is added to encrypted text so that when it is decrypted I can
        // determine whether the password was correct.
        byte[] headerBytes = CRYPT_ENCRYPTED_HEADER.getBytes();

        if (forEncryption) {
            // the +1 is to make provision for the ISO10126Padding byte
            // which contains the number of padding characters that must be
            // stripped
            // away when the message has been received.
            int dataSize = len + headerBytes.length + 1;
            // round up to multiple of block size
            int outSize = (dataSize % AES_BLOCK_SIZE) == 0 ? dataSize : dataSize
                + (AES_BLOCK_SIZE - (dataSize % AES_BLOCK_SIZE));
            byte[] out = new byte[outSize];
            // copy encrypted header into buffer
            System.arraycopy(headerBytes, 0, out, 0, headerBytes.length);
            System.arraycopy(data, ofs, out, headerBytes.length, len);
            out[out.length - 1] = (byte) (out.length - dataSize + 1);
            data = null;

            // encrypt the message block by block
            for (int j = 0; j < outSize; j += AES_BLOCK_SIZE) {
                aes_processBlock(out, j, out, j);
            }

            return out;
        } else {
            for (int j = ofs; j < ofs + len; j += AES_BLOCK_SIZE) {
                aes_processBlock(data, j, data, j);
            }
            // check whether the header was correct i.e. it starts with
            // CRYPT_ENCRYPTED_HEADER
            int j = 0;
            while (j < headerBytes.length && j < data.length && data[j + ofs] == headerBytes[j]) {
                j++;
            }
            // if the correct header is present, strip header and padding
            if (j == headerBytes.length) {
                // compute length of decrypted data if header and padding is
                // stripped off
                int decLen = data.length - CRYPT_ENCRYPTED_HEADER.length() - data[data.length - 1];
                byte[] dec = new byte[decLen];
                // copy decrypted data
                System.arraycopy(data, ofs + CRYPT_ENCRYPTED_HEADER.length(), dec, 0, decLen - ofs);
                return dec;
            } else {
                // could not find header - assume that the password was not
                // correct, so throw
                // exception
                throw new RuntimeException();
            }
        }
    }

    /*
     * This is the main function that is used to encrypt / decrypt message data
     * @param forEncryption true if data should be encrypted, false if data
     * should be decrypted @param key the key that is to be used for encryption,
     * has to be 128/192/256 bits @param input the input data for encryption. If
     * the data is not on a 128 bit boundary it is padded with spaces (ascii 32)
     * @param return the data is returned as either a base64 encoded string if
     * encryption was requested, or the plaintext string if decryption was
     * requested.
     */
    synchronized public static String aes_process(boolean forEncryption, String password, String input) throws Exception {
        
        base64_initialise();
        
        if (forEncryption) {
            byte[] data = input.getBytes("UTF-8");
            // convert to printable text using base64
            return base64_encode(aes_process(true, password, data, 0, data.length));
        } else {
            byte[] data = base64_decode(input);
            return new String(aes_process(false, password, data, 0, data.length));
        }
    }

  /***************************************************************************
     * Base64 Encoding / Decoding
     **************************************************************************/

    private static final int BASE64_PADDING = (byte) '=';

    // encoding and decoding tables
    private static byte[] base64_encodingTable;
    private static byte[] base64_decodingTable;

    /*
     * Initialise encoding and decoding tables that are used for base64 encoding /
     * decoding
     */
    public static final void base64_initialise() {
        base64_encodingTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();
        base64_decodingTable = new byte[128];
        for (int i = 0; i < base64_encodingTable.length; i++) {
            base64_decodingTable[base64_encodingTable[i]] = (byte) i;
        }
    }

    /**
     * encode the input data producing a base 64 output stream.
     * 
     * @return the number of bytes produced.
     */
    static public String base64_encode(byte[] data) throws IOException {
        if (data == null) {
            return "";
        }

        if(base64_decodingTable == null)
            base64_initialise();

        int length = data.length;
        int modulus = length % 3;
        int dataLength = (length - modulus);
        int a1, a2, a3;
        int idx = 0;

        // compute the amount of data to encode
        int outLen = (dataLength / 3) * 4 + ((modulus == 0) ? 0 : 4);
        // create a buffer
        byte[] out = new byte[outLen];

        // encode data
        for (int i = 0; i < dataLength; i += 3) {
            a1 = data[i] & 0xff;
            a2 = data[i + 1] & 0xff;
            a3 = data[i + 2] & 0xff;

            out[idx++] = base64_encodingTable[(a1 >>> 2) & 0x3f];
            out[idx++] = base64_encodingTable[((a1 << 4) | (a2 >>> 4)) & 0x3f];
            out[idx++] = base64_encodingTable[((a2 << 2) | (a3 >>> 6)) & 0x3f];
            out[idx++] = base64_encodingTable[a3 & 0x3f];
        }

        // process the tail end.
        int b1, b2, b3;
        int d1, d2;

        if (modulus != 0) {
            d1 = data[dataLength] & 0xff;
            b1 = (d1 >>> 2) & 0x3f;

            out[idx++] = base64_encodingTable[b1];

            if (modulus == 1) {
                b2 = (d1 << 4) & 0x3f;
                out[idx++] = base64_encodingTable[b2];
                out[idx++] = BASE64_PADDING;
            } else {
                d2 = data[dataLength + 1] & 0xff;
                b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
                b3 = (d2 << 2) & 0x3f;

                out[idx++] = base64_encodingTable[b2];
                out[idx++] = base64_encodingTable[b3];
            }
            out[idx++] = BASE64_PADDING;
        }

        return new String(out);
    }

    /**
     * decode the base 64 encoded String data writing it to the given output
     * stream, whitespace characters will be ignored.
     * 
     * @return the number of bytes produced.
     * @throws IndexOutOfBoundsException
     *             of data contains characters (like newlines) are not part of
     *             the base64 character set
     */
    static public byte[] base64_decode(String data) throws IOException {
        int b1, b2, b3, b4;

        if(base64_decodingTable == null)
            base64_initialise();
        
        //if (DEBUG_CHECK_ARGUMENTS) {
            if ((data.length() % 4 != 0)) {
            //    throw new IllegalArgumentException("data length is " + data.length() + " which is not a multiple of 4");
            }
        //}
        int dataLength = data.length();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // decode all but the last 4 bytes
        for (int i = 0; i < dataLength - 4; i += 4) {
            b1 = base64_decodingTable[data.charAt(i)];
            b2 = base64_decodingTable[data.charAt(i + 1)];
            b3 = base64_decodingTable[data.charAt(i + 2)];
            b4 = base64_decodingTable[data.charAt(i + 3)];

            bos.write((b1 << 2) | (b2 >> 4));
            bos.write((b2 << 4) | (b3 >> 2));
            bos.write((b3 << 6) | b4);
        }

        // decode the trailing 4 bytes, taking into account the
        // padding characters
        if (dataLength >= 4) {
            char c1 = data.charAt(dataLength - 4);
            char c2 = data.charAt(dataLength - 3);
            char c3 = data.charAt(dataLength - 2);
            char c4 = data.charAt(dataLength - 1);

            b1 = base64_decodingTable[c1];
            b2 = base64_decodingTable[c2];

            bos.write((b1 << 2) | (b2 >> 4));
            if (c3 == BASE64_PADDING) {
                // skip
            } else if (c4 == BASE64_PADDING) {
                b3 = base64_decodingTable[c3];
                bos.write((b2 << 4) | (b3 >> 2));
            } else {
                b3 = base64_decodingTable[c3];
                b4 = base64_decodingTable[c4];
                bos.write((b2 << 4) | (b3 >> 2));
                bos.write((b3 << 6) | b4);
            }
        }

        return bos.toByteArray();
    }
    
    /*
    
    // Members
     private static final int       AES_DEFAULT_KEY_SIZE = 16;
     private static final String    AES_ENCRYPTION_KEY = "6170383452343567";
     private static final String    ENCRYPTED_HEADER = "<mxit/>";
     
     /** Return encrypted password for MXit usage *//*
     public static String GetPassword(String password, String key)
     {
         return asHex(EncryptedToByteArray(password, key));
     }
     
     /**
     * Turns array of bytes into string
     *
     * @param buf   Array of bytes to convert to hex string
     * @return	Generated hex string
     *//*
     private static String asHex (byte buf[]) 
     {
          StringBuffer strbuf = new StringBuffer(buf.length * 2);
          int i;

          for (i = 0; i < buf.length; i++)
          {
              if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");

               strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
          }

          return strbuf.toString();
     }

     /** AES Encryption for MXit 128 bit encryption *//*
    private static byte[] EncryptedToByteArray(String text, String password)
    {
        byte[] encrypted = null;
        
        try
        {
            // Get the KeyGenerator
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128); // 192 and 256 bits may not be available

            // init key to encrypt with
            byte[] enc_key = AES_ENCRYPTION_KEY.getBytes();
            // password to encrypt with
            byte[] passwordBytes = password.getBytes();
            
            // substitute the first {N} bytes of init key with the password bytes 
            System.arraycopy(passwordBytes, 0, enc_key, 0, Math.min(AES_DEFAULT_KEY_SIZE, passwordBytes.length));
            
            // use the predefined key spec
            SecretKeySpec skeySpec = new SecretKeySpec(enc_key, "AES");

            // text -> bytes to encrypt
            byte[] textBytes = ENCRYPTED_HEADER.concat(text).getBytes();
            

            // Instantiate the cipher
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            encrypted = cipher.doFinal(textBytes);
            System.out.println("encrypted string: " + asHex(encrypted));

            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] original = cipher.doFinal(encrypted);
            String originalString = new String(original);
            System.out.println("Original string: " + originalString + " " + asHex(original));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return encrypted;
    }
    
    */
}
