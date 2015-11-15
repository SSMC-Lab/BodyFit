package fruitbasket.com.bodyfit.helper;

public class FileHelper {
    private static FileHelper fileHelper=new FileHelper();

    private FileHelper(){}

    public static FileHelper getInstance(){
        return fileHelper;
    }
}
