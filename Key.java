class Key {
    public static byte[] getRcon(int round) {
        int[] rcon = {
            0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80,
            0x1B, 0x36, 0x6C, 0xD8, 0xAB, 0x4D, 0x9A, 0x2F
        };
        return new byte[]{(byte) rcon[round - 1], 0, 0, 0};
    }

    public static byte[] rotate(byte[] word){
        return new byte[]{word[1], word[2], word[3], word[0]};
    }

    public static byte[] substitute(byte[] word) {
        byte[] substituted = new byte[4];
        for (int i = 0; i < 4; i++) {
            substituted[i] = SBox.subByte(word[i]);
        }
        return substituted;
    }

    public static byte[] wordXor(byte[] a, byte[] b) {
        byte[] out = new byte[4];
        for (int i = 0; i < 4; i++) {
            out[i] = (byte) (a[i] ^ b[i]);
        }
        return out;
    }

    public static byte[] nextWord(int i, byte[] previous, byte[] old) {
        byte[] mask = previous;
        if (i % 4 == 0) mask = wordXor(substitute(rotate(previous)), getRcon(i / 4));
        return wordXor(old, mask);
    }

    public static byte[][][] keyExpansion(byte[] key) {
        byte[][] words = new byte[44][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                words[i][j]=  key[i * 4 + j];   
            }
        }
        for (int i = 4; i < 44; i++) {
            words[i] = nextWord(i, words[i - 1], words[i - 4]);
        }
        byte[][][] expandedKeys = new byte[11][4][4];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 4; j++) {
                expandedKeys[i][j] = words[i * 4 + j];
            }
        }
        byte[][][] roundKeys = new byte[11][4][4];
        for (int i = 0; i < 11; i++) {
            roundKeys[i] = GF28.transpose(expandedKeys[i]);
        }
        return roundKeys;
    }
}