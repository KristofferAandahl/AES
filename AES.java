



public class AES {
    public static byte[][] shiftRows(byte[][] state) {
        byte[][] out = new byte[4][4];
        for (byte i = 0; i < 4; i++) {
            for (byte j = 0; j < 4; j ++) {
                out[i][j] = state[i][(i+j) % 4];
            }
        }
        return out;
    }

    public static byte[][] invShiftRows(byte[][] state) {
        byte[][] out = new byte[4][4];
        for (byte i = 0; i < 4; i++) {
            for (byte j = 0; j < 4; j ++) {
                out[i][j] = state[i][(j-i+4) % 4];
            }
        }
        return out;
    }

    public static byte[][] mixColumns(byte[][] state) {
        byte[][] matrix = new byte[][]{
            {2, 3, 1, 1},
            {1, 2, 3, 1},
            {1, 1, 2, 3},
            {3, 1, 1, 2}
        };
        return GF28.matrixMultiply(matrix, state);
    }

    public static byte[][] invMixColumns(byte[][] state) {
        byte[][] matrix = new byte[][]{
            {0x0e, 0x0b, 0x0d, 0x09},
            {0x09, 0x0e, 0x0b, 0x0d},
            {0x0d, 0x09, 0x0e, 0x0b},
            {0x0b, 0x0d, 0x09, 0x0e}
        };
        return GF28.matrixMultiply(matrix, state);
    }

    public static byte[][] subBytes(byte[][] state) {
        byte[][] out = new byte[4][4];
        for (byte i = 0; i < 4; i++) {
            for (byte j = 0; j < 4; j++) {
                out[i][j] = SBox.subByte(state[i][j]);
            }
        }
        return out;
    }

    public static byte[][] invSubBytes(byte[][] state) {
        byte[][] out = new byte[4][4];
        for (byte i = 0; i < 4; i++) {
            for (byte j = 0; j < 4; j++) {
                out[i][j] = SBox.invSubByte(state[i][j]);
            }
        }
        return out;
    }

    public static byte[][] addRoundKey(byte[][] state, byte[][] key) {
        byte[][] out = new byte[4][4];
        for (byte i = 0; i < 4; i++) {
            for (byte j = 0; j < 4; j++) {
                out[i][j] = (byte)(state[i][j] ^ key[i][j]);
            }
        }
        return out;
    }

    public static byte[][] aesRound(byte[][] state, byte[][] key) {
        return addRoundKey(mixColumns(shiftRows(subBytes(state))), key);
    }

    public static byte[][] invRound(byte[][] state, byte[][] key) {
        return invMixColumns(addRoundKey(invSubBytes(invShiftRows(state)), key));
    }

    public static byte[][] aesRounds(byte[][] state, byte[][][] keys) {
        for (int i = 0; i < keys.length; i++) {
            state = aesRound(state, keys[i]);
        }
        return state;
    }

    public static byte[][] aesInvRounds(byte[][] state, byte[][][] keys) {
        for (int i = keys.length - 1; i >= 0; i--) {
            state = invRound(state, keys[i]);
        }
        return state;
    }

    public static byte[][] aesFinalRound(byte[][] state, byte[][] key) {
        return addRoundKey(shiftRows(subBytes(state)), key);
    }

    public static byte[][] invFinalRound(byte[][] state, byte[][] key) {
        return addRoundKey(invSubBytes(invShiftRows(state)), key);
    }

    public static byte[][] msgToState(byte[] msg) {
        byte[][] state = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] = msg[i * 4 + j];
            }
        }
        return state;
    }

    public static byte[] stateToMsg(byte[][] state) {
        state = GF28.transpose(state);
        byte[] msg = new byte[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                msg[i * 4 + j] = state[i][j];
            }
        }
        return msg;
    }

    public static byte[] encrypt(byte[] msg, byte[] keys) {
        byte[][] state = msgToState(msg);
        byte[][][] roundKeys = Key.keyExpansion(keys);

        state = addRoundKey(state, roundKeys[0]);

        byte[][][] middleKeys = new byte[9][4][4];
        for (int i = 1; i < 10; i++) {
            middleKeys[i - 1] = roundKeys[i];
        }
        state = aesRounds(state, middleKeys);

        state = aesFinalRound(state, roundKeys[roundKeys.length - 1]);
        return stateToMsg(state);
    }

    public static byte[] decrypt(byte[] msg, byte[] keys) {
        byte[][] state = msgToState(msg);
        byte[][][] roundKeys = Key.keyExpansion(keys);

        state = addRoundKey(state, roundKeys[roundKeys.length - 1]);

        byte[][][] middleKeys = new byte[9][4][4];
        for (int i = 1; i < 10; i++) {
            middleKeys[i - 1] = roundKeys[i];
        }
        state = aesInvRounds(state, middleKeys);

        state = invFinalRound(state, roundKeys[0]);

        return stateToMsg(state);
    }


    public static void main(String[] args) {
        byte[] msg = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        byte[] key = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        byte[] encrypted = encrypt(msg, key);
        System.out.println("Encrypted message: ");
        for (byte b : encrypted) {
            System.out.printf("%02x ", b);
        }
        System.out.println();

        byte[] decrypted = decrypt(encrypted, key);
        System.out.println("Decrypted message: ");
        for (byte b : decrypted) {
            System.out.printf("%02x ", b);
        }
        System.out.println();
    }
}