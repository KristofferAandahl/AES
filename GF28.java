class GF28 {
    public static byte[][] transpose(byte[][] matrix) {
        byte[][] out = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                out[i][j] = matrix[j][i];
            }
        }
        return out;
    }

    public static byte multiply(byte x, byte y) {
        int p = (256+x) % 256;
        int q = (256+y) % 256;
        int result = 0;
        int mask = 1; // 0000 0001 - mask used to check bites
        for (int i = 0; i < 8; i++) {
            if ((mask & q) != 0) { //if bite checks
                result ^= p;
            }
            //apply xtime again
            p = p << 1;
            if (p > 255) {
                p = (p ^ 0x1B) & 0xFF;
            }
            mask = mask << 1;
        }
        return (byte)result;
    }

    public static byte dot(byte[] xs, byte[] ys) {
        byte out = 0;
        for (int i = 0; i < xs.length; i++) {
            out ^= multiply(xs[i], ys[i]);
        }
        return out;
    }

    public static byte[][] matrixMultiply(byte[][] xss, byte[][] yss) {
        byte[][] out = new byte[4][4];
        byte[][] transposed = transpose(yss);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                out[i][j] = dot(xss[i], transposed[j]);
            }   
        }
        return out;
    }
}