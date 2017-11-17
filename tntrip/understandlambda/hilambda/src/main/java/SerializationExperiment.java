import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by nuc on 2015/11/12.
 */
public class SerializationExperiment {
    interface Foo extends Serializable {
        int m();
    }

    public static void main(String[] args) {
        Foo f1 = null, f2 = null;
        if (args[0].equals("r")) {
            f1 = readFromDisk();
        }

        f2 = () -> 3;

        if (args[0].equals("w")) {
            writeToDisk(f2);
            f1 = readFromDisk();
        }
        System.out.println(f1.getClass());
        System.out.println(f2.getClass());
        System.out.println(f1.getClass() == f2.getClass());
    }


    private static Foo readFromDisk() {
        try {
            FileInputStream fis = new FileInputStream("foo.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);

            Foo fromDisk = (Foo) ois.readObject();
            return fromDisk;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void writeToDisk(Foo f) {
        try {
            FileOutputStream fos = new FileOutputStream("foo.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
