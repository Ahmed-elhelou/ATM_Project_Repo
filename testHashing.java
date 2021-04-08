import java.security.*;
import java.util.Enumeration;

public class testHashing {
    public static void main(String[] args) throws Exception {
        System.out.println(" (" + testHashing.hash("0101") + ") ");
        System.out.println(" (" + testHashing.hash("0202") + ") ");
        System.out.println(" (" + testHashing.hash("0303") + ") ");
        System.out.println(" (" + testHashing.hash("0404") + ") ");
        System.out.println(" (" + testHashing.hash("0505") + ") ");
        System.out.println(" (" + testHashing.hash("0606") + ") ");
        System.out.println(" (" + testHashing.hash("0707") + ") ");
        System.out.println(" (" + testHashing.hash("0808") + ") ");
        System.out.println(" (" + testHashing.hash("0909") + ") ");
        System.out.println(" (" + testHashing.hash("1010") + ") ");
        System.out.println(" (" + testHashing.hash("1111") + ") ");

        // try {
        //     Provider p[] = Security.getProviders();
        //     for (int i = 0; i < p.length; i++) {
        //         System.out.println(p[i]);
        //         for (Enumeration e = p[i].keys(); e.hasMoreElements();)
        //             System.out.println("\t" + e.nextElement());
        //     }
        //   } catch (Exception e) {
        //     System.out.println(e);
        //   }
    }
    private static String hash(String text) throws Exception{
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(text.getBytes());
        String result = String.valueOf(messageDigest.digest());
        messageDigest.reset();
        return result;
    }
    private static String hash2(String text) throws Exception{
        MessageDigest messageDigest2 = MessageDigest.getInstance("SHA3-256");
        messageDigest2.update(text.getBytes());
        String result2 = String.valueOf(messageDigest2.digest());
        messageDigest2.reset();
        return result2;
    }
}
