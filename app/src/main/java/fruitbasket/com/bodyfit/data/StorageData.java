package fruitbasket.com.bodyfit.data;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by cielwu on 2016/6/2.
 */
public class StorageData {

    private String tag="storageData";
    private String savePath="";
    private File saveFile;
    private OutputStream out;
    private int amount=1;
    private double time;
    private long start,end;
    private boolean isfirst=true;

    public StorageData(){
        createDocument();
        createFile();
//        start=System.currentTimeMillis();
        try {
            out=getOutputStream();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    private void createDocument(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录
            File saveDocument = new File(sdCardDir,"/BodyFit/");
/*            if (!saveDocument.exists()) {
                saveDocument.mkdir();
            }*/
            savePath=saveDocument.getPath();
        }
    }

    private void createFile(){
        do {
            saveFile=new File(savePath,"Data"+amount+".txt");
            amount++;
        }while(saveFile.exists());
    }

/*    public void outputData(String data) throws IOException {
        File saveFile=new File(savePath,"jsonData.txt");
        FileOutputStream out = new FileOutputStream(saveFile,true);
        data+="\r";
        out.write(data.getBytes());
        out.close();
    }*/

    public void outputData(DataUnit data) throws IOException {

        if(data==null) return;

        String temp;
        if(isfirst){
            start=System.currentTimeMillis();
            isfirst=false;
        }
        end=System.currentTimeMillis();
        time=(end-start)/1000.0;//0.123456789 12.3456789
        temp=time+" "+(int)((data.getAx()*100))/100.0+" "+(int)((data.getAy()*100))/100.0+" "+(int)((data.getAz()*100))/100.0
                            +" "+(int)((data.getGx()*100))/100.0 +" "+(int)((data.getGy()*100))/100.0+" "+(int)((data.getGz()*100))/100.0
                            +" "+(int)((data.getMx()*100))/100.0+" "+(int)((data.getMy()*100))/100.0+" "+(int)((data.getMz()*100))/100.0
                            +" "+(int)((data.getP1()*100))/100.0+" "+(int)((data.getP2()*100))/100.0+" "+(int)((data.getP3()*100))/100.0+"\r";

        out.write(temp.getBytes());
    }

    public void outputData(DataSet data) throws IOException {
        Log.e(tag,"outputData(DataSet data)");
        if(data==null) return;

        double ax[],ay[],az[],gx[],gy[],gz[],mx[],my[],mz[],p1[],p2[],p3[];
        ax=data.getAxSet();
        ay=data.getAySet();
        az=data.getAzSet();
        gx=data.getGxSet();
        gy=data.getGySet();
        gz=data.getGzSet();
        mx=data.getMxSet();
        my=data.getMySet();
        mz=data.getMzSet();
        p1=data.getP1Set();
        p2=data.getP2Set();
        p3=data.getP3Set();

        String temp;
        if(isfirst){
            start=System.currentTimeMillis();
            isfirst=false;
        }


        int size=data.size();

        for(int i=0;i<size;i++){
//            Log.e(tag,"size="+size+" i="+i);
            end=System.currentTimeMillis();
            time=(end-start)/1000.0;

            temp=time+" "+((int)(ax[i]*100))/100.0+" "+((int)(ay[i]*100))/100.0+" "+((int)(az[i]*100))/100.0+" "
                            +((int)(gx[i]*100))/100.0+" "+((int)(gy[i]*100))/100.0+" "+((int)(gz[i]*100))/100.0+" "
                            +((int)(mx[i]*100))/100.0+" "+((int)(my[i]*100))/100.0+" "+((int)(mz[i]*100))/100.0+" "
                            +((int)(p1[i]*100))/100.0+" "+((int)(p2[i]*100))/100.0+" "+((int)(p3[i]*100))/100.0+"\r";

            out.write(temp.getBytes());
        }
    }

    public void outputDoubleDataArray(double data[]) throws IOException {
        String temp="";
        int i,len;
        len=data.length;

        for(i=0;i<len;i++)
            temp=temp+((int)(data[i]*100))/100.0+" ";
        temp+="\r\r\r";

        out.write(temp.getBytes());

    }

    public String getSavePath(){
        return savePath;
    }

    private OutputStream getOutputStream() throws FileNotFoundException {
        FileOutputStream out = new FileOutputStream(saveFile,true);
        return out;
    }

    public void closeOutputStream(){
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
